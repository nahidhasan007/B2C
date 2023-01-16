package net.sharetrip.wheel.view.spin

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.graphics.Color
import android.media.MediaPlayer
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatDrawableManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.plattysoft.leonids.ParticleSystem
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.profile.ProfileActivity
import net.sharetrip.signup.view.RegistrationActivity
import net.sharetrip.wheel.R
import net.sharetrip.wheel.databinding.FragmentSpinToWinBinding
import net.sharetrip.wheel.library.LuckyWheelView
import net.sharetrip.wheel.network.DataManager
import net.sharetrip.wheel.util.setTitleAndSubtitle
import net.sharetrip.wheel.util.setTripCoin
import java.io.FileDescriptor

class SpinToWinFragment : BaseFragment<FragmentSpinToWinBinding>() {

    private val toWinViewModel: SpinToWinViewModel by viewModels {
        SpinToWinViewModelFactory(
            DataManager.getSharedPref(requireContext()),
            SpinToWinRepository(DataManager.wheelApiService)
        )
    }

    private val player: MediaPlayer = MediaPlayer()
    private var dialog: Dialog? = null
    private var dialogKeepPlaying: Dialog? = null
    private var mediaPlayer: MediaPlayer? = null
    private var mValueAnimator: ValueAnimator? = null

    override fun layoutId(): Int = R.layout.fragment_spin_to_win

    override fun getViewModel(): BaseViewModel = toWinViewModel

    private val registerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                dialog?.dismiss()
                toWinViewModel.checkLoginInformation()
            }
        }

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.spin_to_win))
        setTripCoin()

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = toWinViewModel

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.tick)

        toWinViewModel.luckyDataList.observe(viewLifecycleOwner) {
            bindingView.luckyWheel.setData(it)
            bindingView.luckyWheel.setRound(10)
            toWinViewModel.isPlayEnable = true
        }

        toWinViewModel.isShowMissingName.observe(viewLifecycleOwner) {
            showUpdateNameDialog()
        }

        toWinViewModel.coinValue.observe(viewLifecycleOwner) {
            if (it.first == "prev") {
                animateValueChange(toWinViewModel.oldTripCoinValue, it.second.toInt())
            } else if (it.first == "init") {
                setTripCoin(it.second)
            }
        }

        toWinViewModel.selectedIndex.observe(viewLifecycleOwner) {
            bindingView.luckyWheel.startLuckyWheelWithTargetIndex(it)
            mediaPlayer?.start()
        }

        toWinViewModel.showToast.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        })

        bindingView.luckyWheel.setLuckyRoundItemSelectedListener(object :
            LuckyWheelView.LuckyRoundItemSelectedListener {
            override fun LuckyRoundItemSelected(value: String?) {
                toWinViewModel.showDialog()
                if (toWinViewModel.coinValue.value!!.first == "win") {
                    runParticleAnimation(requireActivity(), bindingView.luckyWheel)
                    playCoinDropSound(requireActivity())
                    animateValueChange(
                        toWinViewModel.oldTripCoinValue,
                        toWinViewModel.coinValue.value!!.second.toInt()
                    )
                }
                try {
                    if (dialog?.isShowing!!) {
                        dialog?.dismiss()
                    }
                    dialog?.show()
                } catch (e: Exception) {
                }
            }
        })

        toWinViewModel.sorryMessage.observe(viewLifecycleOwner, EventObserver {
            showSorryDialog(it)
        })

        toWinViewModel.shareUrl.observe(viewLifecycleOwner) {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "\n\n")
            sharingIntent.putExtra(
                Intent.EXTRA_TEXT,
                requireActivity().resources.getString(R.string.referral_msg_other)
            )

            startActivity(
                Intent.createChooser(
                    sharingIntent,
                    resources.getString(R.string.app_name)
                )
            )
            toWinViewModel.onClickTryAgain()
        }

        bindingView.guestLayout.data = toWinViewModel.popupData
        toWinViewModel.isLoginLiveData.observe(viewLifecycleOwner) {
            if (it) {
                bindingView.rewardText.setTextColor(Color.parseColor("#FFFFFF"))
                Glide.with(requireActivity()).load(R.drawable.ic_reward_light)
                    .into(bindingView.rewardIcon)
            } else {
                bindingView.rewardText.setTextColor(Color.parseColor("#70FFFFFF"))
                Glide.with(requireActivity()).load(R.drawable.ic_reward_grey)
                    .into(bindingView.rewardIcon)
            }
        }

        toWinViewModel.navigateToLogin.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                val intent = Intent(requireContext(), RegistrationActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                registerResult.launch(intent)
            }
        })

        toWinViewModel.goToProfileDetails.observe(viewLifecycleOwner, EventObserver {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(ProfileActivity.ARG_PROFILE_ACTION, toWinViewModel.profileAction)
            startActivity(intent)
        })

        toWinViewModel.navigateToSelf.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateSafe(R.id.action_spinToWinFragment_self)
        })

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (toWinViewModel.coinValue.value!!.second.isNotEmpty() && toWinViewModel.coinValue.value!!.second.toDouble() >= 50) {
                        dialogKeepPlaying()
                    } else {
                        activity?.finish()
                    }
                }
            })
    }

    private fun dialogKeepPlaying() {
        if (dialogKeepPlaying != null) {
            dialogKeepPlaying = null
        }

        dialogKeepPlaying = Dialog(requireContext(), R.style.MyDynamicDialogTheme)
        dialogKeepPlaying?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogKeepPlaying?.setContentView(R.layout.dialog_keep_playing)
        dialogKeepPlaying?.setCancelable(false)

        val btnLeave = dialogKeepPlaying?.findViewById<Button>(R.id.button_keep_leave)
        val btnKeepPlaying = dialogKeepPlaying?.findViewById<Button>(R.id.button_keep_playing)

        btnLeave?.setOnClickListener {
            dialogKeepPlaying?.dismiss()
            activity?.finish()
        }

        btnKeepPlaying?.setOnClickListener {
            dialogKeepPlaying?.dismiss()
        }
        dialogKeepPlaying?.show()
    }

//    private fun initialDialog(title: String, body: String, type: Int) {
//        if (dialog != null) {
//            dialog = null
//        }
//        dialog = Dialog(requireContext(), R.style.MyDynamicDialogTheme)
//        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog?.setContentView(R.layout.dialog_lucky_wheel)
//        dialog?.setCancelable(false)
//
//        val txtTitle = dialog?.findViewById<TextView>(R.id.title)
//        val textViewMessage = dialog?.findViewById<TextView>(R.id.textViewMessage)
//        val btnLeave = dialog?.findViewById<Button>(R.id.btn_leave)
//        val btnShare = dialog?.findViewById<Button>(R.id.btn_share)
//        val btnAgain = dialog?.findViewById<Button>(R.id.btn_again)
//
//        txtTitle?.text = title
//        textViewMessage?.text = body
//
//        if (type == 0) {
//            btnLeave?.visibility = VISIBLE
//            btnShare?.visibility = GONE
//            btnAgain?.visibility = VISIBLE
//        } else {
//            btnLeave?.visibility = GONE
//            btnShare?.visibility = VISIBLE
//            btnAgain?.visibility = VISIBLE
//        }
//
//        btnLeave?.setOnClickListener {
//            dialog?.dismiss()
//            toWinViewModel.onBackPressed()
//        }
//
//        btnShare?.setOnClickListener {
//            dialog?.dismiss()
//            toWinViewModel.fetchShareUrl()
//        }
//
//        btnAgain?.setOnClickListener {
//            dialog?.dismiss()
//            toWinViewModel.onClickTryAgain()
//        }
//    }

    private fun showSorryDialog(body: String) {
        val dialog = Dialog(requireContext(), R.style.MyDynamicDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_sorry)
        dialog.setCancelable(false)

        val textViewSorryMessage = dialog.findViewById<TextView>(R.id.textViewSorryMessage)
        val btnHome = dialog.findViewById<Button>(R.id.btn_home)
        textViewSorryMessage?.text = body

        btnHome.setOnClickListener {
            dialog.dismiss()
            toWinViewModel.onBackPressed()
        }
        dialog.show()
    }

    private fun showUpdateNameDialog() {
        val dialog = Dialog(requireContext(), R.style.MyDynamicDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_missing_name)
        dialog.setCancelable(true)

        val dialogUpdateBtn = dialog.findViewById<Button>(R.id.btn_update)
        val firstName = dialog.findViewById<TextInputEditText>(R.id.first_name_input_edit_text)
        val lastName = dialog.findViewById<TextInputEditText>(R.id.last_name_input_edit_text)

        dialogUpdateBtn.setOnClickListener {
            when {
                firstName.text.toString().trim().isEmpty() -> {
                    dialog.dismiss()
                    Toast.makeText(activity, "First Name Required", Toast.LENGTH_LONG).show()
                }
                lastName.text.toString().trim().isEmpty() -> {
                    dialog.dismiss()
                    Toast.makeText(activity, "Last Name Required", Toast.LENGTH_LONG).show()
                }
                else -> {
                    dialog.dismiss()
                    toWinViewModel.sendUpdateProfile(
                        firstName.text.toString().trim(),
                        lastName.text.toString().trim()
                    )
                }
            }
        }
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer?.release()
        if (mValueAnimator != null) {
            if (mValueAnimator!!.isRunning)
                mValueAnimator!!.cancel()
        }
    }

    fun animateValueChange(startNumber: Int, endNumber: Int) {
        val textView: TextView =
            (activity as AppCompatActivity).findViewById<TextView>(R.id.text_view_trip_coin)

        val startSize = textView.textSize
        val animatorEnlarge = ValueAnimator.ofFloat(startSize, startSize + 20f)
        animatorEnlarge.duration = 1000
        animatorEnlarge.addUpdateListener {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, it.animatedValue as Float)
        }
        animatorEnlarge.start()

        mValueAnimator = ValueAnimator.ofInt(startNumber, endNumber)
        mValueAnimator!!.addUpdateListener {
            if (activity != null) {
                textView.text =
                    it.animatedValue.toString()
            }
        }
        mValueAnimator!!.duration = 1000
        mValueAnimator!!.start()

        val animatorDecrease = ValueAnimator.ofFloat(textView.textSize, startSize)
        animatorDecrease.startDelay = 1000
        animatorDecrease.duration = 1000
        animatorDecrease.addUpdateListener {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, it.animatedValue as Float)
        }
        animatorDecrease.start()
    }

    @SuppressLint("RestrictedApi")
    fun runParticleAnimation(activity: Activity, startView: View) {
        ParticleSystem(
            activity,
            120,
            AppCompatDrawableManager.get()
                .getDrawable(activity as Context, R.drawable.ic_trip_coin_yellow_20dp),
            1000
        )
            .setSpeedModuleAndAngleRange(0.1f, 0.5f, 180, 360)
            .setRotationSpeed(144f)
            .oneShot(startView, 120)
    }

    private fun playCoinDropSound(activity: Activity) {
        val afd: AssetFileDescriptor = activity.resources.openRawResourceFd(R.raw.coins_falling)
        val fileDescriptor: FileDescriptor = afd.fileDescriptor

        try {
            player.setDataSource(fileDescriptor, afd.startOffset, afd.length)
            player.isLooping = false
            player.prepare()
            player.start()
        } catch (exception: Exception) {
        }
    }
}

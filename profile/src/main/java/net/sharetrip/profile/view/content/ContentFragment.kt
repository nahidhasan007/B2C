package net.sharetrip.profile.view.content

import android.view.View
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.core.text.HtmlCompat.fromHtml
import androidx.fragment.app.viewModels
import com.sharetrip.base.data.PrefKey
import net.sharetrip.shared.utils.hideTripCoin
import net.sharetrip.shared.utils.setTitleAndSubtitle
import net.sharetrip.shared.utils.showTripCoin
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.FragmentContentBinding
import net.sharetrip.profile.model.FaqItem
import net.sharetrip.profile.network.DataManager

class ContentFragment : BaseFragment<FragmentContentBinding>() {

    private val viewModel: ContentViewModel by viewModels {
        val faqNumber = arguments?.getInt(ARG_FAQ_NUMBER)
        ContentViewModelFactory(faqNumber!!, ContentRepo(DataManager.profileApiService))
    }

    override fun layoutId() = R.layout.fragment_content

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        val sharedPrefsHelper = DataManager.getSharedPref(requireContext())
        val faqNumber = arguments?.getInt(ARG_FAQ_NUMBER)

        when (faqNumber) {
            ARG_TERMS_AND_CONDITION_TRIVIA, ARG_TERMS_AND_CONDITION_COMMON -> {
                setTitleAndSubtitle(getString(R.string.term_and_condition))
            }

            ARG_PRIVACY_POLICY -> {
                setTitleAndSubtitle(getString(R.string.privacy_policy))
            }

            else -> {
                setTitleAndSubtitle(getString(R.string.profile))
            }
        }

        viewModel.faqResponse.observe(viewLifecycleOwner) {
            setContent(it)
        }

        viewModel.tocResponse.observe(viewLifecycleOwner) {
            setContent(it)
        }

        when (faqNumber) {
            ARG_PRIVACY_POLICY -> {
                val title =
                    getString(R.string.privacy_policy_title_html)
                val body =
                    getString(R.string.privacy_policy_body_html)
                setContent(FaqItem(title, body))
            }

            ARG_TERMS_AND_CONDITION_TRIVIA -> {
                val title =
                    getString(R.string.term_condition_title_trivia)
                val body =
                    sharedPrefsHelper[PrefKey.QUIZ_TERM_AND_CONDITION, ""]
                setContent(FaqItem(title, body))
            }

            ARG_FAQ_TRAVEL_TRIVIA -> {
                val title =
                    getString(R.string.term_condition_title_trivia)
                val body = sharedPrefsHelper[PrefKey.QUIZ_FAQ, ""]
                setContent(FaqItem(title, body))
            }

            ARG_READ_RULES -> {
                setTitleAndSubtitle("How To Read Rules")
                val title = ""
                val body = getString(R.string.read_rules)
                setContent(FaqItem(title, body))
            }

            ARG_TERMS_AND_CONDITION_SPIN_TO_WIN, ARG_TERMS_AND_CONDITION_LOYALTY, ARG_TERMS_AND_CONDITION_COMMON -> {
                viewModel.loadTOCData()
            }

            else -> {
                viewModel.loadFAQData()
            }
        }
    }

    private fun setContent(faqItem: FaqItem) {
        if (faqItem.title.isEmpty()) {
            bindingView.textViewTitle.visibility = View.GONE
        } else {
            bindingView.textViewTitle.text = faqItem.title
        }
        bindingView.textViewDescription.text = fromHtml(faqItem.body,FROM_HTML_MODE_LEGACY)
        bindingView.progressBar.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        hideTripCoin()
    }

    override fun onStop() {
        super.onStop()
        showTripCoin()
    }

    companion object {
        const val ARG_FAQ_NUMBER = "faq_number"
        const val ARG_PRIVACY_POLICY = 0

        const val ARG_FAQ_COMMON = 1
        const val ARG_FAQ_HOTEL = 2
        const val ARG_FAQ_FLIGHT = 3
        const val ARG_FAQ_HOLIDAY = 4
        const val ARG_FAQ_TOUR = 5
        const val ARG_FAQ_TRANSFER = 6
        const val ARG_FAQ_TRIP_COIN = 7
        const val ARG_FAQ_TRAVEL_TRIVIA = 8

        const val ARG_TERMS_AND_CONDITION_COMMON = 9
        const val ARG_TERMS_AND_CONDITION_SPIN_TO_WIN = 10
        const val ARG_TERMS_AND_CONDITION_LOYALTY = 11
        const val ARG_TERMS_AND_CONDITION_TRIVIA = 12
        const val ARG_READ_RULES = 13
    }
}

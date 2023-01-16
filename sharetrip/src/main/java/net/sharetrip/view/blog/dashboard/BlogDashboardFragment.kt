package net.sharetrip.view.blog.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.sharetrip.base.event.EventObserver
import com.sharetrip.base.network.NetworkUtil
import net.sharetrip.shared.utils.SLUG
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.R
import net.sharetrip.databinding.FragmentBlogDashboardBinding
import net.sharetrip.flight.booking.FlightBookingActivity
import net.sharetrip.hotel.booking.HotelBookingActivity
import net.sharetrip.model.BlogBookingType
import net.sharetrip.network.MainDataManager
import net.sharetrip.view.blog.dashboard.adapter.BlogAdapter
import net.sharetrip.view.blog.dashboard.adapter.BlogBookingAdapter
import net.sharetrip.view.blog.dashboard.adapter.BlogTopSliderAdapter
import net.sharetrip.view.blog.dashboard.adapter.TrendingBlogAdapter

class BlogDashboardFragment : BaseFragment<FragmentBlogDashboardBinding>() {
    lateinit var blogTopSliderAdapter: BlogTopSliderAdapter
    private var dotsCount = 0
    private var isLoading = true
    private val topBlogAdapter = BlogAdapter()
    private val trendingTopicAdapter = TrendingBlogAdapter()
    private val blogBookingAdapter = BlogBookingAdapter()
    private lateinit var dots: Array<ImageView?>

    private val viewModel by lazy {
        BlogDashboardVMFactory(
            MainDataManager.mainApiService,
            MainDataManager.getSharedPref(requireContext())
        ).create(BlogDashboardViewModel::class.java)
    }



    override fun layoutId(): Int = R.layout.fragment_blog_dashboard

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel

        viewModel.blogSliderPost.observe(viewLifecycleOwner) {
            blogTopSliderAdapter = BlogTopSliderAdapter(requireContext(), it, viewModel)
            bindingView.viewPagerTopBlog.adapter = blogTopSliderAdapter
            bindingView.viewPagerTopBlog.pageMargin = 24
            setViewPagerDots()
        }

        bindingView.viewPagerTopBlog.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageSelected(position: Int) {
                for (i in 0 until dotsCount) {
                    dots[i]!!.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.nonactive_dot
                        )
                    )
                }
                dots[position]!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.active_dot
                    )
                )
            }

        })

        bindingView.recyclerBlog.adapter = topBlogAdapter
        ItemClickSupport.addTo(bindingView.recyclerBlog).setOnItemClickListener { _, position, _ ->
            navigateToDetails(topBlogAdapter.getItem(position).slug)
        }

        viewModel.topBlogPost.observe(viewLifecycleOwner) {
            topBlogAdapter.updateBlogList(it)
        }

        bindingView.recyclerBlog.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (dy > 0) {
                    val visibleItemCount = linearLayoutManager.childCount
                    val totalItemCount = linearLayoutManager.itemCount
                    val pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition()

                    if (isLoading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            isLoading = false
                            loadMore()
                        }
                    }
                }
            }
        })

        bindingView.recyclerTrendingTopics.adapter = trendingTopicAdapter

        ItemClickSupport.addTo(bindingView.recyclerTrendingTopics)
            .setOnItemClickListener { _, position, _ ->
                navigateToDetails(trendingTopicAdapter.getItem(position).slug)
            }

        viewModel.trendingTopicsPost.observe(viewLifecycleOwner) {
            trendingTopicAdapter.updateTravelerList(it)
        }

        bindingView.recyclerBlogBooking.adapter = blogBookingAdapter

        ItemClickSupport.addTo(bindingView.recyclerBlogBooking)
            .setOnItemClickListener { _, position, _ ->
                navigateToBooking(blogBookingAdapter.getItem(position).buttonText)
            }

        viewModel.blogBookingList.observe(viewLifecycleOwner) {
            blogBookingAdapter.updateBlogBookingList(it)
        }

        viewModel.gotoFlight.observe(viewLifecycleOwner) {
            val intent = Intent(context, FlightBookingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        viewModel.goToCategory.observe(viewLifecycleOwner, EventObserver {
            if (it) findNavController().navigateSafe(R.id.action_action_blog_to_blogCategoryFragment)
        })

        viewModel.goToSearch.observe(viewLifecycleOwner, EventObserver {
            if (it) findNavController().navigateSafe(R.id.action_action_blog_to_searchBlogFragment)
        })

        viewModel.selectedSlug.observe(viewLifecycleOwner, EventObserver {
            if (it.isNotEmpty()) navigateToDetails(it)
        })

    }

    private fun navigateToDetails(slug: String) {
        findNavController().navigateSafe(
            R.id.action_action_blog_to_blogDetailsFragment,
            bundleOf(SLUG to slug)
        )
    }

    private fun navigateToBooking(bookingType: String) {
        if (NetworkUtil.hasNetwork(requireContext())) {
            if (bookingType == BlogBookingType.BOOKING_FLIGHT.bookingName) {
                val intent = Intent(context, FlightBookingActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            } else if (bookingType == BlogBookingType.BOOKING_HOTEL.bookingName) {
                val intent = Intent(context, HotelBookingActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        } else
            Toast.makeText(
                requireContext(),
                getString(R.string.no_internet_found_check_connectivity),
                Toast.LENGTH_SHORT
            ).show()
    }

    private fun setViewPagerDots() {
        dotsCount = blogTopSliderAdapter.count
        dots = arrayOfNulls(dotsCount)

        for (i in 0 until dotsCount) {
            dots[i] = ImageView(context)
            dots[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.nonactive_dot
                )
            )

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(6, 8, 6, 8)
            bindingView.linearSliderDots.addView(dots[i], params)
        }

        dots[0]?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.active_dot
            )
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadMore() {

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            topBlogAdapter.notifyDataSetChanged()
            isLoading = true

        }, 2000)
    }

    
}
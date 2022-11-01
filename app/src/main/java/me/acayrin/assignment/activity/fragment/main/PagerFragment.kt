package me.acayrin.assignment.activity.fragment.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import me.acayrin.assignment.R

class PagerFragment : Fragment(R.layout.fragment_main_pager) {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout = requireActivity().findViewById(R.id.fragment_pager_tablayout)
        viewPager = requireActivity().findViewById(R.id.fragment_pager_viewpager)

        viewPager.adapter = PagerAdapter(childFragmentManager)

        tabLayout.let {
            it.setTabTextColors(R.color.c2, R.color.c4)
            it.setSelectedTabIndicatorColor(resources.getColor(R.color.c4))
            it.setupWithViewPager(viewPager)
        }
    }

    class PagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount(): Int {
            return 3
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> CoursesFragment()
                1 -> ClassesFragment()
                2 -> ExamsFragment()
                else -> CoursesFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "Courses"
                1 -> "Classes"
                2 -> "Exams"
                else -> "Courses"
            }
        }
    }

}
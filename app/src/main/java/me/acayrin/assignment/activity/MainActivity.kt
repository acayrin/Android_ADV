package me.acayrin.assignment.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import me.acayrin.assignment.R
import me.acayrin.assignment.activity.fragment.main.HomeFragment
import me.acayrin.assignment.activity.fragment.main.MapFragment
import me.acayrin.assignment.activity.fragment.main.NewsFragment
import me.acayrin.assignment.activity.fragment.main.PagerFragment
import me.acayrin.assignment.activity.hooks.PreRender

class MainActivity : AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PreRender.render(this)

        tabLayout = findViewById(R.id.v_main_tablayout)
        viewPager = findViewById(R.id.v_main_viewpager)

        viewPager.adapter = PagerAdapter(supportFragmentManager, 4)

        tabLayout.let {
            it.setSelectedTabIndicatorColor(resources.getColor(R.color.c4))
            it.setupWithViewPager(viewPager)

            for (i in 0 until 4) {
                val ico = ResourcesCompat.getDrawable(
                    resources,
                    when (i) {
                        0 -> R.drawable.ic_menu_chome
                        1 -> R.drawable.ic_menu_book
                        2 -> R.drawable.ic_menu_news
                        3 -> R.drawable.ic_menu_map
                        else -> R.drawable.ic_menu_chome
                    },
                    null
                )
                ico?.let { draw ->
                    draw.setTint(resources.getColor(R.color.c4))

                    val iv = ImageView(this)
                    iv.setImageDrawable(ico)

                    it.getTabAt(i)?.customView = iv
                }
            }
        }
    }
}

class PagerAdapter(fm: FragmentManager, private val tabsCount: Int) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int {
        return tabsCount
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> PagerFragment()
            2 -> NewsFragment()
            3 -> MapFragment()
            else -> HomeFragment()
        }
    }
}

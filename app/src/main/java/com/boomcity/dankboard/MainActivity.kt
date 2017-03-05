package com.boomcity.dankboard

import android.content.Context
import android.opengl.Visibility
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.github.clans.fab.FloatingActionButton
import com.google.gson.Gson
import com.github.clans.fab.FloatingActionMenu



class MainActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    lateinit var mViewPager: ViewPager
    lateinit var tabLayout: TabLayout
    lateinit var tabFAM: FloatingActionMenu
    lateinit var renameTabFab: FloatingActionButton
    lateinit var deleteTabFab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        getStoredTabData()

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        mViewPager = findViewById(R.id.container) as ViewPager
        mViewPager.adapter = mSectionsPagerAdapter
        mViewPager.setOffscreenPageLimit(5) //fuck android, set this to the total number of tabs that are created including ALL

        DataService.setViewpager(mViewPager)

        tabLayout = findViewById(R.id.tabs) as TabLayout
        tabLayout.setupWithViewPager(mViewPager)
        tabLayout.addOnTabSelectedListener(this)

        tabFAM = findViewById(R.id.tab_FAM) as FloatingActionMenu
        renameTabFab = findViewById(R.id.floating_menu_rename) as FloatingActionButton
        deleteTabFab = findViewById(R.id.floating_menu_delete) as FloatingActionButton

        tabFAM.visibility = View.INVISIBLE

        renameTabFab.setOnClickListener({
            renameTab()
        })
        deleteTabFab.setOnClickListener({
            //TODO something when floating action menu second item clicked
        })

    }

    private fun getStoredTabData() {
        val sharedPrefs = getPreferences(Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPrefs.getString("TabsDataInfo", "")
        var tabsData = gson.fromJson<TabsData>(json, TabsData::class.java)

        if (tabsData == null) {
            //first startup
            tabsData = TabsData(mutableListOf(TabDataInfo("Favorites",1, mutableListOf())))
        }

        DataService.init(tabsData,sharedPrefs)
    }

    fun addNewTab() {
        tabLayout.addTab(tabLayout.newTab())
        mSectionsPagerAdapter!!.addNewTab()
    }

    fun renameTab() {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab!!.position != 0){
            tabFAM.visibility = View.VISIBLE
        }
        else {
            tabFAM.visibility = View.INVISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_new_tab) {
            addNewTab()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private var tabCount: Int = DataService.getTabsData().tabsInfo!!.size + 1

        override fun getItem(position: Int): Fragment {
            return TabFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            return tabCount
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return "All"
            }
            return DataService.getTabsData().tabsInfo!![position - 1].name
        }

        fun addNewTab() {
            DataService.addNewTab("New Tab", tabCount)
            tabCount++
            notifyDataSetChanged()
        }
    }
}

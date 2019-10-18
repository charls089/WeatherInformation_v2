package com.kobbi.weather.info.ui.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kobbi.weather.info.R
import com.kobbi.weather.info.databinding.ActivityMainBinding
import com.kobbi.weather.info.presenter.viewmodel.AreaViewModel
import com.kobbi.weather.info.presenter.viewmodel.WeatherViewModel
import com.kobbi.weather.info.ui.view.dialog.ProgressDialog
import com.kobbi.weather.info.util.BackPressedCloser
import com.kobbi.weather.info.util.DLog

class MainActivity : AppCompatActivity() {

    private var mAreaVm: AreaViewModel? = null
    private val mProgressDialog by lazy { ProgressDialog(this) }
    private val mBackPressedCloser by lazy { BackPressedCloser(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mProgressDialog.show()
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).run {
            val weatherVm = ViewModelProviders.of(this@MainActivity)[WeatherViewModel::class.java]
            mAreaVm = ViewModelProviders.of(this@MainActivity)[AreaViewModel::class.java].apply {
                area.observe(this@MainActivity, Observer {
                    DLog.d("AreaViewModel","area.observe() --> Area Change")
                    weatherVm.refreshData()
                    vpMainPage.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.anim_fade_in))
                })
            }
            areaVm = mAreaVm
            fragmentManager = supportFragmentManager
            lifecycleOwner = this@MainActivity
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_refresh -> {
                //데이터 새로고침
                mProgressDialog.show()
                mAreaVm?.refreshWeatherInfo(true)
            }
            R.id.action_add_location -> {
                //즐겨찾는 장소 화면 이동
                startActivity(Intent(this, AddPlaceActivity::class.java))
            }
            R.id.action_setting -> {
                //설정 화면 이동
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        mBackPressedCloser.onBackPressed()
    }
}

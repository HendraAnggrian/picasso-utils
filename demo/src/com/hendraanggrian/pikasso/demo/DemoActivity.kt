package com.hendraanggrian.pikasso.demo

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.ColorInt
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.util.PatternsCompat.WEB_URL
import androidx.core.view.GravityCompat.START
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.from
import com.hendraanggrian.material.errorbar.Errorbar
import com.hendraanggrian.material.errorbar.indefiniteErrorbar
import com.hendraanggrian.pikasso.buildPicasso
import com.squareup.picasso.Cache
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_demo.*

class DemoActivity : AppCompatActivity(), OnSharedPreferenceChangeListener {

    val fragment = DemoFragment()
    lateinit var bottomSheetBehavior: BottomSheetBehavior<AppBarLayout>

    lateinit var picasso: Picasso
    lateinit var drawerToggle: ActionBarDrawerToggle

    lateinit var pasteItem: MenuItem
    lateinit var toggleExpandItem: MenuItem
    lateinit var errorbar: Errorbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)
        setSupportActionBar(toolbar)
        supportActionBar!!.run {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        picasso = buildPicasso {
            loggingEnabled(BuildConfig.DEBUG)
            memoryCache(Cache.NONE)
        }
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0).apply {
            isDrawerIndicatorEnabled = true
        }
        drawerLayout.addDrawerListener(drawerToggle)

        bottomSheetBehavior = from(appBarLayout)
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commitNow()
        onSharedPreferenceChanged(fragment.preferenceScreen.sharedPreferences, fragment.input.key)

        toolbar2.inflateMenu(R.menu.activity_demo)
        pasteItem = toolbar2.menu.findItem(R.id.pasteItem).apply {
            setOnMenuItemClickListener {
                fragment.listView.smoothScrollToPosition(0)
                (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).run {
                    if (hasPrimaryClip() && primaryClipDescription
                            .hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        val clipboard = primaryClip.getItemAt(0).text.toString()
                        fragment.input.text = clipboard
                        fragment.onPreferenceChange(fragment.input, clipboard) // trigger
                    }
                }
                true
            }
        }
        toggleExpandItem = toolbar2.menu.findItem(R.id.toggleExpandItem).apply {
            setOnMenuItemClickListener {
                toolbar2.performClick()
                true
            }
        }

        errorbar = photoView.indefiniteErrorbar("Expand panel below to start loading") {
            setAction(R.string.expand) {
                //                panelLayout.panelState = EXPANDED
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fragment.preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        fragment.preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            drawerLayout.openDrawer(START)
        }
        return false
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == fragment.input.key) {
            button.isEnabled = WEB_URL.toRegex().matches(sharedPreferences.getString(key, ""))
        }
    }

    fun toggleExpand(view: View) {
        bottomSheetBehavior.state = when (STATE_COLLAPSED) {
            bottomSheetBehavior.state -> STATE_EXPANDED
            else -> STATE_COLLAPSED
        }
    }

    private companion object {
        infix fun Toolbar.assign(@ColorInt color: Int) {
            setBackgroundColor(color)
            subtitle = "#%06X".format(0xFFFFFF and color)

            if ((Color.red(color) + Color.green(color) + Color.blue(color)) / 3 < 127.5) {
                setTitleTextAppearance(context, R.style.TextAppearance_AppCompat_Small)
                setSubtitleTextAppearance(context, R.style.TextAppearance_AppCompat_Subhead)
            } else {
                setTitleTextAppearance(context, R.style.TextAppearance_AppCompat_Small_Inverse)
                setSubtitleTextAppearance(context, R.style.TextAppearance_AppCompat_Subhead_Inverse)
            }
        }
    }
}
package com.example.picassoutils

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.hendraanggrian.kota.view.hideInput
import com.hendraanggrian.picasso.picasso
import com.hendraanggrian.picasso.target.Targets
import kotlinx.android.synthetic.main.activity_target.*
import org.jetbrains.anko.toast

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class TargetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        setContentView(R.layout.activity_target)
        buttonClear.setOnClickListener { editText!!.setText("") }
        buttonGo.setOnClickListener {
            hideInput()
            picasso(editText.text.toString())
                    .into(Targets.progress(imageView)
                            .callback({ _, _ ->
                                toast("Loaded.")
                            }, {
                                toast("Failed.")
                            }, {
                                toast("Prepare...")
                            }))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
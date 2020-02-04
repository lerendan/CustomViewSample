package com.lerendan.customviewsample

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.lerendan.customviewsample.ruler.RulerTestActivity
import com.lerendan.customviewsample.thumbup.ThumbUpTestActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_thumb_up.setOnClickListener {
            startActivity(Intent(this, ThumbUpTestActivity().javaClass))
        }

        btn_ruler.setOnClickListener {
            startActivity(Intent(this, RulerTestActivity().javaClass))
        }
    }
}

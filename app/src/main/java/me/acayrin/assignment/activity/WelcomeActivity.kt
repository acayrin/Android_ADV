package me.acayrin.assignment.activity

import androidx.appcompat.app.AppCompatActivity
import android.widget.ProgressBar
import android.os.Bundle
import me.acayrin.assignment.activity.hooks.PreRender
import me.acayrin.assignment.R
import android.content.Intent
import android.os.Handler
import java.lang.Exception

class WelcomeActivity : AppCompatActivity() {
    private var progressBar: ProgressBar? = null
    private var progressStat = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreRender.render(this)
        setContentView(R.layout.activity_welcome)
        progressBar = findViewById(R.id.v_loading_progress)

        // progress bar
        while (progressStat < 100) {
            progressStat += 1
            progressBar?.progress = progressStat
            try {
                Thread.sleep(20)
            } catch (e: Exception) {
                // logcat
            }
        }
        Handler().postDelayed({
            startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
            finish()
        }, 2000)
    }
}
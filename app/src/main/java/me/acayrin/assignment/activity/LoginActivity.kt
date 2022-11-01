package me.acayrin.assignment.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import me.acayrin.assignment.R
import me.acayrin.assignment.activity.hooks.PreRender
import me.acayrin.assignment.dao.DAO_NguoiDung
import me.acayrin.assignment.service.AccountService
import me.acayrin.assignment.service.NewsService
import me.acayrin.assignment.service.course.ClassService
import me.acayrin.assignment.service.course.CourseService
import me.acayrin.assignment.service.course.ExamService

class LoginActivity : AppCompatActivity() {
    private var daoNguoiDung: DAO_NguoiDung? = null
    private var etFullname: EditText? = null
    private var etUsername: EditText? = null
    private var etPassword: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // handle services
        startService(Intent(this, AccountService::class.java))
        startService(Intent(this, CourseService::class.java))
        startService(Intent(this, ClassService::class.java))
        startService(Intent(this, NewsService::class.java))
        startService(Intent(this, ExamService::class.java))

        // TODO: IntentFilter is needed for registerReceiver
        val intentFilter = IntentFilter()
        intentFilter.addAction("fpt.account.response")
        registerReceiver(broadcastReceiver, intentFilter)

        PreRender.render(this)
        if (daoNguoiDung == null) daoNguoiDung = DAO_NguoiDung(this)

        if (daoNguoiDung!!.all.size == 0) {
            setContentView(R.layout.activity_login_register)
            etFullname = findViewById(R.id.v_register_in_fname)
            etUsername = findViewById(R.id.v_register_in_username)
            etPassword = findViewById(R.id.v_register_in_password)
            findViewById<Button>(R.id.v_register_btn).setOnClickListener {
                Log.i("View::Login", "Btn:Register")

                val requestBundle = Bundle()
                val requestIntent = Intent()

                requestBundle.putString("inFullname", etFullname?.text.toString())
                requestBundle.putString("inUsername", etUsername?.text.toString())
                requestBundle.putString("inPassword", etPassword?.text.toString())

                requestIntent.putExtras(requestBundle)
                requestIntent.action = "fpt.account.register"
                sendBroadcast(requestIntent)
            }
        } else {
            setContentView(R.layout.activity_login_login)
            etUsername = findViewById(R.id.v_login_in_username)
            etPassword = findViewById(R.id.v_login_in_password)

            findViewById<Button>(R.id.v_login_btn).setOnClickListener { v: View? ->
                Log.i("View::Login", "Btn:Login")

                val requestBundle = Bundle()
                val requestIntent = Intent()

                requestBundle.putString("inUsername", etUsername?.text.toString())
                requestBundle.putString("inPassword", etPassword?.text.toString())

                requestIntent.putExtras(requestBundle)
                requestIntent.action = "fpt.account.request"
                sendBroadcast(requestIntent)
            }
        }
    }

    // listen for responses from account service
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action !== "fpt.account.response") return
            Log.i("View::Login", "Received response from account service")

            val responseBundle = intent.extras as Bundle

            if (responseBundle.getBoolean("responseStatus")) {
                Toast.makeText(
                    this@LoginActivity,
                    "Successfully logged in!",
                    Toast.LENGTH_SHORT
                ).show()

                getSharedPreferences("fpt_user", MODE_PRIVATE)
                    .edit()
                    .putString("username", responseBundle.getString("responseUsername", "unknown"))
                    .putInt("id", responseBundle.getInt("responseUserId", -1))
                    .apply()

                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            } else {
                val responseMessage = responseBundle.getString("responseMessage")
                Toast.makeText(this@LoginActivity, responseMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
package com.example.fishbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginPage : AppCompatActivity() {

    private lateinit var textView: TextView
    lateinit var editEmail: EditText
    private lateinit var editPass: EditText
    lateinit var btnLogin: Button


    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        textView = findViewById(R.id.registerNow)
        btnLogin = findViewById(R.id.btn_login)
        editEmail = findViewById(R.id.email)
        editPass = findViewById(R.id.password)


        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            login()
        }

        textView.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)

            finish()
        }
    }

    private fun login() {
        val email = editEmail.text.toString()
        val pass = editPass.text.toString()

        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else
                Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
        }
    }

}
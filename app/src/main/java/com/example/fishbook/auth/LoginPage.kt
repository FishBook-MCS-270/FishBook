package com.example.fishbook.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.fishbook.MainActivity
import com.example.fishbook.R
import com.google.firebase.auth.FirebaseAuth

class LoginPage : AppCompatActivity() {

    private lateinit var textView: TextView
    lateinit var editEmail: EditText
    private lateinit var editPass: EditText
    lateinit var btnLogin: Button
    lateinit var checkBox: CheckBox
    lateinit var preferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var auth: FirebaseAuth
    lateinit var check: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        checkBox = findViewById(R.id.checkBoxRememberMe)
        textView = findViewById(R.id.registerNow)
        btnLogin = findViewById(R.id.btn_login)
        editEmail = findViewById(R.id.email)
        editPass = findViewById(R.id.password)


        auth = FirebaseAuth.getInstance()

        preferences = getSharedPreferences("checkbox", MODE_PRIVATE)
        check = preferences.getString("remember", "").toString()
        if(check.equals("true")){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }else if(check.equals("false")){
            Toast.makeText(this, "Please sign in.", Toast.LENGTH_SHORT).show()
        }

        btnLogin.setOnClickListener {
            login()
        }

        textView.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)

            finish()
        }

        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (checkBox.isChecked()) {
                preferences = getSharedPreferences("checkbox", MODE_PRIVATE)
                editor = preferences.edit()
                editor.putString("remember", "true")
                editor.apply()
                Toast.makeText(this, "Checked", Toast.LENGTH_SHORT).show()

            } else if (!checkBox.isChecked()) {
                preferences = getSharedPreferences("checkbox", MODE_PRIVATE)
                editor = preferences.edit()
                editor.putString("remember", "false")
                editor.apply()
                Toast.makeText(this, "Unchecked", Toast.LENGTH_SHORT).show()

            }
        }




    }

    private fun login() {
        val email = editEmail.text.toString()
        val pass = editPass.text.toString()

        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else
                Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
        }
    }



}
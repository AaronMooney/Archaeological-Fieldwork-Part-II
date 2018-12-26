package org.wit.hillfort.activities

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_user_signup.*
import org.jetbrains.anko.toast
import org.wit.hillfort.R
import org.wit.hillfort.main.MainApp
import org.wit.hillfort.models.UserModel
import org.wit.hillfort.helpers.hashString

class UserSignupActivity : AppCompatActivity(){

    var user = UserModel()
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_signup)
        app = application as MainApp

        btnUserSignup.setOnClickListener{
            signup()
        }
    }

    fun signup() {
        if (!isValidSignup()){
            return
        }

        user.name = userName.text.toString()
        user.email = userEmail.text.toString()
        user.password = hashString(userPassword.text.toString())

        app.users.addUser(user.copy())

        clear()
        toast(R.string.signup_success)
        setResult(Activity.RESULT_OK,null)
        finish()
    }

    fun isValidSignup(): Boolean {

        if (userName.text.isNullOrEmpty()) {
            toast(R.string.error_signup_name)
            return false
        }

        if (userEmail.text.isNullOrEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail.text).matches()) {
            toast(R.string.error_signup_email)
            return false
        }

        if (userPassword.text.isNullOrEmpty()) {
            toast(R.string.error_signup_password)
            userPassword.text?.clear()
            confirmUserPassword.text?.clear()
            return false
        }

        if (confirmUserPassword.text.toString() != userPassword.text.toString()){
            toast(R.string.error_signup_confirm)
            userPassword.text?.clear()
            confirmUserPassword.text?.clear()
            return false
        }

        return true
    }

    fun clear(){
        userName.text?.clear()
        userPassword.text?.clear()
        userEmail.text?.clear()
        confirmUserPassword.text?.clear()
    }
}

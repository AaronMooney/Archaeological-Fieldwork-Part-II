package org.wit.hillfort.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_user_welcome.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.wit.hillfort.R
import org.wit.hillfort.main.MainApp
import org.wit.hillfort.models.UserModel
import org.wit.hillfort.helpers.hashString

class UserWelcomeActivity : AppCompatActivity(){

    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_welcome)
        app = application as MainApp

        btnUserCreateAccount.setOnClickListener{
            startActivityForResult(intentFor<UserSignupActivity>(),0)
        }

        btnUserLogin.setOnClickListener{
            login()
        }
    }

    fun login() {
        val email = userEmailLogin.text.toString()
        val password = hashString(userPasswordLogin.text.toString())

        var userExists = UserModel()
        var userFound = false

        app.users.getUsers().forEach{
            if (it.email == email && it.password == password){
                userExists = it
                userFound = true
            }
        }

        if (userFound == false){
            toast(R.string.error_login)
            return
        }

        clear()
        toast(R.string.login_success)
        app.currentUser = userExists
        startActivityForResult(intentFor<HillfortListActivity>(), 0)
    }

    fun clear(){
        userEmailLogin.text?.clear()
        userPasswordLogin.text?.clear()
    }
}

package com.ll.risa_second

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    var auth : FirebaseAuth? = null
    var googleSingInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        email_login_button.setOnClickListener{
            signinAndSignup()
        }
        google_sign_in_button.setOnClickListener {
            googleLogin()
        }
        var gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("973705262050-a5cbp8np9f2n73v660m2cijuigvoeoc0.apps.googlesercontent.com")
            .requestEmail()
            .build()
        googleSingInClient = GoogleSignIn.getClient(this,gso)
    }
    fun googleLogin(){
        var signInIntent = googleSingInClient?.signInIntent
        startActivityForResult(signInIntent,GOOGLE_LOGIN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = data?.let { Auth.GoogleSignInApi.getSignInResultFromIntent(it) }
            if(result!!.isSuccess){
                var account = result.signInAccount
                firebaseAuthWithGoogle(account)
            }
    }
    }
    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?){
        var credential = GoogleAuthProvider.getCredential(account?.idToken,null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    //Login
                    moveMainPage(task.result?.user)
                }else{
                    //Show the error message
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                }
            }
    }
    fun signinAndSignup(){
        auth?.createUserWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    //Creating a user account
                    moveMainPage(task.result?.user)
                }else if(task.exception?.message.isNullOrEmpty()) {
                    //show the error message
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                }
                else{
                    //Login if you have account
                    signinEmail()
                }
                }
    }
    fun signinEmail(){
        auth?.signInWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    //Login
                    moveMainPage(task.result?.user)
                }else{
                    //Show the error message
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                }
                }
    }
    fun moveMainPage(user:FirebaseUser?){
        if(user !=null){
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
    override fun onStart(){
        super.onStart()

        //자동 로그인 설정
        moveMainPage(auth?.currentUser)
    }
    fun moveMainpage(user:FirebaseUser?){
        if(user!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}



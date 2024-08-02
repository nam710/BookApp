package com.example.bookapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.example.bookapp.dataStore.DataStoreInstance
import com.example.bookapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    lateinit var dataStoreInstance : DataStoreInstance

    private val mobileNum = "6392949571"
    private val password = "abcd"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        val intent = Intent(this@LoginActivity, MainActivity::class.java)

        dataStoreInstance = DataStoreInstance(this)

       // TODO("Multiple instances of Data Store get created and ANR error is generated")


        CoroutineScope(IO).launch{
            if(dataStoreInstance.getStoredLoginState().first()){
                startActivity(intent)
            }
        }
        setContentView(binding.root)


        binding.btnLogIn.setOnClickListener {
            if (binding.ETmobileNumber.text.toString() == mobileNum) {
                if (binding.ETpassword.text.toString() == password) {
                    CoroutineScope(IO).launch{
                        dataStoreInstance.storeLoginState(true)
                    }
                    startActivity(intent)
                }
            } else {
                Toast.makeText(this, "Incorrect Mobile Number or Password", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }



}
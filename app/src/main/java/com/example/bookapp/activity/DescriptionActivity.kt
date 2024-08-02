package com.example.bookapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bookapp.R
import com.example.bookapp.database.BookDatabase
import com.example.bookapp.database.BookEntity
import com.example.bookapp.databinding.ActivityDescriptionBinding
import com.example.bookapp.repository.Repository
import com.example.bookapp.util.ConnectionManager
import com.example.bookapp.viewModel.DescViewModel
import com.squareup.picasso.Picasso
import org.json.JSONObject


class DescriptionActivity : AppCompatActivity() {

    lateinit var binding: ActivityDescriptionBinding
    private lateinit var sharedViewModel:DescViewModel

    private lateinit var db : BookDatabase
    private  lateinit var repository : Repository



    var bookId: String? = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.progressBar.visibility = View.VISIBLE
        binding.progressLayout.visibility = View.VISIBLE
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Book Details"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        db = BookDatabase.getInstance(applicationContext)
        repository = Repository(db)
        sharedViewModel = DescViewModel(repository)






        if (intent != null) {
            bookId = intent.getStringExtra("book_id")
        } else {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some unexpected error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (bookId == "100") {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some unexpected error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }

        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book/"

        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookId)
        if(ConnectionManager().checkConnectivity(this@DescriptionActivity)){

            val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
                Response.Listener {
                    try{
                        val success = it.getBoolean("success")
                        if(success){
                            val bookJsonObject = it.getJSONObject("book_data")
                            binding.progressLayout.visibility = View.GONE

                            val bookImageUrl = bookJsonObject.getString("image")
                            Picasso.get().load(bookJsonObject.getString("image")).error(R.drawable.default_book_cover).into(binding.ivBookImage)
                            binding.txtBookName.text = bookJsonObject.getString("name")
                            binding.txtBookAuthor.text = bookJsonObject.getString("author")
                            binding.txtBookPrice.text = bookJsonObject.getString("price")
                            binding.txtBookRating.text = bookJsonObject.getString("rating")
                            binding.txtBookDesc.text = bookJsonObject.getString("description")


                            val bookEntity = BookEntity(
                                bookId?.toInt() as Int,
                                binding.txtBookName.text.toString(),
                                binding.txtBookAuthor.text.toString(),
                                binding.txtBookPrice.text.toString(),
                                binding.txtBookRating.text.toString(),
                                binding.txtBookDesc.text.toString(),
                                bookImageUrl
                            )

                            val checkFav = doBackgroundTask(bookEntity,1,sharedViewModel)
                            if(checkFav){
                                binding.btnAddToFav.text = "Remove from Favourites"
                                val favColor = ContextCompat.getColor(applicationContext,R.color.colorAccent)
                                binding.btnAddToFav.setBackgroundColor(favColor)
                            }
                            else{
                                binding.btnAddToFav.text = "Add to Favourites"
                                val noFavColor = ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                                binding.btnAddToFav.setBackgroundColor(noFavColor)
                            }


                            binding.btnAddToFav.setOnClickListener{
                                if(!doBackgroundTask(bookEntity,1,sharedViewModel)){
                                    val inserted = doBackgroundTask(bookEntity,2,sharedViewModel)
                                    if(inserted){
                                        Toast.makeText(this@DescriptionActivity,"Book added to Favourites",Toast.LENGTH_SHORT).show()
                                        binding.btnAddToFav.text = "Remove from Favourites"
                                        val favColor = ContextCompat.getColor(applicationContext,R.color.colorAccent)
                                        binding.btnAddToFav.setBackgroundColor(favColor)
                                    }
                                    else {
                                        Toast.makeText(this@DescriptionActivity,"Some error occurred!",Toast.LENGTH_SHORT).show()
                                    }
                                }
                                else{
                                    val deleted = doBackgroundTask(bookEntity,3,sharedViewModel)
                                    if(deleted){

                                        Toast.makeText(this@DescriptionActivity,"Book removed from Favourites",Toast.LENGTH_SHORT).show()

                                       binding.btnAddToFav.text = "Add to Favourites"
                                        val noFavColor = ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                                        binding.btnAddToFav.setBackgroundColor(noFavColor)
                                    }
                                    else{
                                        Toast.makeText(this@DescriptionActivity,"Some error occurred!",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                        else{
                            Toast.makeText(this@DescriptionActivity,"Some Error Occurred 1!",Toast.LENGTH_SHORT).show()
                        }
                    }catch(e: Exception){
                        e.printStackTrace()
                        Toast.makeText(this@DescriptionActivity,"Some Error occurred 2!",Toast.LENGTH_SHORT).show()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(this@DescriptionActivity,"Volley Error occurred!",Toast.LENGTH_SHORT).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "84cc4e5a2a0a40"
                    return headers
                }


            }
            queue.add(jsonRequest)

        }
        else{

            val dialog = AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){text,listener ->
                //implicit intent to open settings
                val settingsIntent = Intent (Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") {text,listener ->
                //finishes all activities and closes the app
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()

        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //to store id of item selected
        val id = item.itemId

        if(id == android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

//
//    private val viewModel: MainViewModel by viewModels(
//        ownerProducer = { MainActivity::class.java }
//    ) {
//        ViewModelFactory(application,repository)
//    }
//
//
//    val viewModel = ViewModelProvider(this)[MainViewModel::class.java]


    private fun doBackgroundTask(bookEntity: BookEntity,mode: Int,sharedViewModel:DescViewModel):Boolean{


        // Mode 1 -> Check DB if the book is favourite or not
        // Mode 2 -> Save the book into DB as favourite
        // Mode 3 -> Remove the favourite book from DB


            when(mode){
                1 -> {

                    return sharedViewModel.checkFav(bookEntity)

                }
                2 -> {
                    sharedViewModel.insertBook(bookEntity)
                    return true
                }
                3 -> {
                    sharedViewModel.deleteBook(bookEntity)
                    return true
                }
            }
        return false

    }

//    class DBAsyncTask() : AsyncTask<Void, Void, Boolean>() {
//
//
//        override fun doInBackground(vararg params: Void?): Boolean {
//
//
//            return false
//        }
//
//    }
}
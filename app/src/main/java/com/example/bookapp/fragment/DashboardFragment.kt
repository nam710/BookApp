package com.example.bookapp.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bookapp.adapter.DashboardRecyclerAdapter
import com.example.bookapp.databinding.FragmentDashboardBinding
import com.example.bookapp.model.Book
import com.example.bookapp.util.ConnectionManager
import org.json.JSONException

class DashboardFragment : Fragment() {

    private var binding: FragmentDashboardBinding? = null
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var recyclerDashboard : RecyclerView
    lateinit var recyclerAdapter : DashboardRecyclerAdapter
    var bookInfoList = arrayListOf<Book>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater,container,false)

        recyclerDashboard = binding!!.recyclerDashboard
        layoutManager =  LinearLayoutManager(activity)

        binding?.progressLayout?.visibility =  View.VISIBLE
        binding?.progressBar?.visibility = View.VISIBLE



        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v1/book/fetch_books/"

        if(ConnectionManager().checkConnectivity(activity as Context)){


            //anonymous object used here
            //we will send the header here
            //they convey the type of content received from the API
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET,url,null,
                Response.Listener {


                    try{
                        binding?.progressLayout?.visibility  = View.GONE

                        //here we will check internet connectivity
                        //we will add a try catch block for volley or server errors
                        //we will parse the JSON response and put that JSON object in an arrayList
                        //finally we will send this arrayList to the adapter
                        val success = it.getBoolean("success")
                        if(success){
                            //extract data from json array
                            val data = it.getJSONArray("data")
                            for (i in 0 until data.length()){
                                //store each object
                                val bookJsonObject = data.getJSONObject(i)
                                //parse object and store inside a book object
                                //which will be displayed
                                val bookObject = Book(
                                    bookJsonObject.getString("book_id"),
                                    bookJsonObject.getString("name"),
                                    bookJsonObject.getString("author"),
                                    bookJsonObject.getString("rating"),
                                    bookJsonObject.getString("price"),
                                    bookJsonObject.getString("image")
                                )
                                //add object to the arrayList
                                bookInfoList.add(bookObject)
                                //passing bookInfoList to adapter

//                                TODO("When changing to Dark mode the activity is recreated, hence the context is lost" +
//                                        "because of which the app crashes")
                                // using as to type cast the activity to context is the worst practice and leads to memory leak
                                recyclerAdapter = DashboardRecyclerAdapter(bookInfoList)

                                recyclerDashboard.adapter = recyclerAdapter
                                recyclerDashboard.layoutManager = layoutManager
                                //the below code adds decorative divider lines between the rows of recycler view
                                //we don't need it because we are using card views and it won't look good to have
                                // dividers between cards

//                                recyclerDashboard.addItemDecoration(
//                                    DividerItemDecoration(
//                                        recyclerDashboard.context,
//                                        (layoutManager as LinearLayoutManager).orientation
//                                    )
//                                )
                            }
                        }
                        else{
                            Toast.makeText(activity as Context,"Some Error Occurred!", Toast.LENGTH_SHORT).show()
                        }

                    }
                    catch(e: JSONException){
                        Toast.makeText(activity as Context,"Some unexpected error occurred!",Toast.LENGTH_SHORT).show()

                    }

                },Response.ErrorListener {

                    Toast.makeText(activity as Context,"Volley error occurred",Toast.LENGTH_SHORT).show()

                }){
                //body of the anonymous object lambda
                //we send the header here
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String,String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "84cc4e5a2a0a40"
                    return headers
                }

            }

            queue.add(jsonObjectRequest)

        }
        else{
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){text,listener ->
                //implicit intent to open settings
                val settingsIntent = Intent (Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") {text,listener ->
                //finishes all activities and closes the app
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }



        return binding?.root
    }

    //view binding in fragment is done a little differently
    //we make the binding variable null safe and make it null on destroy

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}
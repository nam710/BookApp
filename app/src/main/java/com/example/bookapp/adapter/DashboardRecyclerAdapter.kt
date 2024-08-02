package com.example.bookapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.R
import com.example.bookapp.activity.DescriptionActivity
import com.example.bookapp.model.Book
import com.squareup.picasso.Picasso


//We make a primary constructor here because any data that the
//adapter binds to the view held by the view holder, it requires the
//activity or the fragment to send that data to it along with the context

// we removed the context parameter from here since we can easily use the parent.context of the view holder
class DashboardRecyclerAdapter(val itemList: ArrayList<Book>) : RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {

    //created viewHolder class to hold the views
    //we take the view in constructor to pass it to the super class
    //RecyclerView.ViewHolder to implement its functionality
    lateinit var mContext : Context
    class DashboardViewHolder(view:View):RecyclerView.ViewHolder(view){
        val txtBookName: TextView = view.findViewById(R.id.txtBookName)
        val txtBookAuthor:TextView = view.findViewById(R.id.txtBookAuthor)
        val txtBookPrice:TextView = view.findViewById(R.id.txtBookPrice)
        val txtBookRating:TextView = view.findViewById(R.id.txtBookRating)
        val imgBookImage:ImageView = view.findViewById(R.id.imgBookImage)

        val cvRow:CardView = view.findViewById(R.id.cvRow)




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dashboard_recycler_row,parent,false)
        mContext = parent.context
        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val book = itemList[position]
        holder.txtBookName.text = book.bookName
        holder.txtBookAuthor.text = book.bookAuthor
        holder.txtBookPrice.text = book.bookPrice
        holder.txtBookRating.text = book.bookRating
        Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover).into(holder.imgBookImage)



        holder.cvRow.setOnClickListener{
            val intent = Intent(mContext,DescriptionActivity::class.java)
            intent.putExtra("book_id",book.bookId)
            mContext.startActivity(intent)
        }
    }
}
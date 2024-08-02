package com.example.bookapp.repository


import com.example.bookapp.database.BookDatabase
import com.example.bookapp.database.BookEntity
import kotlinx.coroutines.flow.Flow


class Repository(private val db: BookDatabase) {



    fun checkFav(bookEntity:BookEntity):Boolean{
        val book: BookEntity? = db.bookDao().getBookById(bookEntity.book_id.toString())
        db.close()
        return book!=null
    }
    fun getAllBooks(): Flow<List<BookEntity>> {
        val res = db.bookDao().getAllBooks()
        db.close()
        return res
    }
    fun insertBook(bookEntity: BookEntity){
        db.bookDao().insertBook(bookEntity)
        db.close()
        return
    }
    fun deleteBook(bookEntity: BookEntity){
        db.bookDao().deleteBook((bookEntity))
        db.close()
        return
    }
}
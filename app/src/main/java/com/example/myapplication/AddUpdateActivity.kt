package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import kotlinx.android.synthetic.main.activity_add_update.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

class AddUpdateActivity : AppCompatActivity() {

    var noteDatabase: NoteDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_update)

        noteDatabase = NoteDatabase.getInstance(this)

        generateDate()

        aua_btn_save.onClick {
            insertNewNote()
        }
    }

    private fun generateDate() {
        val locale = Locale("in", "ID")
        val dateFormat: SimpleDateFormat? =
            SimpleDateFormat("EEE , dd MMM yyyy , hh:mm aaa", locale)
        val date = Calendar.getInstance().time
        val createDate = dateFormat?.format(date).toString()
        aua_tv_date.text = createDate
    }

    fun doEncrypt() : String{
        val RSA = RSA()
        val keyEncrpt = RSA.eValue(RSA.Qn)

        val plainteks = aua_edt_note.text.toString()

        var cipherTeks = ""
        for (i in 0 until plainteks.length){
            val character = plainteks[i]
            val cipher = RSA.encrypt(character, keyEncrpt, RSA.n)
            cipherTeks+= cipher
        }
        return cipherTeks
    }

    fun insertNewNote(){
        val cipher = doEncrypt()
        val note = NoteModel(
            title = aua_edt_title.text.toString(),
            message = cipher,
            createAt = aua_tv_date.text.toString()
        )

        GlobalScope.launch {
            noteDatabase?.noteDao()?.insertNote(note)
            runOnUiThread {
                toast("Note berhasil dibuat")
                aua_edt_note.text = null
                aua_edt_title.text = null
                generateDate()
            }
        }
    }
}

package com.araya.arto

//import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.LiveData
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.araya.arto.adapters.NoteAdapter
import com.araya.arto.datas.databases.entities.NoteEntity
import com.araya.arto.datas.databases.viewmodels.NoteViewModel
import com.araya.arto.utils.Arayas.SDF_DATE
import kotlinx.android.synthetic.main.activity_note.*
import kotlinx.android.synthetic.main.comp_header_main.*
import java.util.*

class NoteActivity : AppCompatActivity() {

    /*Databases*/
    private var viewModel: NoteViewModel? = null
    private var noteList: List<NoteEntity>? = null
    private lateinit var noteLiveData: LiveData<List<NoteEntity>>
    private lateinit var noteAdapter: NoteAdapter

    private var year: String = ""
    private var month: String = ""
    private var day: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        /*Start Set Date*/
        val date: Date = Calendar.getInstance().time
        val dateString: String = SDF_DATE.format(date)
        year = dateString.substring(0, 4)
        month = dateString.substring(5, 7)
        day = dateString.substring(8)
        /*End Set Date*/

        /*Start set rvNote*/
        rvNote.layoutManager = GridLayoutManager(this,
                1, LinearLayoutManager.VERTICAL, false)
        rvNote.addItemDecoration(DividerItemDecoration(
                rvNote.context, DividerItemDecoration.VERTICAL
        ))
        /*End set rvNote*/

        /*Set LiveData ViewModel*/
        viewModel = NoteViewModel(this)
        noteLiveData = viewModel!!.readAllNote()

        setHeaderComponent()

        loadMainDataList()
    }

    private fun setHeaderComponent() {
        btnHeaderAddNote.visibility = View.GONE
        btnHeaderMenu.visibility = View.GONE

        btnHeaderAdd.setOnClickListener {
            val intent = Intent(this, FormNoteActivity::class.java)
            startActivity(intent)
        }
    }
//
    private fun loadMainDataList() {
        noteList = noteLiveData.value
        noteLiveData.observe(this, Observer<List<NoteEntity>> {
            noteAdapter = NoteAdapter(this, it)
            rvNote.adapter = noteAdapter
        })
    }
}

package com.araya.arto

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.araya.arto.customview.EditNumber
import kotlinx.android.synthetic.main.activity_form_note.*

class FormNoteActivity : AppCompatActivity() {

    private val notes: ArrayList<LinearLayout> = ArrayList()
    private val widthTx: Int = 110

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_note)
        btnAdd.setOnClickListener {
            makeNewNote()
        }

        btnSave.setOnClickListener {
            saveNotesButtonAction()
        }
    }

    private fun saveNotesButtonAction() {
        if (notes.isNotEmpty()) {
            val noteValues: ArrayList<String> = ArrayList()
            val countValues: ArrayList<String> = ArrayList()

            for (i in 0 until notes.size) {
                val mainLayout: LinearLayout = notes[i]
                val mainLayoutCount: Int = notes[i].childCount
                for (j in 0 until mainLayoutCount) {
                    val subLayoutCount: Int = mainLayout.childCount
                    val subLayout = mainLayout.getChildAt(j)
                    for (k in subLayoutCount downTo 0) {
                        if (subLayout is LinearLayout) {
                            val indexFromK = k - 1
                            if (subLayout.getChildAt(indexFromK) is EditText) {
                                val childEditText: EditText = subLayout.getChildAt(indexFromK) as EditText
                                var value: String = childEditText.text.toString()
                                var isNumber = false
                                try {
                                    value.toInt()
                                    isNumber = true
                                } catch (e: Exception) {
                                    println("catch $e")
                                }
                                println("isNumber: $isNumber")
                                if (!isNumber) {
                                    // save as text note
                                    noteValues.add(value)
                                } else {
                                    // save as integer note
                                    if (value.isEmpty()) {
                                        value = "0"
                                    }
                                    countValues.add(value)
                                }
                                println("value($value) isNumber($isNumber) EditTextId(${childEditText.id})")
                            }
                        }
                        Log.i("CEK :", "noteTx: $noteValues countTx: $countValues")
                    }
                }
            }
            Log.i("CEK :", "completedNote notes: $noteValues")
            Log.i("CEK :", "completedNote count: $countValues")
        }
    }

    private fun makeNewNote() {
        val mainLinearLayout = LinearLayout(this)
        mainLinearLayout.orientation = LinearLayout.VERTICAL

        if (notes.isNotEmpty()) {
            val lineParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1
            )
            lineParams.topMargin = 10
            lineParams.bottomMargin = 10
            val hLine: View = View(this)
            hLine.layoutParams = lineParams
            hLine.setBackgroundColor(Color.parseColor("#000000"))
            mainLinearLayout.addView(hLine)
        }

        val mainParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        mainLinearLayout.layoutParams = mainParams
        mainLinearLayout.gravity = Gravity.CENTER

        val noteLayout = LinearLayout(this)
        noteLayout.orientation = LinearLayout.HORIZONTAL

        val tvNote = TextView(this)
        val tvNoteParams = LinearLayout.LayoutParams(
            widthTx, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        tvNoteParams.gravity = Gravity.CENTER_VERTICAL
        tvNote.layoutParams = tvNoteParams
        tvNote.text = "Notes"

        val etNote = EditText(this)
        val etParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        etParams.weight = 1F
        etNote.inputType = InputType.TYPE_CLASS_TEXT
        etNote.layoutParams = etParams

        noteLayout.addView(tvNote, 0)
        noteLayout.addView(etNote, 1)

        val countLayout = LinearLayout(this)
        countLayout.orientation = LinearLayout.HORIZONTAL

        val tvCount = TextView(this)
        val tvCountParams = LinearLayout.LayoutParams(
            widthTx, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        tvCountParams.gravity = Gravity.CENTER_VERTICAL
        tvCount.layoutParams = tvCountParams
        tvCount.text = "Count Rp. "

//        val etCount = EditText(this)
        val etCount = EditNumber(this)
        val etCountParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        etCountParams.bottomMargin = 10
        etCountParams.weight = 1F
        etCount.inputType = InputType.TYPE_CLASS_NUMBER
        etCount.setText("0")
        etCount.layoutParams = etCountParams

        countLayout.addView(tvCount, 0)
        countLayout.addView(etCount, 1)

        mainLinearLayout.addView(noteLayout)
        mainLinearLayout.addView(countLayout)

        notes.add(mainLinearLayout)
        contentNote.addView(mainLinearLayout)
    }
}

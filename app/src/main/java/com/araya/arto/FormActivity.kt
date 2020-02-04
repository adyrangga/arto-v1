package com.araya.arto

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.araya.arto.datas.databases.entities.ArtoEntity
import com.araya.arto.datas.databases.viewmodels.ArtoViewModel
import com.araya.arto.utils.Arayas
import com.araya.arto.utils.Arayas.INTENT_FORM_EDIT
import com.araya.arto.utils.Arayas.INTENT_FORM_KEY
import com.araya.arto.utils.Arayas.INTENT_KEY_ID
import com.araya.arto.utils.Arayas.SDF_DATE
import com.araya.arto.utils.Arayas.SDF_DATETIME
import com.araya.arto.utils.Arayas.dateIndo
import com.araya.arto.utils.Arayas.spinnerCategoryList
import com.araya.arto.utils.Arayas.spinnerKindList
import com.araya.arto.utils.Arayas.toRupiah
import kotlinx.android.synthetic.main.activity_form.*
import kotlinx.android.synthetic.main.comp_header_main.*
import java.util.*

class FormActivity : AppCompatActivity(), TextWatcher {

    private var stateCategory = Arayas.OUTCOME_LABEL
    private var stateForm = Arayas.INTENT_FORM_ADD
    private lateinit var currentDateTime: Date

    /*Databases*/
    private var viewModel: ArtoViewModel? = null
    private var entity: ArtoEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        currentDateTime = Calendar.getInstance().time
        viewModel = ArtoViewModel(this)

        /*Start Get Extra From Intent*/
        if (intent.extras != null) {
            val bundle = intent.extras
            stateForm = bundle?.getString(INTENT_FORM_KEY).toString()
            containerFormMain.visibility = View.VISIBLE
            if (stateForm == INTENT_FORM_EDIT) {
                val id = bundle?.getInt(INTENT_KEY_ID)
                viewModel!!.readDataById(id!!, object : ArtoViewModel.OnReadDataByIdListener {
                    override fun onRead(artoEntity: LiveData<ArtoEntity>) {
                        runOnUiThread {
                            artoEntity.observe(this@FormActivity, Observer<ArtoEntity> {
                                entity = it
                                stateCategory = it!!.colCategory
                                setEditForm()
                            })
                        }
                    }
                })
            }
        }
        /*End Get Extra From Intent*/

        /*Start Set Component Header*/
        onHeaderComponentAction()
        /*End Set Component Header*/

        loadSpinnerCategory()
        loadSpinnerKind()

        etCountForm.addTextChangedListener(this)
    }

    private fun onHeaderComponentAction() {
        btnHeaderBack.setOnClickListener {
            if (etCountForm.text!!.isNotEmpty() || etDescriptionForm.text.isNotEmpty()) {
                showAlertModal()
            } else {
                finish()
            }
        }
        tvHeaderTitle.text = dateIndo(SDF_DATE.format(currentDateTime))
        btnHeaderAdd.setImageResource(R.drawable.ic_floppy_disk)
        btnHeaderAdd.setOnClickListener { onClickSubmitButton() }
        btnHeaderMenu.visibility = View.GONE
        btnHeaderAddNote.visibility = View.GONE
    }

    private fun onClickSubmitButton() {
        println("======> ")
        val artoEntity = ArtoEntity()
        val category: String = spinnerCategoryForm.selectedItem.toString()
        val kind: String = spinnerKindForm.selectedItem.toString()
        val count: String = etCountForm.text.toString()
        val desc: String = etDescriptionForm.text.toString()
        if (count.isNotEmpty()) {
            if (stateForm == INTENT_FORM_EDIT) {
                artoEntity.id = entity!!.id
                artoEntity.colCategory = category
                artoEntity.colKind = kind
                artoEntity.colCount = count.toInt()
                artoEntity.colDescription = desc
                artoEntity.colCreatedAt = entity!!.colCreatedAt
                artoEntity.colDeletedFlag = entity!!.colDeletedFlag
                artoEntity.colDeletedAt = entity!!.colDeletedAt

                viewModel?.updateData(artoEntity, object : ArtoViewModel.OnUpdatedListener {
                    override fun onUpdated(value: Int) {
                        runOnUiThread {
                            var message = "Failed"
                            if (value > 0) message = "Success"
                            Toast.makeText(this@FormActivity, message, Toast.LENGTH_SHORT).show()
                            println("======> $artoEntity")
                            finish()
                        }
                    }

                })
            } else {
                artoEntity.colCategory = category
                artoEntity.colKind = kind
                artoEntity.colCount = count.toInt()
                artoEntity.colDescription = desc
                artoEntity.colCreatedAt = SDF_DATETIME.format(currentDateTime)
                artoEntity.colDeletedFlag = 0
                artoEntity.colDeletedAt = ""
                viewModel?.insertData(artoEntity, object : ArtoViewModel.OnUpdatedListener {
                    override fun onUpdated(value: Int) {
                        runOnUiThread {
                            var message = "Failed"
                            if (value > 0) message = "Success"
                            Toast.makeText(this@FormActivity, message, Toast.LENGTH_SHORT).show()
                            println("======> $artoEntity")
                            finish()
                        }
                    }

                })
            }
        } else {
            tilCountForm.error = "Count Form must not be empty"
        }
    }

    private fun showAlertModal() {
        AlertDialog.Builder(this)
                .setTitle("Alert Dialog")
                .setMessage("Are You Sure Not Saving Changed?")
                .setPositiveButton("Yes") { alert, _ ->
                    alert.dismiss()
                    finish()
                }
                .setNegativeButton("Cancel") { alert, _ ->
                    alert.dismiss()
                }.show()
    }

    private fun loadSpinnerCategory() {
        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(this,
                R.layout.support_simple_spinner_dropdown_item,
                spinnerCategoryList())
        spinnerCategoryForm.adapter = spinnerAdapter
        spinnerCategoryForm.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //not implement
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                stateCategory = spinnerCategoryForm.selectedItem.toString()
                loadSpinnerKind()
            }

        }
    }

    private fun loadSpinnerKind() {
        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(this,
                R.layout.support_simple_spinner_dropdown_item,
                spinnerKindList(stateCategory))
        spinnerKindForm.adapter = spinnerAdapter
    }

    private fun setEditForm() {
        val indexSpinnerCat: Int = spinnerCategoryList().indexOf(entity!!.colCategory)
        spinnerCategoryForm.setSelection(indexSpinnerCat)

        val indexSpinnerKind: Int = spinnerKindList(stateCategory).indexOf(entity!!.colKind)
        spinnerKindForm.setSelection(indexSpinnerKind)
        tilCountForm.hint = toRupiah(entity!!.colCount.toString())
        etCountForm.setText(entity!!.colCount.toString())
        etCountForm.setSelection(entity!!.colCount.toString().length)
        etDescriptionForm.setText(entity?.colDescription)
    }

    /*Implement text watcher*/
    override fun afterTextChanged(s: Editable?) {
        if (s?.isNotEmpty()!! && s[0].toString() == "0") {
            val tx: String = s.toString()
            etCountForm.setText(tx.removePrefix("0"))
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // not implement
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (etCountForm.text.toString().isNotEmpty()) {
            tilCountForm.error = null
            tilCountForm.hint = toRupiah(s.toString())
        } else {
            tilCountForm.hint = "Count"
        }
    }
}

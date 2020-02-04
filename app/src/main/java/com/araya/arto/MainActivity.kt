package com.araya.arto

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.araya.arto.adapters.MainAdapter
import com.araya.arto.datas.databases.entities.ArtoEntity
import com.araya.arto.datas.databases.viewmodels.ArtoViewModel
import com.araya.arto.utils.Arayas.INCOME_LABEL
import com.araya.arto.utils.Arayas.INTENT_FORM_ADD
import com.araya.arto.utils.Arayas.INTENT_FORM_EDIT
import com.araya.arto.utils.Arayas.INTENT_FORM_KEY
import com.araya.arto.utils.Arayas.INTENT_KEY_ID
import com.araya.arto.utils.Arayas.OUTCOME_LABEL
import com.araya.arto.utils.Arayas.SDF_DATE
import com.araya.arto.utils.Arayas.SPINNER_DAILY_OUTCOME
import com.araya.arto.utils.Arayas.dateIndo
import com.araya.arto.utils.Arayas.getDayOfWeek
import com.araya.arto.utils.Arayas.spinnerOption
import com.araya.arto.utils.Arayas.toRupiah
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.comp_header_main.*
import kotlinx.android.synthetic.main.comp_info.*
import kotlinx.android.synthetic.main.comp_recyclerview.*
import kotlinx.android.synthetic.main.modal_item_adapter.view.*
import kotlinx.android.synthetic.main.modal_menu.view.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var stateCategory = OUTCOME_LABEL
    private var stateSpinner = SPINNER_DAILY_OUTCOME

    private var year: String = ""
    private var month: String = ""
    private var day: String = ""

    /*Databases*/
    private var viewModel: ArtoViewModel? = null
    private var entityList: List<ArtoEntity>? = null
    private lateinit var entityLiveData: LiveData<List<ArtoEntity>>

    private lateinit var mainAdapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*Start Set Date*/
        val date: Date = Calendar.getInstance().time
        val dateString: String = SDF_DATE.format(date)
        year = dateString.substring(0, 4)
        month = dateString.substring(5, 7)
        day = dateString.substring(8)
        /*End Set Date*/

        /*Set LiveData ViewModel*/
        viewModel = ArtoViewModel(this)
        entityLiveData = viewModel!!.readDatasBetweenCreatedAt(stateCategory,
                "$year-$month-$day 00:00:00", "$year-$month-$day 23:59:59")

        /*Start Set Component Header*/
        btnHeaderBack.visibility = View.GONE
        onHeaderComponentAction()
        /*End Set Component Header*/

        /*Start set rvCompRecycler*/
        rvCompRecycler.layoutManager = GridLayoutManager(this,
                1, LinearLayoutManager.VERTICAL, false)
        rvCompRecycler.addItemDecoration(DividerItemDecoration(
                rvCompRecycler.context, DividerItemDecoration.VERTICAL
        ))
        /*End set rvCompRecycler*/

        onSpinnerOptionAction()

        /*Start Set Component Info*/
        onComponentInfoAction()
        /*End Set Component Info*/

        loadMainDataList()
    }

    private fun onHeaderComponentAction() {
        btnHeaderAddNote.setOnClickListener {
            val intent = Intent(this, NoteActivity::class.java)
            startActivity(intent)
        }

        btnHeaderAdd.setOnClickListener {
            val intent = Intent(this, FormActivity::class.java)
            intent.putExtra(INTENT_FORM_KEY, INTENT_FORM_ADD)
            startActivity(intent)
        }

        btnHeaderMenu.setOnClickListener {
            onClickMenuButton()
        }
    }

    @SuppressLint("InflateParams")
    private fun onClickMenuButton() {
        val categoryModalLabel: String = if (stateCategory === OUTCOME_LABEL) {
            INCOME_LABEL
        } else {
            OUTCOME_LABEL
        }

        var alertDialog: AlertDialog? = null
        val modal = LayoutInflater.from(this).inflate(R.layout.modal_menu, null)
        modal.tvModalMenuCategory.text = categoryModalLabel
        modal.tvModalMenuCategory.setOnClickListener {
            stateCategory = categoryModalLabel
            entityLiveData = viewModel!!.readDatasBetweenCreatedAt(stateCategory,
                    "$year-$month-$day 00:00:00", "$year-$month-$day 23:59:59")
            onComponentInfoAction()
            onLoadSpinnerOptionData()
            loadMainDataList()
            alertDialog?.dismiss()

        }
        modal.tvModalMenuSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
            alertDialog?.dismiss()
        }
        alertDialog?.setCancelable(true)
        alertDialog = AlertDialog.Builder(this).setView(modal).show()
    }

    private fun onComponentInfoAction() {
        val end = "$year-$month-$day 23:59:59"

        val dailyStart = "$year-$month-$day 00:00:00"
        onLoadDailyDataInfo(dailyStart, end)

        Handler().postDelayed({
            val weeklyStart = "$year-$month-${getDayOfWeek()} 00:00:00"
            onLoadWeeklyDataInfo(weeklyStart, end)
        }, 500)

        val monthlyStart = "$year-$month-01 00:00:00"
        onLoadMonthlyDataInfo(monthlyStart, end)
    }

    private fun onLoadDailyDataInfo(start: String, end: String) {
        viewModel!!.readSumCountCategoryBetweenCreatedAt(
                stateCategory, start, end, object : ArtoViewModel.OnReadSumCount {
            override fun onReadSumCount(sumCount: LiveData<Int>) {
                runOnUiThread {
                    sumCount.observe(this@MainActivity, Observer<Int> {
                        val valueDaily = it ?: 0
                        tvInfoLabelDaily.text = spinnerOption(stateCategory)[0]
                        tvInfoDaily.text = toRupiah(valueDaily.toString())
                    })
                }
            }

        })
    }

    private fun onLoadWeeklyDataInfo(start: String, end: String) {
        viewModel!!.readSumCountCategoryBetweenCreatedAt(
                stateCategory, start, end, object : ArtoViewModel.OnReadSumCount {
            override fun onReadSumCount(sumCount: LiveData<Int>) {
                runOnUiThread {
                    sumCount.observe(this@MainActivity, Observer<Int> {
                        val valueMonthly = it ?: 0
                        tvInfoLabelWeekly.text = spinnerOption(stateCategory)[1]
                        tvInfoWeekly.text = toRupiah(valueMonthly.toString())
                    })
                }
            }

        })
    }

    private fun onLoadMonthlyDataInfo(start: String, end: String) {
        viewModel!!.readSumCountCategoryBetweenCreatedAt(
                stateCategory, start, end, object : ArtoViewModel.OnReadSumCount {
            override fun onReadSumCount(sumCount: LiveData<Int>) {
                runOnUiThread {
                    sumCount.observe(this@MainActivity, Observer<Int> {
                        val valueMonthly = it ?: 0
                        tvInfoLabelMonthly.text = spinnerOption(stateCategory)[2]
                        tvInfoMonthly.text = toRupiah(valueMonthly.toString())
                    })
                }
            }

        })
    }

    private fun onSpinnerOptionAction() {
        onLoadSpinnerOptionData()
        ibChevronDown.setOnClickListener { spinnerMainOption.performClick() }
        spinnerMainOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //not implement
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val end = "$year-$month-$day 23:59:59"
                when (position) {
                    1 -> {
                        /*Weekly Data List*/
                        val weeklyStart = "$year-$month-${getDayOfWeek()} 00:00:00"
                        onLoadWeeklyDataInfo(weeklyStart, end)
                        entityLiveData = viewModel!!.readDatasBetweenCreatedAt(
                                stateCategory, weeklyStart, end)
                    }
                    2 -> {
                        /*Monthly Data List*/
                        val monthlyStart = "$year-$month-01 00:00:00"
                        onLoadWeeklyDataInfo(monthlyStart, end)
                        entityLiveData = viewModel!!.readDatasBetweenCreatedAt(
                                stateCategory, monthlyStart, end)
                    }
                    else -> {
                        /*Daily Data List*/
                        val dailyStart = "$year-$month-$day 00:00:00"
                        onLoadWeeklyDataInfo(dailyStart, end)
                        entityLiveData = viewModel!!.readDatasBetweenCreatedAt(
                                stateCategory, dailyStart, end)
                    }
                }
                onComponentInfoAction()
                loadMainDataList()
                stateSpinner = spinnerOption(stateCategory)[position]
            }

        }
    }

    private fun onLoadSpinnerOptionData() {
        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(
                this, R.layout.support_simple_spinner_dropdown_item,
                spinnerOption(stateCategory)
        )
        spinnerMainOption.adapter = spinnerAdapter
    }

    private fun loadMainDataList() {
        entityList = entityLiveData.value
        entityLiveData.observe(this, Observer<List<ArtoEntity>> {
            mainAdapter = MainAdapter(this, it)
            rvCompRecycler.adapter = mainAdapter
        })
    }

    @SuppressLint("InflateParams")
    fun onLongClickAdapterMainListener(item: ArtoEntity): Boolean {
        var alertDialog: AlertDialog? = null
        val modal = LayoutInflater.from(this)
                .inflate(R.layout.modal_item_adapter, null)
        val title = "${resources.getString(R.string.modal_item_info)} ${item.colCategory}"
        modal.tvModalTitle.text = title
        modal.tvModalDate.text = dateIndo(item.colCreatedAt.substring(0, 10))
        modal.tvModalKind.text = item.colKind
        modal.tvModalCount.text = toRupiah(item.colCount.toString())
        modal.tvModalDescription.text = item.colDescription
        modal.btnModalDelete.setOnClickListener {
            alertDialog?.dismiss()
            showAlertModal(item)
        }
        modal.btnModalEdit.setOnClickListener {
            val intent = Intent(this, FormActivity::class.java)
            intent.putExtra(INTENT_FORM_KEY, INTENT_FORM_EDIT)
            intent.putExtra(INTENT_KEY_ID, item.id)
            startActivity(intent)
            alertDialog?.dismiss()
        }
        alertDialog?.setCancelable(true)
        alertDialog = AlertDialog.Builder(this).setView(modal).show()
        return false
    }

    private fun showAlertModal(item: ArtoEntity) {
        AlertDialog.Builder(this)
                .setTitle("Alert Dialog")
                .setMessage("Deleted Selected Item?")
                .setPositiveButton("Yes") { _, _ ->
                    viewModel!!.deleteData(item, object : ArtoViewModel.OnUpdatedListener {
                        override fun onUpdated(value: Int) {
                            runOnUiThread {
                                val message = if (value > 0) "Success" else "Failed. Try Again"
                                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                            }
                        }

                    })
                }
                .setNegativeButton("No") { alert, _ ->
                    alert.dismiss()
                }.setCancelable(true).show()
    }
}

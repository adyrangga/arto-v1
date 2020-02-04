package com.araya.arto

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.araya.arto.datas.SharedPrefManager
import com.araya.arto.datas.databases.viewmodels.ArtoViewModel
import com.araya.arto.utils.Arayas
import com.araya.arto.utils.Arayas.OUTCOME_LABEL
import com.araya.arto.utils.Arayas.SDF_DATE
import com.araya.arto.utils.Arayas.getDayOfWeek
import com.araya.arto.utils.Arayas.onExportDb
import com.araya.arto.utils.Arayas.showDefaultAlertModal
import com.araya.arto.utils.Arayas.toRupiah
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.comp_header_main.*
import kotlinx.android.synthetic.main.comp_info.*
import java.util.*

class SettingActivity : AppCompatActivity(), TextWatcher {
//    private lateinit var db: AppDatabases

    private var stateCategory = OUTCOME_LABEL
    private var year: String = ""
    private var month: String = ""
    private var day: String = ""

    /*database*/
    private var viewModel: ArtoViewModel? = null
    private lateinit var prefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        /*Start Set Date*/
        val date: Date = Calendar.getInstance().time
        val dateString: String = SDF_DATE.format(date)
        year = dateString.substring(0, 4)
        month = dateString.substring(5, 7)
        day = dateString.substring(8)
        /*End Set Date*/

        viewModel = ArtoViewModel(this)
        prefManager = SharedPrefManager(this)
        etLimitForm.addTextChangedListener(this)
        /*Start Set Component Header*/
        onHeaderComponentAction()
        /*End Set Component Header*/

        val limit = if (prefManager.limitOutcome > 0) prefManager.limitOutcome else 0
        etLimitForm.setText(limit.toString())
        setNotification()
        /*Start Set Component Info*/
        onComponentInfoAction()
        /*End Set Component Info*/

        btnExportDb.setOnClickListener { onExportDb(this) }
    }

    private fun onHeaderComponentAction() {
        btnHeaderBack.setOnClickListener {
            if (etLimitForm.isEnabled && etLimitForm.text.isNotEmpty()) {
                showDefaultAlertModal(this, this as AppCompatActivity)
            } else {
                finish()
            }
        }
        btnHeaderAdd.setImageResource(R.drawable.ic_edit)
        btnHeaderAdd.tag = R.drawable.ic_edit
        btnHeaderAdd.setOnClickListener { onClickSubmitButton() }
        btnHeaderMenu.visibility = View.GONE
    }

    private fun onClickSubmitButton() {
        val stateForm: Int = btnHeaderAdd.tag as Int
        if (etLimitForm.text.toString().isNotEmpty()) {
            if (stateForm == R.drawable.ic_floppy_disk) {
                btnHeaderAdd.setImageResource(R.drawable.ic_edit)
                btnHeaderAdd.tag = R.drawable.ic_edit
                val limit: String = etLimitForm.text.toString()
                prefManager.limitOutcome = limit.toInt()
                etLimitForm.isEnabled = false
                Handler().postDelayed({ setNotification() }, 500)
            } else {
                btnHeaderAdd.setImageResource(R.drawable.ic_floppy_disk)
                btnHeaderAdd.tag = R.drawable.ic_floppy_disk
                Handler().postDelayed({
                    etLimitForm.isEnabled = true
                    etLimitForm.requestFocus()
                    val endIndex: Int = etLimitForm.text.toString().length
                    etLimitForm.setSelection(endIndex)
                }, 500)
            }
        }
    }

    private fun setNotification() {
        val start = "$year-$month-$day 00:00:00"
        val end = "$year-$month-$day 23:59:59"
        viewModel!!.readSumCountCategoryBetweenCreatedAt(OUTCOME_LABEL, start, end,
                object : ArtoViewModel.OnReadSumCount {
                    override fun onReadSumCount(sumCount: LiveData<Int>) {
                        runOnUiThread {
                            sumCount.observe(this@SettingActivity, Observer<Int> {
                                val value = it ?: 0
                                setNotificationInfo(value)
                            })
                        }
                    }

                })
    }

    private fun setNotificationInfo(value: Int) {
        if (value > 0) {
            var bgNotif = R.drawable.bg_green_rounded
            var txNotif: String = resources.getString(R.string.safe)
            var txColorDaily = ContextCompat.getColor(this, R.color.colorGreen)
            val limit: Int = prefManager.limitOutcome
            val down: Int = (limit * 0.75).toInt()
            println("==> limit: down($down) limit($limit) outcome($value)")
            if (value in (down + 1)..limit) {
                bgNotif = R.drawable.bg_yellow_rounded
                txNotif = resources.getString(R.string.warning)
                txColorDaily = ContextCompat.getColor(this, R.color.colorYellow)
            } else if (value > limit) {
                bgNotif = R.drawable.bg_red_rounded
                txNotif = resources.getString(R.string.danger)
                txColorDaily = ContextCompat.getColor(this, R.color.colorRed)
            }
            llBGNotif.setBackgroundResource(bgNotif)
            tvTitleNotif.text = txNotif
            tvCurrentLimit.text = toRupiah(limit.toString())
            tvInfoDaily.setTextColor(txColorDaily)
        }
    }


    private fun onComponentInfoAction() {
        val end = "$year-$month-$day 23:59:59"

        val dailyStart = "$year-$month-$day 00:00:00"
        onLoadDailyDataInfo(dailyStart, end)

        val weeklyStart = "$year-$month-${getDayOfWeek()} 00:00:00"
        onLoadWeeklyDataInfo(weeklyStart, end)

        val monthlyStart = "$year-$month-01 00:00:00"
        onLoadMonthlyDataInfo(monthlyStart, end)

        viewModel!!.readSumCountOutcomeAlongTime(stateCategory, object : ArtoViewModel.OnReadSumCount {
            override fun onReadSumCount(sumCount: LiveData<Int>) {
                runOnUiThread {
                    sumCount.observe(this@SettingActivity, Observer<Int> {
                        val value = it ?: 0
                        tvAlongOutcome.text = toRupiah(value.toString())
                    })
                }
            }

        })
    }

    private fun onLoadDailyDataInfo(start: String, end: String) {
        viewModel!!.readSumCountCategoryBetweenCreatedAt(
                stateCategory, start, end, object : ArtoViewModel.OnReadSumCount {
            override fun onReadSumCount(sumCount: LiveData<Int>) {
                runOnUiThread {
                    sumCount.observe(this@SettingActivity, Observer<Int> {
                        val value = it ?: 0
                        tvInfoLabelDaily.text = Arayas.spinnerOption(stateCategory)[0]
                        tvInfoDaily.text = toRupiah(value.toString())
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
                    sumCount.observe(this@SettingActivity, Observer<Int> {
                        val value = it ?: 0
                        tvInfoLabelWeekly.text = Arayas.spinnerOption(stateCategory)[1]
                        tvInfoWeekly.text = toRupiah(value.toString())
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
                    sumCount.observe(this@SettingActivity, Observer<Int> {
                        val value = it ?: 0
                        tvInfoLabelMonthly.text = Arayas.spinnerOption(stateCategory)[2]
                        tvInfoMonthly.text = toRupiah(value.toString())
                    })
                }
            }

        })
    }

//    private fun showDefaultAlertModal() {
//        AlertDialog.Builder(this)
//                .setTitle("Alert Dialog")
//                .setMessage("Are You Sure Not Saving Changed?")
//                .setPositiveButton("Yes") { alert, _ ->
//                    alert.dismiss()
//                    finish()
//                }
//                .setNegativeButton("Cancel") { alert, _ ->
//                    alert.dismiss()
//                }.show()
//    }

    override fun afterTextChanged(s: Editable?) {
        if (s?.isEmpty()!!) {
            etLimitForm.setText("0")
            etLimitForm.setSelection(1)
        } else if (etLimitForm.text.toString().length > 1 && s.isNotEmpty() && s[0].toString() == "0") {
            val tx: String = s.toString()
            etLimitForm.setText(tx.removePrefix("0"))
            val endIndex: Int = etLimitForm.text.toString().length
            etLimitForm.setSelection(endIndex)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // not implement
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (etLimitForm.text.toString().isNotEmpty()) {
            tilLimitForm.hint = toRupiah(s.toString())
        } else {
            tilLimitForm.hint = resources.getString(R.string.current_limit)
        }
    }
}

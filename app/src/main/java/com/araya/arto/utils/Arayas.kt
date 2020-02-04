package com.araya.arto.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.araya.arto.datas.databases.AppDatabases
import java.io.File
import java.io.FileWriter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by el araya on 01/12/2019
 * adyranggahidayat@gmail.com
 * Copyright (c) 2019
 **/
object Arayas {
    /*Start Simple Date Formater*/
    @SuppressLint("SimpleDateFormat")
    val SDF_DATETIME = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    @SuppressLint("SimpleDateFormat")
    val SDF_DATE = SimpleDateFormat("yyyy-MM-dd")
    @SuppressLint("SimpleDateFormat")
    val SDF_TIME = SimpleDateFormat("HH:mm:ss")
    /*End Simple Date Formater*/

    private val localeId: Locale = Locale("in", "ID")
    private val formatRp: NumberFormat = NumberFormat.getCurrencyInstance(localeId)
    private val REGEX_NUMBERS_ONLY = Regex("[^\\d]")

    /*Key and Value for Inten*/
    const val INTENT_FORM_KEY = "FORM_KEY"
    const val INTENT_KEY_ID = "ID"
    const val INTENT_FORM_ADD = "ADD"
    const val INTENT_FORM_EDIT = "EDIT"
    const val INTENT_MENU_FORM_KEY = "MENU_FORM_KEY"
    const val INTENT_MENU_FORM_ARTO = "ARTO"
    const val INTENT_MENU_FORM_NOTE = "NOTE"

    const val OUTCOME_LABEL = "Outcome"
    const val INCOME_LABEL = "Income"

    /*SpinnerOptionDataList State*/
    const val SPINNER_DAILY_OUTCOME = "Daily Outcome"
    private const val SPINNER_WEEKLY_OUTCOME = "Weekly Outcome"
    private const val SPINNER_MONTHLY_OUTCOME = "Monthly Outcome"
    private const val SPINNER_DAILY_INCOME = "Daily Income"
    private const val SPINNER_WEEKLY_INCOME = "Weekly Income"
    private const val SPINNER_MONTHLY_INCOME = "Monthly Income"

    fun spinnerOption(category: String): ArrayList<String> {
        val list: ArrayList<String> = ArrayList()
        if (category == OUTCOME_LABEL) {
            list.add(0, SPINNER_DAILY_OUTCOME)
            list.add(1, SPINNER_WEEKLY_OUTCOME)
            list.add(2, SPINNER_MONTHLY_OUTCOME)
        } else {
            list.add(0, SPINNER_DAILY_INCOME)
            list.add(1, SPINNER_WEEKLY_INCOME)
            list.add(2, SPINNER_MONTHLY_INCOME)
        }
        return list
    }

    fun spinnerCategoryList(): ArrayList<String> {
        val datas: ArrayList<String> = ArrayList()
        datas.add(0, OUTCOME_LABEL)
        datas.add(1, INCOME_LABEL)
        return datas
    }

    fun spinnerKindList(category: String): ArrayList<String> {
        val datas: ArrayList<String> = ArrayList()
        if (category == OUTCOME_LABEL) {
            datas.add(0, "Makan")
            datas.add(1, "Kost / Kontrakan")
            datas.add(2, "Pulsa / Internet")
            datas.add(3, "Listrik / Air")
            datas.add(4, "Transportasi / BBM")
            datas.add(5, "Kredit / Angsuran")
        } else {
            datas.add(0, "Gaji")
            datas.add(1, "Sampingan")
            datas.add(2, "Investasi")
            datas.add(3, "Pengembalian Pinjaman")
        }
        return datas
    }

    fun dateIndo(date: String): String {
        val monthList: ArrayList<String> = monthList()

        val getMonth = date.substring(5, 7)
        val indexMonth = Integer.parseInt(getMonth) - 1

        val day = date.substring(8)
        val month = monthList[indexMonth]
        val year = date.substring(0, 4)
        return "$day $month $year"
    }

    private fun monthList(): ArrayList<String> {
        val monthList: ArrayList<String> = ArrayList()
        // List month index 0 - 11
        monthList.add("January")
        monthList.add("February")
        monthList.add("March")
        monthList.add("April")
        monthList.add("May")
        monthList.add("June")
        monthList.add("July")
        monthList.add("August")
        monthList.add("September")
        monthList.add("October")
        monthList.add("November")
        monthList.add("December")
        return monthList
    }

    fun getDayOfWeek(): String {
        val startDayWeek = Calendar.getInstance()
        startDayWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return SDF_DATE.format(startDayWeek.time).substring(8)
    }

    fun toRupiah(value: String): String {
        var result: String = value.replace(REGEX_NUMBERS_ONLY, "")
        result = if (isNotNullOrEmpty(result)) formatRp.format(result.toDouble()) else "Rp.0"
        return result
    }

    private fun isNotNullOrEmpty(value: String?): Boolean {
        var res = false
        if (!value.isNullOrEmpty()) res = true
        return res
    }

    fun showDefaultAlertModal(context: Context, activity: AppCompatActivity) {
        AlertDialog.Builder(context)
                .setTitle("Alert Dialog")
                .setMessage("Are You Sure Not Saving Changed?")
                .setPositiveButton("Yes") { alert, _ ->
                    alert.dismiss()
                    activity.finish()
                }
                .setNegativeButton("Cancel") { alert, _ ->
                    alert.dismiss()
                }.show()
    }

    fun onExportDb(context: Context) {
        val db: AppDatabases = AppDatabases.instanceDB(context)
        val exportDir = File(Environment.getExternalStorageDirectory(), "Artto")
        if (!exportDir.exists()) exportDir.mkdirs()
        val file = File(exportDir, "export_tb_arto.csv")
        try {
            file.createNewFile()
            val csvWriter = CSVWriter(FileWriter(file))
            val curSCV = db.query("SELECT * FROM tb_arto ORDER BY id DESC", null)
            csvWriter.writeNext(curSCV.columnNames)
            while (curSCV.moveToNext()) {
                //Which column you want to exprort
                val colToExport = arrayOfNulls<String>(curSCV.columnCount)
                for (i in 0 until curSCV.columnCount) {
                    colToExport[i] = curSCV.getString(i)
                }
                csvWriter.writeNext(colToExport)
            }
            csvWriter.close()
            curSCV.close()
            Toast.makeText(context, file.absolutePath, Toast.LENGTH_LONG).show()
            println("==> exported on  $exportDir / ${file.absolutePath}")
        } catch (e: Exception) {
            println("error exportDB ${e.message} $e")
        }
    }
}
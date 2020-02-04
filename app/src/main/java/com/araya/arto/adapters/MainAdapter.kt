package com.araya.arto.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.araya.arto.MainActivity
import com.araya.arto.R
import com.araya.arto.datas.databases.entities.ArtoEntity
import com.araya.arto.utils.Arayas.toRupiah
import kotlinx.android.synthetic.main.adapter_main.view.*

/**
 * Created by el araya on 02/12/2019
 * adyranggahidayat@gmail.com
 * Copyright (c) 2019
 **/
class MainAdapter(
        private val activity: MainActivity,
        private val datas: List<ArtoEntity>?
) : RecyclerView.Adapter<MainAdapter.Holder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Holder {
        return Holder(LayoutInflater.from(p0.context)
                .inflate(R.layout.adapter_main, p0, false))
    }

    override fun getItemCount(): Int = datas?.size ?: 0

    override fun onBindViewHolder(p0: Holder, p1: Int) {
        p0.bind(datas!![p1])
    }

    inner class Holder(private val v: View) : RecyclerView.ViewHolder(v) {
        fun bind(item: ArtoEntity?) {
            v.tvTitleAdapterMain.text = item?.colKind.toString()
            v.tvCountAdapterMain.text = toRupiah(item?.colCount.toString())
            v.clAdapterMain.setOnLongClickListener {
                activity.onLongClickAdapterMainListener(item!!)
            }
        }
    }
}
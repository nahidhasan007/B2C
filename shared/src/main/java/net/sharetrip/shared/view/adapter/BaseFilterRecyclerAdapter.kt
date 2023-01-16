package net.sharetrip.shared.view.adapter

import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class BaseFilterRecyclerAdapter<T, VH : RecyclerView.ViewHolder?> : RecyclerView.Adapter<VH>, Filterable {
    private var mDataSet: MutableList<T>
    private var mFilterDataSet: MutableList<T>
    private var mFilterText: String? = null

    constructor() {
        mDataSet = ArrayList()
        mFilterDataSet = ArrayList()
    }

    constructor(mDataSet: MutableList<T>) {
        this.mDataSet = mDataSet
        mFilterDataSet = ArrayList()
    }

    override fun getItemCount(): Int {
        return mFilterDataSet.size
    }

    fun clean() {
        mDataSet.clear()
        filter.filter(mFilterText)
    }

    fun resetItems(newDataSet: List<T>) {
        mDataSet.clear()
        addItems(newDataSet)
    }

    private fun addItems(newDataSetItems: List<T>) {
        mDataSet.addAll(newDataSetItems)
        notifyDataSetChanged()
        filter.filter(mFilterText)
    }

    fun addItem(mItem: T) {
        if (!mDataSet.contains(mItem)) {
            mDataSet.add(mItem)
            notifyItemInserted(mDataSet.size - 1)
            filter.filter(mFilterText)
        }
    }

    private fun removeItem(mItem: T) {
        val indexOfItem = mDataSet.indexOf(mItem)
        if (indexOfItem != -1) {
            mDataSet.removeAt(indexOfItem)
            notifyItemRemoved(indexOfItem)
            filter.filter(mFilterText)
        }
    }

    fun removeItem(mPosition: Int) {
        val mdata: T? = mDataSet[mPosition]
        if (mdata != null) {
            mDataSet.remove(mdata)
            notifyItemRemoved(mPosition)
            filter.filter(mFilterText)
        }
    }

    fun getItem(mIndex: Int): T {
        return if (mFilterDataSet[mIndex] != null) {
            mFilterDataSet[mIndex]
        } else {
            throw IllegalArgumentException("Item with index $mIndex doesn't exist, mDataSet is $mDataSet")
        }
    }

    protected val dataSet: List<T>
        protected get() = mDataSet

    protected val filterDataSet: List<T>
        protected get() = mFilterDataSet

    protected fun setFilterDataSet(mFilterDataSet: List<T>) {
        this.mFilterDataSet.clear()
        this.mFilterDataSet.addAll(mFilterDataSet)
        notifyDataSetChanged()
    }

    fun filter(mFilterText: String) {
        this.mFilterText = mFilterText
        filter.filter(mFilterText)
    }
}
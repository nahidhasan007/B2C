package net.sharetrip.shared.view.adapter

import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class BaseRecyclerAdapter<T, VH : RecyclerView.ViewHolder?> : RecyclerView.Adapter<VH> {
    private var mDataSet: MutableList<T>

    constructor() {
        mDataSet = ArrayList()
    }

    constructor(mDataSet: MutableList<T>) {
        setHasStableIds(true)
        this.mDataSet = mDataSet
    }

    fun clean() {
        mDataSet!!.clear()
    }

    fun resetItems(newDataSet: List<T>) {
        mDataSet!!.clear()
        addItems(newDataSet)
    }

    fun addItems(newDataSetItems: List<T>) {
        mDataSet!!.addAll(newDataSetItems)
        notifyDataSetChanged()
    }

    fun addItem(mItem: T) {
        if (!mDataSet!!.contains(mItem)) {
            mDataSet!!.add(mItem)
            notifyItemInserted(mDataSet!!.size - 1)
        }
    }

    private fun removeItem(mItem: T) {
        val indexOfItem = mDataSet!!.indexOf(mItem)
        if (indexOfItem != -1) {
            mDataSet!!.removeAt(indexOfItem)
            notifyItemRemoved(indexOfItem)
        }
    }

    fun removeItem(mPosition: Int) {
        val mdata = mDataSet!![mPosition]
        if (mdata != null) {
            mDataSet!!.remove(mdata)
            notifyItemRemoved(mPosition)
        }
    }

    fun getItem(mIndex: Int): T {
        return if (mDataSet != null && mIndex in 0 until mDataSet.size && mDataSet[mIndex] != null) {
            mDataSet[mIndex]
        } else {
            throw IllegalArgumentException("Item with index $mIndex doesn't exist, mDataSet is $mDataSet")
        }
    }

    val dataSet: List<T>
        get() = mDataSet

    override fun getItemCount(): Int {
        return mDataSet!!.size
    }
}
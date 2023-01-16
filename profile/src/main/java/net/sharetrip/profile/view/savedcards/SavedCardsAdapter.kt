package net.sharetrip.profile.view.savedcards

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.ItemSaveCardsBinding
import net.sharetrip.profile.model.SavedCards

class SavedCardsAdapter(private val savedCardsViewModel: SavedCardsViewModel) :
    RecyclerView.Adapter<SavedCardsAdapter.SavedCardsViewHolder>() {
    private val savedCardList = ArrayList<SavedCards>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedCardsViewHolder {
        val quickPickView = DataBindingUtil.inflate<ItemSaveCardsBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_save_cards,
            parent,
            false
        )
        return SavedCardsViewHolder(quickPickView)
    }

    override fun onBindViewHolder(holder: SavedCardsViewHolder, position: Int) {
        holder.bindingView.savedCards = savedCardList[position]
        holder.bindingView.ivDelete.setOnClickListener {
            savedCardsViewModel.deleteSavedCards(getItem(position).uid)
        }
    }

    override fun getItemCount() = savedCardList.size

    fun updateQuickPickList(savedCardList: List<SavedCards>) {
        if (!savedCardList.isNullOrEmpty()) {
            this.savedCardList.clear()
            this.savedCardList.addAll(savedCardList)
            notifyDataSetChanged()
        }
    }

    inner class SavedCardsViewHolder(val bindingView: ItemSaveCardsBinding) :
        RecyclerView.ViewHolder(bindingView.root)

    fun getItem(position: Int): SavedCards {
        return savedCardList[position]
    }
}
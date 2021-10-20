package com.gabrielsantana.projects.controledegastos.ui.dashboard

import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gabrielsantana.projects.controledegastos.databinding.ListItemTransactionBinding
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import com.gabrielsantana.projects.controledegastos.util.setTextColorByTransactionType
import com.gabrielsantana.projects.controledegastos.util.setTransactionCategoryIcon
import com.gabrielsantana.projects.controledegastos.util.setTransactionTypeIcon
import com.gabrielsantana.projects.controledegastos.util.toCurrency

class TransactionHistoryAdapter(
    private val onItemClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionHistoryAdapter.TransactionViewHolder>(
    TransactionDiffCallback
) {

    lateinit var tracker: SelectionTracker<Long>

    class MyItemKeyProvider(
        private val adapter: TransactionHistoryAdapter
    ) : ItemKeyProvider<Long?>(SCOPE_CACHED) {
        override fun getKey(position: Int): Long? =
            adapter.currentList[position].id

        override fun getPosition(key: Long): Int =
            adapter.currentList.indexOfFirst { it.id == key }
    }

    class MyItemDetailsLookup(private val recyclerView: RecyclerView) :
        ItemDetailsLookup<Long>() {
        override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
            val view = recyclerView.findChildViewUnder(event.x, event.y)
            if (view != null) {
                return (recyclerView.getChildViewHolder(view) as TransactionViewHolder).getItemDetails()
            }
            return null
        }
    }


    class TransactionViewHolder(
        private val binding: ListItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            transaction: Transaction,
            tracker: SelectionTracker<Long>,
            onItemClick: (Transaction) -> Unit
        ) = binding.apply {
            title.text = transaction.title
            price.text = transaction.amountSpent.toCurrency()
            typeIcon.setTransactionTypeIcon(transaction)
            price.setTextColorByTransactionType(transaction)
            categoryName.setText(transaction.transactionCategory.nameStringRes)
            categoryIcon.setTransactionCategoryIcon(transaction)

            root.isChecked = tracker.isSelected(transaction.id)
            root.setOnClickListener {
                onItemClick(transaction)
            }
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = bindingAdapterPosition
                override fun getSelectionKey(): Long =
                    (bindingAdapter as TransactionHistoryAdapter).currentList[bindingAdapterPosition].id
            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TransactionViewHolder(
            ListItemTransactionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        Log.d("adapter", "onBindViewHolder: $position")
        holder.bind(getItem(position), tracker, onItemClick)
    }

    private object TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean =
            oldItem.id == newItem.id


        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean =
            newItem == oldItem


    }
}

class HeaderAdapter(
    @LayoutRes private val layoutRes: Int
) : RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder>() {

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        companion object {
            fun create(parent: ViewGroup, @LayoutRes layoutRes: Int): HeaderViewHolder {
                return HeaderViewHolder(
                    LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder =
        HeaderViewHolder.create(parent, layoutRes)

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {

    }

    override fun getItemCount(): Int = 1

}
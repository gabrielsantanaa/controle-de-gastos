package com.gabrielsantana.projects.controledegastos.ui.dashboard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
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
    private val onItemClick: (Transaction) -> Unit,
    private val onItemLongClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionHistoryAdapter.TransactionViewHolder>(
    TransactionDiffCallback
) {


    class TransactionViewHolder private constructor(
        private val binding: ListItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            transaction: Transaction,
            onItemClick: (Transaction) -> Unit,
            onItemLongClick: (Transaction) -> Unit
        ) {
            binding.apply {
                title.text = transaction.title
                price.text = transaction.amountSpent.toCurrency()
                typeIcon.setTransactionTypeIcon(transaction)
                price.setTextColorByTransactionType(transaction)
                categoryName.setText(transaction.transactionCategory.nameStringRes)
                categoryIcon.setTransactionCategoryIcon(transaction)
                root.setOnClickListener {
                    onItemClick(transaction)
                }
                root.setOnLongClickListener {
                    onItemLongClick(transaction)
                    true
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): TransactionViewHolder {
                return TransactionViewHolder(
                    ListItemTransactionBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder =
        TransactionViewHolder.from(parent)


    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        Log.d("adapter", "onBindViewHolder: $position")
        holder.bind(getItem(position), this.onItemClick, this.onItemLongClick)
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
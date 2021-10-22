package com.gabrielsantana.projects.controledegastos.ui.transationhistory

import android.os.CountDownTimer
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import com.gabrielsantana.projects.controledegastos.R
import com.gabrielsantana.projects.controledegastos.databinding.TransactionHistoryFragmentBinding

class BindingAdapter(
    private val viewModel: TransactionHistoryViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val binding: TransactionHistoryFragmentBinding
) {

    init {
        setupFieldObservers()
        setupOnClickListeners()

    }

    private fun setupOnClickListeners() {
        binding.buttonMenu.setOnClickListener {
            showMenu(it, R.menu.menu_search_bar)
        }
    }

    private fun showMenu(view: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(view.context, view)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            // Respond to menu item click.
            false
        }
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }

    private fun setupFieldObservers() {
       binding.editTextSearchBar.addTextChangedListener {
           if (it.isNullOrEmpty()) {
               viewModel.filterTransactionsByDate()
           } else {
               object: CountDownTimer(250, 250) {
                   override fun onTick(millisUntilFinished: Long) { }

                   override fun onFinish() {
                       viewModel.filterTransactionByTitle(it.toString())
                   }
               }.start()
           }
       }
    }
}
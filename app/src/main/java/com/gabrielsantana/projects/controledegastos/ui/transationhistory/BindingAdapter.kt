package com.gabrielsantana.projects.controledegastos.ui.transationhistory

import android.content.Context
import android.graphics.Color
import android.os.CountDownTimer

import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.gabrielsantana.projects.controledegastos.R
import com.gabrielsantana.projects.controledegastos.databinding.TransactionHistoryFragmentBinding
import com.gabrielsantana.projects.controledegastos.util.themeInt
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.transition.MaterialContainerTransform

class BindingAdapter(
    private val viewModel: TransactionHistoryViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val binding: TransactionHistoryFragmentBinding,
    private val context: Context
) {

    init {
        setupFieldObservers()
        setupOnClickListeners()
        setupLiveDataObservers()
    }

    private fun setupLiveDataObservers() {
        viewModel.apply {
            selectedTransactionIds.observe(lifecycleOwner) {
                binding.toolbarSelection.title = binding.root.context.getString(
                    R.string.toolbar_selection_title,
                    it.size.toString()
                )

                if (it.isNotEmpty() && searchBarIsVisible()) {
                    setSearchbarVisibility(false)

                } else if (it.isEmpty() && !searchBarIsVisible()) {
                    setSearchbarVisibility(true)
                }
            }
        }
    }

    private fun searchBarIsVisible() = binding.cardSearchBar.visibility == View.VISIBLE

    private fun setSearchbarVisibility(showCard: Boolean) {
        binding.run {
            val transform = MaterialContainerTransform().apply {
                startView = if (showCard) toolbarSelection else cardSearchBar
                endView = if (showCard) cardSearchBar else toolbarSelection

                duration = context.themeInt(R.attr.motionDurationMedium1).toLong()
                scrimColor = Color.TRANSPARENT

                if (!showCard) {
                    startShapeAppearanceModel =
                        ShapeAppearanceModel.Builder().setAllCornerSizes(64f).build()
                } else {
                    endShapeAppearanceModel =
                        ShapeAppearanceModel.Builder().setAllCornerSizes(64f).build()
                }

                //only endView should be animated
                addTarget(endView!!)

            }
            val changeBounds = ChangeBounds().apply {
                duration = context.themeInt(R.attr.motionDurationShort2).toLong()

                addTarget(binding.transactionsRecycler)
            }
            TransitionManager.beginDelayedTransition(binding.root, TransitionSet().apply {
                ordering = TransitionSet.ORDERING_TOGETHER
                addTransition(transform)
                addTransition(changeBounds)
            })
            cardSearchBar.visibility = if (showCard) View.VISIBLE else View.GONE
            toolbarSelection.visibility = if (showCard) View.GONE else View.VISIBLE
        }


    }

    private fun setupOnClickListeners() {
        binding.apply {
            buttonMenu.setOnClickListener {
                showMenu(it, R.menu.menu_search_bar)
            }
            toolbarSelection.setNavigationOnClickListener {
                viewModel.clearSelection()

            }
            toolbarSelection.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.ic_delete_transactions -> {
                        viewModel.showTransactionsDeletionConfirmation()
                        true
                    }
                    R.id.ic_select_all_transactions -> {
                        viewModel.selectAllTransactions()
                        true
                    }
                    else -> false
                }
            }

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
                object : CountDownTimer(250, 250) {
                    override fun onTick(millisUntilFinished: Long) {}

                    override fun onFinish() {
                        viewModel.filterTransactionByTitle(it.toString())
                    }
                }.start()
            }
        }
    }
}
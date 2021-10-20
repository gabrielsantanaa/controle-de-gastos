package com.gabrielsantana.projects.controledegastos.ui.dashboard

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import com.gabrielsantana.projects.controledegastos.R
import com.gabrielsantana.projects.controledegastos.databinding.DashboardFragmentBinding
import com.gabrielsantana.projects.controledegastos.util.*
import com.google.android.material.transition.MaterialContainerTransform


class BindingAdapter(
    private val viewModel: DashboardViewModel,
    private val binding: DashboardFragmentBinding,
    private val lifecycleOwner: LifecycleOwner,
    private val context: Context
) {

    init {

        setOnClickListeners()
        setLiveDataObservers()
        setupFabScrollingBehavior()
        setupUiModel()
    }


    private fun setupUiModel() {
        viewModel.uiModel.observe(lifecycleOwner) { uiModel ->
            when (uiModel) {
                is DashboardViewModel.UiModel.Dashboard -> {
                    if (uiModel.animateUiChange) setSearchBarVisibility(show = false)
                    else setSearchBarVisibilityNoAnim(show = false)

                }
                is DashboardViewModel.UiModel.Search -> {
                    if (uiModel.animateUiChange) setSearchBarVisibility(show = true)
                    else setSearchBarVisibilityNoAnim(show = true)
                }
            }
        }
    }

    private fun setOnClickListeners() = binding.apply {
        fabAdd.setOnClickListener {
            viewModel.navigateToAddTransaction()
        }
        fabSearch.setOnClickListener {
            viewModel.updateUiModel(DashboardViewModel.UiModel.Search())
        }
        buttonSelectDate.setOnClickListener {
            viewModel.showSelectDateDialog()
        }
        buttonCloseSearchBar.setOnClickListener {
            it.hideKeyboard()
            viewModel.updateUiModel(DashboardViewModel.UiModel.Dashboard())
        }
        editTextSearchBar.setOnEditorActionListener { textView, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.filterTransactionsByTitle(editTextSearchBar.text.toString())
                textView.hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }
        buttonBalanceVisibility.setOnClickListener {
            viewModel.updateAmountsVisibility()

        }
        toolbarSelection.setNavigationOnClickListener {
            viewModel.closeTransactionSelection()
        }
        toolbarSelection.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.ic_delete_transactions -> {
                    viewModel.deleteSelectedTransactions()
                    true
                }
                else -> false
            }
        }

    }


    private fun setLiveDataObservers() = viewModel.apply {
        currentBalance.observe(lifecycleOwner) {
            binding.textViewBalance.text = it.toCurrency()
        }
        currentExpenses.observe(lifecycleOwner) {
            binding.textViewExpenses.text = it.toCurrency()
        }
        currentIncomes.observe(lifecycleOwner) {
            binding.textViewIncomes.text = it.toCurrency()
        }
        selectedDate.observe(lifecycleOwner) {
            binding.buttonSelectDate.text = it.formatMonthAndYear(LocaleListCompat.getDefault()[0])
        }
        amountsVisibility.observe(lifecycleOwner) {
            binding.apply {
                if (it) {
                    showAmounts()
                    buttonBalanceVisibility.icon = ContextCompat.getDrawable(
                        buttonBalanceVisibility.context,
                        R.drawable.ic_outline_open_eye_24
                    )
                } else {
                    buttonBalanceVisibility.icon = ContextCompat.getDrawable(
                        buttonBalanceVisibility.context,
                        R.drawable.ic_round_closed_eye
                    )
                    hideAmounts()
                }

            }
        }
        fabsVisibility.observe(lifecycleOwner) {
            if (it) {
                showFabs()
            } else {
                hideFabs()
            }
        }
        selectedTransactionIds.observe(lifecycleOwner) {
            binding.toolbarSelection.title =
                context.getString(R.string.toolbar_selection_title, it.size.toString())
            setSelectionToolbarVisibility(show = it.isNotEmpty())
        }
    }

    private fun setSelectionToolbarVisibility(show: Boolean) {
        binding.toolbarSelection.visibility = if (show) View.VISIBLE else View.GONE
    }


    private fun hideFabs() {
        binding.fabSearch.hide()
        binding.fabAdd.hide()
    }

    private fun showFabs() {
        binding.fabSearch.show()
        binding.fabAdd.show()
    }


    private fun showAmounts() = binding.apply {
        textViewIncomes.text = viewModel.currentIncomes.value!!.toCurrency()
        textViewBalance.text = viewModel.currentBalance.value!!.toCurrency()
        textViewExpenses.text = viewModel.currentExpenses.value!!.toCurrency()
    }


    private fun hideAmounts() = binding.apply {
        textViewIncomes.text = "*".repeat(viewModel.currentIncomes.value!!.toString().length)
        textViewBalance.text = "*".repeat(viewModel.currentBalance.value!!.toString().length)
        textViewExpenses.text = "*".repeat(viewModel.currentExpenses.value!!.toString().length)
    }


    private fun setSearchBarVisibility(show: Boolean) = binding.apply {
        // TODO: 23/09/2021 fix scrollbar on searchMode

        clearFabScrollingBehavior()

        //fix fabSearch position at transformation to fab
        if (!show) fabAdd.visibility = View.INVISIBLE

        val duration = context.themeInt(R.attr.motionDurationLong2).toLong()

        fabSearch.visibility = if (show) View.GONE else View.VISIBLE
        cardSearchBar.visibility = if (show) View.VISIBLE else View.GONE

        val changeBounds = ChangeBounds().apply {
            this.duration = duration
        }
        val transform = MaterialContainerTransform().apply {
            this.duration = duration
            startView = if (show) fabSearch else cardSearchBar
            endView = if (show) cardSearchBar else fabSearch
            startContainerColor = context.themeColor(R.attr.colorSecondary)
            endContainerColor = context.themeColor(R.attr.colorSecondary)
            scrimColor = Color.TRANSPARENT
            isElevationShadowEnabled = true
            addTarget(if (show) fabSearch else cardSearchBar)
        }
        circularAnimation(cardPanel, show, duration) {
            TransitionManager.beginDelayedTransition(
                binding.rootLayout,
                changeBounds
            )
        }
        TransitionManager.beginDelayedTransition(binding.rootLayout, TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            addTransition(changeBounds)
            addTransition(transform)
            addTransitionEndListener {
                if (show) binding.fabAdd.hide()
                else {
                    binding.fabAdd.show()
                    setupFabScrollingBehavior()
                }
            }
        })
    }


    private fun setSearchBarVisibilityNoAnim(show: Boolean) = binding.apply {
        if (show) {
            hideFabs()
            cardSearchBar.visibility = View.VISIBLE
            cardPanel.visibility = View.GONE
        } else {
            showFabs()
            cardSearchBar.visibility = View.GONE
            cardPanel.visibility = View.VISIBLE
        }
    }

    private fun clearFabScrollingBehavior() = binding.transactionsRecycler.clearOnScrollListeners()

    private fun setupFabScrollingBehavior() =
        binding.transactionsRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) viewModel.updateFabsVisibility(false)
                else viewModel.updateFabsVisibility(true)
            }
        })

}



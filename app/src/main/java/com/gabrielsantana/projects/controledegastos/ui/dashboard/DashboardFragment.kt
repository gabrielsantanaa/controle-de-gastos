package com.gabrielsantana.projects.controledegastos.ui.dashboard

import android.animation.Animator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.ViewGroupCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import com.gabrielsantana.projects.controledegastos.R
import com.gabrielsantana.projects.controledegastos.databinding.DashboardFragmentBinding
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import com.gabrielsantana.projects.controledegastos.ui.selectdate.SelectDateDialogFragment
import com.gabrielsantana.projects.controledegastos.util.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private val dashboardViewModel: DashboardViewModel by viewModels()

    private var _binding: DashboardFragmentBinding? = null
    private val binding
        get() = _binding!!

    private val adapter by lazy {
        TransactionHistoryAdapter(
            onItemClick = { transaction ->
                dashboardViewModel.showTransactionDetails(transaction)
            },
            onLongItemClick = { transaction ->
                dashboardViewModel.showDeletionSnackbar(transaction)
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = Hold().apply {
            duration = requireContext().themeInt(R.attr.motionDurationLong2).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DashboardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        requireActivity().window.setStatusBarColorSmoothly(R.attr.colorPrimaryVariant)

        setupBinding()
        setupOnBackPressedHandler()
        setupLiveDataObservers()
        setupFabScrollingBehavior()
        setupFragmentTransition()
        setupEventsObserver()
    }

    private fun setupOnBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, true) {
            if(searchModeIsActive()) {
                dashboardViewModel.closeSearchBar()
            } else {
                requireActivity().finish()
            }
        }
    }

    private fun searchModeIsActive(): Boolean =
        binding.cardSearchBar.visibility == View.VISIBLE

    private fun setupBinding() {
        binding.apply {
            dashboardViewModel = this@DashboardFragment.dashboardViewModel
            locale = LocaleListCompat.getDefault()[0]
            lifecycleOwner = this@DashboardFragment.viewLifecycleOwner
        }
        setupRecyclerView()
    }

    private fun setupEventsObserver() {
        dashboardViewModel.eventChannel.observeOnLifecycle(viewLifecycleOwner) { event ->
            when (event) {
                is DashboardViewModel.Event.NavigateToAddTransaction -> {
                    navigateToAddTransaction()
                }
                is DashboardViewModel.Event.ShowDeletionSnackbar -> {
                    showDeletionSnackbar(event.transaction)
                }
                is DashboardViewModel.Event.ShowTransactionDetails -> {
                    showTransactionDetails(event.transaction)
                }
                is DashboardViewModel.Event.ShowSelectDateDialog -> {
                    showSelectDateDialog()
                }
                is DashboardViewModel.Event.CloseSearchBar -> {
                    setSearchBarVisibility(false)
                }
                is DashboardViewModel.Event.ShowSearchBar -> {
                    setSearchBarVisibility(true)
                }
            }
        }
    }

    private fun showSelectDateDialog() {
       SelectDateDialogFragment().show(childFragmentManager, null)
    }

    private fun showTransactionDetails(transaction: Transaction) {
        val action = DashboardFragmentDirections
            .actionDashboardFragmentToTransactionDetailsBottomSheet(transaction)
        try {
            findNavController().navigate(action)
        } catch (e: Exception) {

        }
    }

    private fun showDeletionSnackbar(transaction: Transaction) {
        val anchorView =
            if (binding.fabSearch.visibility == View.VISIBLE) binding.fabSearch
            else null
        Snackbar.make(
            binding.root,
            R.string.deletion_confirmation_snackbar_message,
            Snackbar.LENGTH_LONG
        )
            .setAnchorView(anchorView)
            .setAction(R.string.deletion_confirmation_snackbar_action) {
                dashboardViewModel.deleteTransaction(transaction)
            }.show()
    }

    private fun navigateToAddTransaction() {
        findNavController().navigate(
            R.id.action_dashboardFragment_to_addTransactionFragment,
            null,
            null,
            FragmentNavigatorExtras(
                binding.fabAdd to getString(R.string.dashboard_to_add_transaction_transition_name)
            )
        )
    }

    private fun setupFragmentTransition() {
        ViewCompat.setTransitionName(
            binding.fabAdd,
            getString(R.string.dashboard_to_add_transaction_transition_name)
        )
        ViewGroupCompat.setTransitionGroup(binding.rootLayout, true)
    }

    private fun setupFabScrollingBehavior() {
        binding.transactionsRecycler.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) dashboardViewModel.updateFabsVisibility(false)
                else dashboardViewModel.updateFabsVisibility(true)
            }
        })

    }
    private fun clearFabScrollingBehavior() {
        binding.transactionsRecycler.clearOnScrollListeners()
    }

    private fun setupLiveDataObservers() {
        dashboardViewModel.run {
            transactions.observe(viewLifecycleOwner) {
                adapter.submitList(it)
                dashboardViewModel.fetchAmounts()
            }
        }
    }

    private fun setSearchBarVisibility(show: Boolean) {
        binding.run {
            // TODO: 23/09/2021 fix scrollbar on searchMode

            clearFabScrollingBehavior()

            //fix fabSearch position at transformation to fab
            if(!show) fabAdd.visibility = View.INVISIBLE

            val durationLong = requireContext().themeInt(R.attr.motionDurationLong2).toLong()

            fabSearch.visibility = if (show) View.GONE else View.VISIBLE
            cardSearchBar.visibility = if (show) View.VISIBLE else View.GONE

            val changeBounds = ChangeBounds().apply {
                duration = durationLong
            }
            val transform = MaterialContainerTransform().apply {
                duration = durationLong
                startView = if (show) fabSearch else cardSearchBar
                endView = if (show) cardSearchBar else fabSearch
                startContainerColor = requireContext().themeColor(R.attr.colorSecondary)
                endContainerColor = requireContext().themeColor(R.attr.colorSecondary)
                scrimColor = Color.TRANSPARENT
                isElevationShadowEnabled = true
                addTarget(if (show) fabSearch else cardSearchBar)
            }
            circularAnimation(cardPanel, show, durationLong) {
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
                    if(show) binding.fabAdd.hide()
                    else {
                        binding.fabAdd.show()
                        setupFabScrollingBehavior()
                    }
                }
            })
        }
    }

    private fun setupRecyclerView() {
        binding.transactionsRecycler.apply {
            val headerAdapter = HeaderAdapter(R.layout.view_holder_title)
            adapter = ConcatAdapter(headerAdapter, this@DashboardFragment.adapter)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


}

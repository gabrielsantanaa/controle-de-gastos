package com.gabrielsantana.projects.controledegastos.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.ViewCompat
import androidx.core.view.ViewGroupCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.transition.*
import com.gabrielsantana.projects.controledegastos.R
import com.gabrielsantana.projects.controledegastos.databinding.DashboardFragmentBinding
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import com.gabrielsantana.projects.controledegastos.ui.selectdate.SelectDateDialogFragment
import com.gabrielsantana.projects.controledegastos.util.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.Hold
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels()

    private lateinit var selectionTracker: SelectionTracker<Long>

    private var _binding: DashboardFragmentBinding? = null
    private val binding
        get() = _binding!!

    private val adapter by lazy {
        TransactionHistoryAdapter(
            onItemClick = { transaction ->
                viewModel.showTransactionDetails(transaction)
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
        setupFragmentTransition()
        setupEventsObserver()
        setupRecyclerView(savedInstanceState)
    }

    private fun setupOnBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, true) {
            Log.d("dashboardFragment", "onBackPressed")
            if (searchModeIsActive()) {
                viewModel.updateUiModel(DashboardViewModel.UiModel.Dashboard())
            } else {
                requireActivity().finish()
            }
        }
    }

    private fun searchModeIsActive(): Boolean =
        viewModel.uiModel.value!! is DashboardViewModel.UiModel.Search

    private fun setupBinding() =
        BindingAdapter(viewModel, binding, viewLifecycleOwner, requireContext())


    private fun setupEventsObserver() {
        viewModel.eventChannel.observeOnLifecycle(viewLifecycleOwner) { event ->
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
                DashboardViewModel.Event.CloseTransactionSelection -> {
                    selectionTracker.clearSelection()
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
                viewModel.deleteTransaction(transaction)
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

    private fun setupLiveDataObservers() {
        viewModel.run {
            transactions.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }

        }
    }

    private fun setupRecyclerView(savedInstanceState: Bundle?) {

        binding.transactionsRecycler.apply {
            adapter = this@DashboardFragment.adapter

            selectionTracker = SelectionTracker.Builder<Long>(
                "mySelection",
                this,
                TransactionHistoryAdapter.MyItemKeyProvider(this@DashboardFragment.adapter),
                TransactionHistoryAdapter.MyItemDetailsLookup(this),
                StorageStrategy.createLongStorage()
            ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
            ).build()
            selectionTracker.onRestoreInstanceState(savedInstanceState)

            this@DashboardFragment.adapter.tracker = selectionTracker

            selectionTracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    viewModel.updateSelectedTransactionIds(selectionTracker.selection.toList())
                }
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::selectionTracker.isInitialized) {
            selectionTracker.onSaveInstanceState(outState)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchAmounts()
    }

    override fun onDestroyView() {
        _binding = null
        viewModel.disableAnimationOnUiModelChange()
        super.onDestroyView()
    }


}

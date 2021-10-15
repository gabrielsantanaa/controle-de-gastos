package com.gabrielsantana.projects.controledegastos.ui.dashboard

import android.os.Bundle
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
import androidx.recyclerview.widget.ConcatAdapter
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

    private var _binding: DashboardFragmentBinding? = null
    private val binding
        get() = _binding!!

    private val adapter by lazy {
        TransactionHistoryAdapter(
            onItemClick = { transaction ->
                viewModel.showTransactionDetails(transaction)
            },
            onItemLongClick = { transaction ->
                viewModel.showDeletionSnackbar(transaction)
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
    }

    private fun setupOnBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, true) {
            if (searchModeIsActive()) {
                viewModel.updateSearchMode(show = false, animate = true)
            } else {
                requireActivity().finish()
            }
        }
    }

    private fun searchModeIsActive(): Boolean =
        viewModel.searchMode.value!!.isActive

    private fun setupBinding() {
        setupRecyclerView()
        BindingAdapter(viewModel, binding, viewLifecycleOwner, requireContext())
    }

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

    private fun setupRecyclerView() {
        binding.transactionsRecycler.apply {
            val headerAdapter = HeaderAdapter(R.layout.view_holder_title)
            adapter = ConcatAdapter(headerAdapter, this@DashboardFragment.adapter)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchAmounts()
    }

    override fun onDestroyView() {
        _binding = null
        viewModel.updateSearchMode(
            viewModel.searchMode.value!!.isActive,
            false
        )
        super.onDestroyView()
    }


}

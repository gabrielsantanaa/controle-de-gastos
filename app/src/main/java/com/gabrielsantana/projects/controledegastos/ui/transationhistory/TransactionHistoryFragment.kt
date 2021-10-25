package com.gabrielsantana.projects.controledegastos.ui.transationhistory

import android.os.Bundle
import android.view.*
import androidx.core.view.ViewGroupCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import com.gabrielsantana.projects.controledegastos.R
import com.gabrielsantana.projects.controledegastos.databinding.TransactionHistoryFragmentBinding
import com.gabrielsantana.projects.controledegastos.util.observeOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransactionHistoryFragment : Fragment() {

    private var _binding: TransactionHistoryFragmentBinding? = null
    private val binding
        get() = _binding!!

    internal val viewModel by viewModels<TransactionHistoryViewModel>()

    private lateinit var selectionTracker: SelectionTracker<Long>

    private val adapter by lazy {
        TransactionHistoryAdapter(
            onItemClick = { transaction ->

                    val direction = TransactionHistoryFragmentDirections
                        .actionFragmentTransactionHistoryToTransactionDetailsBottomSheet(transaction)
                    findNavController().navigate(direction)

            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TransactionHistoryFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupRecyclerView(savedInstanceState)
        setupTransition()
        setupEventObserver()
        setupBindingAdapter()
        setupLiveDataObservers()
    }

    private fun setupEventObserver() {
        viewModel.eventChannel.observeOnLifecycle(viewLifecycleOwner) { event ->
            when (event) {
                TransactionHistoryViewModel.Event.ClearSelection -> {
                    selectionTracker.clearSelection()
                }
                TransactionHistoryViewModel.Event.ShowTransactionsDeletionConfirmation -> {
                    showTransactionsDeletionConfirmation()
                }
                TransactionHistoryViewModel.Event.SelectAllTransactions -> {
                    selectAllTransactions()
                }
            }
        }
    }

    private fun selectAllTransactions() {
        selectionTracker.setItemsSelected(adapter.snapshot().items.map { it.id }, true)
    }

    private fun showTransactionsDeletionConfirmation() {
        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(R.string.transactions_deletion_confirmation_dialog_title)
            .setMessage(R.string.transactions_deletion_confirmation_dialog_message)
            .setPositiveButton(R.string.transactions_deletion_confirmation_dialog_positive_button) { _, _ ->
                viewModel.deleteSelectedTransactions()
            }
            .setNegativeButton(
                R.string.transactions_deletion_confirmation_dialog_negative_button,
                null
            )
            .show()
    }

    private fun setupBindingAdapter() =
        BindingAdapter(viewModel, viewLifecycleOwner, binding, requireContext())

    private fun setupTransition() {
        ViewGroupCompat.setTransitionGroup(binding.root, true)
    }

    private fun setupLiveDataObservers() {
        viewModel.apply {
            transactions.observe(viewLifecycleOwner) {
                lifecycleScope.launch {
                    if (viewModel.isFirstInit) {
                        delay(350)
                        viewModel.isFirstInit = false
                    }
                    adapter.submitData(it)
                }

            }
        }
    }

    private fun setupRecyclerView(savedInstanceState: Bundle?) {

        lifecycleScope.launch {
            binding.transactionsRecycler.apply {
                adapter = this@TransactionHistoryFragment.adapter

                selectionTracker = SelectionTracker.Builder<Long>(
                    "mySelection",
                    this,
                    TransactionHistoryAdapter.MyItemKeyProvider(this@TransactionHistoryFragment.adapter),
                    TransactionHistoryAdapter.MyItemDetailsLookup(this),
                    StorageStrategy.createLongStorage()
                ).withSelectionPredicate(
                    SelectionPredicates.createSelectAnything()
                ).build()
                selectionTracker.onRestoreInstanceState(savedInstanceState)

                this@TransactionHistoryFragment.adapter.tracker = selectionTracker

                selectionTracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
                    override fun onSelectionChanged() {
                        viewModel.updateSelectedTransactions(selectionTracker.selection.toList())
                    }
                })
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::selectionTracker.isInitialized) {
            selectionTracker.onSaveInstanceState(outState)
        }
    }


}
package com.gabrielsantana.projects.controledegastos.ui.transationhistory

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewGroupCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import com.gabrielsantana.projects.controledegastos.databinding.TransactionHistoryFragmentBinding
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransactionHistoryFragment : Fragment() {

    private var _binding: TransactionHistoryFragmentBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: TransactionHistoryViewModel by viewModels()

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
        setupLiveDataObservers()
        setupTransition()
        setupBindingAdapter()
    }

    private fun setupBindingAdapter() =
        BindingAdapter(viewModel, viewLifecycleOwner, binding)

    private fun setupTransition() {
        ViewGroupCompat.setTransitionGroup(binding.root, true)
    }

    private fun setupLiveDataObservers() {
        viewModel.apply {
            lifecycleScope.launch {
                //this delay prevents UI lag in transition caused by recycler items rendering
                delay(300)
                transactions.observe(viewLifecycleOwner) {
                    adapter.submitData(lifecycle, it)
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
                        (requireActivity() as AppCompatActivity)
                            .supportActionBar?.title = selectionTracker.selection.size().toString()
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
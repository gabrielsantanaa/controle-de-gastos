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
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels()

    private var _binding: DashboardFragmentBinding? = null
    private val binding
        get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DashboardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupBinding()
        setupFragmentTransition()
        setupEventsObserver()
    }

    private fun setupBinding() =
        BindingAdapter(viewModel, binding, viewLifecycleOwner, requireContext())

    private fun setupEventsObserver() {
        viewModel.eventChannel.observeOnLifecycle(viewLifecycleOwner) { event ->
            when (event) {
                is DashboardViewModel.Event.ShowSelectDateDialog -> {
                    showSelectDateDialog()
                }
            }
        }
    }

    private fun showSelectDateDialog() {
        SelectDateDialogFragment().show(childFragmentManager, null)
    }

    private fun setupFragmentTransition() {
        ViewGroupCompat.setTransitionGroup(binding.root, true)
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchAmounts()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


}

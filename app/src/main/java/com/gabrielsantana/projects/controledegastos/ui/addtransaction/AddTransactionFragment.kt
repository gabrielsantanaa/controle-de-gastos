package com.gabrielsantana.projects.controledegastos.ui.addtransaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.ViewGroupCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gabrielsantana.projects.controledegastos.R
import com.gabrielsantana.projects.controledegastos.databinding.AddTransactionFragmentBinding
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionCategory
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionType
import com.gabrielsantana.projects.controledegastos.ui.components.CustomButton
import com.gabrielsantana.projects.controledegastos.ui.components.CustomChip
import com.gabrielsantana.projects.controledegastos.util.observeOnLifecycle
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddTransactionFragment : Fragment() {

    private var _binding: AddTransactionFragmentBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: AddTransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddTransactionFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //requireActivity().window.setStatusBarColorSmoothly(R.attr.colorPrimary)

        setupBinding()
        setupTransition()
        setupOnBackPressedHandler()
        setupEventsObserver()
    }


    private fun setupOnBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, true) {
            viewModel.onBackPressed()
        }
    }

    private fun setupBinding() {
        BindingAdapter(viewModel, binding)
        setupChips()
        setupToggleButtons()

    }

    private fun setupToggleButtons() {
        binding.toggleButtonTransactionType.children.forEachIndexed { index, view ->
            (view as CustomButton).setTransactionType(TransactionType.values()[index])
        }
    }

    private fun setupChips() {
        binding.chipGroupCategory.apply {
            TransactionCategory.values().forEach { category ->
                val chip = layoutInflater.inflate(
                    R.layout.chip_transaction_category,
                    this,
                    false
                ) as CustomChip
                chip.setTransactionCategory(category)
                addView(chip)
            }
        }
    }

    private fun setupEventsObserver() {
        viewModel.eventChannel.observeOnLifecycle(viewLifecycleOwner) { event ->
            when (event) {
                AddTransactionViewModel.Event.NavigateToDashboardFragment -> {
                    navigateToDashboardFragment()
                }
                AddTransactionViewModel.Event.ShowDatePicker -> {

                }
                AddTransactionViewModel.Event.InvalidFields -> {
                    showMissingFieldsWarning()
                }
                AddTransactionViewModel.Event.ShowClosingConfirmationDialog -> {
                    showClosingConfirmationDialog()
                }

            }
        }
    }

    private fun showClosingConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(R.string.closing_confirmation_dialog_title)
            .setMessage(R.string.closing_confirmation_dialog_message)
            .setPositiveButton(R.string.closing_confirmation_dialog_positive_button) { _, _ ->
                viewModel.navigateToDashboardFragment()
            }
            .setNegativeButton(R.string.closing_confirmation_dialog_negative_button, null)
            .show()
    }

    private fun showMissingFieldsWarning() {
        Snackbar.make(
            binding.root,
            R.string.incomplete_fields_snackbar_message,
            Snackbar.LENGTH_SHORT
        )
            .show()
    }

    private fun setupTransition() {
        ViewGroupCompat.setTransitionGroup(binding.root, true)
    }


    private fun navigateToDashboardFragment() =
        findNavController().popBackStack()

    private fun showDatePicker() =
        MaterialDatePicker.Builder.datePicker()
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    viewModel.saveTransaction(Date(it))
                }
            }
            .show(activity?.supportFragmentManager!!, null)


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}
package com.gabrielsantana.projects.controledegastos.ui.addtransaction

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gabrielsantana.projects.controledegastos.databinding.AddTransactionFragmentBinding
import com.google.android.material.datepicker.MaterialDatePicker
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.ViewCompat
import androidx.core.view.ViewGroupCompat
import androidx.core.view.children
import androidx.fragment.app.viewModels
import com.google.android.material.transition.MaterialContainerTransform
import com.gabrielsantana.projects.controledegastos.R
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionCategory
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionType
import com.gabrielsantana.projects.controledegastos.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.ZoneOffset.UTC
import java.util.*
import java.util.Locale

@AndroidEntryPoint
class AddTransactionFragment : Fragment() {

    private var _binding: AddTransactionFragmentBinding? = null
    private val binding
        get() = _binding!!

    private val addTransactionViewModel: AddTransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = requireContext().themeInt(R.attr.motionDurationLong2).toLong()
            endContainerColor = requireContext().themeColor(R.attr.colorSurface)
            startContainerColor = requireContext().themeColor(R.attr.colorSurface)
            isElevationShadowEnabled = false
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddTransactionFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        requireActivity().window.setStatusBarColorSmoothly(R.attr.colorPrimary)

        setupBinding()
        setupTransition()
        setupOnBackPressedHandler()
        setupEventsObserver()
    }

    private fun setupOnBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, true) {
            addTransactionViewModel.onBackPressed()
        }
    }

    private fun setupBinding() {
        binding.apply {
            viewModel = this@AddTransactionFragment.addTransactionViewModel
            lifecycleOwner = this@AddTransactionFragment.viewLifecycleOwner
        }
        setupDescriptionField()
        setupPriceFormatter()
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
                val chip = layoutInflater.inflate(R.layout.chip_transaction_category, this, false) as CustomChip
                chip.setTransactionCategory(category)
                addView(chip)
            }
        }
    }

    private fun setupPriceFormatter() {
        binding.textInputEditTextPrice.apply {
            addTextChangedListener(CurrencyTextWatcher(this) {
                addTransactionViewModel.transactionAmountSpent.value = it
            })
        }
    }

    private fun setupEventsObserver() {
        addTransactionViewModel.eventChannel.observeOnLifecycle(viewLifecycleOwner) { event ->
            when (event) {
                AddTransactionViewModel.Event.NavigateToDashboardFragment -> {
                    navigateToDashboardFragment()
                }
                AddTransactionViewModel.Event.ShowDatePicker -> {
                    showDatePicker()
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
                addTransactionViewModel.navigateToDashboardFragment()
            }
            .setNegativeButton(R.string.closing_confirmation_dialog_negative_button, null)
            .show()
    }

    private fun showMissingFieldsWarning() {
        Snackbar.make(binding.rootLayout, R.string.incomplete_fields_snackbar_message, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun setupTransition() {
        ViewCompat.setTransitionName(
            binding.root,
            getString(R.string.dashboard_to_add_transaction_transition_name)
        )
        ViewGroupCompat.setTransitionGroup(binding.rootLayout, true)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDescriptionField() {
        //fix text field scroll when it is focused
        binding.textInputEditTextDescription.apply {
            setOnTouchListener(OnTouchListener { v, event ->
                if (hasFocus()) {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_SCROLL -> {
                            v.parent.requestDisallowInterceptTouchEvent(false)
                            return@OnTouchListener true
                        }
                    }
                }
                false
            })
        }
    }

    private fun navigateToDashboardFragment() =
        findNavController().popBackStack()

    private fun showDatePicker() =
        MaterialDatePicker.Builder.datePicker()
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    addTransactionViewModel.saveTransaction(Date(it))
                }
            }
            .show(activity?.supportFragmentManager!!, null)

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}
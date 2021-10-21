package com.gabrielsantana.projects.controledegastos.ui.transactiondetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.gabrielsantana.projects.controledegastos.databinding.DialogFragmentTransactionDetailsBinding
import com.gabrielsantana.projects.controledegastos.util.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionDetailsBottomSheet : BottomSheetDialogFragment() {

    //private val args: TransactionDetailsBottomSheetArgs by navArgs()

    private var _binding: DialogFragmentTransactionDetailsBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentTransactionDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTransactionData()
    }

    private fun setupTransactionData() {
//        binding.apply {
//            categoryIcon.setTransactionCategoryIcon(args.transaction)
//            transactionTitle.text = args.transaction.title
//            transactionCategory.setText(args.transaction.transactionCategory.nameStringRes)
//            transactionDescription.text = args.transaction.description
//            transactionDate.text = args.transaction.dateCreation.formatDate()
//            transactionTypeIcon.setTransactionTypeIcon(args.transaction)
//            transactionPrice.text = args.transaction.amountSpent.toCurrency()
//            transactionPrice.setTextColorByTransactionType(args.transaction)
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


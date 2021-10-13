package com.gabrielsantana.projects.controledegastos.ui.addtransaction


import android.util.Log
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.databinding.*
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionCategory
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionType
import com.gabrielsantana.projects.controledegastos.util.fromCurrency
import com.gabrielsantana.projects.controledegastos.util.toCurrency
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

@BindingAdapter("app:onNavigationIconClick")
fun MaterialToolbar.setOnNavigationIconClick(onClick: () -> Unit) {
    setNavigationOnClickListener {
        onClick()
    }
}

///////////////////////////////

@BindingAdapter("app:transactionCategory")
fun ChipGroup.setTransactionCategory(category: TransactionCategory?) {
    category?.let {
        val chip = children.single { (it as CustomChip).getTransactionCategory() == category } as CustomChip
        chip.isCheckable = true
        chip.isChecked = true
    }

}
@InverseBindingAdapter(attribute = "app:transactionCategory")
fun ChipGroup.getTransactionCategory(): TransactionCategory {
    val chip = findViewById<CustomChip>(checkedChipId)
    return chip.getTransactionCategory()
}

@BindingAdapter("app:transactionCategoryAttrChanged")
fun ChipGroup.setListeners(
    attrChange: InverseBindingListener
) {
    setOnCheckedChangeListener { group, checkedId ->
        attrChange.onChange()
    }
}

/////////////////////////////////

@BindingAdapter("app:transactionType")
fun MaterialButtonToggleGroup.setTransactionCategory(type: TransactionType?) {
    type?.let {
        val chip = children.single { (it as CustomButton).getTransactionType() == type } as CustomButton
        chip.isCheckable = true
        chip.isChecked = true
    }
}

@InverseBindingAdapter(attribute = "app:transactionType")
fun MaterialButtonToggleGroup.getTransactionCategory(): TransactionType {
    val chip = findViewById<CustomButton>(checkedButtonId)
    return chip.getTransactionType()
}

@BindingAdapter("app:transactionTypeAttrChanged")
fun MaterialButtonToggleGroup.setListeners(
    attrChange: InverseBindingListener
) {
    addOnButtonCheckedListener { group, checkedId, isChecked ->
        attrChange.onChange()
    }
}



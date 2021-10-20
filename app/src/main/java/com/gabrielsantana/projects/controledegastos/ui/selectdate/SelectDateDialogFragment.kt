package com.gabrielsantana.projects.controledegastos.ui.selectdate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gabrielsantana.projects.controledegastos.R
import com.gabrielsantana.projects.controledegastos.databinding.DialogFragmentSelectDateBinding
import com.gabrielsantana.projects.controledegastos.databinding.ListItemMenuBinding
import com.gabrielsantana.projects.controledegastos.ui.dashboard.DashboardViewModel
import com.gabrielsantana.projects.controledegastos.util.formatYear
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*


class SelectDateDialogFragment : DialogFragment() {

    private val viewModel: DashboardViewModel by viewModels(ownerProducer = { requireParentFragment() })

    private val adapter by lazy {
        MyAdapter { position ->
            val month = DateFormatSymbols.getInstance().shortMonths[position]

            val formatter = SimpleDateFormat("MMM-yyyy", LocaleListCompat.getDefault()[0])
            val dateInString = month + "-" + binding.textViewCurrentYear.text.toString().toInt()
            viewModel.updateDate(formatter.parse(dateInString)!!)
            dismiss()
        }.apply {
            val dateFormatSymbols = DateFormatSymbols.getInstance()
            submitList(dateFormatSymbols.shortMonths.toMutableList())
        }
    }

    private var _binding: DialogFragmentSelectDateBinding? = null
    private val binding
        get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentSelectDateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUiListeners()
        setupRecyclerView()
        setupLiveDataObservers()
    }

    private fun setupRecyclerView() {
        binding.recycler.apply {
            adapter = this@SelectDateDialogFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupUiListeners() {
        binding.apply {
            buttonPreviousMonth.setOnClickListener {
                viewModel.decreaseYearFromDate()

            }
            buttonNextMonth.setOnClickListener {
                viewModel.increaseYearFromDate()
            }
            buttonClose.setOnClickListener { dismiss() }
            buttonCurrentDate.setOnClickListener {
                viewModel.updateDate(Date())
                dismiss()
            }
        }
    }

    private fun setupLiveDataObservers() {
        viewModel.selectedDate.observe(viewLifecycleOwner) {
            binding.textViewCurrentYear.text =
                it.formatYear(LocaleListCompat.getDefault()[0])
        }
    }

    override fun onStart() {
        super.onStart()
        requireDialog().window?.setBackgroundDrawableResource(R.drawable.rounded_shape_dialog_select_date)
        setupDialogWindowSize()
    }

    private fun setupDialogWindowSize() {
        val widthPixels = resources.displayMetrics.widthPixels

        val newWidth = widthPixels - (widthPixels / 5)

        requireDialog().window?.setLayout(newWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

class MyAdapter(private val onItemClick: (Int) -> Unit) :

    ListAdapter<String, MyAdapter.MyViewHolder>(MyDiffCallback) {

    class MyViewHolder private constructor(
        private val binding: ListItemMenuBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(value: String, onItemClick: (Int) -> Unit, position: Int) {
            binding.root.text = value
            binding.root.setOnClickListener {
                onItemClick(position)
            }
        }

        companion object {
            fun create(parent: ViewGroup): MyViewHolder {
                return MyViewHolder(
                    ListItemMenuBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    private object MyDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem


        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            newItem == oldItem

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick, position)
    }

}
















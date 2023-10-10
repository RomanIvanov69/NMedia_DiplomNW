package ru.netology.nmedia.ui.user

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewJobBinding
import ru.netology.nmedia.ui.DatePickerFragment
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.JobViewModel
import java.util.Calendar

class NewJobFragment : Fragment() {

    private val viewModel: JobViewModel by activityViewModels()

    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.clearEdited()
            findNavController().navigateUp()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewJobBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)


        viewModel.getEditJob()?.apply {
            binding.jobName.setText(this.name)
            binding.jobPosition.setText(this.position)
            binding.jobStart.text = AndroidUtils.formatDate(this.start)
            binding.jobFinish.text = this.finish?.let { AndroidUtils.formatDate(it) } ?: ""
        }

        binding.jobStart.setOnClickListener {
            val datePickerFragment =
                DatePickerFragment { day, month, year ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month - 1)
                    selectedDate.set(Calendar.DAY_OF_MONTH, day)
                    val date = AndroidUtils.formatDatePicker(selectedDate.time)
                    binding.jobStart.text = date
                }
            datePickerFragment.show(childFragmentManager, "datePicker")
        }

        binding.jobFinish.setOnClickListener {
            val datePickerFragment =
                DatePickerFragment { day, month, year ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month - 1)
                    selectedDate.set(Calendar.DAY_OF_MONTH, day)
                    val date = AndroidUtils.formatDatePicker(selectedDate.time)
                    binding.jobFinish.text = date
                }
            datePickerFragment.show(childFragmentManager, "datePicker")
        }

        binding.createButton.setOnClickListener {
            val name = binding.jobName.text.toString()
            val position = binding.jobPosition.text.toString()
            val start = binding.jobStart.text.toString() + "T00:00:00.000000Z"
            val finish = if (binding.jobFinish.text?.isNotBlank() == true) {
                binding.jobFinish.text.toString() + "T00:00:00.000000Z"
            } else null
            viewModel.changeContent(name, position, start, finish, null)
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
        }

        viewModel.jobCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        return binding.root
    }
}
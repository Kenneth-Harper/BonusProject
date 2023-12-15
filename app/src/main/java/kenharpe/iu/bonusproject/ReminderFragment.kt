package kenharpe.iu.bonusproject

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import kenharpe.iu.bonusproject.databinding.FragmentReminderBinding
import androidx.core.widget.addTextChangedListener
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.format.DateTimeFormatter
import java.util.Calendar

/**
 * A simple [Fragment] subclass.
 * Use the [ReminderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReminderFragment : Fragment()
{
    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GlobalViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment
        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.viewModel = viewModel

        if (!viewModel.isNewReminder)
        {
            binding.editTextTitle.setText(viewModel.reminderTitle.value)
            binding.editTextDescription.setText(viewModel.reminderDescription.value)
            binding.buttonTimePicker.text = viewModel.reminderTime.value!!.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            binding.buttonDatePicker.text = viewModel.reminderDate.value!!.format(DateTimeFormatter.ofPattern("HH:ss"))
        }



        binding.editTextTitle.addTextChangedListener { text ->
            viewModel.updateTitle(text.toString())
        }
        binding.editTextDescription.addTextChangedListener { text ->
            viewModel.updateDescription(text.toString())
        }

        binding.buttonTimePicker.setOnClickListener {
            val timePicker = TimePickerDialog(
                requireContext(),
                {_, hourOfDay, minute ->
                    viewModel.updateTime(hourOfDay, minute)
                    binding.buttonTimePicker.text = hourOfDay.toString() + ":" + minute.toString()
                },
                12,
                0,
                false
            )
            timePicker.show()
        }

        binding.buttonDatePicker.setOnClickListener{
            val c = Calendar.getInstance()

            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePicker  = DatePickerDialog(
                requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    viewModel.updateDate(year, monthOfYear, dayOfMonth)
                    binding.buttonDatePicker.text = (monthOfYear + 1).toString() + "/" + dayOfMonth.toString() + "/" + year
                },
                year,
                month,
                day
            )
            datePicker.show()
        }

        viewModel.navigateToList.observe(viewLifecycleOwner, Observer {
            viewModel.onListNavigated()
            view.findNavController().navigate(R.id.action_reminderFragment_to_reminderList)
        })

        val callback = object : OnBackPressedCallback(true)
        {
            override fun handleOnBackPressed()
            {
                Log.v("ReminderFragment", "OnBackPressed Called")
                viewModel.saveReminder()
                viewModel.goToList()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewModel = null
    }
}
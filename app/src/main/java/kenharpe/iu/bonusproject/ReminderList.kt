package kenharpe.iu.bonusproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kenharpe.iu.bonusproject.databinding.FragmentReminderListBinding


class ReminderList : Fragment()
{
    private var _binding: FragmentReminderListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GlobalViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment
        _binding = FragmentReminderListBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.buttonAddReminder.setOnClickListener {
            viewModel.newReminder()
        }

        viewModel.navigateToReminder.observe(viewLifecycleOwner, Observer { reminderId ->
            reminderId?.let {
                val action = ReminderListDirections.actionReminderListToReminderFragment()
                this.findNavController().navigate(action)
            }
        })

        if (!viewModel.isInitialized)
        {
            val application = requireNotNull(this.activity).application
            val dao = ReminderDatabase.getInstance(application).reminderDao
            viewModel.setDao(dao)
        }

        viewModel.loadReminders()
        /**
         * <h1> yesPressed </h1>
         * A helper function used in tandem with deleteClicked to establish the deletion alertdialogfragment behavior
         * @param reminderId : Long
         *      The Id of the note which should be deleted
         */
        fun yesPressed(reminderId : Long)
        {
            binding.viewModel?.deleteReminder(reminderId)
        }

        /**
         * <h1> deleteClicked </h1>
         * A helper function used in tandem with yesPressed to establish the deletion alertdialogfragment behavior
         */
        fun deleteClicked(reminderId: Long)
        {
            ConfirmDeleteDialogFragment(reminderId,::yesPressed).show(childFragmentManager, ConfirmDeleteDialogFragment.TAG)
        }

        val adapter = ReminderItemAdapter({reminderId -> viewModel.onReminderClicked(reminderId)}, ::deleteClicked)
        binding.rvReminderList.layoutManager = StaggeredGridLayoutManager(1, VERTICAL)
        binding.rvReminderList.adapter = adapter
        viewModel.reminders.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        val callback = object : OnBackPressedCallback(true)
        {
            override fun handleOnBackPressed() { }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewModel = null
    }
}
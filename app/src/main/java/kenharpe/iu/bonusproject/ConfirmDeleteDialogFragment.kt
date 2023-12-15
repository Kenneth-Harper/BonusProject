package kenharpe.iu.bonusproject

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View.OnClickListener
import androidx.fragment.app.DialogFragment

class ConfirmDeleteDialogFragment(val reminderId: Long, val clickListener: (reminderId: Long) -> Unit) : DialogFragment()
{
    val TAG = "ConfirmDeleteDialogFragment"
    interface myClickListener{
        fun yesPressed()
    }

    var listener : myClickListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage("Are you sure you want to delete this reminder?")
            .setPositiveButton("Yes!") {_,_ -> clickListener(reminderId)}
            .setNegativeButton("I'm not sure") {_,_ -> }
            .create()

    companion object {
        const val TAG = "ConfirmDeleteDialogFragment"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try
        {
            listener = context as myClickListener
        }
        catch (e: Exception)
        {
            Log.d(TAG, e.message.toString())
        }
    }
}
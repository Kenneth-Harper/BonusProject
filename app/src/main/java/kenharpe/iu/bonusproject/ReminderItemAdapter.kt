package kenharpe.iu.bonusproject

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kenharpe.iu.bonusproject.databinding.ReminderItemBinding

class ReminderItemAdapter (private val titleClickListener: (noteId: Long) -> Unit, private val deleteClickListener: (noteId: Long) -> Unit)
    : ListAdapter<Reminder, ReminderItemAdapter.ItemViewHolder>(ReminderDiffItemCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ItemViewHolder = ItemViewHolder.inflateFrom(parent)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int)
    {
        val item = getItem(position)
        holder.bind(item, titleClickListener, deleteClickListener)
    }

    class ItemViewHolder(private val binding: ReminderItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        companion object{
            fun inflateFrom(parent: ViewGroup) : ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ReminderItemBinding.inflate(layoutInflater, parent, false)
                return ItemViewHolder(binding)
            }
        }

        fun bind(item: Reminder, titleClickListener: (reminderID: Long) -> Unit, deleteClickListener: (reminderID: Long) -> Unit) {
            binding.reminder = item
            binding.textViewTitle.setOnClickListener {titleClickListener(item.reminderID)}
            binding.imageButtonDelete.setOnClickListener {deleteClickListener(item.reminderID)}
        }
    }
}

class ReminderDiffItemCallback : DiffUtil.ItemCallback<Reminder>()
{
    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder) = (oldItem.reminderID == newItem.reminderID)
    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder) = (oldItem == newItem)
}
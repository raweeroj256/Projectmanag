package je.raweeroj.projectmanag.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import je.raweeroj.projectmanag.activities.TaskListActivity
import je.raweeroj.projectmanag.databinding.ItemTaskBinding
import je.raweeroj.projectmanag.models.Task
import java.util.Collections

open class TaskListItemsAdapter(private val context: Context,
                           private var list: ArrayList<Task>):
    RecyclerView.Adapter<TaskListItemsAdapter.MyViewHolder>() {

    private var mPositionDraggedFrom = -1
    private var mPositionDraggedTo = -1

    inner class MyViewHolder(binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root){
        val myViewBinding = binding

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val viewBinding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),parent,false)
        val layoutParams = LinearLayout.LayoutParams((parent.width * 0.7).toInt(),LinearLayout.LayoutParams.WRAP_CONTENT)

        layoutParams.setMargins((15.toDP()).toPX(),0,(40.toDP()).toPX(),0)
        viewBinding.root.layoutParams = layoutParams

        return MyViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            if(position == list.size -1){
                holder.myViewBinding.tvAddTaskList.visibility = View.VISIBLE
                holder.myViewBinding.llTaskItem.visibility = View.GONE
            }else{
                holder.myViewBinding.tvAddTaskList.visibility = View.GONE
                holder.myViewBinding.llTaskItem.visibility = View.VISIBLE
            }

            holder.myViewBinding.tvTaskListTitle.text = model.title
            holder.myViewBinding.tvAddTaskList.setOnClickListener {
                holder.myViewBinding.tvAddTaskList.visibility = View.GONE
                holder.myViewBinding.cvAddTaskListName.visibility = View.VISIBLE
            }

            holder.myViewBinding.ibCloseListName.setOnClickListener {
                holder.myViewBinding.tvAddTaskList.visibility = View.VISIBLE
                holder.myViewBinding.cvAddTaskListName.visibility = View.GONE
            }

            holder.myViewBinding.ibDoneListName.setOnClickListener {
               val listName = holder.myViewBinding.etTaskListName.text.toString()

                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.createTaskList(listName)
                    }
                }else{
                    Toast.makeText(context,"Please Enter List Name",Toast.LENGTH_SHORT).show()
                }
            }

            holder.myViewBinding.ibEditListName.setOnClickListener {
                holder.myViewBinding.etEditTaskListName.setText(model.title)
                holder.myViewBinding.llTitleView.visibility = View.GONE
                holder.myViewBinding.cvEditTaskListName.visibility = View.VISIBLE

            }

            holder.myViewBinding.ibCloseEditableView.setOnClickListener {
                holder.myViewBinding.llTitleView.visibility = View.VISIBLE
                holder.myViewBinding.cvEditTaskListName.visibility = View.GONE
            }

            holder.myViewBinding.ibDoneEditListName.setOnClickListener {
                val listName = holder.myViewBinding.etEditTaskListName.text.toString()

                if(list.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.updateTaskList(position,listName,model)
                    }
                }else{
                    Toast.makeText(context,"Please enter a list name.",Toast.LENGTH_SHORT).show()
                }
            }

            holder.myViewBinding.ibDeleteList.setOnClickListener {
                alertDialogForDeleteList(position,model.title)
            }

            holder.myViewBinding.tvAddCard.setOnClickListener {
                holder.myViewBinding.tvAddCard.visibility = View.GONE
                holder.myViewBinding.cvAddCard.visibility = View.VISIBLE
            }

            holder.myViewBinding.ibCloseCardName.setOnClickListener {
                holder.myViewBinding.tvAddCard.visibility = View.VISIBLE
                holder.myViewBinding.cvAddCard.visibility = View.GONE
            }

            holder.myViewBinding.ibDoneCardName.setOnClickListener {
                val cardName = holder.myViewBinding.etCardName.text.toString()

                if(cardName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.addCardToTaskList(position,cardName)
                    }
                }else{
                    Toast.makeText(context,"Please Enter Card Name",Toast.LENGTH_SHORT).show()
                }
            }

            holder.myViewBinding.rvCardList.layoutManager =
                LinearLayoutManager(context)
            holder.myViewBinding.rvCardList.setHasFixedSize(true)

            val adapter = CardListItemsAdapter(context,model.cards)
            holder.myViewBinding.rvCardList.adapter = adapter

            adapter.setOnClickListener(object :
                CardListItemsAdapter.OnClickListener {
                override fun onClick(cardPosition: Int) {

                    if (context is TaskListActivity) {
                        context.cardDetails(holder.adapterPosition, cardPosition)
                    }
                }
            })

            val dividerItemDecoration =
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            holder.myViewBinding.rvCardList.addItemDecoration(dividerItemDecoration)

            val helper = ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

                /*Called when ItemTouchHelper wants to move the dragged item from its old position to
                 the new position.*/
                override fun onMove(
                    recyclerView: RecyclerView,
                    dragged: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val draggedPosition = dragged.adapterPosition
                    val targetPosition = target.adapterPosition

                    // TODO (Step 4: Assign the global variable with updated values.)
                    // START
                    if (mPositionDraggedFrom == -1) {
                        mPositionDraggedFrom = draggedPosition
                    }
                    mPositionDraggedTo = targetPosition
                    // END

                    /**
                     * Swaps the elements at the specified positions in the specified list.
                     */
                    Collections.swap(list[holder.adapterPosition].cards, draggedPosition, targetPosition)

                    // move item in `draggedPosition` to `targetPosition` in adapter.
                    adapter.notifyItemMoved(draggedPosition, targetPosition)

                    return false // true if moved, false otherwise
                }

                // Called when a ViewHolder is swiped by the user.
                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) { // remove from adapter
                }

                // TODO (Step 5: Finally when the dragging is completed than call the function to update the cards in the database and reset the global variables.)
                // START
                /*Called by the ItemTouchHelper when the user interaction with an element is over and it
                 also completed its animation.*/
                override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                    super.clearView(recyclerView, viewHolder)

                    if (mPositionDraggedFrom != -1 && mPositionDraggedTo != -1 && mPositionDraggedFrom != mPositionDraggedTo) {

                        (context as TaskListActivity).updateCardsInTaskList(
                            holder.adapterPosition,
                            list[holder.adapterPosition].cards
                        )
                    }

                    // Reset the global variables
                    mPositionDraggedFrom = -1
                    mPositionDraggedTo = -1
                }
                // END
            })
            helper.attachToRecyclerView(holder.myViewBinding.rvCardList)
        }
    }

    private fun alertDialogForDeleteList(position: Int,title:String){
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Alert")

        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes"){dialogInterface,which ->
            dialogInterface.dismiss()

            if (context is TaskListActivity){
                context.deleteTaskList(position)
            }
        }

        builder.setNegativeButton("No"){dialogInterface,which ->
            dialogInterface.dismiss()
        }
        val alertDialog:AlertDialog = builder.create()

        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun Int.toDP() : Int =
        (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPX() : Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()
}
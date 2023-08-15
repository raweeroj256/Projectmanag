package je.raweeroj.projectmanag.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import je.raweeroj.projectmanag.activities.TaskListActivity
import je.raweeroj.projectmanag.databinding.ItemBoardBinding
import je.raweeroj.projectmanag.databinding.ItemCardBinding
import je.raweeroj.projectmanag.databinding.ItemTaskBinding
import je.raweeroj.projectmanag.models.Card
import je.raweeroj.projectmanag.models.SelectedMembers

class CardListItemsAdapter(private val context: Context,
                           private var list: ArrayList<Card>):
    RecyclerView.Adapter<CardListItemsAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null


    inner class MyViewHolder(binding: ItemCardBinding): RecyclerView.ViewHolder(binding.root){
        val myViewBinding = binding

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemCardBinding.inflate(
            LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

            holder.myViewBinding.tvCardName.text = model.name
        if(model.labelColor.isNotEmpty()){
            holder.myViewBinding.viewLabelColor.visibility = View.VISIBLE
            holder.myViewBinding.viewLabelColor.setBackgroundColor(Color.parseColor(model.labelColor))
        }else{
            holder.myViewBinding.viewLabelColor.visibility = View.GONE
        }
        if((context as TaskListActivity).mAssignedMemberDetailList.size >0){
            val selectedMembersList:ArrayList<SelectedMembers> = ArrayList()

            for(i in context.mAssignedMemberDetailList.indices){
                for(j in model.assignedTo){
                    if(context.mAssignedMemberDetailList[i].id == j){
                        val selectedMembers = SelectedMembers(
                            context.mAssignedMemberDetailList[i].id,
                            context.mAssignedMemberDetailList[i].image
                        )
                        selectedMembersList.add(selectedMembers)
                    }
                }
            }
            if(selectedMembersList.size>0){
                if(selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy){
                    holder.myViewBinding.rvCardSelectedMembersList.visibility = View.GONE
                }else{
                    holder.myViewBinding.rvCardSelectedMembersList.visibility = View.VISIBLE

                    holder.myViewBinding.rvCardSelectedMembersList.layoutManager =
                        GridLayoutManager(context,4)

                    val adapter = CardMemberListItemsAdapter(context,selectedMembersList,false)
                    holder.myViewBinding.rvCardSelectedMembersList.adapter = adapter
                    adapter.setOnClickListener(object :CardMemberListItemsAdapter.OnClickListener{
                        override fun onClick() {
                            if(onClickListener!=null){
                                onClickListener!!.onClick(holder.adapterPosition)
                            }
                        }

                    })
                }
            }else{
                holder.myViewBinding.rvCardSelectedMembersList.visibility = View.GONE
            }
        }




        holder.itemView.setOnClickListener{
            if (onClickListener != null) {
                onClickListener!!.onClick(position)
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(cardPosition: Int)
    }

}
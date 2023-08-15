package je.raweeroj.projectmanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import je.raweeroj.projectmanag.R
import je.raweeroj.projectmanag.databinding.ItemCardSelectedMemberBinding
import je.raweeroj.projectmanag.models.SelectedMembers

open class CardMemberListItemsAdapter (private val context: Context,
                                       private var list: ArrayList<SelectedMembers>,
                                        private val assignMembers:Boolean):
    RecyclerView.Adapter<CardMemberListItemsAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null


    inner class MyViewHolder(binding: ItemCardSelectedMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        val myViewBinding = binding

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemCardSelectedMemberBinding.inflate(
                LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        if(position == list.size-1 && assignMembers){
            holder.myViewBinding.ivAddMember.visibility = View.VISIBLE
            holder.myViewBinding.ivSelectedMemberImage.visibility = View.GONE
        }else{
            holder.myViewBinding.ivAddMember.visibility = View.GONE
            holder.myViewBinding.ivSelectedMemberImage.visibility = View.VISIBLE

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.myViewBinding.ivSelectedMemberImage)

        }

        holder.itemView.setOnClickListener {
            if(onClickListener!=null){
                onClickListener!!.onClick()
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
        fun onClick()
    }


}
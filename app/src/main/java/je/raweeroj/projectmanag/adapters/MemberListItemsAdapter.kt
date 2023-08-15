package je.raweeroj.projectmanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import je.raweeroj.projectmanag.R
import je.raweeroj.projectmanag.databinding.ItemCardBinding
import je.raweeroj.projectmanag.databinding.ItemMemberBinding
import je.raweeroj.projectmanag.databinding.ItemTaskBinding
import je.raweeroj.projectmanag.models.Task
import je.raweeroj.projectmanag.models.User
import je.raweeroj.projectmanag.utils.Constants

class MemberListItemsAdapter(private val context: Context,
                             private var list: ArrayList<User>):
    RecyclerView.Adapter<MemberListItemsAdapter.MyViewHolder>() {

    inner class MyViewHolder(binding: ItemMemberBinding): RecyclerView.ViewHolder(binding.root){
        val myViewBinding = binding

    }

    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemMemberBinding.inflate(
                LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        Glide
            .with(context)
            .load(model.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(holder.myViewBinding.ivMemberImage)

        holder.myViewBinding.tvMemberName.text = model.name
        holder.myViewBinding.tvMemberEmail.text = model.email

        if(model.selected){
        holder.myViewBinding.ivSelectedMember.visibility= View.VISIBLE
        }else{
            holder.myViewBinding.ivSelectedMember.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if(onItemClickListener != null){
                if(model.selected){
                    onItemClickListener!!.onClick(position,model,Constants.UN_SELECT)
                }else{
                    onItemClickListener!!.onClick(position,model,Constants.SELECT)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnItemClickListener) {
        this.onItemClickListener = onClickListener
    }

    interface OnItemClickListener {
        fun onClick(position: Int,user:User,action:String)
    }
}
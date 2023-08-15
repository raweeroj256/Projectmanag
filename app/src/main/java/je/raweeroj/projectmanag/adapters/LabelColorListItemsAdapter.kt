package je.raweeroj.projectmanag.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import je.raweeroj.projectmanag.databinding.ItemLabelColorBinding


open class LabelColorListItemsAdapter (private val context: Context,
                                  private var list: ArrayList<String>,
                                    private val mSelectedColor : String):
    RecyclerView.Adapter<LabelColorListItemsAdapter.MyViewHolder>() {

     var onItemClickListener: OnItemClickListener? = null


    inner class MyViewHolder(binding: ItemLabelColorBinding) : RecyclerView.ViewHolder(binding.root) {
        val myViewBinding = binding

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemLabelColorBinding.inflate(
            LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]

        holder.myViewBinding.viewMain.setBackgroundColor(Color.parseColor(item))

        if(item == mSelectedColor){
            holder.myViewBinding.ivSelectedColor.visibility = View.VISIBLE
        }else{
            holder.myViewBinding.ivSelectedColor.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if(onItemClickListener != null){
                onItemClickListener!!.onClick(position,item)
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
        fun onClick(cardPosition: Int,color:String)
    }
}
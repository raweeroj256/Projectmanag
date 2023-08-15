package je.raweeroj.projectmanag.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.getSystem
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.api.ResourceProto.resource
import io.grpc.internal.SharedResourceHolder.Resource
import je.raweeroj.projectmanag.R
import je.raweeroj.projectmanag.activities.MainActivity
import je.raweeroj.projectmanag.activities.TaskListActivity
import je.raweeroj.projectmanag.databinding.ItemBoardBinding
import je.raweeroj.projectmanag.firebase.FirestoreClass
import je.raweeroj.projectmanag.models.Board
import pl.kitek.rvswipetodelete.SwipeToDeleteCallback

open class BoardItemsAdapter(private val context:Context,
private var list: ArrayList<Board>):
RecyclerView.Adapter<BoardItemsAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class MyViewHolder(binding:ItemBoardBinding):RecyclerView.ViewHolder(binding.root){
        val myViewBinding = binding
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemBoardBinding.inflate(
            LayoutInflater.from(parent.context),parent,false))
    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.myViewBinding.ivBoardImage)

            holder.myViewBinding.tvName.text = model.name
            holder.myViewBinding.tvCreatedBy.text = "Created By : ${model.createdBy}"

            holder.itemView.setOnClickListener{
                if(onClickListener != null){
                    onClickListener!!.onClick(position,model)
                }
            }
        holder.myViewBinding.ivDeleteBoard.visibility = View.VISIBLE
         holder.myViewBinding.ivDeleteBoard.setOnClickListener {
             alertDialogForDeleteBoard(model.name,model.documentId)
           // removeAt(model.documentId)
         }



    }

    interface OnClickListener{
        fun onClick(position: Int,model:Board)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }


    override fun getItemCount(): Int {
        return list.size
    }


    fun removeAt(documentId: String) {
        if(context is MainActivity){
            context.showProgressDialog("Please Wait")
        }
        FirestoreClass().deleteBoard(context,documentId)

    }

    private fun alertDialogForDeleteBoard(title:String,documentId : String){
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Alert")

        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes"){dialogInterface,which ->
            dialogInterface.dismiss()

            if(context is MainActivity){
                context.showProgressDialog("Please Wait")
                FirestoreClass().deleteBoard(context,documentId)
            }

        }

        builder.setNegativeButton("No"){dialogInterface,which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()

        alertDialog.setCancelable(false)
        alertDialog.show()
    }




}
package eu.tutorials.projectmanager_v2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eu.tutorials.projectmanager_v2.R
import eu.tutorials.projectmanager_v2.models.UserModel
import eu.tutorials.projectmanager_v2.utils.Constants
import kotlinx.android.synthetic.main.item_member.view.*

open class MemberListItemAdapter(
        private val context:Context,
        private var list:ArrayList<UserModel>
        ):RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener:OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(
            R.layout.item_member,
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is MyViewHolder){
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.iv_member_image)

            holder.itemView.tv_member_name.text=model.name
            holder.itemView.tv_member_email.text=model.email

            if (model.selected){
                holder.itemView.iv_selected_member.visibility=View.VISIBLE
            }
            else{
                holder.itemView.iv_selected_member.visibility=View.GONE
            }

            holder.itemView.setOnClickListener {
                if (onClickListener != null){
                    if(model.selected){
                        onClickListener!!.onClick(position,model,Constants.UN_SELECT)
                    }
                    else{
                        onClickListener!!.onClick(position,model,Constants.SELECT)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener{
        fun onClick(position: Int,user:UserModel,action:String)
    }

    fun setOnClickListener(onClickListener:OnClickListener){
        this.onClickListener=onClickListener
    }

    class MyViewHolder(view:View):RecyclerView.ViewHolder(view)

}
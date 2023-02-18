package eu.tutorials.projectmanager_v2.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import eu.tutorials.projectmanager_v2.R
import eu.tutorials.projectmanager_v2.adapters.LabelColorListItemsAdapter
import eu.tutorials.projectmanager_v2.adapters.MemberListItemAdapter
import eu.tutorials.projectmanager_v2.models.UserModel
import kotlinx.android.synthetic.main.dialog_list.view.*

abstract class MembersListDialog (
    context:Context,
    private var list:ArrayList<UserModel>,
    private val title:String=""
):Dialog(context){
    private var adapter:MemberListItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list,null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setupRecyclerView(view)
    }

    private fun setupRecyclerView(view: View){
        view.tvTitle.text=title

        if(list.size >0){

            view.rvList.layoutManager= LinearLayoutManager(context)
            adapter= MemberListItemAdapter(context,list)
            view.rvList.adapter=adapter

            adapter!!.setOnClickListener(object : MemberListItemAdapter.OnClickListener{
                override fun onClick(position: Int, user: UserModel, action: String) {
                    dismiss()
                    onItemSelected(user,action)
                }
            })
        }
    }

    protected abstract fun onItemSelected(user:UserModel,action:String)
}
package eu.tutorials.projectmanager_v2.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import eu.tutorials.projectmanager_v2.R
import eu.tutorials.projectmanager_v2.firebase.FirestoreClass
import eu.tutorials.projectmanager_v2.models.Board
import eu.tutorials.projectmanager_v2.utils.Constants
import kotlinx.android.synthetic.main.activity_create_board.*
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        var boardDocumentID=""
        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentID=intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this,boardDocumentID)

    }

    fun boardDetails(board: Board){
        hideProgressDialog()
        setupActionBar(board.name)
    }

    private fun setupActionBar(title:String) {

        setSupportActionBar(toolbar_task_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title=title
        }

        toolbar_task_list_activity.setNavigationOnClickListener { onBackPressed() }
    }
}
package eu.tutorials.projectmanager_v2.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import eu.tutorials.projectmanager_v2.R
import eu.tutorials.projectmanager_v2.models.Board
import eu.tutorials.projectmanager_v2.utils.Constants
import kotlinx.android.synthetic.main.activity_card_details.*
import kotlinx.android.synthetic.main.activity_members.*

class CardDetailsActivity : AppCompatActivity() {

    private lateinit var mBoardDetails:Board
    private var mTaskListPosition=-1
    private var mCardListPosition=-1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        setupActionBar()

        getIntentData()
    }

    private fun getIntentData(){
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails=intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition=intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION,-1)
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mCardListPosition=intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION,-1)
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_card_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title=mBoardDetails
                .taskList[mTaskListPosition]
                .cards[mCardListPosition]
                .name
        }

        toolbar_card_details_activity.setNavigationOnClickListener { onBackPressed() }
    }
}
package eu.tutorials.projectmanager_v2.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import eu.tutorials.projectmanager_v2.R
import eu.tutorials.projectmanager_v2.adapters.CardMemberListItemsAdapter
import eu.tutorials.projectmanager_v2.dialogs.LabelColorListDialog
import eu.tutorials.projectmanager_v2.dialogs.MembersListDialog
import eu.tutorials.projectmanager_v2.firebase.FirestoreClass
import eu.tutorials.projectmanager_v2.models.*
import eu.tutorials.projectmanager_v2.utils.Constants
import kotlinx.android.synthetic.main.activity_card_details.*
import kotlinx.android.synthetic.main.activity_members.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailsActivity : BaseActivity() {

    private lateinit var mBoardDetails:Board
    private var mTaskListPosition=-1
    private var mCardPosition=-1
    private var mSelectedColor=""
    private lateinit var mMembersDetailList:ArrayList<UserModel>
    private var mSelectedDueDateMillis:Long=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        getIntentData()

        setupActionBar()

        et_name_card_details.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
        et_name_card_details.setSelection(et_name_card_details.text.toString().length)

        mSelectedColor=mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        if(mSelectedColor.isNotEmpty()){
            setColor()
        }

        btn_update_card_details.setOnClickListener {
            if (et_name_card_details.text.toString().isNotEmpty()){
                updateCardDetails()
            }
            else{
                Toast.makeText(this,resources.getString(R.string.please_enter_a_card_name),Toast.LENGTH_SHORT).show()
            }
        }

        tv_select_label_color.setOnClickListener {
            labelColorsListDialog()
        }

        tv_select_members.setOnClickListener {
            membersListDialog()
        }

        mSelectedDueDateMillis=mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].dueDate

        if (mSelectedDueDateMillis > 0){
            val simpleDateFormat= SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val selectedDate=simpleDateFormat.format(Date(mSelectedDueDateMillis))
            tv_select_due_date.text=selectedDate
        }

        tv_select_due_date.setOnClickListener {
            showDatePicker()
        }

        setupSelectedMembersList()
    }

    private fun getIntentData(){
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition=intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION,-1)
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mCardPosition=intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION,-1)
        }
        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMembersDetailList=intent.getParcelableArrayListExtra<UserModel>(Constants.BOARD_MEMBERS_LIST)!!
        }
    }

    private fun membersListDialog(){
        var cardAssignedMembersList=mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo

        if(cardAssignedMembersList.size > 0){
            for (i in mMembersDetailList.indices){
                for(j in cardAssignedMembersList){
                    if(mMembersDetailList[i].id==j){
                        mMembersDetailList[i].selected=true
                    }
                }
            }
        }
        else{
            for (i in mMembersDetailList.indices){
                mMembersDetailList[i].selected=false
            }
        }

        val listDialog = object : MembersListDialog(
            this,
            mMembersDetailList,
            resources.getString(R.string.str_select_member)
        ){
            override fun onItemSelected(user: UserModel, action: String) {
                if(action==Constants.SELECT){
                    if(!mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.contains(user.id)){
                        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.add(user.id)
                    }
                }
                else{
                    mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.remove(user.id)
                    for(i in mMembersDetailList.indices){
                        if(mMembersDetailList[i].id==user.id){
                            mMembersDetailList[i].selected=false
                        }
                    }
                }
                setupSelectedMembersList()
            }
        }

        listDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_card->{
                alertDialogForDeleteCard(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun colorsList():ArrayList<String>{
        val colorsList:ArrayList<String> = ArrayList()
        colorsList.add(resources.getString(R.string.color_list_grey))
        colorsList.add(resources.getString(R.string.color_list_dark_grey))
        colorsList.add(resources.getString(R.string.color_list_blue))
        colorsList.add(resources.getString(R.string.color_list_dark_blue))
        colorsList.add(resources.getString(R.string.color_list_dark_red))
        colorsList.add(resources.getString(R.string.color_list_lime_green))
        colorsList.add(resources.getString(R.string.color_list_orange))
        colorsList.add(resources.getString(R.string.color_list_red))
        colorsList.add(resources.getString(R.string.color_list_yellow))

        return colorsList
    }

    private fun setupSelectedMembersList(){
        val cardAssignedMemberList=mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo

        val selectedMembersList:ArrayList<SelectedMembers> = ArrayList()

        for (i in mMembersDetailList.indices){
            for(j in cardAssignedMemberList){
                if(mMembersDetailList[i].id==j){
                    val selectedMember = SelectedMembers(
                        mMembersDetailList[i].id,
                        mMembersDetailList[i].image
                    )
                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if(selectedMembersList.size > 0){
            selectedMembersList.add(SelectedMembers("",""))
            tv_select_members.visibility=View.GONE
            rv_selected_members_list.visibility=View.VISIBLE

            rv_selected_members_list.layoutManager=GridLayoutManager(this,6)
            val adapter = CardMemberListItemsAdapter(this,selectedMembersList,true)
            rv_selected_members_list.adapter=adapter
            adapter.setOnClickListener(object:CardMemberListItemsAdapter.OnClickListener{
                override fun onClick() {
                    membersListDialog()
                }
            })
        }
        else{
            tv_select_members.visibility=View.VISIBLE
            rv_selected_members_list.visibility=View.GONE
        }
    }

    private fun setColor(){
        tv_select_label_color.text=""
        tv_select_label_color.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    private fun labelColorsListDialog(){
        val colorsList:ArrayList<String> = colorsList()

        val listDialog= object: LabelColorListDialog(
            this,
            colorsList,
            resources.getString(R.string.str_select_label_color),
            mSelectedColor
        ){
            override fun onItemSelected(color: String) {
                mSelectedColor=color
                setColor()
            }
        }
        listDialog.show()
    }

    private fun updateCardDetails(){
        val card=Card(et_name_card_details.text.toString(),
        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
        mSelectedColor,
        mSelectedDueDateMillis)

        val taskList:ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition]=card

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)
    }

    private fun deleteCard(){
        val cardsList:ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        cardsList.removeAt(mCardPosition)

        val taskList:ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        taskList[mTaskListPosition].cards=cardsList

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)
    }

    private fun alertDialogForDeleteCard(cardName:String){
        val builder=AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.alert))
        builder.setMessage(
            resources.getString(R.string.confirmation_message_to_delete_card, cardName)
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.yes)){dialogInterface,which->
            dialogInterface.dismiss()
            deleteCard()
        }
        builder.setNegativeButton(resources.getString(R.string.no)){dialogInterface,which->
            dialogInterface.dismiss()
        }

        val alertDialog:AlertDialog=builder.create()

        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun showDatePicker(){
        val mCalendar =Calendar.getInstance()
        val year=mCalendar.get(Calendar.YEAR)
        val month=mCalendar.get(Calendar.MONTH)
        val day = mCalendar.get(Calendar.DAY_OF_MONTH)

        val dpd=DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val sDayOfMonth = if (dayOfMonth<10) "0$dayOfMonth" else "$dayOfMonth"
                val sMonthOfYear = if ((monthOfYear + 1) < 10) "0${monthOfYear+1}" else "${monthOfYear+1}"
                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
                tv_select_due_date.text=selectedDate

                val sdf=SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val theDate=sdf.parse(selectedDate)
                mSelectedDueDateMillis=theDate!!.time
            },year,month,day
        )
        dpd.show()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_card_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title=mBoardDetails
                .taskList[mTaskListPosition]
                .cards[mCardPosition]
                .name
        }

        toolbar_card_details_activity.setNavigationOnClickListener { onBackPressed() }
    }
}
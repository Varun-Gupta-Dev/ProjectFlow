package com.example.projectflow.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectflow.R
import com.example.projectflow.adapters.BoardItemsAdapter
import com.example.projectflow.databinding.ActivityMainBinding
import com.example.projectflow.firebase.FirestoreClass
import com.example.projectflow.models.Board
import com.example.projectflow.models.User
import com.example.projectflow.utils.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private  lateinit var navUserImage: CircleImageView
    private  lateinit var tvUsername: TextView
    private lateinit var mUserName: String
    private lateinit var rvBoardsList: RecyclerView
    private lateinit var  tvNoBoardsAvailable: TextView

    private var fabCreateBoard: FloatingActionButton? = null

    private lateinit var toolBarMainActivity: Toolbar

    companion object{
        const val MY_PROFILE_REQUEST_CODE : Int = 11
        const val CREATE_BOARD_REQUEST_CODE: Int = 12
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        rvBoardsList = findViewById(R.id.rv_boards_list)
        tvNoBoardsAvailable = findViewById(R.id.tv_no_boards_available)
        fabCreateBoard = findViewById(R.id.fab_create_board)

        binding.navView.setNavigationItemSelectedListener(this)

        FirestoreClass().loadUserData(this, true)

        fabCreateBoard?.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }

    }

    // Function to populate the boards list in the UI
    fun populateBoardsListToUI(boardsList: ArrayList<Board>){
        hideProgressDialog()

        if(boardsList.size > 0){
            rvBoardsList.visibility = View.VISIBLE
            tvNoBoardsAvailable.visibility = View.GONE
            rvBoardsList.layoutManager = LinearLayoutManager(this)
            rvBoardsList.setHasFixedSize(true)
            val adapter = BoardItemsAdapter(this, boardsList)
            rvBoardsList.adapter = adapter

            adapter.setOnClickListener(object: BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })
        }else{
            rvBoardsList.visibility = View.GONE
            tvNoBoardsAvailable.visibility = View.VISIBLE
        }
    }

    private fun setupActionBar(){
        toolBarMainActivity = findViewById(R.id.toolbar_main_activity)
        setSupportActionBar(toolBarMainActivity)
        toolBarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolBarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer(){

        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()
        }
    }

    fun updateNavigationUserDetails(user: User, readBoardsList: Boolean){

        navUserImage = findViewById(R.id.nav_user_image)
        tvUsername = findViewById(R.id.tv_username)
        mUserName = user.name

//        navUserImage.let {
//            Glide
//                .with(this)
//                .load(user.image)
//                .fitCenter()
//                .placeholder(R.drawable.ic_user_place_holder)
//                .into(it)
//        };
        Glide
            .with(this)
            .load(user.image)
            .fitCenter()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage);

        tvUsername.text = user.name

        if (readBoardsList){
            showProgressDialog("Please Wait")
            FirestoreClass().getBoardsList(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE){
            FirestoreClass().loadUserData(this)
        }else if(resultCode == Activity.RESULT_OK && requestCode == CREATE_BOARD_REQUEST_CODE){
            FirestoreClass().getBoardsList(this)

        }
        else{
            Log.e("update error","Profile update error")
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.nav_my_profile ->{
               startActivityForResult(Intent(this, MyProfileActivity::class.java ),
                   MY_PROFILE_REQUEST_CODE)
            }

            R.id.nav_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

//                Intent.FLAG_ACTIVITY_CLEAR_TOP: This flag instructs Android to clear all activities on top of the target activity from the current task stack. If the target activity is already running in the task stack, it will be brought to the top of the stack, and all activities above it will be removed. This ensures that only the target activity remains at the top of the stack.
//
//                Intent.FLAG_ACTIVITY_NEW_TASK: This flag is used to start the target activity in a new task stack, separate from the existing task stack. If there is no existing task stack associated with the target activity, a new task stack will be created for it. This flag is typically used when starting an activity from outside of an existing task, such as from a service or a broadcast receiver.
//
//                Combining these flags in the addFlags() method of an Intent means that the target activity will be started with the following behavior:
//
//                If the activity is already running, it will be brought to the top of the stack, and all activities above it will be removed.
//                If the activity is not running, it will be started in a new task stack.
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

}




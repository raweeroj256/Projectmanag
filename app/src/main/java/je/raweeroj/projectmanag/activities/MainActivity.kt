package je.raweeroj.projectmanag.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import de.hdodenhof.circleimageview.CircleImageView
import je.raweeroj.projectmanag.R
import je.raweeroj.projectmanag.adapters.BoardItemsAdapter
import je.raweeroj.projectmanag.databinding.ActivityMainBinding
import je.raweeroj.projectmanag.firebase.FirestoreClass
import je.raweeroj.projectmanag.models.Board
import je.raweeroj.projectmanag.models.User
import je.raweeroj.projectmanag.utils.Constants
import pl.kitek.rvswipetodelete.SwipeToDeleteCallback


class MainActivity : BaseActivity() , NavigationView.OnNavigationItemSelectedListener{

    private var binding : ActivityMainBinding? = null
    private var btnCreateBoard : FloatingActionButton? = null
    private lateinit var mSharedPreferences: SharedPreferences

    private lateinit var mUserName : String
   //private var navHeaderMainBinding : NavHeaderMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        //navHeaderMainBinding = NavHeaderMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        binding?.navView?.setNavigationItemSelectedListener(this)

        mSharedPreferences=this.getSharedPreferences(Constants.PROJECTMANAJ_PREFERENCES,Context.MODE_PRIVATE)

        val isTokenUpdated=mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED,false)

        if (isTokenUpdated){
            showProgressDialog("Please Wait")
            FirestoreClass().loadUserData(this,true)
            Log.e("FCM token","already exist From main activity")
            println("****** FCM token already exist From main activity")
        }else {
            /**
             * Check this if InvalidRegistration http failure from doInBackground
             */
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                //val msg = getString(R.string.msg_token_fmt, token)
                updateFCMToken(token)
                Log.e("FCM token", "From main activity new created $token")
                //println("******** FCM token From main activity new created $token")
              //  Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
        }

        FirestoreClass().loadUserData(this,true)

        btnCreateBoard = findViewById(R.id.fab_create_board)
        btnCreateBoard?.setOnClickListener{
            val intent = Intent(this,CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME,mUserName)
            startCreateBoardActivityAndGetResult.launch(intent)
        }





    }

    fun populateBoardsListToUI(boardsList:ArrayList<Board>){
        hideProgressDialog()
        var rvboardlist = findViewById<RecyclerView>(R.id.rv_boards_list)
        var tvnoboardavailable = findViewById<TextView>(R.id.tv_no_boards_available)
        if(boardsList.size > 0){
            rvboardlist.visibility = View.VISIBLE
            tvnoboardavailable.visibility = View.GONE

            rvboardlist.layoutManager = LinearLayoutManager(this)
            rvboardlist.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this,boardsList)
            rvboardlist.adapter = adapter

            adapter.setOnClickListener(object :BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity,TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,model.documentId)
                    boardOnClickLauncher.launch(intent)
                }

            })

        }else{
            rvboardlist.visibility = View.GONE
            tvnoboardavailable.visibility = View.VISIBLE
        }

    }

    private val boardOnClickLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == Activity.RESULT_OK){
            FirestoreClass().loadUserData(this,true)
        }
    }

    private fun  setupActionBar(){
        var mainToolBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main_activity)
        setSupportActionBar(mainToolBar)
        mainToolBar.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        mainToolBar.setNavigationOnClickListener {
            toogleDrawer()
        }
    }

    private fun toogleDrawer() {
       if(binding?.drawerLayout?.isDrawerOpen(GravityCompat.START)==true){
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }else{
            binding?.drawerLayout?.openDrawer(GravityCompat.START)
       }

    }

    override fun onBackPressed() {
        if(binding?.drawerLayout?.isDrawerOpen(GravityCompat.START)==true){
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()
        }
    }

    fun updateNavigationUserDetails(user:User, readBoardsList : Boolean){
        hideProgressDialog()
        val navuserimage : CircleImageView = findViewById(R.id.nav_user_image)
        val tvUsername : TextView = findViewById(R.id.tv_username)

        mUserName = user.name

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navuserimage)

        tvUsername.text = user.name

        if(readBoardsList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this)
        }
    }

    private val startUpdateActivityAndGetResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                FirestoreClass().loadUserData(this)
            } else {
                Log.e("onActivityResult()", "Profile update cancelled by user")
            }
        }

    private val startCreateBoardActivityAndGetResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                FirestoreClass().getBoardsList(this)
            } else {
                Log.e("onActivityResult()", "Profile update cancelled by user")
            }
        }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile -> {
                startUpdateActivityAndGetResult.launch(Intent(this, MyProfileActivity::class.java))
            }
            R.id.nav_sign_out ->{
                FirebaseAuth.getInstance().signOut()

                mSharedPreferences.edit().clear().apply()

                val intent = Intent(this,IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

        }
        binding?.drawerLayout?.closeDrawer(GravityCompat.START)

        return true
    }

    fun tokenUpdateSuccess() {
        hideProgressDialog()
        val editor : SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED,true)
        editor.apply()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().loadUserData(this, true)
    }

    private fun updateFCMToken(token:String){
        val userHashMap = HashMap<String,Any>()
        userHashMap[Constants.FCM_TOKEN] = token
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserProfileData(this,userHashMap)
    }
}
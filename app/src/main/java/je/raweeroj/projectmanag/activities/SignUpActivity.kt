package je.raweeroj.projectmanag.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import je.raweeroj.projectmanag.R
import je.raweeroj.projectmanag.databinding.ActivitySignUpBinding
import je.raweeroj.projectmanag.firebase.FirestoreClass
import je.raweeroj.projectmanag.models.User
import java.util.jar.Attributes.Name

class SignUpActivity : BaseActivity() {

    private var binding : ActivitySignUpBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        // This is used to align the xml view to this class
        setContentView(binding?.root)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // TODO (Step 9: Call the setup actionBar function.)
        setupActionBar()
    }

    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarSignUpActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding?.toolbarSignUpActivity?.setNavigationOnClickListener { onBackPressed() }

        binding?.btnSignUp?.setOnClickListener { registerUser() }
    }

    private fun registerUser(){
        val name:String = binding?.etName?.text.toString().trim {it<= ' '}
        val email:String = binding?.etEmail?.text.toString().trim {it<= ' '}
        val password:String = binding?.etPassword?.text.toString().trim {it<= ' '}

        if(validateForm(name,email,password)){
          showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email,password).addOnCompleteListener(
                    {
                    task->
                        hideProgressDialog()
                    if(task.isSuccessful){
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid,name,registeredEmail)
                        FirestoreClass().registerUser(this,user)
                    }else{

                        Toast.makeText(this,
                        task.exception!!.message,Toast.LENGTH_SHORT).show()
                    }
                }
                )
        }
    }

    private fun validateForm(name: String,email:String,password:String): Boolean {
        return when {
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter a name")
                false
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter an email")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter a password")
                false
            }else -> {
                true
            }
        }

    }

    fun userRegisteredSuccess() {
        Toast.makeText(this,"you have " +
                "successfully registered"
               ,Toast.LENGTH_SHORT).show()
        FirebaseAuth.getInstance().signOut()
        hideProgressDialog()
        finish()
    }

}
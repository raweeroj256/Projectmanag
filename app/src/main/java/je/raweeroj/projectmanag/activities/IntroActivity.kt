package je.raweeroj.projectmanag.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import je.raweeroj.projectmanag.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private var binding: ActivityIntroBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding?.btnSignUpIntro?.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))

        }

        binding?.btnSignInIntro?.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}
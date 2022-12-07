package com.example.shopping.signinandsigupfeature.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import androidx.lifecycle.lifecycleScope
import com.example.shopping.MainActivity
import com.example.shopping.MyApplication
import com.example.shopping.R
import com.example.shopping.signinandsigupfeature.ui.di.AppContainer
import com.example.shopping.signinandsigupfeature.ui.di.UserContainer
import com.example.shopping.viewmodel.OnBoardingFormViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Matcher
import java.util.regex.Pattern

class LoginFragment : Fragment() {
    //private val userViewModel: UserViewModel by activityViewModels()
    private val onBoardingFormViewModel:OnBoardingFormViewModel by viewModels()
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    lateinit var appContainer: AppContainer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    private fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        appContainer=(activity?.application as MyApplication).appContainer
        appContainer.userContainer= UserContainer(appContainer.registerUserUseCase,appContainer.checkValidUserUseCase,appContainer.getIdUsingRowId)
        val userViewModel=appContainer.userContainer?.userViewModelFactory?.create()

        if(requireActivity() is MainActivity){
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view).visibility=View.GONE
        }
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.apply {
            title="Login"
            setDisplayHomeAsUpEnabled(true)
        }

        emailInputLayout=view.findViewById<TextInputLayout>(R.id.emailInputLayout)
        passwordInputLayout=view.findViewById<TextInputLayout>(R.id.passwordLayout)

        if(onBoardingFormViewModel.username!=""){
            emailInputLayout.editText?.setText(onBoardingFormViewModel.username)
        }
        if(onBoardingFormViewModel.password!=""){
            passwordInputLayout.editText?.setText(onBoardingFormViewModel.password)
        }

        if(onBoardingFormViewModel.isPasswordHidden){
            println("password is hidden by default")
           passwordInputLayout.editText?.transformationMethod=PasswordTransformationMethod()
        }else{
            println("password is visible by default")
            passwordInputLayout.editText?.transformationMethod=null
        }

        //passwordInputLayout.endIconMode=TextInputLayout.END_ICON_PASSWORD_TOGGLE

        /*passwordInputLayout.setEndIconOnClickListener {
            if(onBoardingFormViewModel.isPasswordHidden){
                onBoardingFormViewModel.isPasswordHidden=false
                println("password is visible now")
                passwordInputLayout.editText?.transformationMethod=null
                passwordInputLayout.editText?.length()
                    ?.let { it1 -> passwordInputLayout.editText?.setSelection(it1) }
            }else{
                onBoardingFormViewModel.isPasswordHidden=true
                println("password is hidden now")
                passwordInputLayout.editText?.transformationMethod=PasswordTransformationMethod()
                passwordInputLayout.editText?.length()
                    ?.let { it1 -> passwordInputLayout.editText?.setSelection(it1) }
            }
        }*/

        val registerButton=view.findViewById<Button>(R.id.loginButton)
        registerButton.setOnClickListener {
            if(validateInputs()){
                val email=emailInputLayout.editText?.text.toString()
                val password=passwordInputLayout.editText?.text.toString()
                lifecycleScope.launch{
                    val job=launch(Dispatchers.IO) {
                        val validUserId=userViewModel?.isValidUser(email,password)
                        withContext(Dispatchers.Main){
                            if(validUserId!=-1){
                                val sharePreferences=activity?.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
                                with(sharePreferences?.edit()){
                                    this?.putBoolean("login_skipped",false)
                                    this?.putBoolean("login_status",true)
                                    if (validUserId != null) {
                                        this?.putInt("userId",validUserId)
                                    }
                                    this?.apply()
                                }
                                val intent= Intent(requireContext(), MainActivity::class.java)
                                println("got intent string as ${requireActivity().intent.getStringExtra("fragment")}")
                                when(requireActivity().intent.getStringExtra("fragment_from")){
                                    "cart"->{intent.putExtra("fragment","cart")}
                                    "account"->{intent.putExtra("fragment","account")}
                                    "wishlist"->{intent.putExtra("fragment","wishlist")}
                                }
                                /*if(requireActivity().intent.getStringExtra("fragment_from")=="cart"){
                                    intent.putExtra("fragment","cart")
                                }else if(requireActivity().intent.getStringExtra("fragment_from")=="account")*/
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                                /*parentFragmentManager.commit {
                                    replace(R.id.fragment_container,HomeFragment())
                                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view).visibility=View.VISIBLE
                                }*/
                                Toast.makeText(requireContext(),"Successfully logged in",Toast.LENGTH_SHORT).show()
                            }else{
                                passwordInputLayout.isErrorEnabled =true
                                passwordInputLayout.error ="Wrong Password. Try again"
                                //Toast.makeText(requireContext(),"Invalid Credentials",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    job.join()
                }
                /*val newUser= User(0,email,password)
                registerUser(newUser)*/
            }
        }
    }

    private fun validateInputs(): Boolean {
        var returnValue=true
        val mail=view?.findViewById<TextInputLayout>(R.id.emailInputLayout)
        val password=view?.findViewById<TextInputLayout>(R.id.passwordLayout)

        if(mail!=null){
            if(mail.editText?.text.toString()==""){
                mail.isErrorEnabled =true
                mail.error ="Enter the Email id"
                returnValue=returnValue and false
            }else if(!mail.editText?.text.toString().isValidEmail()){
                mail.isErrorEnabled =true
                mail.error ="Invalid email id"
                returnValue=returnValue and false
            }
            else{
                mail.error =null
                mail.isErrorEnabled =false
            }
        }

        if(password!=null){
            val pass=password.editText?.text.toString()
            if(pass==""){
                password.isErrorEnabled =true
                password.error ="Enter the Password"
                returnValue=returnValue and false
            }else{
                password.error =null
                password.isErrorEnabled =false
            }
            /*else if(!pass.isValidPassword()){
                password.isErrorEnabled =true
                password.error ="Password must be atleast 8 characters containing Special character, Number and Uppercase"
                returnValue=returnValue and false
            }*/
        }

       return returnValue
    }

    private fun String.isValidPassword(): Boolean {
        return if (this.length>=8){
            val upperCase: Pattern = Pattern.compile("[A-Z]")
            val digit: Pattern = Pattern.compile("[0-9]")
            val special: Pattern = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]")

            val hasLetter: Matcher = upperCase.matcher(this)
            val hasDigit: Matcher = digit.matcher(this)
            val hasSpecial: Matcher = special.matcher(this)

            hasLetter.find() && hasDigit.find() && hasSpecial.find()
        }else
            false
    }

    override fun onDestroy() {
        println("Destroy called")
        onBoardingFormViewModel.apply {
            username=emailInputLayout.editText?.text.toString()
            password=passwordInputLayout.editText?.text.toString()
        }
        super.onDestroy()
    }
}
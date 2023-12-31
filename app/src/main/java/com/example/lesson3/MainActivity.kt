package com.example.lesson3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.addCallback
import com.example.lesson3.data.Food
import com.example.lesson3.fragments.FacultyFragment
import com.example.lesson3.fragments.CourseFragment
import com.example.lesson3.fragments.FoodInputFragment
import com.example.lesson3.interfaces.MainActivityCallbacks
import com.example.lesson3.repository.AppRepository

public class MainActivity : AppCompatActivity(), MainActivityCallbacks {
    interface Edit{
        fun append()
        fun update()
        fun delete()
    }

    data object AuthStatus{
        var userType: Int = -1
    }

//    public var userType : Int = -1
    lateinit var etLogin : EditText
    lateinit var etPwd : EditText
    lateinit var btnAuth : Button
    lateinit var authBrurb : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_auth)
        etLogin = findViewById(R.id.etLogin)
        etPwd = findViewById(R.id.etPwd)
        btnAuth = findViewById(R.id.btnAuth)
        btnAuth.setOnClickListener(auth)
        authBrurb = findViewById(R.id.authBrurb)
        authBrurb.setOnClickListener(authListener)
        authBrurb.text = "-1"


//
//        while (true){
//            if (userType != -1){
//                break
//            }
//        }


    }


//
    val auth = View.OnClickListener {
        btnAuth.error = null
        var errLogin = false
        var errPwd = false
        if (etLogin.text.toString().isBlank()){
            etLogin.error = "Пустой логин!"
            errLogin = true
        } else {
            etPwd.error = null
        }
        if (etPwd.text.toString().isBlank()){
            etPwd.error = "Пустой пароль!"
            errPwd = true
        } else {
            etPwd.error = null
        }
        if (!errLogin && !errPwd) {
            AppRepository.getInstance().login(etLogin.text.toString(), etPwd.text.toString(), authBrurb)
        }
    }

    val authListener = View.OnClickListener {
        AuthStatus.userType = authBrurb.text.toString().toInt()
        if (AuthStatus.userType != -1){
            start()
        } else {
            btnAuth.error = "Неверный логин/пароль!"
        }
    }

    fun start(){
        setContentView(R.layout.activity_main)
        onBackPressedDispatcher.addCallback(this){
            if(supportFragmentManager.backStackEntryCount>0){
                supportFragmentManager.popBackStack()
                when (activeFragment){
                    NamesOfFragment.FACULTY->{
                        finish()
                    }
                    NamesOfFragment.GROUP->{
                        activeFragment=NamesOfFragment.FACULTY
                    }
                    NamesOfFragment.STUDENT->{
                        activeFragment=NamesOfFragment.GROUP
                    }
                    else ->{

                    }
                }
                updateMenu(activeFragment)
            }
            else{
                finish()
            }
        }
        showFragment(activeFragment, null)
    }

    private var _miAppendFaculty: MenuItem?= null
    private var _miUpdateFaculty: MenuItem?= null
    private var _miDeleteFaculty: MenuItem?= null
    private var _miAppendGroup: MenuItem?= null
    private var _miUpdateGroup: MenuItem?= null
    private var _miDeleteGroup: MenuItem?= null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        _miAppendFaculty = menu?.findItem(R.id.miAppendFaculty)
        _miUpdateFaculty = menu?.findItem(R.id.miUpdateFaculty)
        _miDeleteFaculty = menu?.findItem(R.id.miDeleteFaculty)
        _miAppendGroup = menu?.findItem(R.id.miAppendGroup)
        _miUpdateGroup = menu?.findItem(R.id.miUpdateGroup)
        _miDeleteGroup = menu?.findItem(R.id.miDeleteGroup)
        updateMenu(activeFragment)
        return true
    }

    var activeFragment : NamesOfFragment=NamesOfFragment.FACULTY

    private fun updateMenu(fragmentType: NamesOfFragment){
        _miAppendFaculty?.isVisible = fragmentType == NamesOfFragment.FACULTY
        _miUpdateFaculty?.isVisible = fragmentType == NamesOfFragment.FACULTY
        _miDeleteFaculty?.isVisible = fragmentType == NamesOfFragment.FACULTY
        _miAppendGroup?.isVisible = fragmentType == NamesOfFragment.GROUP
        _miUpdateGroup?.isVisible = fragmentType == NamesOfFragment.GROUP
        _miDeleteGroup?.isVisible = fragmentType == NamesOfFragment.GROUP
        if (AuthStatus.userType != 1) {
            _miAppendFaculty?.isVisible = false
            _miUpdateFaculty?.isVisible = false
            _miDeleteFaculty?.isVisible = false
            _miAppendGroup?.isVisible = false
            _miUpdateGroup?.isVisible = false
            _miDeleteGroup?.isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.miAppendFaculty -> {
                val fedit: Edit = FacultyFragment.getInstance()
                fedit.append()
                true
            }
            R.id.miUpdateFaculty -> {
                val fedit: Edit = FacultyFragment.getInstance()
                fedit.update()
                true
            }
            R.id.miDeleteFaculty -> {
                val fedit: Edit = FacultyFragment.getInstance()
                fedit.delete()
                true
            }
            R.id.miAppendGroup -> {
                val fedit: Edit = CourseFragment.getInstance()
                fedit.append()
                true
            }
            R.id.miUpdateGroup -> {
                val fedit: Edit = CourseFragment.getInstance()
                fedit.update()
                true
            }
            R.id.miDeleteGroup -> {
                val fedit: Edit = CourseFragment.getInstance()
                fedit.delete()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showFragment(fragmentType: NamesOfFragment, food: Food?){
        when(fragmentType){
            NamesOfFragment.FACULTY->{
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fcMain, FacultyFragment.getInstance())
                    .addToBackStack(null)
                    .commit()
            }
            NamesOfFragment.GROUP->{
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fcMain, CourseFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }
            NamesOfFragment.STUDENT->{
                if(food!=null)
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fcMain, FoodInputFragment.newInstance(food))
                        .addToBackStack(null)
                        .commit()
            }
        }
        activeFragment=fragmentType
        updateMenu(fragmentType)
    }

    override fun newTitle(_title: String) {
        title = _title
    }

    override fun onStop() {
//        AppRepository.getInstance().saveData()
        super.onStop()
    }
}
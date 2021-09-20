package com.minhhop.easygolf.views.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.ContactsContract
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minhhop.core.domain.Contact
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.ContactAdapter
import com.minhhop.easygolf.base.WozBaseActivity
import com.minhhop.easygolf.base.fragment.HandleResponse
import com.minhhop.easygolf.framework.models.ContactResult
import com.minhhop.easygolf.framework.models.ContactRetrofit
import com.minhhop.easygolf.services.ApiService
import java.util.*


class ContactActivity: WozBaseActivity(), ContactAdapter.EventContact{


    companion object {
        private const val CODE_SETTING_CONTACT = 0x0001
    }
    private val mContactRetrofit = ContactRetrofit()
    private lateinit var locale: Locale
    /**
     * Event when select contact
     * */
    private var mMenuItemDone:MenuItem? = null

    internal val handler = Handler()

    private var mQuery:String? = null

    override fun onClick(contact: Contact) {
        mContactRetrofit.removeOrAdd(contact)
    }

    private lateinit var mViewRoot: View
    private lateinit var mContactAdapter: ContactAdapter
    private lateinit var mLayoutSettingPermission:View
    private lateinit var mListContact:RecyclerView

    override fun setLayoutView(): Int = R.layout.activity_contact

    override fun initView() {
        mViewRoot = findViewById(R.id.viewRoot)
        mLayoutSettingPermission = findViewById(R.id.layoutSettingPermission)
        locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0)
        } else {
            resources.configuration.locale
        }

        mContactAdapter = ContactAdapter(this,this)

        mListContact = findViewById(R.id.listContact)
        mListContact.layoutManager = LinearLayoutManager(this)
        mListContact.adapter = mContactAdapter
        mLayoutSettingPermission.visibility = View.GONE

        findViewById<View>(R.id.btSettingContact).setOnClickListener {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent,CODE_SETTING_CONTACT)
        }

        findViewById<EditText>(R.id.editSearch).addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                val term = s.toString()
                Log.d("onTextChanged", term)
                mQuery = term
                if (term.length >= 2) {
                    handler.removeMessages(0)
                    handler.postDelayed({
                        mStatusLoading = STATUS.FIRST_LOAD
                        doSearch(mQuery)

                    }, 350)
                } else if (term.isEmpty()) {
                    mStatusLoading = STATUS.FIRST_LOAD
                    doSearch(mQuery)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    private fun doSearch(query: String?){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            mLayoutSettingPermission.visibility = View.GONE
            mListContact.visibility = View.VISIBLE
            getContactsIntoArrayList(query)
        }
    }

    private fun getFormatSection(query:String?):String?{
        return if(query != null) "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ? OR ${ContactsContract.CommonDataKinds.Phone.NUMBER} LIKE ?" else null
    }


    override fun loadData() {
//  TODO @quipham      Dexter.withActivity(this).withPermission(Manifest.permission.READ_CONTACTS)
//                .withListener(object : PermissionListener {
//                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
//                    }
//                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
//                        Log.e("WOW", "onPermissionDenied: ")
//                        mLayoutSettingPermission.visibility = View.VISIBLE
//                        mListContact.visibility = View.GONE
//                    }
//                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
//                        Log.e("WOW", "onPermissionRationaleShouldBeShown: ")
//                        token.continuePermissionRequest()
//                    }
//                }).check()
    }

    override fun onResume() {
        super.onResume()
        doSearch(mQuery)
    }

    private fun getContactsIntoArrayList(query: String?){
        showLoading()
        mContactAdapter.clearAll()

        val mSelectionArgs = if(query != null)
            arrayOf("%$query%","%$query%")
        else null

        val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                getFormatSection(query),mSelectionArgs,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC")

        cursor?.apply {
            while (moveToNext()){

                Log.e("WOW","cursor???`")
                val avatar = this.getString(this.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
                val fullName = this.getString(this.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                var firstName = ""
                val temp = fullName.split(" ")
                if(temp.isNotEmpty()){
                    firstName = temp[0]
                }
                val lastName = fullName.replace(firstName,"")

                val phone = this.getString(this.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                val newContact = Contact(avatar, firstName, lastName, phone, locale.country)
                mContactAdapter.addItem(newContact)
            }

        }
        cursor?.close()
        hideLoading()
    }

    private fun callApiInviteFriends(){
        showLoading()
        registerResponse(ApiService.getInstance().userService.inviteFriend(mContactRetrofit),
                object : HandleResponse<ContactResult>{
                    override fun onSuccess(result: ContactResult) {
                        Toast.makeText(this@ContactActivity,"Your friend has invited",Toast.LENGTH_LONG).show()
                        hideLoading()
                        finish()
                    }

                })
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let {menuOption->
            mMenuItemDone = menuOption.findItem(R.id.actionDone)
            mMenuItemDone?.setTitle("Done")
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.apply {
            when(itemId){
                R.id.actionDone->{
                    if(mContactRetrofit.getSizeFriend() > 0) {
                        callApiInviteFriends()
                    }else{
                        Toast.makeText(this@ContactActivity,"Please select your friend, who you want to invite",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setMenuRes(): Int {
        return R.menu.menu_done
    }
}
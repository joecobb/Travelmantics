package com.josephcobbinah.travelmantics.util

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.josephcobbinah.travelmantics.models.TravelDeal
import com.josephcobbinah.travelmantics.ui.DealActivity
import com.josephcobbinah.travelmantics.ui.ListActivity
import java.util.*

object FirebaseUtil {
    val RC_SIGN_IN: Int = 123
    var mFirebaseDatabase: FirebaseDatabase? = null
    var mDatabaseReference: DatabaseReference? = null
    var mStorage: FirebaseStorage? = null
    var mStorageRef: StorageReference? = null
    var mFirebaseAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    var caller: AppCompatActivity? = null
    var isAdmin: Boolean? = false
    private var firebaseUtil: FirebaseUtil? = null

    var mDeals: MutableList<TravelDeal>? = null
    fun openFbReference(ref: String, callerActivity: AppCompatActivity) {
        if (firebaseUtil == null) {
            firebaseUtil = FirebaseUtil
            mFirebaseDatabase = FirebaseDatabase.getInstance()
            mFirebaseAuth = FirebaseAuth.getInstance()


            caller = callerActivity
            mAuthListener = FirebaseAuth.AuthStateListener {
                if (it.currentUser == null) {
                    signIn()
                } else {
                    val uid = it.currentUser!!.uid
                    checkAdmin(uid)
                }
            }
            mDatabaseReference = mFirebaseDatabase?.reference?.child(ref)
            mDeals = ArrayList()


        }
        mDatabaseReference = mFirebaseDatabase?.reference?.child(ref)

        connectStorage()

    }

    private fun checkAdmin(uid: String) {
        isAdmin = false
        callShowMenu()
        val ref = mFirebaseDatabase!!.reference.child("administrators").child(uid)
        val listener = object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                isAdmin = true
                callShowMenu()
                Log.d("Admin", "You are an administrator")
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        }
        ref.addChildEventListener(listener)
    }

    fun attachListener() {
        mFirebaseAuth?.addAuthStateListener(mAuthListener!!)
    }

    fun detachListener() {
        mFirebaseAuth?.removeAuthStateListener(mAuthListener!!)
    }

    fun signIn() {
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setAndroidPackageName(
                "com.josephcobbinah.travelmantics", true,
                null
            )
            .setHandleCodeInApp(true)
            .setUrl("https://google.com") // This URL needs to be whitelisted
            .build()
// Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        caller!!.startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(
                    providers
                )
                .build(),
            RC_SIGN_IN
        )
    }

    fun callShowMenu() {
        if (caller is ListActivity) {
            (caller as ListActivity).showMenu()
        } else if (caller is DealActivity) {
            (caller as DealActivity).showMenu()
        }
    }

    fun connectStorage(){
        mStorage = FirebaseStorage.getInstance()
        mStorageRef = mStorage?.reference?.child("deals_pictures")
    }
}

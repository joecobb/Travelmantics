package com.josephcobbinah.travelmantics.ui

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.UploadTask
import com.josephcobbinah.travelmantics.R
import com.josephcobbinah.travelmantics.models.TravelDeal
import com.josephcobbinah.travelmantics.util.FirebaseUtil
import com.josephcobbinah.travelmantics.util.toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_deal.*


class DealActivity : AppCompatActivity() {
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mDeal: TravelDeal
    private val PICTURE_RESULT: Int = 42

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal)
        FirebaseUtil.openFbReference("traveldeals", this)
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase!!
        mDatabaseReference = FirebaseUtil.mDatabaseReference!!
        if (intent.hasExtra("Deal")) {
            mDeal = intent.getSerializableExtra("Deal") as TravelDeal
        } else {
            mDeal = TravelDeal()
        }

        txtTitle.editText?.setText(mDeal.title)
        txtDescription.editText?.setText(mDeal.description)
        txtPrice.editText?.setText(mDeal.price)
        showImage(mDeal.imageUrl)
        btnImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(Intent.createChooser(intent, "Insert Picture"), PICTURE_RESULT)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)
        val saveMenu = menu?.findItem(R.id.save_menu)
        val deleteMenu = menu?.findItem(R.id.delete_deal)
        if (FirebaseUtil.isAdmin!!) {
            saveMenu?.isVisible = true
            deleteMenu?.isVisible = true
            enableEditTexts(true)
        } else {
            saveMenu?.isVisible = false
            deleteMenu?.isVisible = false
            enableEditTexts(false)
        }
        return true
    }

    private fun enableEditTexts(isEnabled: Boolean) {
        txtPrice.isEnabled = isEnabled
        txtDescription.isEnabled = isEnabled
        txtTitle.isEnabled = isEnabled
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_menu -> {
                saveDeal()
                toast("Deal Saved")
                clean()
                backToList()
            }
            R.id.delete_deal -> {
                deleteDeal()
                toast("Deal Deleted")
                backToList()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun clean() {
        txtTitle.editText?.setText("")
        txtDescription.editText?.setText("")
        txtPrice.editText?.setText("")
        txtTitle.requestFocus()
    }

    private fun saveDeal() {
        mDeal.title = txtTitle.editText?.text.toString()
        mDeal.description = txtDescription.editText?.text.toString()
        mDeal.price = txtPrice.editText?.text.toString()


        if (mDeal.id == "") {
            mDatabaseReference.push().setValue(mDeal)
        } else {
            mDatabaseReference.child(mDeal.id.toString()).setValue(mDeal)
        }
    }

    private fun deleteDeal() {
        if (mDeal.id == "") {
            toast("Please save the deal before deleting")
        }
        mDatabaseReference.child(mDeal.id.toString()).removeValue()

        if(!mDeal.imageName.isNullOrEmpty()){
            val picRef = FirebaseUtil.mStorage?.reference?.child(mDeal.imageName!!)
            picRef?.delete()?.addOnSuccessListener {
              Log.d("Delete Image", "Image Successfully Deleted")
            }?.addOnFailureListener {
                Log.d("Delete Image", it.message!!)
            }
        }
    }

    private fun backToList() {
        finish()
    }

    fun showMenu() {
        invalidateOptionsMenu()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICTURE_RESULT && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            val ref = FirebaseUtil.mStorageRef?.child(imageUri?.lastPathSegment!!)

            ref?.putFile(imageUri!!)?.addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot?) {
                    val firebaseUri = taskSnapshot?.storage?.downloadUrl
                    val imageName = taskSnapshot?.storage?.path
                    firebaseUri?.addOnSuccessListener {
                        val url = it.toString()
                        mDeal.imageUrl = url
                        mDeal.imageName = imageName
                        showImage(url)
                    }
                }
            })


        }
    }

    fun showImage(url:String?){
        if(!url.isNullOrEmpty()){
            val width:Int = Resources.getSystem().displayMetrics.widthPixels
            Picasso.with(this)
                .load(url)
                .resize(width,width*2/3)
                .centerCrop()
                .into(image)
        }
    }

}

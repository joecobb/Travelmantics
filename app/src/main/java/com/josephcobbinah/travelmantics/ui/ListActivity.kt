package com.josephcobbinah.travelmantics.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.josephcobbinah.travelmantics.R
import com.josephcobbinah.travelmantics.adapters.DealAdapter
import com.josephcobbinah.travelmantics.util.FirebaseUtil
import com.josephcobbinah.travelmantics.util.toast
import kotlinx.android.synthetic.main.activity_list.*


class ListActivity : AppCompatActivity() {

    private lateinit var adapter: DealAdapter
    private lateinit var layoutManager: LinearLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_activity_menu, menu)
        val insertMenu =  menu?.findItem(R.id.insert_menu)
        insertMenu?.isVisible = FirebaseUtil.isAdmin!!
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.insert_menu -> {
                val intent = Intent(this, DealActivity::class.java)
                startActivity(intent)
            }
            R.id.logout_menu -> {
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        toast("User logged out")
//                        adapter.deals?.clear()
//                        adapter.notifyDataSetChanged()
                    }
            }
        }

        return true
    }


    override fun onPause() {
        super.onPause()
        FirebaseUtil.detachListener()
    }

    override fun onResume() {
        super.onResume()
        FirebaseUtil.openFbReference("traveldeals", this)
        adapter = DealAdapter(this)
        layoutManager = LinearLayoutManager(
            this, RecyclerView.VERTICAL, false
        )
        rvDeals.adapter = adapter
        rvDeals.layoutManager = layoutManager
        FirebaseUtil.attachListener()
    }

    fun showMenu(){
        invalidateOptionsMenu()
    }
}

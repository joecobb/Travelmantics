package com.josephcobbinah.travelmantics.adapters

import android.content.Intent
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.josephcobbinah.travelmantics.R
import com.josephcobbinah.travelmantics.models.TravelDeal
import com.josephcobbinah.travelmantics.ui.DealActivity
import com.josephcobbinah.travelmantics.util.FirebaseUtil
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_deal.*
import kotlinx.android.synthetic.main.rv_row.view.*

class DealAdapter(activity:AppCompatActivity) : RecyclerView.Adapter<DealAdapter.DealViewHolder>() {


    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mChildEventListener: ChildEventListener? = null
    var deals: MutableList<TravelDeal>?

    init {
        FirebaseUtil.openFbReference("traveldeals",activity)
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference
        deals = FirebaseUtil.mDeals
        deals?.clear()
        mChildEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val travelDeal = dataSnapshot.getValue(TravelDeal::class.java)
                travelDeal!!.id = dataSnapshot.key
                deals?.add(travelDeal)
                notifyItemInserted(deals!!.size - 1)
                Log.d("RV - Results->", travelDeal.toString())
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }

        }
        mDatabaseReference?.addChildEventListener(mChildEventListener as ChildEventListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealViewHolder {
        val context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.rv_row, parent, false)
        return DealViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return deals!!.size
    }

    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {
        val deal = deals!![position]
        holder.bind(deal)
    }

    inner class DealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        fun bind(deal: TravelDeal) {
            itemView.tvTitle.text = deal.title
            itemView.tvPrice.text = deal.price
            itemView.tvDescription.text = deal.description
            showImage(deal.imageUrl,itemView.imageDeal)
        }

        init {
            itemView.setOnClickListener(this)

        }

        override fun onClick(view: View?) {
            val position = adapterPosition
            Log.d("DealViewHolder->", adapterPosition.toString())
            val selectedDeal = deals!![position]
            val intent = Intent(view!!.context, DealActivity::class.java)
            intent.putExtra("Deal", selectedDeal)
            view.context.startActivity(intent)
        }
    }

    fun showImage(url:String?,imageView: ImageView){
        if(!url.isNullOrEmpty()){

            Picasso.with(imageView.context)
                .load(url)
                .resize(180,180)
                .centerCrop()
                .into(imageView)
        }
    }


}
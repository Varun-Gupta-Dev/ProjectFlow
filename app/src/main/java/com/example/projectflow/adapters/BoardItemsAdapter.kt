package com.example.projectflow.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectflow.R
import com.example.projectflow.models.Board
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// To display the list items inside the recycler view
open class BoardItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Board>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_board, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        val currentDate = Date() // Get the current date and time
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Define your desired date format
        val dateString = formatter.format(currentDate) // Format the date into a string
        if (holder is MyViewHolder) {
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.iv_board_image)

            holder.tv_name.text = model.name
            holder.tv_created_by.text = "Created By: ${model.createdBy}"
            holder.tv_created_date.text = "Created On: $dateString"

            holder.itemView.setOnClickListener{
                if (onClickListener != null){
                    onClickListener!!.onClick(position, model)
                }
            }
        }


    }

    interface OnClickListener{
        fun onClick(position: Int, model: Board)
    }
    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

     class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val iv_board_image: CircleImageView = itemView.findViewById(R.id.iv_board_image)
         val tv_name: TextView = itemView.findViewById(R.id.tv_name)
         val tv_created_by: TextView = itemView.findViewById(R.id.tv_created_by)
         val tv_created_date: TextView = itemView.findViewById(R.id.tv_board_created_date)
    }
}
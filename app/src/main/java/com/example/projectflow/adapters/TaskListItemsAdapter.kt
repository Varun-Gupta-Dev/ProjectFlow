package com.example.projectflow.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectflow.R
import com.example.projectflow.activities.TaskListActivity
import com.example.projectflow.models.Task
import com.google.common.io.Resources
import de.hdodenhof.circleimageview.CircleImageView

open class TaskListItemsAdapter(
    private val context: Context,
    private val list: ArrayList<Task>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =  LayoutInflater.from(context).inflate(
                R.layout.item_task,
                parent,
                false
            )

        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins((15.toDp().toPx()),0,(40.toDp().toPx()), 0)
        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder){
            if(position == list.size-1){
                holder.tv_add_task_list.visibility = View.VISIBLE
                holder.ll_task_item.visibility = View.GONE
            }else{
                holder.tv_add_task_list.visibility = View.GONE
                holder.ll_task_item.visibility = View.VISIBLE
            }
            holder.tv_task_list_title.text = model.title
            holder.tv_add_task_list.setOnClickListener{
                holder.tv_add_task_list.visibility = View.GONE
                holder.cv_add_task_list_name.visibility = View.VISIBLE
            }

            holder.ib_close_list_name.setOnClickListener {
                holder.tv_add_task_list.visibility = View.VISIBLE
                holder.cv_add_task_list_name.visibility = View.GONE
            }

            holder.ib_done_list_name.setOnClickListener {
                val listName = holder.et_task_list_name.text.toString()
                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.createTaskList(listName)
                    }
                }else{
                    Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
                }

            }

        }
    }

    private fun Int.toDp(): Int = (this/android.content.res.Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int = (this*android.content.res.Resources.getSystem().displayMetrics.density).toInt()

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val tv_add_task_list: TextView = itemView.findViewById(R.id.tv_add_task_list)
        val ll_task_item: LinearLayout = itemView.findViewById(R.id.ll_task_item)
        var tv_task_list_title: TextView = itemView.findViewById(R.id.tv_task_list_title)
        val cv_add_task_list_name: CardView = itemView.findViewById(R.id.cv_add_task_list_name)
        val ib_close_list_name: ImageButton = itemView.findViewById(R.id.ib_close_list_name)
        val ib_done_list_name: ImageButton = itemView.findViewById(R.id.ib_done_list_name)
        val et_task_list_name: EditText = itemView.findViewById(R.id.et_task_list_name)

    }
 }
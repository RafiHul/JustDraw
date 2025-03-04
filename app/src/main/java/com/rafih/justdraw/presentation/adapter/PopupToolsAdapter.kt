package com.rafih.justdraw.presentation.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rafih.justdraw.databinding.PopupMenuToolLayoutBinding
import com.rafih.justdraw.util.MainDrawTool

class PopupToolsAdapter(var toolItem: List<Pair<Drawable?, MainDrawTool>>, var actionClick: (MainDrawTool) -> Unit = {}): RecyclerView.Adapter<PopupToolsAdapter.MyViewHolder>() {
    inner class MyViewHolder(val binding: PopupMenuToolLayoutBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(currentItem: Pair<Drawable?, MainDrawTool>){
            val drawable = currentItem.first
            val tool = currentItem.second

            binding.buttonTool.apply {
                setImageDrawable(drawable)
                setOnClickListener{
                    actionClick(tool)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        return MyViewHolder(
            PopupMenuToolLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return toolItem.size
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        val currItem = toolItem[position]
        holder.bind(currItem)
    }
}
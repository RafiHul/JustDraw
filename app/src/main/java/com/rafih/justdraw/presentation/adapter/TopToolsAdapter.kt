package com.rafih.justdraw.presentation.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rafih.justdraw.databinding.PopupMenuToolLayoutBinding
import com.rafih.justdraw.util.TopTool

class TopToolsAdapter(var toolItem: List<Pair<Drawable?, TopTool>>, var actionClick: (Drawable,TopTool) -> Unit): RecyclerView.Adapter<TopToolsAdapter.MyViewHolder>() {
    inner class MyViewHolder(val binding: PopupMenuToolLayoutBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(currentItem: Pair<Drawable?, TopTool>){
            val drawable = currentItem.first
            val tool = currentItem.second

            binding.buttonTool.apply {
                setImageDrawable(drawable)
                setOnClickListener{
                    actionClick(drawable!!, tool)
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

    fun changeToolItemList(nitem: List<Pair<Drawable?, TopTool>>){
        toolItem = nitem
        notifyDataSetChanged()
    }
}
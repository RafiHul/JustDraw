package com.rafih.justdraw.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rafih.justdraw.databinding.RecyclerviewColorPaletteBinding

class ColorPaletteAdapter(val colorPalette: Array<String>, val changeColorAction: (Int) -> Unit): RecyclerView.Adapter<ColorPaletteAdapter.MyViewHolder>() {
    inner class MyViewHolder(val binding: RecyclerviewColorPaletteBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(currentColor: String){

            val colorCode = Color.parseColor(currentColor)
            binding.root.apply {
                setCardBackgroundColor(colorCode)
                setOnClickListener{ changeColorAction(colorCode) }

            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        return MyViewHolder(
            RecyclerviewColorPaletteBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return colorPalette.size
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        holder.bind(colorPalette[position])
    }
}
package com.rafih.justdraw.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rafih.justdraw.databinding.RecyclerviewColorPaletteBinding
import com.rafih.justdraw.util.DefaultDrawToolProperty

class ColorPaletteAdapter(val colorPalette: Array<String>, val changeColorAction: (Int) -> Unit): RecyclerView.Adapter<ColorPaletteAdapter.MyViewHolder>() {
    
    private var positionSelected: Int? = null
    private var oldPositionSelected: Int? = null

    // TODO: bikin kalo misalnya menambahkan warna baru, maka warna tersebut langsung terpakai
    inner class MyViewHolder(val binding: RecyclerviewColorPaletteBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(currentColor: String, position: Int){

            val colorCode = Color.parseColor(currentColor)
            setSelectedColor(0)

            if (colorCode == Color.BLACK && positionSelected == null){ //first selected color / default selected color when open app (black)
                positionSelected = position
                setSelectedColor(DefaultDrawToolProperty.colorPaletteIndicatorSize)
            }

            binding.root.apply {
                setCardBackgroundColor(colorCode)
                setOnClickListener{
                    oldPositionSelected = positionSelected
                    positionSelected = position
                    setSelectedColor(DefaultDrawToolProperty.colorPaletteIndicatorSize)
                    changeColorAction(colorCode)

                    //this to handle 2 times click
                    if(oldPositionSelected != positionSelected) {
                        notifyItemChanged(oldPositionSelected!!)
                    }
                }
            }
        }

        fun setSelectedColor(value: Int){
            binding.root.strokeWidth = value
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
        holder.bind(colorPalette[position], position)
    }
}
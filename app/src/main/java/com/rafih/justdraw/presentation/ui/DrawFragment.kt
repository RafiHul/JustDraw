package com.rafih.justdraw.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.slider.Slider
import com.rafih.justdraw.R
import com.rafih.justdraw.databinding.FragmentDrawBinding
import com.rafih.justdraw.databinding.PopupMenuToolLayoutBinding
import com.rafih.justdraw.presentation.adapter.ColorPaletteAdapter
import com.rafih.justdraw.presentation.view.DrawView
import com.rafih.justdraw.util.DrawTool

class DrawFragment : Fragment(R.layout.fragment_draw) {

    private var _binding : FragmentDrawBinding? = null
    private val binding get() = _binding!!

    private lateinit var popUpMenuTool: PopupWindow
    private lateinit var popUpMenuToolBinding: PopupMenuToolLayoutBinding
    private lateinit var drawView: DrawView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDrawBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drawView = binding.drawView
        val arrColorPallete = resources.getStringArray(R.array.default_color_palettes)

        val colorPaletteAdapter = ColorPaletteAdapter(arrColorPallete){
            drawView.changeColor(it)
        }

        //setupPopupMenuTool
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_menu_tool_layout,null)
        popUpMenuTool = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popUpMenuToolBinding = PopupMenuToolLayoutBinding.bind(popupView)
        setUpToolButton(popUpMenuToolBinding.buttonBrushTool, DrawTool.BRUSH)
        setUpToolButton(popUpMenuToolBinding.buttonEraserTool, DrawTool.ERASER)
        setUpToolButton(popUpMenuToolBinding.buttonFillColor, DrawTool.FILLCOLOR)

        binding.recyclerViewColorPalette.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
            adapter = colorPaletteAdapter
        }

        binding.apply {
            imageViewUndo.setOnClickListener{
                drawView.undo()
            }
            imageViewRedo.setOnClickListener{
                drawView.redo()
            }
            imageViewOpenPopupTool.setOnClickListener{

                if (popUpMenuTool.isShowing){
                    popUpMenuTool.dismiss()
                } else {
                    popUpMenuTool.showAsDropDown(it,0,-it.height * 3)
                }
            }
        }

        binding.sliderSize.value = drawView.currentTool.toolsSize

        binding.sliderSize.addOnSliderTouchListener(object : Slider.OnSliderTouchListener{
            override fun onStartTrackingTouch(slider: Slider) {
            }

            override fun onStopTrackingTouch(slider: Slider) {
            }

        })

        binding.sliderSize.addOnChangeListener(object : Slider.OnChangeListener{
            override fun onValueChange(
                slider: Slider,
                value: Float,
                fromUser: Boolean
            ) {
                drawView.changeToolSize(slider.value)
            }
        })
    }

    fun changeUseToolImage(tool: DrawTool){
        when(tool){
            DrawTool.BRUSH -> binding.imageViewOpenPopupTool.setImageResource(R.drawable.baseline_brush_24)
            DrawTool.ERASER -> binding.imageViewOpenPopupTool.setImageResource(R.drawable.eraser_svgrepo_com)
            DrawTool.FILLCOLOR -> binding.imageViewOpenPopupTool.setImageResource(R.drawable.baseline_format_paint_24)
        }
    }

    fun setUpToolButton(button: View, tool: DrawTool){
        button.setOnClickListener{
            changeUseToolImage(tool)
            val toolsSize = drawView.changeUseTool(tool) //return picked tools size
            binding.sliderSize.value = toolsSize
            popUpMenuTool.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
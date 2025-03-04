package com.rafih.justdraw.presentation.ui

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.slider.Slider
import com.rafih.justdraw.R
import com.rafih.justdraw.databinding.FragmentDrawBinding
import com.rafih.justdraw.presentation.adapter.ColorPaletteAdapter
import com.rafih.justdraw.presentation.adapter.PopupToolsAdapter
import com.rafih.justdraw.presentation.view.DrawView
import com.rafih.justdraw.util.MainDrawTool
import com.rafih.justdraw.util.SecDrawTool

class DrawFragment : Fragment(R.layout.fragment_draw) {

    private var _binding : FragmentDrawBinding? = null
    private val binding get() = _binding!!

    private lateinit var drawView: DrawView
    private lateinit var popupToolsAdapter: PopupToolsAdapter
    private lateinit var recyclerViewPopupTools: RecyclerView

    //map tools
    private lateinit var mainToolItem: List<Pair<Drawable?, MainDrawTool>>

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
        setUpToolItemListMap()
        setUpRecyclerViewPopupTools()
        setUpColorPallete()

        binding.imageButtonFillColor.setOnClickListener{
            drawView.changeSecUseTool(SecDrawTool.FILLCOLOR,binding.imageButtonFillColor, binding.sliderSize ,Color.BLACK)
        }

//        binding.imageButtonShape.setOnClickListener{
//            drawView.changeSecUseTool(SecDrawTool.SHAPE, binding.imageButtonShape, binding.sliderSize ,Color.BLACK)
//        }

        binding.imageViewOpenPopupMainTool.setOnClickListener{
            popupToolsAdapter.toolItem = mainToolItem
            popupToolsAdapter.actionClick = {
                setUpMainToolButton(it)
            }
        }

        binding.apply {
            imageViewUndo.setOnClickListener{
                drawView.undo()
            }
            imageViewRedo.setOnClickListener{
                drawView.redo()
            }
        }

        binding.sliderSize.value = drawView.getCurrentToolSize()

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
                drawView.changeToolSize(slider.value)  // TODO: bugs when tools change, show tools size indicator 
            }
        })
    }

    private fun setUpColorPallete() {
        val arrColorPallete = resources.getStringArray(R.array.default_color_palettes)

        val colorPaletteAdapter = ColorPaletteAdapter(arrColorPallete){
            drawView.changeColor(it)
        }

        binding.recyclerViewColorPalette.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
            adapter = colorPaletteAdapter
        }
    }

    private fun setUpToolItemListMap() {
        mainToolItem = mapOf(
            getDrawableFromContext(R.drawable.baseline_brush_24) to MainDrawTool.BRUSH,
            getDrawableFromContext(R.drawable.eraser_svgrepo_com) to MainDrawTool.ERASER
        ).toList()
    }

    private fun setUpRecyclerViewPopupTools() {
        recyclerViewPopupTools = binding.recyclerViewPopupTools
        popupToolsAdapter = PopupToolsAdapter(mainToolItem){ setUpMainToolButton(it) }

        binding.recyclerViewPopupTools.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = popupToolsAdapter
        }
    }

    fun changeMainUseToolImage(tool: MainDrawTool){
        when(tool){
            MainDrawTool.BRUSH -> binding.imageViewOpenPopupMainTool.setImageResource(R.drawable.baseline_brush_24)
            MainDrawTool.ERASER -> binding.imageViewOpenPopupMainTool.setImageResource(R.drawable.eraser_svgrepo_com)
        }
    }

    fun setUpMainToolButton(tool: MainDrawTool){
        changeMainUseToolImage(tool)
        drawView.changeMainUseTool(tool) //return picked tools size
        binding.sliderSize.value = drawView.getCurrentToolSize() // TODO: bugs when tools change, show tools size indicator
    }

    private fun getDrawableFromContext(resid: Int): Drawable? {
        return ContextCompat.getDrawable(requireContext(), resid)
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
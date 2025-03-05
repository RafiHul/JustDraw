package com.rafih.justdraw.presentation.ui

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.slider.Slider
import com.rafih.justdraw.R
import com.rafih.justdraw.databinding.FragmentDrawBinding
import com.rafih.justdraw.presentation.adapter.ColorPaletteAdapter
import com.rafih.justdraw.presentation.adapter.TopToolsAdapter
import com.rafih.justdraw.presentation.view.DrawView
import com.rafih.justdraw.util.MainDrawTool
import com.rafih.justdraw.util.SecDrawTool
import com.rafih.justdraw.util.ShapeToolType

class DrawFragment : Fragment(R.layout.fragment_draw) {

    private var _binding : FragmentDrawBinding? = null
    private val binding get() = _binding!!

    private lateinit var drawView: DrawView
    private lateinit var topToolsAdapter: TopToolsAdapter

    //map tools
    private lateinit var mainToolItem: List<Pair<Drawable?, MainDrawTool>>
    private lateinit var shapeToolItem: List<Pair<Drawable?, ShapeToolType>>

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
        setUpRecyclerViewTopTools()
        setUpColorPallete()

        binding.imageViewMainTool.setOnClickListener{
            topToolsAdapter.changeToolItemList(mainToolItem)
            topToolsAdapter.actionClick = { toolDrawable, tool ->
                setUpMainToolButton(it as ImageButton, toolDrawable, tool as MainDrawTool)
            }
        }

        binding.imageButtonFillColor.setOnClickListener{
            drawView.changeSecUseTool(SecDrawTool.FILLCOLOR,it as ImageButton, binding.sliderSize, Color.BLACK)
        }

        binding.imageButtonShapeTool.setOnClickListener{
            drawView.changeSecUseTool(SecDrawTool.SHAPE, it as ImageButton, binding.sliderSize, Color.BLACK)
            topToolsAdapter.changeToolItemList(shapeToolItem)
            topToolsAdapter.actionClick = { toolDrawable, tool ->
//                setUpShapeToolButton(it as ImageView,toolDrawable, tool as ShapeToolType)
                // TODO: this for change shape type
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

        shapeToolItem = mapOf(
            getDrawableFromContext(R.drawable.outline_rectangle_24) to ShapeToolType.RECTANGLE,
            getDrawableFromContext(R.drawable.outline_circle_24) to ShapeToolType.CIRCLE
        ).toList()
    }

    private fun setUpRecyclerViewTopTools() {
        topToolsAdapter = TopToolsAdapter(mainToolItem) { toolDrawable, tool ->
            setUpMainToolButton(binding.imageViewMainTool, toolDrawable, tool as MainDrawTool)
        }

        binding.recyclerViewTopTools.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = topToolsAdapter
        }
    }

    fun changeToolImage(imageButton: ImageButton, toolDrawable: Drawable){
        imageButton.setImageDrawable(toolDrawable)
    }

    fun setUpMainToolButton(imageButton: ImageButton, toolDrawable: Drawable, tool: MainDrawTool){
        changeToolImage(imageButton, toolDrawable)
        drawView.changeMainUseTool(tool) //return picked tools size
        binding.sliderSize.value = drawView.getCurrentToolSize() // TODO: bugs when tools change, show tools size indicator
    }

//    fun setUpShapeToolButton(imageButton: ImageButton, toolDrawable: Drawable,tool: ShapeToolType){
//        changeToolImage(imageButton, toolDrawable)
//        drawView.changeSecUseTool()
//        drawView.changeMainUseTool(tool) //return picked tools size
//        binding.sliderSize.value = drawView.getCurrentToolSize()
//    }

    private fun getDrawableFromContext(resid: Int): Drawable? {
        return ContextCompat.getDrawable(requireContext(), resid)
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
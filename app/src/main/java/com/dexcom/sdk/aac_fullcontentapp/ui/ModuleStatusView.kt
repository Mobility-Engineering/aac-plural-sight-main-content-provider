package com.dexcom.sdk.aac_fullcontentapp.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.dexcom.sdk.aac_fullcontentapp.R

/**
 * TODO: document your custom view class.
 */
class ModuleStatusView : View {



    private lateinit var paintFill: Paint
    private lateinit var paintOutline: Paint
    private lateinit var moduleRectangles: Array<Rect>
    private lateinit var textPaint: TextPaint
    private var moduleStatus:Array<Boolean>  = Array<Boolean>(7){true}
    private var maxHorizontalModules: Int = 0
    private  var radius: Float = 0f
    private val outlineWidth:Int = 6
    private val shapeSize:Int = 144
    private val spacing:Int = 30
    private var fillColor: Int = 0
    private var textWidth: Float = 0f
    private var textHeight: Float = 0f
    private var outLineColor:Int = 0

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        if (isInEditMode)
            setUpEditModeValues()
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.ModuleStatusView, defStyle, 0
        )

        a.recycle()

        //setupModuleRectangles()

        outLineColor = Color.BLACK
        paintOutline = Paint(Paint.ANTI_ALIAS_FLAG)
        paintOutline.setStyle(Paint.Style.STROKE)
        paintOutline.setColor(outLineColor)

        fillColor = context.resources.getColor(R.color.fullcontentapp_orange)
        paintFill = Paint(Paint.ANTI_ALIAS_FLAG)
        paintFill.setColor(fillColor)

        radius = (shapeSize.toFloat()  - outlineWidth) / 2
    }


    private fun setUpEditModeValues(){
        var exampleModeValues = Array<Boolean>(7){ false }
        val middle = EDIT_MODE_MODULE_COUNT
        for (i in 0..middle-1){
            exampleModeValues[i] = true
        }
     moduleStatus = exampleModeValues
    }
   private fun setupModuleRectangles(width:Int){

       val availableWidth = width - getPaddingLeft() - getPaddingRight()
       val horizontalModulesThatCanFit = availableWidth / (shapeSize + spacing)
       val maxHorizontalModules = Math.min(horizontalModulesThatCanFit, moduleStatus.size)
       moduleRectangles = Array<Rect>(moduleStatus?.size) {Rect()} //TODO validate
       for (moduleIndex in 0..moduleRectangles.lastIndex)
       {
           val column = moduleIndex % maxHorizontalModules // index needs to restart from 0
           val row = moduleIndex /maxHorizontalModules // when reaching size or multiple, row increments
           val x = getPaddingLeft() + column * (shapeSize + spacing)
           val y = getPaddingTop() + row * (shapeSize + spacing)
           moduleRectangles[moduleIndex] = Rect(x, y, x + shapeSize, y +shapeSize)
       }
   }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        setupModuleRectangles(w)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var desiredWidth = 0
        var desiredHeight = 0

        val specWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableWidth = specWidth - getPaddingLeft() - getPaddingRight()
        val horizontalModulesThatCanFit = availableWidth /(shapeSize + spacing)
        maxHorizontalModules = Math.min(horizontalModulesThatCanFit, moduleStatus.size)

        desiredWidth = (moduleStatus.size * (shapeSize + spacing)) - spacing
        desiredWidth += getPaddingLeft() + getPaddingRight()

        val rows = (moduleStatus.size - 1)/maxHorizontalModules + 1
        desiredHeight = rows * (shapeSize + spacing) - spacing
        desiredHeight += getPaddingTop() + getPaddingBottom()

        val width = resolveSizeAndState(desiredWidth, widthMeasureSpec, 0)
        val height = resolveSizeAndState(desiredHeight, heightMeasureSpec, 0)


        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (moduleIndex in 0..moduleRectangles.lastIndex){
            val x = moduleRectangles[moduleIndex].centerX()
            val y = moduleRectangles[moduleIndex].centerY()
            val xf = x.toFloat()
            val yf = y.toFloat()
            if(moduleStatus[moduleIndex]){
                canvas.drawCircle(xf, yf, radius, paintFill)

                canvas.drawCircle(xf,yf, radius, paintOutline)

            }

        }
        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom
    }

    companion object {
        const val EDIT_MODE_MODULE_COUNT = 7
    }
}
package com.seda.drawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context,attrs: AttributeSet) :View(context,attrs){
//Paint geometri ,metin ve bitmap nasıl cizileceğine ilişkin stil ve renk bilgileri tutar
    private var mDrawingPath :CustomPath? = null
    private var mCanvasBitmap:Bitmap? = null
    private var mDrawPaint:Paint? = null
    private var mCanvasPaint :Paint? = null
    private var mBrushSize:Float = 0.toFloat()
    private  var color = Color.BLACK
//Beyaz tahtatuval
    private var canvas:Canvas? = null

    private val mPaths = ArrayList<CustomPath>()
    init {
        setUpDrawing()
    }

    private fun setUpDrawing() {
        mDrawPaint = Paint()
        mDrawingPath = CustomPath(color,mBrushSize)
        //çizim boyanın ayarlanması
        mDrawPaint!!.color = color
        //stili ayarlama
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND

        //tuvalın rengi

        mCanvasPaint = Paint(Paint.DITHER_FLAG)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mCanvasBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
         canvas= Canvas(mCanvasBitmap!!)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas?.drawBitmap(mCanvasBitmap!!,0f,0f,mCanvasPaint)
for(path in mPaths){
    mDrawPaint!!.strokeWidth = path.brushThickness
    mDrawPaint!!.color = path.color
    canvas?.drawPath(path,mDrawPaint!!)
}

if(mDrawingPath != null){
    //Boyanın kalınlığı
    mDrawPaint!!.strokeWidth = mDrawingPath!!.brushThickness
    mDrawPaint!!.color = mDrawingPath!!.color
    canvas?.drawPath(mDrawingPath!!,mDrawPaint!!)
}

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val touchX = event?.x
        val touchY = event?.y

        when(event?.action){
         MotionEvent.ACTION_DOWN -> {
    //Yolun kalınlığı
    mDrawingPath?.color = color
    mDrawingPath?.brushThickness = mBrushSize
    mDrawingPath?.reset()
             if (touchY != null) {
                 if (touchX != null) {
                     mDrawingPath?.moveTo(touchX,touchY)
                 }
             }

}
            MotionEvent.ACTION_MOVE->{
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawingPath!!.lineTo(touchX,touchY)
                    }
                }

            }
            MotionEvent.ACTION_UP->{
                mPaths.add(mDrawingPath!!)
mDrawingPath = CustomPath(color,mBrushSize)

            }
            else->return false
        }

        invalidate()
        return true
    }

    fun setSizeForBrush(newSize:Float){
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,newSize,resources.displayMetrics)

            mDrawPaint!!.strokeWidth = mBrushSize

    }


    fun setColor(newColor:String){
        color = Color.parseColor(newColor)
        mDrawPaint!! .color = color
    }

    internal inner class CustomPath(var color:Int,var brushThickness:Float): Path() {

    }




}
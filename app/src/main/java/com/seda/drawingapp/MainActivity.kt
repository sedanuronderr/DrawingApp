package com.seda.drawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.seda.drawingapp.databinding.ActivityMainBinding
import com.seda.drawingapp.databinding.DialogBrushSizeBinding

class MainActivity : AppCompatActivity() {
private lateinit var binding : ActivityMainBinding
private var mImageButtonCurrentPaint:ImageButton?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
val paintColors = binding.paintColor
        mImageButtonCurrentPaint =paintColors[1] as ImageButton
         mImageButtonCurrentPaint!!.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallet_pressed))

binding.drawingView.setSizeForBrush(20.toFloat())
        binding.brush.setOnClickListener {
            showBrushSizeChooserDialog()
        }

    }
    private fun showBrushSizeChooserDialog(){
        var brushbinding = DialogBrushSizeBinding.inflate(layoutInflater)

            val brushDialog = Dialog(this)
        brushDialog.setContentView(brushbinding.root)

            brushDialog.setTitle("Brush Size:")

        val smallBtn = brushbinding.ibSmallBrush
    smallBtn.setOnClickListener {
        binding.drawingView.setSizeForBrush(10.toFloat())

        brushDialog.dismiss()




}
        val mediumBtn = brushbinding.ibMediumBrush
        mediumBtn.setOnClickListener {
            binding.drawingView.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }

        val largeBtn = brushbinding.ibLargeBrush
        largeBtn.setOnClickListener {
            binding.drawingView.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }
        brushDialog.show()
    }
}
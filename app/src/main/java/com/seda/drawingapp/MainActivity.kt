package com.seda.drawingapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.seda.drawingapp.databinding.ActivityMainBinding
import com.seda.drawingapp.databinding.DialogBrushSizeBinding

class MainActivity : AppCompatActivity() {
private lateinit var binding : ActivityMainBinding
private var mImageButtonCurrentPaint:ImageButton?=null
    val openGalleryLuncher:ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->

            if(result.resultCode == RESULT_OK && result.data!= null){
                binding.ivBackground.setImageURI(result.data?.data)
            }
        }

   val requestPermission:ActivityResultLauncher<Array<String>> =
       registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
 permissions->
           permissions.entries.forEach {
               val permissionName = it.key
               val isGranted =  it.value

               if(isGranted){
                   Toast.makeText(
                       this,
                       "Permission granted for location",
                       Toast.LENGTH_LONG )
                       .show()
 val pickIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
openGalleryLuncher.launch(pickIntent)
               }
               else{
                   if(permissionName == Manifest.permission.READ_EXTERNAL_STORAGE){
                       Toast.makeText(
                           this,
                           "Permission denied for storage",
                           Toast.LENGTH_LONG
                       )
                           .show()
                   }

               }
           }

       }

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
        binding.ibGallery.setOnClickListener {
            requestStoragePermission()
        }


        binding.ibUndo.setOnClickListener {
            binding.drawingView.onClickUndo()
        }


    }



    private fun showBrushSizeChooserDialog(){
        val brushbinding = DialogBrushSizeBinding.inflate(layoutInflater)

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


    private fun requestStoragePermission(){
        //Android sistemi, kullanıcıya bir açıklama göstermenin gerekli
        // olup olmadığına karar vermemizi kolaylaştıran bir metod sunmuştur:
        // shouldShowRequestPermissionRationale() metodu true dönerse,
        // kullanıcıya daha önceden
        // android standart dialog’u gösterilmiş ve kullanıcı izni onaylamamıştır.
       // Bu sebeple true döndüğü durumlarda önce iznin
        // ne için gerekli olduğunu anlatan bir açıklama gösterilmesi
        // ve kullanıcı bu açıklamayı onaylarsa standart izin dialog’una
        // yönlendirilmesi gerekir. İzin kullanıcıdan ilk defa istenecekse
        // ya da kullanıcı “Never ask again” durumu onaylanmışsa metod false
        // döner.
         if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){

             showRationaleDialog("Kids Drawing App","Kids Drawing App" + "needs to Access Your External Storage")
         }
        else{
            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
         }


    }
    fun paintClicked(view: View){
        if(view !== mImageButtonCurrentPaint){
            val imageButton = view as ImageButton

            val colorTag = imageButton.tag.toString()
            binding.drawingView.setColor(colorTag)

            imageButton!!.setImageDrawable(ContextCompat.getDrawable
                (this,R.drawable.pallet_pressed))

            mImageButtonCurrentPaint!!.setImageDrawable(ContextCompat.getDrawable
                (this,R.drawable.pallet_normal))

    mImageButtonCurrentPaint=view

        }

    }
    private fun showRationaleDialog(
        title: String,
        message: String,
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }
    private fun getBitmapFromView(view: View): Bitmap {

        //Define a bitmap with the same size as the view.
        // CreateBitmap : Returns a mutable bitmap with the specified width and height
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)
        //Get the view's background
        val bgDrawable = view.background
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas)
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE)
        }
        // draw the view on the canvas
        view.draw(canvas)
        //return the bitmap
        return returnedBitmap
    }
}
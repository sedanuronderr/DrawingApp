package com.seda.drawingapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

binding.ibGallery.setOnClickListener {
requestStoragePermission()
}

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

}
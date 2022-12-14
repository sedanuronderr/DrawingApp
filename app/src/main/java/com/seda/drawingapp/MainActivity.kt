package com.seda.drawingapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.Image
import android.media.MediaScannerConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import com.seda.drawingapp.databinding.ActivityMainBinding
import com.seda.drawingapp.databinding.DialogBrushSizeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
private lateinit var binding : ActivityMainBinding
private var mImageButtonCurrentPaint:ImageButton?=null
    var customProgressDialog: Dialog? = null

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
        mImageButtonCurrentPaint = paintColors[1] as ImageButton
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

        binding.ibSave.setOnClickListener {
            showProgressDialog()
            if(isReadStorageAllowed()){
                lifecycleScope.launch{

                    saveBitmapFile(getBitmapFromView(binding.flDrawingViewContainer))
                }
            }
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



   private fun isReadStorageAllowed():Boolean{
          val result = ContextCompat.checkSelfPermission(this,
          Manifest.permission.READ_EXTERNAL_STORAGE)

       return result == PackageManager.PERMISSION_GRANTED
      }

//iznin neden istendi??ini belirten fonksiyon
    private fun requestStoragePermission(){
        //Android sistemi, kullan??c??ya bir a????klama g??stermenin gerekli
        // olup olmad??????na karar vermemizi kolayla??t??ran bir metod sunmu??tur:
        // shouldShowRequestPermissionRationale() metodu true d??nerse,
        // kullan??c??ya daha ??nceden
        // android standart dialog???u g??sterilmi?? ve kullan??c?? izni onaylamam????t??r.
       // Bu sebeple true d??nd?????? durumlarda ??nce iznin
        // ne i??in gerekli oldu??unu anlatan bir a????klama g??sterilmesi
        // ve kullan??c?? bu a????klamay?? onaylarsa standart izin dialog???una
        // y??nlendirilmesi gerekir. ??zin kullan??c??dan ilk defa istenecekse
        // ya da kullan??c?? ???Never ask again??? durumu onaylanm????sa metod false
        // d??ner.
         if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){

             showRationaleDialog("Kids Drawing App","Kids Drawing App" + "needs to Access Your External Storage")
         }
        else{
            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE))
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
        message: String,) {
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

  private suspend fun saveBitmapFile(mBitmap :Bitmap?):String{
      var result =""
      withContext(Dispatchers.IO){
          if (mBitmap != null){
              try {
                  // FileOutputStream s??n??f??,
                  // dosyalara veri (bayt cinsinden) yazmak i??in kullan??labilir.
                  var bytes = ByteArrayOutputStream()
                  mBitmap.compress(Bitmap.CompressFormat.PNG,90,bytes)
//Cache Storage : Ge??ici dosyalar??n tutuldu??u aland??r.
// Uygulama kald??r??ld??????nda bu k??s??mda bulunan dosyalar da silinir.
// K??s??tl?? bir alan oldu??undan buradaki dosyalar?? i??imiz bitince silmeliyiz.
                  val f = File(externalCacheDir?.absoluteFile.toString() + File.separator + "DrawingApp_"+ System.currentTimeMillis() /1000 + ".png")
                 val fo = FileOutputStream(f)
                  fo.write(bytes.toByteArray())
                   fo.close()

                  result = f.absolutePath

                  runOnUiThread {
                      cancelProgressDialog()
                      if(result.isNotEmpty()){
                          Toast.makeText(this@MainActivity,
                              "File saved successfully:$result",
                              Toast.LENGTH_LONG
                          ).show()
                          shareImage(result)
                      }else{
                          Toast.makeText(this@MainActivity,
                              "Something went wrong while saving the file",
                              Toast.LENGTH_LONG
                          ).show()
                      }
                  }
              }catch (e:Exception){
                  result = ""
                  e.printStackTrace()
              }
          }
      }
return  result
  }

    private fun showProgressDialog() {
        customProgressDialog = Dialog(this@MainActivity)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        customProgressDialog?.setContentView(R.layout.dialog_custom_progress)

        //Start the dialog and display it on screen.
        customProgressDialog?.show()
    }
    private fun cancelProgressDialog() {
        if (customProgressDialog != null) {
            customProgressDialog?.dismiss()
            customProgressDialog = null
        }
    }


    private fun shareImage(result:String){
        //medya taray??c?? hizmeti dosyadan meat verileri okuyacak
        //ve dosyay?? medya i??erik sa??lay??c??s??na ekleyecektir
        MediaScannerConnection.scanFile(this, arrayOf(result),null){
          path,uri ->
//Uri (uniform resource identifier) yani nizami kaynak belirteci,
// bir kayna???? ya da
// veriyi isimlendirmek i??in kullan??lan bir standartt??r.
                val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM,uri)
            shareIntent.type = "image/png"
            //Uygulama se??im diyalogu g??sterme
            startActivity(Intent.createChooser(shareIntent,"Share"))

        }

    }

}
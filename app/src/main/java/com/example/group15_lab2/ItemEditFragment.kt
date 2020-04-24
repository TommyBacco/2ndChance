package com.example.group15_lab2

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_item_edit2.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

class ItemEditFragment : Fragment() {

    private var itemID:Int = -1
    private var new_item:Boolean = false
    private var imageByteArray: ByteArray? = null
    private var rotation: Float = 0F
    private val sharedPref: SharedPreferences by lazy { this.activity!!.getPreferences(Context.MODE_PRIVATE) }

    private val REQUEST_IMAGE_CAPTURE = 10
    private val REQUEST_SELECT_GALLERY_PHOTO = 20
    private val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 21

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_item_edit2, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // todo Serve metodo per gestire meglio onSaveInstanceState
        retainInstance = true
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerForContextMenu(activity!!.findViewById(R.id.item_camera_button))
        item_camera_button.setOnClickListener {v -> activity!!.openContextMenu(v)}

        item_rotate_button.setOnClickListener{
            rotation += 90
            if(rotation >= 360) rotation = 0F
            var source = (item_image_edit.drawable as BitmapDrawable).bitmap
            source = rotateImage(source, 90F)
            item_image_edit.setImageBitmap(source)
        }

        if(savedInstanceState == null) {
            itemID = arguments?.getInt("group15.lab2.ITEM_ID") ?: -1
            new_item= arguments?.getBoolean("group15.lab2.NEW_ITEM") ?: false

            if(!new_item){
                val key:String = KEY_ItemDetails + itemID
                val jsonString: String? = sharedPref.getString(key, null)
                if (jsonString != null) {
                    val item: Item = Gson().fromJson(jsonString, Item::class.java)
                    populateTextView(item)
                    populateImageView(rotation)
                }
            }

        } else {
            rotation = savedInstanceState.getFloat("ROTATION_E", 0F)
            showImage(savedInstanceState.getByteArray("IMAGE_E"), rotation)
        }


    }

    private fun populateTextView(item:Item) {
        item_title_edit.setText(item.title)
        item_price_edit.setText(item.price)
        item_category_edit.setText(item.expireDate)
        item_category_edit.setText(item.category)
        item_sub_category_edit.setText(item.subcategory)
        item_location_edit.setText(item.location)
        item_delivery_edit.setText(item.delivery)
        item_description_edit.setText(item.description)
        rotation = item.imageRotation
     }

    private fun populateImageView(rotation: Float){
        try{
            val fileName = FILE_ItemImage + itemID
            val byteArray = context!!.openFileInput(fileName)?.readBytes()
            if(byteArray != null){
                imageByteArray = byteArray
                var imageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                if(rotation != 0F)
                    imageBitmap = rotateImage(imageBitmap,rotation)
                item_image_edit.setImageBitmap(imageBitmap)
            } else
                item_image_edit.setImageResource(R.drawable.item_icon)
        } catch (exc:Exception){
            item_image_edit.setImageResource(R.drawable.item_icon)
        }
    }

    private fun showImage(byteArray: ByteArray?, rotation:Float){
        if(byteArray != null){
            imageByteArray=byteArray
            var imageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            if(rotation != 0F)
                imageBitmap = rotateImage(imageBitmap, rotation)
            item_image_edit.setImageBitmap(imageBitmap)
            item_rotate_button.visibility = View.VISIBLE
        }
        else {
            item_image_edit.setImageResource(R.drawable.item_icon)
            item_rotate_button.visibility = View.INVISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_button -> {
                savePreferences()
                saveImage(imageByteArray)
                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun savePreferences(){
        if(new_item)
            sharedPref.edit().putInt(KEY_ItemLastID,++itemID).apply()

        val item = Item(
            item_title_edit.text.toString(),
            item_price_edit.text.toString(),
            item_expire_date_edit.text.toString(),
            item_category_edit.text.toString(),
            item_sub_category_edit.text.toString(),
            item_location_edit.text.toString(),
            item_delivery_edit.text.toString(),
            item_description_edit.text.toString(),
            rotation,
            itemID)

        val jsonString:String = Gson().toJson(item)

        sharedPref.edit().putString(KEY_ItemDetails+itemID.toString(),jsonString).apply()
    }

    private fun saveImage(byteArray: ByteArray?){
        if(byteArray != null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && this.activity != null)
                checkEnoughFreeMemory(byteArray)
            try{
                val fileName:String = FILE_ItemImage + itemID
                this.activity?.openFileOutput(fileName, Context.MODE_PRIVATE)?.write(byteArray)
            } catch (exc:Exception){
                exc.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkEnoughFreeMemory(byteArray: ByteArray) {
        val NUM_BYTES_NEEDED_FOR_MY_APP =  byteArray.size * 1L

        val storageManager = this.activity?.getSystemService(StorageManager::class.java)
        val appSpecificInternalDirUuid: UUID = storageManager!!.getUuidForPath(this.activity!!.filesDir)
        val availableBytes: Long = storageManager.getAllocatableBytes(appSpecificInternalDirUuid)
        if (availableBytes >= NUM_BYTES_NEEDED_FOR_MY_APP) {
            storageManager.allocateBytes(appSpecificInternalDirUuid, NUM_BYTES_NEEDED_FOR_MY_APP)
        } else  // Display prompt to user, requesting that they choose files to remove.
            Intent().apply { action = StorageManager.ACTION_MANAGE_STORAGE }
    }


    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        menu.setHeaderTitle("Choose a new image")
        activity?.menuInflater?.inflate(R.menu.menu_camera, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            //Thanks to this intent no need to ask explicitly permission for camera
            R.id.menu_camera -> {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)}
                }
                true
            }
            R.id.menu_gallery -> {
                if (ContextCompat.checkSelfPermission(activity!!,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //Ask for permission
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                } else {
                    // Permission has already been granted
                    val photoPickerIntent =  Intent(Intent.ACTION_PICK);
                    photoPickerIntent.type = "image/*";
                    startActivityForResult(photoPickerIntent, REQUEST_SELECT_GALLERY_PHOTO)
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay!
                    val photoPickerIntent =  Intent(Intent.ACTION_PICK);
                    photoPickerIntent.type = "image/*";
                    startActivityForResult(photoPickerIntent, REQUEST_SELECT_GALLERY_PHOTO)

                } else {
                    // permission denied, boo!
                    Toast.makeText(activity!!,"Permission is needed\nto choose a new image!",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setImageByteArray(image: Bitmap, quality:Int) {
        rotation = 0F
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        imageByteArray = stream.toByteArray()
        item_rotate_button.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //New image via camera
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            if(imageBitmap != null) {
                item_image_edit.setImageBitmap(imageBitmap)
                setImageByteArray(imageBitmap,100)
            }
        }

        //New image from gallery
        if (requestCode == REQUEST_SELECT_GALLERY_PHOTO && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                val imageBitmap = handleSamplingBitmap(activity!!, imageUri)
                if(imageBitmap != null) {
                    setImageByteArray(imageBitmap, 50)
                    item_image_edit.setImageBitmap(imageBitmap)
                }
            }
        }
    }

    @Throws(IOException::class)
    fun handleSamplingBitmap(context: Context, selectedImage: Uri): Bitmap? {
        val MAX_HEIGHT = 1024
        val MAX_WIDTH = 1024
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var imageStream: InputStream? = context.contentResolver.openInputStream(selectedImage)
        BitmapFactory.decodeStream(imageStream, null, options)
        imageStream?.close()
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT)
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        imageStream = context.contentResolver.openInputStream(selectedImage)
        var img = BitmapFactory.decodeStream(imageStream, null, options)
        if(img!=null)
            img = rotateImageIfRequired(context, img, selectedImage);
        return img
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) { // Calculate ratios of height and width to requested height and width
            val heightRatio =
                Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio =
                Math.round(width.toFloat() / reqWidth.toFloat())
            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio

            val totalPixels = width * height.toFloat()
            // Anything more than 2x the requested pixels we'll sample down further
            val totalReqPixelsCap = reqWidth * reqHeight * 2.toFloat()
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }
        }
        return inSampleSize
    }

    @Throws(IOException::class)
    private fun rotateImageIfRequired(context: Context, img: Bitmap, selectedImage: Uri): Bitmap? {
        val input = context.contentResolver.openInputStream(selectedImage)
        val ei: ExifInterface
        if(input!=null) {
            ei =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    ExifInterface(input)
                else
                    ExifInterface(selectedImage.path.toString())
            val orientation =
                ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90F)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180F)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270F)
                else -> img
            }
        }
        return null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        with(outState){
            putByteArray("IMAGE_E",imageByteArray)
            putFloat("ROTATION_E", rotation)
        }
    }


}

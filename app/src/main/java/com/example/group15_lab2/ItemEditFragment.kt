package com.example.group15_lab2

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
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
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_item_edit.*
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
    private var saveBundle:Bundle? = null
    private var categoryPosition:Int = -1

    private val REQUEST_IMAGE_CAPTURE = 10
    private val REQUEST_SELECT_GALLERY_PHOTO = 20
    private val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 21

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_item_edit, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState != null)
            saveBundle = savedInstanceState.getBundle("group15.lab2.SAVED_STATE")
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


        //Spinners
        val currency_adapter = ArrayAdapter.createFromResource(activity!!,R.array.Currencies,R.layout.spinner_currency)
        currency_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_currency.adapter = currency_adapter

        val delivery_adapter = ArrayAdapter.createFromResource(activity!!,R.array.Delivery_type,R.layout.spinner_item)
        currency_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_delivery_type.adapter = delivery_adapter

        val category_adapter = ArrayAdapter.createFromResource(activity!!,R.array.Categories,R.layout.spinner_category)
        currency_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_item_category.adapter = category_adapter

        spinner_item_category.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?){}

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?, position: Int, id: Long
            ) {
                setSubCategorySpinner(position,null)
            }
        }


        //PopulateViews

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

        }
        //Date Picker
        val dateSetListener =DatePickerDialog.OnDateSetListener { _ , year,month,day ->
            val myMonth = month +1
                item_expire_date_edit.setText("$day/$myMonth/$year")
            }

        val cal = Calendar.getInstance()
        item_expire_date_edit.setOnClickListener {

            val day = cal.get(Calendar.DAY_OF_MONTH)
            val month = cal.get(Calendar.MONTH)
            val year = cal.get(Calendar.YEAR)

            DatePickerDialog(activity!!,dateSetListener,year,month,day).show()
            }
    }

    private fun populateTextView(item:Item) {
        item_title_edit.setText(item.title)
        item_price_edit.setText(item.price)
        item_expire_date_edit.setText(item.expireDate)
        item_location_edit.setText(item.location)
        item_description_edit.setText(item.description)
        populateCategorySpinners(item.category,item.subcategory)
        rotation = item.imageRotation

        //Currency spinner
            val currencyPosition = resources.getStringArray(R.array.Currencies).indexOf(item.currency)
            spinner_currency.setSelection(currencyPosition)

        //Delivery spinner
        if(item.delivery != null){
            val deliveryPosition = resources.getStringArray(R.array.Currencies).indexOf(item.delivery)
            spinner_delivery_type.setSelection(deliveryPosition)
        }
     }

    private fun populateCategorySpinners(category:String?,subCategory:String?){

        var categoryPosition = 0
        if(category != null)
            categoryPosition = resources.getStringArray(R.array.Categories).indexOf(category)
        spinner_item_category.setSelection(categoryPosition)

        setSubCategorySpinner(categoryPosition,subCategory)

    }


    private fun setSubCategorySpinner(position:Int, subCategory:String?){

        if(position != categoryPosition) {

            categoryPosition = position

            val array = when (position) {
                1 -> R.array.SubcategoryArt
                2 -> R.array.SubcategorySport
                3 -> R.array.SubcategoryBaby
                4 -> R.array.SubcategoryWomen
                5 -> R.array.SubcategoryMen
                6 -> R.array.SubcategoryElectronic
                7 -> R.array.SubcategoryGame
                8 -> R.array.SubcategoryAuto
                else -> R.array.SubcategoryDefault
            }

            val adapter =
                ArrayAdapter.createFromResource(activity!!, array, R.layout.spinner_subcategory)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_item_sub_category.adapter = adapter

            if (subCategory != null){
                val subCategoryPosition = resources.getStringArray(array).indexOf(subCategory)
                spinner_item_sub_category.setSelection(subCategoryPosition)
            }

        }
    }

    private fun populateImageView(rotation: Float){
        try{
            val fileName = FILE_ItemImage + itemID
            val byteArray = activity!!.openFileInput(fileName)?.readBytes()
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
                if(item_title_edit.text.toString().length > 15){
                    Snackbar.make(item_title_edit,"Too many characters in the Title",Snackbar.LENGTH_SHORT)
                        .show()
                } else {
                    savePreferences()
                    saveImage(imageByteArray)
                    findNavController().popBackStack()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun savePreferences(){
        if(new_item)
            sharedPref.edit().putInt(KEY_ItemLastID,++itemID).apply()

        var category:String? = null
        var subCategory:String? = null
        var delivery:String? = null

        val categoryPosition = spinner_item_category.selectedItemPosition
        val subCategoryPosition = spinner_item_sub_category.selectedItemPosition
        val deliveryPosition = spinner_delivery_type.selectedItemPosition

        if(categoryPosition != 0)
            category = spinner_item_category.selectedItem.toString()
        if(subCategoryPosition != 0)
            subCategory = spinner_item_sub_category.selectedItem.toString()
        if(deliveryPosition != 0)
            delivery = spinner_delivery_type.selectedItem.toString()

        val item = Item(
            item_title_edit.text.toString(),
            item_price_edit.text.toString(),
            item_expire_date_edit.text.toString(),
            category,
            subCategory,
            item_location_edit.text.toString(),
            delivery,
            item_description_edit.text.toString(),
            rotation,
            currency = spinner_currency.selectedItem.toString())

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
                    val photoPickerIntent =  Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    photoPickerIntent.type = "image/*"
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
                    val photoPickerIntent =  Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    photoPickerIntent.type = "image/*"
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


    private fun saveState(): Bundle{
        val bundle = Bundle()
        with(bundle){
            putInt("ITEM_ID_E",itemID)
            putBoolean("NEW_ITEM_E",new_item)
            putString("ITEM_TITLE_E",item_title_edit.text.toString())
            putString("ITEM_PRICE_E",item_price_edit.text.toString())
            putString("ITEM_EXPIRE_DATE_E",item_expire_date_edit.text.toString())
            putString("ITEM_LOCATION_E",item_location_edit.text.toString())
            putString("ITEM_DESCRIPTION_E",item_description_edit.text.toString())
            putInt("ITEM_CURRENCY_E",spinner_currency.selectedItemPosition)
            putString("ITEM_CATEGORY_E",spinner_item_category.selectedItem.toString())
            putString("ITEM_SUB_CATEGORY_E",spinner_item_sub_category.selectedItem.toString())
            putInt("ITEM_DELIVERY_E",spinner_delivery_type.selectedItemPosition)
            putByteArray("ITEM_IMAGE_E",imageByteArray)
            putFloat("ITEM_IMAGE_ROTATION_E", rotation)
        }
        return bundle
    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveBundle = saveState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(item_title_edit != null) saveBundle = null
        outState.putBundle("group15.lab2.SAVED_STATE", saveBundle ?: saveState() )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(savedInstanceState != null){
            with(savedInstanceState.getBundle("group15.lab2.SAVED_STATE")){
                itemID=this?.getInt("ITEM_ID_E") ?: -1
                new_item=this?.getBoolean("NEW_ITEM_E") ?: false
                rotation=this?.getFloat("ITEM_IMAGE_ROTATION_E") ?: 0F
                showImage(this?.getByteArray("ITEM_IMAGE_E"),rotation)
                item_title_edit.setText(this?.getString("ITEM_TITLE_E"))
                item_price_edit.setText(this?.getString("ITEM_PRICE_E"))
                item_expire_date_edit.setText(this?.getString("ITEM_EXPIRE_DATE_E"))
                item_location_edit.setText(this?.getString("ITEM_LOCATION_E"))
                item_description_edit.setText(this?.getString("ITEM_DESCRIPTION_E"))
                val category = this?.getString("ITEM_CATEGORY_E")
                val subCategory = this?.getString("ITEM_SUB_CATEGORY_E")
                populateCategorySpinners(category,subCategory)
                spinner_currency.setSelection(this?.getInt("ITEM_CURRENCY_E") ?: 0)
                spinner_delivery_type.setSelection(this?.getInt("ITEM_DELIVERY_E") ?: 0)
            }
        }
    }

}

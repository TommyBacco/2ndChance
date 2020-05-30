package com.example.group15_lab2.ItemEdit

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.android.volley.toolbox.Volley
import com.example.group15_lab2.MainActivity.MainActivity
import com.example.group15_lab2.R
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_item_edit.*
import java.io.File
import java.io.IOException
import java.util.*

class ItemEditFragment : Fragment() {

    private val myViewModel: ItemEditVM by viewModels()
    private lateinit var cameraPhotoPath: String
    private var itemID:String? = null
    private var categoryPosition: Int = -1
    private val noUsers = "No user is interested"

    private val REQUEST_IMAGE_CAPTURE = 10
    private val REQUEST_SELECT_GALLERY_PHOTO = 20
    private val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 21

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_item_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setToolbarTitle("Edit Item")

        registerForContextMenu(requireActivity().findViewById(R.id.item_camera_button))
        item_camera_button.setOnClickListener { v -> requireActivity().openContextMenu(v) }

        item_rotate_button.setOnClickListener {
            myViewModel.rotateImage()
        }

        //Spinners ##############################################################
        val currency_adapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.Currencies,
            R.layout.spinner_currency
        )
        currency_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_currency.adapter = currency_adapter

        val item_status_adapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.ItemStatus,
            R.layout.spinner_item_status
        )
        item_status_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_item_status.adapter = item_status_adapter

        val delivery_adapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.Delivery_type,
            R.layout.spinner_item
        )
        delivery_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_delivery_type.adapter = delivery_adapter

        val category_adapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.Categories,
            R.layout.spinner_category
        )
        category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_item_category.adapter = category_adapter

        spinner_item_category.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                setSubCategorySpinner(position, null)
                var text:String? = null
                if(position != 0)
                    text = spinner_item_category.selectedItem.toString()
                myViewModel.editItemData("category",text)
            }
        }
        spinner_item_sub_category.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                var text:String? = null
                if(position != 0)
                    text = spinner_item_sub_category.selectedItem.toString()
                myViewModel.editItemData("subcategory",text)
            }
        }
        spinner_currency.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                myViewModel.editItemData("currency",spinner_currency.selectedItem.toString())
            }
        }
        spinner_item_status.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val status = spinner_item_status.selectedItem.toString()
                myViewModel.editItemData("status",status)
                setSoldSpinner(status)
            }
        }
        spinner_delivery_type.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                var text:String? = null
                if(position != 0)
                    text = spinner_delivery_type.selectedItem.toString()
                myViewModel.editItemData("deliveryType",text)
            }
        }
        spinner_userToSold.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(spinner_userToSold.selectedItem.toString() != "No user is interested")
                    myViewModel.setUserToSoldItem(position)
            }
        }
        //##############################################################################

        if (savedInstanceState == null) {
            val id = arguments?.getString("group15.lab3.ITEM_ID")
            myViewModel.setItemID(id)
        }

        myViewModel.getItemID().observe(viewLifecycleOwner, Observer {
            itemID = it
        })

        //Date Picker
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val myMonth = month + 1
            val date = "$day/$myMonth/$year"
            myViewModel.editItemData("expireData",date)
            item_expire_date_edit.setText(date)
        }

        val cal = Calendar.getInstance()
        item_expire_date_edit.setOnClickListener {

            val day = cal.get(Calendar.DAY_OF_MONTH)
            val month = cal.get(Calendar.MONTH)
            val year = cal.get(Calendar.YEAR)

            DatePickerDialog(requireActivity(), dateSetListener, year, month, day).show()
        }

        populateItemViews()
        enableEditMode()

        myViewModel.getImage().observe(viewLifecycleOwner, Observer { bitmap ->
            if (!myViewModel.isImageOld()){
                item_image_edit.setImageBitmap(bitmap)
                item_rotate_button.visibility = View.VISIBLE
            }
        })
    }


    private fun populateItemViews() {
        myViewModel.getItemData().observe(viewLifecycleOwner, Observer { item ->
            item_title_edit.setText(item.title)
            item_price_edit.setText(item.price)
            item_expire_date_edit.setText(item.expireDate)
            item_location_edit.setText(item.location)
            item_description_edit.setText(item.description)

            populateCategorySpinners(item.category, item.subcategory)
            populateSoldSpinner(item.soldTo)

            //Currency spinner
            val currencyPosition =
                resources.getStringArray(R.array.Currencies).indexOf(item.currency)
            spinner_currency.setSelection(currencyPosition)

            //Status spinner
            if (item.status != null) {
                val statusPosition =
                    resources.getStringArray(R.array.ItemStatus).indexOf(item.status)
                spinner_item_status.setSelection(statusPosition)
            }

            //Delivery spinner
            if (item.deliveryType != null) {
                val deliveryPosition =
                    resources.getStringArray(R.array.Delivery_type).indexOf(item.deliveryType)
                spinner_delivery_type.setSelection(deliveryPosition)
            }

            if (myViewModel.isImageOld()){
                Picasso.get()
                    .load(item.imageURL.toUri())
                    .fit()
                    .centerInside()
                    .error(R.drawable.user_icon)
                    .into(item_image_edit)

                item_rotate_button.visibility = View.INVISIBLE
            }
        })

    }

    private fun populateCategorySpinners(category: String?, subCategory: String?) {
        var categoryPosition = 0
        if (category != null)
            categoryPosition = resources.getStringArray(R.array.Categories).indexOf(category)
        spinner_item_category.setSelection(categoryPosition)

        setSubCategorySpinner(categoryPosition, subCategory)
    }


    private fun setSubCategorySpinner(position: Int, subCategory: String?) {
        if (position != categoryPosition) {

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
                ArrayAdapter.createFromResource(
                    requireActivity(), array,
                    R.layout.spinner_subcategory
                )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_item_sub_category.adapter = adapter

            if (subCategory != null) {
                val subCategoryPosition = resources.getStringArray(array).indexOf(subCategory)
                spinner_item_sub_category.setSelection(subCategoryPosition)
            }
        }
    }

    private fun setSoldSpinner(status: String) {
        if(status == "Sold"){
            sold_to.visibility = View.VISIBLE
            spinner_userToSold.visibility = View.VISIBLE
        } else {
            sold_to.visibility = View.GONE
            spinner_userToSold.visibility = View.GONE
        }
    }

    private fun populateSoldSpinner(soldTo: String?) {
        myViewModel.getInterestedUsers().observe(viewLifecycleOwner, Observer {usersList ->
            val list = mutableListOf<String>()
            var i = 0
            var spinnerPosition = 0

            if(usersList.isEmpty())
                list.add(noUsers)

            for(user in usersList){
                if(soldTo == user.ID)
                    spinnerPosition = i
                i++
                list.add("$i. ${user.nickname}")

            }

            val userToSoldAdapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                list
            )
            userToSoldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_userToSold.adapter = userToSoldAdapter
            spinner_userToSold.setSelection(spinnerPosition)
        })
    }

    private fun enableEditMode(){
        item_title_edit.doOnTextChanged { text, _, _, _ ->
            myViewModel.editItemData("title",text.toString())
        }
        item_price_edit.doOnTextChanged { text, _, _, _ ->
            myViewModel.editItemData("price",text.toString())
        }
        item_expire_date_edit.doOnTextChanged { text, _, _, _ ->
            myViewModel.editItemData("expireData",text.toString())
        }
        item_location_edit.doOnTextChanged { text, _, _, _ ->
            myViewModel.editItemData("location",text.toString())
        }
        item_description_edit.doOnTextChanged { text, _, _, _ ->
            myViewModel.editItemData("description",text.toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_button -> {
                if (item_title_edit.text.toString().length > 15) {
                    //Title too long, RED Snackbar
                   setSnackbar("longTitle")
                } else {
                   val request = myViewModel.saveData()

                    if(myViewModel.isNewItem())
                        setSnackbar("newItem")
                    else
                        setSnackbar("edited")

                    if(request != null)
                        Volley.newRequestQueue(context).add(request)

                    findNavController().popBackStack()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setSnackbar(tipe:String){
        val text:String
        val backgrount:Int
        when(tipe){
            "edited" -> {
                text = "Item has been correctly updated"
                backgrount = R.color.editedItem
            }
            "newItem" -> {
                text = "New item has been added"
                backgrount = R.color.newItem
            }
           else -> {
                text = "Too many characters in the Title"
                backgrount =  R.color.longTitle
            }
        }
        val snack: Snackbar = Snackbar.make(item_title_edit, text, Snackbar.LENGTH_LONG)
        val tv: TextView = snack.view.findViewById(com.google.android.material.R.id.snackbar_text)
        tv.setTextColor(Color.WHITE)
        tv.typeface = Typeface.DEFAULT_BOLD
        snack.view.setBackgroundColor(ContextCompat.getColor(requireContext(), backgrount))
        snack.show()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menu.setHeaderTitle("Choose a new image")
        activity?.menuInflater?.inflate(R.menu.menu_camera, menu)
    }
    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            //Thanks to this intent no need to ask explicitly permission for camera
            R.id.menu_camera -> {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    // Ensure that there's a camera activity to handle the intent
                    takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                        // Create the File where the photo should go
                        val photoFile: File? = try {
                            createImageFile()
                        } catch (ex: IOException) {
                            Log.d("Error-TAG","Errore durante caricamento immagine")
                            null
                        }
                        // Continue only if the File was successfully created
                        photoFile?.also {
                            val photoURI: Uri = FileProvider.getUriForFile(
                                requireContext(),
                                "com.example.group15_lab2",
                                it
                            )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)

                        }
                    }
                }
                true
            }
            R.id.menu_gallery -> {
                if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    //Ask for permission
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                    )
                } else {
                    // Permission has already been granted
                    var photoPickerIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    photoPickerIntent.type = "image/*"
                    startActivityForResult(photoPickerIntent, REQUEST_SELECT_GALLERY_PHOTO)
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay!
                    var photoPickerIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    photoPickerIntent.type = "image/*"
                    startActivityForResult(photoPickerIntent, REQUEST_SELECT_GALLERY_PHOTO)

                } else {
                    // permission denied, boo!
                    Toast.makeText(
                        requireActivity(), "Permission is needed\nto choose a new image!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //New image via camera
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            if (cameraPhotoPath != null)
                myViewModel.setImage(setPic())
        }

        //New image from gallery
        if (requestCode == REQUEST_SELECT_GALLERY_PHOTO && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null)
                myViewModel.setImage(setPic(imageUri))
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        //val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val timeStamp = "Item_"+itemID
        @Suppress("DEPRECATION")
        //val storageDir:File = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            cameraPhotoPath = absolutePath
        }
    }

    private fun setPic(photoPath: Uri? = null) :Bitmap? {
        // Get the max dimensions
        val targetW: Int = 1024
        val targetH: Int = 1024

        @Suppress("DEPRECATION")
        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }

        var bitmap: Bitmap? =
            if (photoPath == null)
                BitmapFactory.decodeFile(cameraPhotoPath, bmOptions)
            else {
                val imageStream = requireContext().contentResolver.openInputStream(photoPath)
                BitmapFactory.decodeStream(imageStream, null, bmOptions)
            }

        bitmap = rotateImageIfRequired(bitmap, photoPath)
        return bitmap
    }

    @Throws(IOException::class)
    private fun rotateImageIfRequired(img: Bitmap?, selectedImage: Uri?): Bitmap? {
        if (img == null)
            return null
        else {
            var ei: ExifInterface? = null

            if (selectedImage != null) {
                val input = requireContext().contentResolver.openInputStream(selectedImage)
                if (input != null) {
                    ei =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            ExifInterface(input)
                        else
                            ExifInterface(selectedImage.path.toString())
                }
            } else
                ei = ExifInterface(cameraPhotoPath)

            val orientation =
                ei?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                    ?: ExifInterface.ORIENTATION_NORMAL
            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90F)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180F)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270F)
                else -> img
            }
        }
    }

    private fun rotateImage(startBitmap: Bitmap, degree: Float): Bitmap {
        var matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(
            startBitmap, 0, 0, startBitmap.width, startBitmap.height,
            matrix, true
        )
    }

}

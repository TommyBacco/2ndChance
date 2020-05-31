package com.example.group15_lab2.EditProfile

import android.Manifest
import android.app.Activity.RESULT_OK
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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.group15_lab2.*
import com.example.group15_lab2.MainActivity.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import java.io.File
import java.io.IOException

class EditProfileFragment : Fragment() {

    private val REQUEST_IMAGE_CAPTURE = 10
    private val REQUEST_SELECT_GALLERY_PHOTO = 20
    private val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 21

    private val myViewModel: EditProfileVM by viewModels()
    private lateinit var cameraPhotoPath: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setToolbarTitle("Edit Profile")

        user_address_edit.setOnClickListener(){

            findNavController().navigate(R.id.action_nav_editProfile_to_mapFragment)

        }

        registerForContextMenu(requireActivity().findViewById(R.id.camera_button))
        camera_button.setOnClickListener { v -> requireActivity().openContextMenu(v) }

        myViewModel.getUserData().observe(viewLifecycleOwner, Observer { u ->
            user_fullname_edit.setText(u.fullName)
            user_nickname_edit.setText(u.nickname)
            user_email_edit.setText(u.email)
            user_location_edit.setText(u.location)
            user_address_edit.setText(u.address)
            user_telephone_edit.setText(u.telephone)

            if (myViewModel.isImageOld()){
                Picasso.get()
                    .load(u.avatarURL.toUri())
                    .fit()
                    .centerInside()
                    .error(R.drawable.user_icon)
                    .into(user_avatar_edit)

                rotate_button.visibility = View.INVISIBLE
            }
        })

        myViewModel.getImage().observe(viewLifecycleOwner, Observer { bitmap ->
            if (!myViewModel.isImageOld()){
                user_avatar_edit.setImageBitmap(bitmap)
                rotate_button.visibility = View.VISIBLE
            }
        })

        rotate_button.setOnClickListener {
            myViewModel.rotateImage()
        }

        enableEditMode()
    }

    private fun enableEditMode(){
        user_fullname_edit.doOnTextChanged { text, _, _, _ ->
            myViewModel.editData("fullName",text.toString())
        }
        user_nickname_edit.doOnTextChanged { text, _, _, _ ->
            myViewModel.editData("nickname",text.toString())
        }
        user_email_edit.doOnTextChanged { text, _, _, _ ->
            myViewModel.editData("email",text.toString())
        }
        user_location_edit.doOnTextChanged { text, _, _, _ ->
            myViewModel.editData("location",text.toString())
        }

        user_address_edit.doOnTextChanged { text, _, _, _ ->
            myViewModel.editData("address",text.toString())
        }

        user_telephone_edit.doOnTextChanged { text, _, _, _ ->
            myViewModel.editData("telephone",text.toString())
        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_button -> {
                myViewModel.saveData()
                setSnackbar()
                findNavController().popBackStack()
                true
            }


            else -> super.onOptionsItemSelected(item)

        }
    }



    private fun setSnackbar(){
        val snack: Snackbar = Snackbar.make(user_avatar_edit, "Profile has been updated", Snackbar.LENGTH_LONG)
        val tv: TextView = snack.view.findViewById(com.google.android.material.R.id.snackbar_text)
        tv.setTextColor(Color.WHITE)
        tv.typeface = Typeface.DEFAULT_BOLD
        snack.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.editedItem))
        snack.show()
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
        if (requestCode == REQUEST_SELECT_GALLERY_PHOTO && resultCode == RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null)
                myViewModel.setImage(setPic(imageUri))
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        //val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val timeStamp = "Avatar"
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
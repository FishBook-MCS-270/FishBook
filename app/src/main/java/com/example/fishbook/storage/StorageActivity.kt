package com.example.fishbook.storage

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fishbook.databinding.ActivityStorageBinding
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*


class storageActivity : AppCompatActivity() {

    lateinit var binding: ActivityStorageBinding
    lateinit var ImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStorageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.uploadImageBtn.setOnClickListener{

            uploadImage()
        }

        binding.selectImageBtn.setOnClickListener{

            selectImage()
        }



    }

    private fun uploadImage(){

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading File...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

        storageReference.putFile(ImageUri).addOnSuccessListener {

            binding.firebaseImage.setImageURI(null)
            Toast.makeText(this@storageActivity,"Successfully uploaded",Toast.LENGTH_SHORT).show()
            if(progressDialog.isShowing) progressDialog.dismiss()

        }.addOnFailureListener{

            if(progressDialog.isShowing)progressDialog.dismiss()
            Toast.makeText(this@storageActivity,"Failed",Toast.LENGTH_SHORT).show()
        }

    }

    private fun selectImage(){

        val intent = Intent()
        intent.type = "images/"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == RESULT_OK){

            ImageUri = data?.data!!
            binding.firebaseImage.setImageURI(ImageUri)
        }

    }





}
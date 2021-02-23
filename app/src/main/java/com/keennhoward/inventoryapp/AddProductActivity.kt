package com.keennhoward.inventoryapp

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide

import com.keennhoward.inventoryapp.databinding.ActivityAddProductBinding
import com.keennhoward.inventoryapp.model.Product
import java.util.*
import java.util.jar.Manifest

class AddProductActivity : AppCompatActivity() {

    companion object{
        const val TAG = "AddProductActivity"
        const val START_GALLERY_REQUEST = 5
    }

    private lateinit var mainIntent:Intent
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    private var imageUri:String? = null

    private lateinit var binding: ActivityAddProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        val view = binding.root

        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        mainIntent = intent

        var addEditId = 0


        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            binding.tvAddDate.text = "${month+1}/${dayOfMonth}/${year}"
        }



        binding.tvAddDate.text = "${month+1}/${day}/${year}"
        binding.addExpDate.setOnClickListener {
            val dialog = DatePickerDialog(this,android.R.style.ThemeOverlay_Material_Dialog,
            dateSetListener,year,month,day)
            dialog.show()
        }


        if(mainIntent.getParcelableExtra<Product>("edit_product") != null){
            val editProduct = mainIntent.getParcelableExtra<Product>("edit_product")
            Log.d("EDT_PRODUCT", editProduct.toString())
            imageUri = editProduct.image
            addEditId = editProduct.id

            binding.buttonAddProduct.text = "Update"

            binding.etAddName.setText(editProduct.name)
            binding.etAddUnit.setText(editProduct.unit)
            binding.etAddPrice.setText(editProduct.price.toString())
            binding.tvAddDate.text = editProduct.date
            binding.etAddQty.setText(editProduct.qty.toString())

            binding.addImage.setImageURI(Uri.parse(imageUri))

            //Glide.with(applicationContext)
            //    .load(Uri.parse(editProduct.image))
             //   .into(binding.addImage)
        }

        binding.buttonAddProduct.setOnClickListener {
            if(checker()){
                val intent = Intent()
                val product = Product(
                    id = addEditId,
                    name = binding.etAddName.text.toString(),
                    unit = binding.etAddUnit.text.toString(),
                    price = binding.etAddPrice.text.toString().toDouble(),
                    date = binding.tvAddDate.text.toString(),
                    qty = binding.etAddQty.text.toString().toInt(),
                    image = imageUri!!
                )

                if(mainIntent.getParcelableExtra<Product>("edit_product") != null){
                    updateDialog(product)
                }else{
                    intent.putExtra("product", product)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }

        binding.addImage.setOnClickListener {
            openGalleryForImage()
        }

        setContentView(view)
    }

    fun checker(): Boolean{

        binding.etlAddName.error = null
        binding.etlAddUnit.error = null
        binding.etlAddPrice.error = null
        binding.etlAddQty.error = null

        if(binding.etAddName.text.toString().trim().isEmpty()){
            binding.etlAddName.error = getString(R.string.et_empty)
            return false;
        }else if(binding.etAddUnit.text.toString().trim().isEmpty()){
            binding.etlAddUnit.error = getString(R.string.et_empty)
            return false;
        }else if(binding.etAddPrice.text.toString().isEmpty()){
            binding.etlAddPrice.error = getString(R.string.et_empty)
            return false
        }else if(binding.etAddQty.text.toString().isEmpty()){
            binding.etlAddQty.error = getString(R.string.et_empty)
            return false
        }else if (imageUri == null){
            Toast.makeText(this,"Please add an Image",Toast.LENGTH_SHORT).show()
            return false
        }
        else{
            return true
        }
    }

    fun updateDialog(product:Product) {
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage("Do you Want to Update ${product.name}?")
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                Toast.makeText(this, "Updated ${product.name}", Toast.LENGTH_SHORT).show()
                intent.putExtra("product", product)
                setResult(Activity.RESULT_OK, intent)
                finish()

            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            })
        val alert = dialogBuilder.create()
        alert.setTitle("Update")
        alert.show()
    }

    //image
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startActivityForResult(intent, START_GALLERY_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == START_GALLERY_REQUEST){
            Log.d("DEBUG URI", data?.data.toString())
            imageUri = data?.data.toString()
            binding.addImage.setImageURI(data?.data)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
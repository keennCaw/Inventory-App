package com.keennhoward.inventoryapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.keennhoward.inventoryapp.databinding.ActivityMainBinding
import com.keennhoward.inventoryapp.db.InventoryDatabase
import com.keennhoward.inventoryapp.db.MainRepository
import com.keennhoward.inventoryapp.model.Product
import com.keennhoward.inventoryapp.vm.MainViewModel
import com.keennhoward.inventoryapp.vm.MainViewModelFactory
import java.lang.Exception

class MainActivity : AppCompatActivity(), ItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var mainAdapter: MainAdapter

    companion object {
        const val LAUNCH_ADD_PRODUCT_ACTIVITY = 1
        const val LAUNCH_EDIT_PRODUCT_ACTIVITY = 2
        const val EXTERNAL_REQUEST_CODE = 101
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val view = binding.root
        val dao = InventoryDatabase.getInstance(this).productDao()
        val repository = MainRepository(dao)
        val factory =
            MainViewModelFactory(repository)
        mainAdapter = MainAdapter(this, applicationContext)
        mainViewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)


        binding.productsRecyclerview.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = mainAdapter
        }

        mainViewModel.products.observe(this, Observer {
            mainAdapter.submitList(ArrayList(it))
        })

        binding.addProductButton.setOnClickListener {
            val intent: Intent = Intent(this, AddProductActivity::class.java)
            startActivityForResult(intent, LAUNCH_ADD_PRODUCT_ACTIVITY)
        }


        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    deleteDialog(mainAdapter.getUserAt(viewHolder.adapterPosition))
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.productsRecyclerview)


        setupPermissions()

        setContentView(view)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.delete_all_products -> {
                deleteAllDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



        if (requestCode == LAUNCH_ADD_PRODUCT_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getParcelableExtra<Product>("product")
                mainViewModel.insert(product = result)
                Toast.makeText(this, "Added: ${result!!.name}", Toast.LENGTH_LONG).show()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "No Result", Toast.LENGTH_LONG).show()
            }
        }
        if (requestCode == LAUNCH_EDIT_PRODUCT_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getParcelableExtra<Product>("product")
                mainViewModel.update(product = result)
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "No Result", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onProductItemClickListener(product: Product) {
        Toast.makeText(this, "${product.name}", Toast.LENGTH_SHORT).show()
        val intent: Intent = Intent(this, AddProductActivity::class.java)
        intent.putExtra("edit_product", product)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, LAUNCH_EDIT_PRODUCT_ACTIVITY)
    }


    fun deleteDialog(product: Product) {
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage("Do you Want to Delete ${product.name}")
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                mainViewModel.delete(product)
                Toast.makeText(this, "Deleted ${product.name}", Toast.LENGTH_SHORT).show()
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                mainAdapter.notifyDataSetChanged()
            })
        val alert = dialogBuilder.create()
        alert.setTitle("Delete")
        alert.show()
    }

    private fun deleteAllDialog(){
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage("Delete All Items in Inventory?")
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                mainViewModel.deleteAll()
                Toast.makeText(this, "All items Deleted", Toast.LENGTH_SHORT).show()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->

            })

        val alert = dialogBuilder.create()
        alert.setTitle("Delete All")
        alert.show()
    }

    private fun setupPermissions() {
        val permission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("TAG", "Permission Denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), EXTERNAL_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            EXTERNAL_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("PERMISSION", "Permission has been denied by user")
                } else {
                    Log.i("PERMISSION", "Permission has been granted by user")
                }
            }
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
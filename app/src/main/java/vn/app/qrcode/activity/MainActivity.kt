package vn.app.qrcode.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.MAX_IMAGE_DIMENSION
import vn.app.qrcode.data.constant.REQUEST_CODE_CONNECT_WIFI
import vn.app.qrcode.data.constant.REQUEST_CODE_PHOTO_LIBRARY
import vn.app.qrcode.data.constant.WorkflowState
import vn.app.qrcode.databinding.ActivityMainBinding
import vn.app.qrcode.ui.home.camera.CameraViewModel
import vn.app.qrcode.ui.home.camera.mlkit.Utils


class MainActivity : AppCompatActivity() {
    lateinit var viewBinding: ActivityMainBinding

    val viewModel: CameraViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.bottomNavigationView.background = null

        val navView: BottomNavigationView = viewBinding.bottomNavigationView

        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHost.navController

        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.cameraFragment,
//                R.id.productFragment,
                R.id.historyFragment,
//                R.id.settingFragment,
                R.id.studioFragment,
                -> showBottomNav()
                else -> hideBottomNav()
            }
        }

        viewBinding.floatingActionButton.setOnClickListener {
            if (!viewModel.isCameraFragmentActive) {
                navController.popBackStack(R.id.navigation_main, false)
                navController.navigate(R.id.cameraFragment)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK &&
            data != null
        ) {
            when (requestCode) {
                REQUEST_CODE_PHOTO_LIBRARY -> {
                    val inputBitmap = data.data?.let {
                        Utils.loadImage(
                            this, it,
                            MAX_IMAGE_DIMENSION
                        )
                    }
                    val image = InputImage.fromBitmap(inputBitmap!!, 0)
                    val scanner = BarcodeScanning.getClient()

                    scanner.process(image)?.addOnSuccessListener { results ->
                        val barcodeInCenter = results.firstOrNull()

                        if (barcodeInCenter == null) {
                            viewModel.setWorkflowState(WorkflowState.DETECTING)
                            Toast.makeText(this, R.string.can_not_scan, Toast.LENGTH_LONG)
                                .show()
                        } else {
                            viewModel.inputImage = inputBitmap
                            viewModel.setWorkflowState(WorkflowState.DETECTED)
                            viewModel.detectedBarcode.value = barcodeInCenter
                        }
                    }
                        ?.addOnFailureListener {
                            Toast.makeText(this, R.string.can_not_scan, Toast.LENGTH_LONG)
                                .show()
                        }
                }
                REQUEST_CODE_CONNECT_WIFI -> {
                    Toast.makeText(this, R.string.save_this_network, Toast.LENGTH_LONG).show()
                }

                else -> {
                    super.onActivityResult(requestCode, resultCode, data)
                }
            }
        }
    }

    private fun showBottomNav() {
        viewBinding.apply {
            bottomAppBar.visibility = View.VISIBLE
            floatingActionButton.visibility = View.VISIBLE
            setMarginBottom(resources.getDimension(R.dimen.height_bottom_menu).toInt())
        }

    }

    private fun hideBottomNav() {
        viewBinding.apply {
            bottomAppBar.visibility = View.GONE
            floatingActionButton.visibility = View.GONE
            setMarginBottom(0)
        }
    }

    private fun setMarginBottom(bottom: Int) {
        viewBinding.apply {
            val param = fragmentContainerView.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0, 0, 0, bottom)
            fragmentContainerView.layoutParams = param
        }
    }
}

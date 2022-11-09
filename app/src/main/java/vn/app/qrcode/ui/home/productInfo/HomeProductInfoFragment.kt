package vn.app.qrcode.ui.home.productInfo

import android.os.Bundle
import android.view.View
import com.base.common.base.fragment.BaseMVVMFragment
import com.base.common.base.viewmodel.CommonEvent
import kotlinx.android.synthetic.main.layout_header_app_bar.tvTitle
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.app.qrcode.R
import vn.app.qrcode.databinding.FragmentHomeProductinfoBinding
import vn.app.qrcode.ui.home.camera.CameraViewModel

class HomeProductInfoFragment :
    BaseMVVMFragment<CommonEvent, FragmentHomeProductinfoBinding, CameraViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_home_productinfo
    override val viewModel: CameraViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewEvent()
    }

    private fun setupViewEvent() {
        viewDataBinding.apply {
            tvTitle.text=getString(R.string.home_productinfo_title)
        }

    }
}
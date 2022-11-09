package vn.app.qrcode.di.module

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import vn.app.qrcode.activity.MainNavFragmentViewModel
import vn.app.qrcode.ui.detail.QrCodeDetailViewModel
import vn.app.qrcode.ui.history.HistoryViewModel
import vn.app.qrcode.ui.home.camera.CameraViewModel
import vn.app.qrcode.ui.splash.SplashViewModel
import vn.app.qrcode.ui.studio.StudioViewModel
import vn.app.qrcode.ui.studio.contact.CreatorContactViewModel
import vn.app.qrcode.ui.studio.creator.CreatorViewModel
import vn.app.qrcode.ui.studio.email.CreatorEmailViewModel
import vn.app.qrcode.ui.studio.event.EventViewModel
import vn.app.qrcode.ui.studio.message.MessageViewModel
import vn.app.qrcode.ui.studio.namecard.CreatorNameCardViewModel
import vn.app.qrcode.ui.studio.resultcreator.ResultCreatorViewModel
import vn.app.qrcode.ui.studio.text.TextViewModel
import vn.app.qrcode.ui.studio.url.UrlViewModel
import vn.app.qrcode.ui.studio.wifi.CreatorWifiViewModel

val viewModelModule = module {
    viewModel { MainNavFragmentViewModel() }
    viewModel { SplashViewModel() }

    viewModel { StudioViewModel(get()) }
    viewModel { CreatorViewModel() }
    viewModel { HistoryViewModel(get()) }
    viewModel { UrlViewModel() }
    viewModel { TextViewModel() }
    viewModel { MessageViewModel(get()) }
    viewModel { CameraViewModel(get()) }
    viewModel { CreatorWifiViewModel() }
    viewModel { CreatorEmailViewModel(get(), get()) }
    viewModel { CreatorNameCardViewModel() }
    viewModel { CreatorContactViewModel(get()) }

    viewModel { ResultCreatorViewModel(get()) }

    viewModel { EventViewModel(get()) }
    viewModel { QrCodeDetailViewModel(get()) }
}

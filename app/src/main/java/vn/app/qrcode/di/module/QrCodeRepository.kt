package vn.app.qrcode.di.module

import org.koin.dsl.module
import vn.app.qrcode.data.repository.QrCodeRepository

val qrCodeRepositoryModule = module {
    single { QrCodeRepository(get()) }
}


package vn.app.qrcode.di.module

import org.koin.dsl.module
import retrofit2.Retrofit
import vn.app.qrcode.data.remote.AuthenticateRepository
import vn.app.qrcode.data.remote.AuthenticateRepositoryImpl
import vn.app.qrcode.data.remote.AuthenticateSource

val repositoryModule = module {
    factory { AuthenticateRepositoryImpl(get()) as AuthenticateRepository }

    single { createWebService<AuthenticateSource>(get()) }
}


inline fun <reified T> createWebService(retrofit: Retrofit): T {
    return retrofit.create(T::class.java)
}
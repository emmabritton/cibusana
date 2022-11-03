package app.emmabritton.cibusana.data

import app.emmabritton.cibusana.data.network.UserApi
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

const val DI_URL = "url.string"

val dataModule = module {

    single {
        val interceptor: Interceptor = get()
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    single {
        Moshi.Builder()
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(get<String>(named(DI_URL)))
            .client(get())
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .build()
    }

    single {
        val retrofit: Retrofit = get()
        retrofit.create(UserApi::class.java)
    }

    single {
        UserController(get(), get())
    }
}
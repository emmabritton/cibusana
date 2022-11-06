package app.emmabritton.cibusana.network

import app.emmabritton.cibusana.network.apis.DataApi
import app.emmabritton.cibusana.network.apis.FoodApi
import app.emmabritton.cibusana.network.apis.MeApi
import app.emmabritton.cibusana.network.apis.UserApi
import com.squareup.moshi.Moshi
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.*

const val DI_URL = "url.string"
const val DI_CACHE_FILE = "cache_dir.file"

private val internalNetworkModule = module {

    single {
        val file = getOrNull<File>(named(DI_CACHE_FILE))
        val interceptor: Interceptor = get()
        OkHttpClient.Builder()
            .apply {
                if (file != null) {
                    this.cache(Cache(
                        directory = File(get<File>(named(DI_CACHE_FILE)), "http_cache"),
                        maxSize = 50L * 1024L * 1024L // 50 MiB
                    ))
                }
            }
            .addInterceptor(interceptor)
            .build()
    }

    single {
        Moshi.Builder()
            .add(UUID::class.java, UuidAdapter())
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
        val retrofit: Retrofit = get()
        retrofit.create(FoodApi::class.java)
    }

    single {
        val retrofit: Retrofit = get()
        retrofit.create(DataApi::class.java)
    }

    single {
        val retrofit: Retrofit = get()
        retrofit.create(MeApi::class.java)
    }
}

val networkModule = module {
    includes(internalNetworkModule)

    single {
        Api(get(), get(), get(), get(), get())
    }
}
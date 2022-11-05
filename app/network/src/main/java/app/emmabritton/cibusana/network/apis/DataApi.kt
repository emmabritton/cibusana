package app.emmabritton.cibusana.network.apis

import app.emmabritton.cibusana.network.models.ResponseWrapper
import retrofit2.Call
import retrofit2.http.GET

internal interface DataApi {
    @GET("/data/flags")
    fun getFlags(): Call<ResponseWrapper<List<String>>>

    @GET("/data/allergens")
    fun getAllergens(): Call<ResponseWrapper<List<String>>>

    @GET("/data/flavors")
    fun getFlavors(): Call<ResponseWrapper<List<String>>>

    @GET("/data/cuisines")
    fun getCuisines(): Call<ResponseWrapper<List<String>>>

    @GET("/data/meal_times")
    fun getMealTimes(): Call<ResponseWrapper<List<String>>>

    @GET("/data/companies")
    fun getCompanies(): Call<ResponseWrapper<Map<String,List<String>>>>

    @GET("/data/categories")
    fun getCategories(): Call<ResponseWrapper<Map<String,List<String>>>>
}
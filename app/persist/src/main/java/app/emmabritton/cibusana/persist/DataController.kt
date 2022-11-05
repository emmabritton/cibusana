package app.emmabritton.cibusana.persist

import app.emmabritton.cibusana.network.Api
import app.emmabritton.cibusana.network.Logger
import kotlin.reflect.KMutableProperty0

class DataController(private val api: Api, private val logger: Logger) {
    private var categories: Map<String, List<String>> = HashMap()
    private var companies: Map<String, List<String>> = HashMap()
    private var flags = emptyList<String>()
    private var allergens = emptyList<String>()
    private var cuisines = emptyList<String>()
    private var flavors = emptyList<String>()
    private var mealTimes = emptyList<String>()

    fun getCompanies() = getMap(::companies, api::getCompanies)
    fun getCategories() = getMap(::categories, api::getCategories)
    fun getFlags() = getList(::flags, api::getFlags)
    fun getAllergens() = getList(::allergens, api::getAllergens)
    fun getCuisines() = getList(::cuisines, api::getCuisines)
    fun getFlavors() = getList(::flavors, api::getFlavors)
    fun getMealTimes() = getList(::mealTimes, api::getMealTimes)

    private fun getMap(cache: KMutableProperty0<Map<String, List<String>>>, apiMethod: () -> Result<Map<String, List<String>>>): Result<Map<String, List<String>>> {
        val list = cache.get()
        return if (list.isNotEmpty()) {
            Result.success(list)
        } else {
            apiMethod()
                .map {
                    cache.set(it)
                    it
                }
        }
    }

    private fun getList(cache: KMutableProperty0<List<String>>, apiMethod: () -> Result<List<String>>): Result<List<String>> {
        val list = cache.get()
        return if (list.isNotEmpty()) {
            Result.success(list)
        } else {
            apiMethod()
                .onFailure {
                    logger.e(it, "DataController getting ${cache.name}")
                }
                .map {
                    cache.set(it)
                    it
                }
        }
    }
}
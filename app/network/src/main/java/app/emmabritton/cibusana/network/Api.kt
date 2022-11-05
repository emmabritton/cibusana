package app.emmabritton.cibusana.network

import app.emmabritton.cibusana.network.apis.DataApi
import app.emmabritton.cibusana.network.apis.FoodApi
import app.emmabritton.cibusana.network.apis.UserApi
import app.emmabritton.cibusana.network.models.*

class Api internal constructor(
    private val userApi: UserApi,
    private val foodApi: FoodApi,
    private val dataApi: DataApi,
    private val logger: Logger
) {
    fun login(email: String, password: String): Result<LoginResponse> {
        val request = LoginRequest(email, password)

        return executeRequest(logger, userApi.login(request))
    }

    fun register(email: String, password: String, name: String): Result<RegisterResponse> {
        val request = RegisterRequest(email, password, name)

        return executeRequest(logger, userApi.register(request))
    }

    fun searchFood(name: String? = null, page: Int = 0): Result<Pair<Int, List<FoodResponse>>> {
        return executePagedRequest(logger, foodApi.searchFoods(page, name))
    }

    fun getFlags() = executeRequest(logger, dataApi.getFlags())
    fun getCategories() = executeRequest(logger, dataApi.getCategories())
    fun getCuisines() = executeRequest(logger, dataApi.getCuisines())
    fun getCompanies() = executeRequest(logger, dataApi.getCompanies())
    fun getMealTimes() = executeRequest(logger, dataApi.getMealTimes())
    fun getFlavors() = executeRequest(logger, dataApi.getFlavors())
    fun getAllergens() = executeRequest(logger, dataApi.getAllergens())

}
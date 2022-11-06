package app.emmabritton.cibusana.network

import app.emmabritton.cibusana.network.apis.DataApi
import app.emmabritton.cibusana.network.apis.FoodApi
import app.emmabritton.cibusana.network.apis.MeApi
import app.emmabritton.cibusana.network.apis.UserApi
import app.emmabritton.cibusana.network.models.*
import java.time.ZonedDateTime
import java.util.UUID

internal const val HEADER_TOKEN = "x-token"

class Api internal constructor(
    private val userApi: UserApi,
    private val foodApi: FoodApi,
    private val dataApi: DataApi,
    private val meApi: MeApi,
    private val logger: Logger
) {
    fun login(email: String, password: String) =
        executeRequest(logger, userApi.login(LoginRequest(email, password)))

    fun register(email: String, password: String, name: String) =
        executeRequest(logger, userApi.register(RegisterRequest(email, password, name)))

    fun searchFood(name: String? = null, page: Int = 0) =
        executePagedRequest(logger, foodApi.searchFoods(page, name))

    fun getFood(id: UUID) = executeRequest(logger, foodApi.food(id))

    fun getFlags() = executeRequest(logger, dataApi.getFlags())
    fun getCategories() = executeRequest(logger, dataApi.getCategories())
    fun getCuisines() = executeRequest(logger, dataApi.getCuisines())
    fun getCompanies() = executeRequest(logger, dataApi.getCompanies())
    fun getMealTimes() = executeRequest(logger, dataApi.getMealTimes())
    fun getFlavors() = executeRequest(logger, dataApi.getFlavors())
    fun getAllergens() = executeRequest(logger, dataApi.getAllergens())

    fun getWeights(token: UUID, start: ZonedDateTime, end: ZonedDateTime) =
        executeRequest(logger, meApi.weight(token, start, end))

    fun setWeight(token: UUID, kgs: Float, date: ZonedDateTime) =
        executeRequest(logger, meApi.weight(token, WeightRequest(kgs, date)))
}
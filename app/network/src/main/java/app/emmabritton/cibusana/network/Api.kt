package app.emmabritton.cibusana.network

import app.emmabritton.cibusana.network.apis.DataApi
import app.emmabritton.cibusana.network.apis.FoodApi
import app.emmabritton.cibusana.network.apis.MeApi
import app.emmabritton.cibusana.network.apis.UserApi
import app.emmabritton.cibusana.network.models.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

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

    fun register(request: RegisterRequest) =
        executeRequest(logger, userApi.register(request))

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
        executeRequest(logger, meApi.weight(token, start.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), end.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)))
    fun getFirstWeight(token: UUID) = executeOptionalRequest(logger, meApi.firstWeight(token))
    fun getLastWeight(token: UUID) = executeOptionalRequest(logger, meApi.lastWeight(token))
    fun deleteWeight(token: UUID, date: ZonedDateTime) = executeRequest(logger, meApi.weight(token, date))
    fun addWeight(token: UUID, grams: Int, date: ZonedDateTime) =
        executeRequest(logger, meApi.weight(token, WeightRequest(grams, date)))

    fun getFirstEntry(token: UUID) = executeOptionalRequest(logger, meApi.firstEntry(token))
    fun getLastEntry(token: UUID) = executeOptionalRequest(logger, meApi.lastEntry(token))
    fun deleteEntry(token: UUID, id: Long) = executeRequest(logger, meApi.entry(token, id))
    fun getEntries(token: UUID, start: ZonedDateTime, end: ZonedDateTime) =
        executeRequest(logger, meApi.entry(token, start.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), end.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)))
    fun addEntry(token: UUID, request: MealEntryRequest) =
        executeRequest(logger, meApi.entry(token, request))

    fun addMeasurement(token: UUID, request: MeasurementRequest) =
        executeRequest(logger, meApi.measurement(token, request))
    fun getFirstMeasurement(token: UUID) = executeOptionalRequest(logger, meApi.firstMeasurement(token))
    fun getLastMeasurement(token: UUID) = executeOptionalRequest(logger, meApi.lastMeasurement(token))
    fun deleteMeasurement(token: UUID, id: ZonedDateTime) = executeRequest(logger, meApi.measurement(token, id))
    fun getMeasurements(token: UUID, start: ZonedDateTime, end: ZonedDateTime) =
        executeRequest(logger, meApi.measurement(token, start.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), end.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)))
}
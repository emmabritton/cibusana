package app.emmabritton.cibusana

import app.emmabritton.cibusana.data.network.BadRequestException

fun Throwable.errorCodes(): List<Int> {
    return if (this is BadRequestException) {
        this.codes
    } else {
        emptyList()
    }
}
package app.emmabritton.cibusana.network.exceptions

class BadRequestException(val codes: List<Int>, message: String) : Exception("Server rejected with $message")
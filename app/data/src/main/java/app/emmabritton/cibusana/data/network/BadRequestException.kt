package app.emmabritton.cibusana.data.network

class BadRequestException(val codes: List<Int>, message: String) : Exception("Server rejected with $message")
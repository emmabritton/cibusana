package app.emmabritton.cibusana.network.network

class BadRequestException(val codes: List<Int>, message: String) : Exception("Server rejected with $message")
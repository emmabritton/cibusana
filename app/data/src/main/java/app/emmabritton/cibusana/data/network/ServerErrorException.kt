package app.emmabritton.cibusana.data.network

class ServerErrorException(val code: Int) : Exception("Server returned error $code")
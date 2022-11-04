package app.emmabritton.cibusana.data.network

class ServerErrorException(code: Int) : Exception("Server returned error $code")
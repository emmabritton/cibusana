package app.emmabritton.cibusana.network.network

class ServerErrorException(code: Int) : Exception("Server returned error $code")
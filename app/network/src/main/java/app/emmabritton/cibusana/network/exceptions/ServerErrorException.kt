package app.emmabritton.cibusana.network.exceptions

class ServerErrorException(code: Int) : Exception("Server returned error $code")
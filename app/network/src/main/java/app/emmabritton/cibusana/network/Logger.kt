package app.emmabritton.cibusana.network

interface Logger {
    fun d(msg: String)
    fun e(msg: String)
    fun e(exception: Exception, msg: String)
}
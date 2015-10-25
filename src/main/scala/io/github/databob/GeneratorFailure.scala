package io.github.databob

case class GeneratorFailure(msg: String, e: Throwable = null) extends Exception(msg, e)

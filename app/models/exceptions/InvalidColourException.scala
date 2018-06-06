package models.exceptions

class InvalidColourException(key: String) extends Exception(s"Colour: $key is not a valid star colour!")

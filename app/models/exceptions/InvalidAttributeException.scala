package models.exceptions

class InvalidAttributeException(key: String) extends Exception(s"Attribute: $key not found!")

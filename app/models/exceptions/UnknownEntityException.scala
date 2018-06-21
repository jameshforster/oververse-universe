package models.exceptions

class UnknownEntityException(key: String) extends Exception(s"$key is not a valid entity type!")

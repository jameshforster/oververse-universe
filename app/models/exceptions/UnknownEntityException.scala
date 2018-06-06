package models.exceptions

class UnknownEntityException(key: String) extends Exception {
  val message = s"$key is not a valid entity type!"
}

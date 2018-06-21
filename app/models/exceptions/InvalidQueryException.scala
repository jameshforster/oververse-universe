package models.exceptions

class InvalidQueryException(select: String) extends Exception(s"Missing data or Invalid Select for $select")

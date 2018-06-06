package services

import com.google.inject.Inject

import scala.util.Random

class RandomService @Inject()() {

  def generateRandomInteger(max: Int, min: Int = 0): Int = {
    Random.nextInt(1 + max - min) + min
  }

  def selectRandomElement[A](seq: Seq[A]): Option[A] = {
    if (seq.nonEmpty) Some(seq(generateRandomInteger(seq.size - 1)))
    else None
  }
}

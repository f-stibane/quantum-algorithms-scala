package qa.dsl

import qa.Test
import qa.dsl.ApproximateStateMatcher.approximatelyEqual

import scala.collection.immutable.{Vector => StdVector}
import scala.util.Random

class StateTest extends Test {

  "The simplest SingularState" should "be constructed by multiplying it with a Double" in {
    val simplestState = 0.5 * State(0)
    simplestState should approximatelyEqual (SingularState(0.5, StdVector(0)))
  }

  "Adding a SingularState with the same Qubits" should "add coefficients" in {
    val s1 = 0.1 * State(0)
    val s2 = 0.2 * State(0)
    s1 + s2 should approximatelyEqual (0.3 * State(0))
  }

  "Adding a State with another Qubit" should "build a CompositeState" in {
    val s1 = State(0)
    val s2 = State(1)

    s1 + s2 should approximatelyEqual (SuperposedState(s1, s2))
  }

  "Adding States with different numbers of Qubits" should "not work" in {
    intercept[IllegalArgumentException] {
      State(0, 1, 1, 0) + State(1, 0)
    }
  }

  "A State's Qubit" must "be either one or Zero" in {
    intercept[IllegalArgumentException] {
      State(-1)
    }
    intercept[IllegalArgumentException] {
      State(2)
    }
    intercept[IllegalArgumentException] {
      State(5)
    }
  }

  "Tensoring two SingularStates" should "produce a new SingularState with more Qubits" in {
    SingularState(1, StdVector(0)) ⊗ SingularState(1, StdVector(1)) should approximatelyEqual(SingularState(1, StdVector(0, 1)))
    // TODO: Check coefficients
  }

  //TODO: Property-based test
  "Measuring on a SingularState" should "return the state itself" in {
    val orig = State(1)
    val (measured, remainingState) = orig.measureSingleQubit(0)
    measured shouldEqual 1
    remainingState should approximatelyEqual(orig)
  }

  "Measuring 0 on a SuperposedState" should "only select the 0-states and normalize coefficients" in {
    implicit val random0_3 = new Random() {
      override def nextDouble(): Double = 0.3
    }

    val orig = (1 / math.sqrt(2)) * (State(0) + State(1))
    val (measured, remainingState) = orig.measureSingleQubit(0)
    measured shouldEqual 0
    remainingState should approximatelyEqual(State(0))
  }

  "|0> ⊗ |1>" should "equal |0, 1>" in {
    State(0) ⊗ State(1) should approximatelyEqual(State(0, 1))
  }

  "A more complex tensor product" should "also work" in {
    val a = 0.4 * State(1, 0)
    val b = 0.5 * State(0, 1)

    a ⊗ b should approximatelyEqual(0.2 * State(1, 0, 0, 1))
  }

  // TODO: Unallowed operations (different sizes etc.)

}

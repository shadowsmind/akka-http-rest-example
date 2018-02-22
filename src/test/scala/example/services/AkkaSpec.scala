package example.services

import akka.actor.ActorSystem
import akka.testkit.{ ImplicitSender, TestKit }
import org.scalatest._

class AkkaSpec extends TestKit(ActorSystem("ExampleTestSystem")) with WordSpecLike with ImplicitSender with Matchers with BeforeAndAfterAll {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

}

class AkkaAsyncSpec extends TestKit(ActorSystem("ExampleTestSystem")) with AsyncWordSpecLike with ImplicitSender with Matchers with BeforeAndAfterAll {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

}

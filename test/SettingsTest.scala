import play.api.test.FakeRequest
import play.api.test.Helpers._
import org.specs2.mutable._
import play.api.test.WithApplication

class SettingsTest extends Specification {
  "controllers.Settings" should {
    "respond /legalMoves requests" in new WithApplication {
      val result = controllers.Settings.getLegalMovesAsString("q3kb1r/1b1n1ppp/4pn2/1pp5/3P4/5NP1/1PQ1PPBP/1NB1K2R w Kkq - 0 12")
      println(result.get)
//      result must equalTo(OK)
//      contentType(result) must beSome("application/xml")
//      contentAsString(result) must contain("products")
    }
  }
}

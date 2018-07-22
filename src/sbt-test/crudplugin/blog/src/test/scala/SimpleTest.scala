import org.scalatest._
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.WSClient
import play.api.test.Helpers._
import play.api.libs.json._
import org.scalatest.Matchers._
import play.Configuration
import play.api.Play



class SimpleTest extends PlaySpec with GuiceOneServerPerSuite with BeforeAndAfter {

  val myPublicAddress = s"localhost:$port"

  before {

    val wsClient = app.injector.instanceOf[WSClient]
    val baseUrl = s"http://$myPublicAddress/tables/blog"
    val response = await(wsClient.url(baseUrl).get())
    val json: JsArray = Json.parse(response.body).as[JsArray]
    for (j <- json.value) {
      val blog_id = j \ "blog_id"
      val deleteUrl = s"$baseUrl/${blog_id.get}"
      val response = await(wsClient.url(deleteUrl).delete())
      assert(response.status == OK)
    }
  }


  "The simple get to index page trait" must {
    "test server logic" in {
      val wsClient = app.injector.instanceOf[WSClient]
      val myPublicAddress = s"localhost:$port"
      val testPaymentGatewayURL = s"http://$myPublicAddress"
      val response = await(wsClient.url(testPaymentGatewayURL).get())
      response.status mustBe (OK)
    }
  }

  def withCreateEntry(doTest: => Unit) {
    val wsClient = app.injector.instanceOf[WSClient]
    val baseUrl = s"http://$myPublicAddress/tables/blog"
    val data = Json.obj(
      "blog_user_id" -> 1,
      "blog_user_handle"->"clash",
      "blog_created"->"2011-01-01 10:25:00",
      "blog_text"->"clash"
    )
    val response = await(wsClient.url(baseUrl).post(data))
    doTest
  }

    "that a simple post" must {
      "post an entry" in {
        val wsClient = app.injector.instanceOf[WSClient]
        val baseUrl = s"http://$myPublicAddress/tables/blog"
        val data = Json.obj(
          "blog_user_id" -> 1,
          "blog_user_handle"->"clash",
          "blog_created"->"2011-01-01 10:25:00",
          "blog_text"->"clash"
        )
        val response = await(wsClient.url(baseUrl).post(data))
        response.status mustBe (OK)
        println(" GOT BACK " + response.body)

      }
    }

    "that a simple,get " must {
      "get an entry "  in withCreateEntry{
        val wsClient = app.injector.instanceOf[WSClient]
        val baseUrl = s"http://$myPublicAddress/tables/blog"
        val responseGet = await(wsClient.url(baseUrl).get())
        responseGet.status mustBe (OK)
        val json: JsArray = Json.parse(responseGet.body).as[JsArray]
        for (j <- json.value) {
          (j \ "blog_text").as[String] should equal ("clash")
        }
      }

    }

    "that a simple,get for a single entry " must {
      "get one entry "  in withCreateEntry{
        val wsClient = app.injector.instanceOf[WSClient]
        val baseUrl = s"http://$myPublicAddress/tables/blog/1"
        val responseGet = await(wsClient.url(baseUrl).get())
        responseGet.status mustBe (OK)
        println(" GOT BACK " + responseGet.body)
        val json = Json.parse(responseGet.body).as[JsObject]
        (json \ "blog_text").as[String] should equal ("clash")
        (json \ "blog_id").as[Int] should equal (1)
      }

    }

    "that a simple update" must {
      "get an entry "  in withCreateEntry{
        val wsClient = app.injector.instanceOf[WSClient]
        val baseUrl = s"http://$myPublicAddress/tables/blog"
        val responseGet = await(wsClient.url(baseUrl).get())
        responseGet.status mustBe ( OK)

        val json = Json.parse(responseGet.body).as[JsArray].value
        val row = json(0)
        val updated = row.as[JsObject]  + ("blog_text" -> JsString("HELLO"))


        val updateResponse = await(wsClient.url(baseUrl).post(updated))
        updateResponse.status mustBe (OK)
        println(" GOT BACK " + updateResponse.body)

      }
    }
    "the a simple delete works" must {
      "delete it" in withCreateEntry{
        val wsClient = app.injector.instanceOf[WSClient]
        val baseUrl = s"http://$myPublicAddress/tables/blog"
        val responseDelete = await(wsClient.url(baseUrl+"/1").delete())
        responseDelete.status mustBe (OK)
        println("DELETED OK")
      }
    }
}

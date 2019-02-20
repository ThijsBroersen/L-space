package lspace.services

import com.twitter.finagle.http.Status
import io.finch.Input
import lspace.services.rest.endpoints.LabeledNodeApi
import org.scalatest.{AsyncWordSpec, Matchers}

object LServiceSpec {}
trait LServiceSpec extends AsyncWordSpec with Matchers {

  implicit class WithApiService(labeledNodeApi: LabeledNodeApi)(implicit service: LService) {
    import util._
    def labeledApiTests = {
      val label = labeledNodeApi.ontology.label("en").toLowerCase()
      s"have an $label-api" in {
        val input = Input
          .get(s"/$label/")
          .withHeaders("Accept" -> "application/ld+json")
        val res = service.service(input.request)

        res.map { response =>
          val headers = response.headerMap
          response.status shouldBe Status.Ok
          response.contentType shouldBe Some("application/ld+json")
        }
      }
    }
  }
}
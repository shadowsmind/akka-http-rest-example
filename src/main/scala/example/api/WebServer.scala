package example.api

import akka.http.scaladsl.server.{ HttpApp, MethodRejection, RejectionHandler, Route }
import example.api.protocol.{ ApiJsonProtocol, ApiResponse }

class WebServer(route: Route) extends HttpApp {

  import ApiJsonProtocol._

  val rejectionsHandler: RejectionHandler = RejectionHandler.newBuilder()
    .handleAll[MethodRejection] { _ ⇒
      complete(ApiResponse.BadRequest)
    }
    .result()

  override protected def routes: Route = handleRejections(rejectionsHandler) {
    route
  }

}

object WebServer {

  def apply(route: Route): WebServer = new WebServer(route)

}

package example.api

import example.api.directives.BaseDirectives

package object routers extends BaseDirectives {

  type Route = akka.http.scaladsl.server.Route

}

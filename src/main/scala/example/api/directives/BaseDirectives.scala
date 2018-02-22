package example.api.directives

trait BaseDirectives extends akka.http.scaladsl.server.Directives
  with CommonDirectives
  with ValidationDirectives
  with AuthDirectives

object BaseDirectives extends BaseDirectives

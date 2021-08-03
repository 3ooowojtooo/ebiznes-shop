package controllers.view

import javax.inject.{Inject, Singleton}
import play.api.mvc.{MessagesControllerComponents, _}

@Singleton
class IndexViewController @Inject()(cc: MessagesControllerComponents) extends MessagesAbstractController(cc) {

  def index: Action[AnyContent] = Action {
    Ok(views.html.index())
  }

}
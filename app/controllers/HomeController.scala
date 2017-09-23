package controllers

import javax.inject._

import models.Place
import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
/*Controller to handle the actions*/
/*action: DefaultActionBuilder,
parse: PlayBodyParsers,
messagesApi: MessagesApi*/
@Singleton
class HomeController @Inject() (val controllerComponents: ControllerComponents) extends BaseController {

  //val places2: List[Place] = Place(1, "Robledo") :: Place(2, "Medellin") :: Place(3, "Barbosa") :: Nil

  var places = List(
    Place(1, "Robledo", None),
    Place(2, "Medellín", Some("Beautiful city")),
    Place(3, "Barbosa", Some("So hot"))
  )


  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */

  def index = Action {
    Ok(views.html.index())
  }

  /**
    * Action to return all places
    */
  def listPlaces = Action {
    val jsonPlaces = Json.toJson(places)
    Ok(jsonPlaces)
  }

  /**
    * Action to return a specific place given its id
    * @param id
    */
  def getPlace(id:Int) = Action {
    val specificPlace = places.filter(_.id == id)
    val jsonPlace = Json.toJson(specificPlace)
    Ok(jsonPlace)
  }

  /**
    * Action to save a place
    * @return
    */
  def addPlace() = Action { implicit request =>
    val bodyAsJson = request.body.asJson.get

    bodyAsJson.validate[Place].fold(

      /*Successful*/
      valid =  response => {
        /*Validate that the id does not exist*/
        val message: Option[String] =
        {
          places.find(_.id == response.id) match {
            case Some(q) => Option("The place you want to enter already exists")
            case None => places = places :+ response
              Option("Successful registration")
          }
        }

        /* Json response*/
        Ok(Json.toJson(
          Map("message" -> message)
        )) }
        ,
      /*Error*/
      invalid = error => BadRequest(Json.toJson(
        Map("error" -> "Bad Parameters", "description" -> "Missing a parameter"))))
  }

  /**
    * Action to remove a specific place given its id
    * @param id
    * @return
    */
  def removePlace(id:Int) = Action {
    places = places.filterNot(_.id == id)
    Ok(Json.toJson(
      Map("message" -> "Borrado exitoso")
    ))
  }

  /**
    *
    * @return
    */
  def updatePlace() = Action { implicit request =>
    val bodyAsJson = request.body.asJson.get

    bodyAsJson.validate[Place].fold(
      valid = response => {
        var newPlace = Place(response.id, response.name, response.description)
        places = places.map(x => if (x.id == response.id) newPlace else x)
        Ok(Json.toJson(
          Map("message" -> "Successful update")
        ))
      },
      invalid = error => BadRequest(Json.toJson(
        Map("error" -> "Could not update", "description" -> "Bad parameters"))))
  }
}





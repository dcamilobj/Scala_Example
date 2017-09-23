package controllers

import javax.inject._

import models.Place
import play.api.mvc._
import play.api.libs.json._
import play.api.db._

import scala.collection.mutable.ListBuffer


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
/*Controller to handle the actions*/
/*action: DefaultActionBuilder,
parse: PlayBodyParsers,
messagesApi: MessagesApi*/
@Singleton
class HomeController @Inject() (cc: ControllerComponents, db: Database) extends AbstractController(cc) {


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
    val placesL = listPlacesDB()
    val jsonPlaces = Json.toJson(placesL)
    Ok(jsonPlaces)
  }

  def listPlacesDB() : List[Place] = {
    var placesList = ListBuffer[Place]()
    db.withConnection {
      conn => {
        try {
          val statement = conn.createStatement
          val query = "SELECT * from place"
          val resultSet = statement.executeQuery(query)
          while(resultSet.next()) {
            val id = resultSet.getInt("id")
            val name = resultSet.getString("name")
            val description = resultSet.getString("description")
            val newPlace = new Place(id, name, Some(description))
            placesList += newPlace
          }
        }
      }
    }
    return placesList.toList
  }

  /**
    * Action to return a specific place given its id
    * @param id
    */
  def getPlace(id:Int) = Action {
    val specificPlace = getPlaceDB(id)
    var jsonPlace = Json.toJson("Error: Not Found")
    if(specificPlace != null) {
      jsonPlace = Json.toJson(specificPlace)
    }
    Ok(jsonPlace)
  }

  def getPlaceDB(id:Int) :Place = {
    var specificPlace : Place = null
    db.withConnection {
      conn=>{
        try{
          val statement = conn.createStatement()
          val getStatement = s"SELECT * FROM place WHERE id=$id"
          val result = statement.executeQuery(getStatement)
          while(result.next())
            {
              val id = result.getInt("id")
              val name = result.getString("name")
              val description = result.getString("description")
              specificPlace = new Place(id, name, Some(description))
            }
        }
      }
    }
   return specificPlace
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
          val places = listPlacesDB()
          places.find(_.id == response.id) match {
            case Some(q) => Option("The place you want to enter already exists")
            //case None => places = places :+ response
            case None => addPlaceDB(response);
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
        Map("error" -> "Bad Parameters", "description" -> "Missing a parameter")))
    )
  }

  def addPlaceDB(place: Place) = {
    db.withConnection {
      conn => {
        try {
          val statement = conn.createStatement
          val insertStatement = s"INSERT INTO place VALUES(${place.id}, '${place.name}', '${place.name}');"
          statement.execute(insertStatement)
        }
      }
    }
  }

  /**
    * Action to remove a specific place given its id
    * @param id
    * @return
    */
  def removePlace(id:Int) = Action {
    //places = places.filterNot(_.id == id)
    removePlaceDB(id)
    Ok(Json.toJson(
      Map("message" -> "Borrado exitoso")
    ))
  }

  def removePlaceDB(id: Int) = {
    db.withConnection {
      conn  => {
        try {
          val statement = conn.createStatement
          val deleteStatement = s"DELETE FROM place WHERE id = $id;"
          statement.execute(deleteStatement)
        }
      }
    }
  }

  /**
    *
    * @return
    */
  def updatePlace() = Action { implicit request =>
    val bodyAsJson = request.body.asJson.get

    bodyAsJson.validate[Place].fold(
      valid = response => {
        //var newPlace = Place(response.id, response.name, response.description)
        //places = places.map(x => if (x.id == response.id) newPlace else x)
        updatePlaceDB(response)
        Ok(Json.toJson(
          Map("message" -> "Successful update")
        ))
      },
      invalid = error => BadRequest(Json.toJson(
        Map("error" -> "Could not update", "description" -> "Bad parameters"))))
  }

  /**
    *
    * @param place
    * @return
    */
  def updatePlaceDB(place:Place) = {
    db.withConnection {
      conn => {
        try{
          val statement = conn.createStatement
          val updateStatement = s"UPDATE place SET id=${place.id}, name= '${place.name}', description ='${place.description}'" +
            s"WHERE id=${place.id};"
          statement.execute(updateStatement)
        }
      }
    }
  }
}





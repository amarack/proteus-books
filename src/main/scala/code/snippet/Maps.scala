/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package code
package snippet

// TODO: CONVERT

import net.liftweb.common.Logger
import net.liftweb.http.js.JE.JsArray
import net.liftweb.http.js.JE.Num
import net.liftweb.http.js.JE.Str
import net.liftweb.http.js.JsCmds
import net.liftweb.http.js.JsCmds._

import net.liftweb.http.js.JsExp
//import org.galagosearch.core.tools.Search._
import net.liftweb._
import code.comet.TheCart
import http._
import scala.collection.mutable.ListBuffer
import util._
import Helpers._
import scala.collection.JavaConversions._
import akka.dispatch.Future

class Maps extends Logger {

  var idList:ListBuffer[JsArray] = new ListBuffer[JsArray]
// "JavaScript-producing" scala code goes here
  def render = {
    info("SESSION ID: " + S.session.openOr("NONE") + ", RENDER MAP")
    val book = S.param("book").openOr("no book in parameters")
    var arrOfLocs:ListBuffer[JsArray] = new ListBuffer[JsArray]
    arrOfLocs = getPageLocations(book)
    // creates a javascript array of arrays variable named "locs"
    val locs_array = JsCrVar("locs", JsArray(arrOfLocs.toList.map {l => l:JsExp}:_*))
    val id_array = JsCrVar("ids", JsArray(idList.toList.map {l => l:JsExp}:_*))
    
    //".book" #> S.param("book").openOr("No book param") &
    ".locsJS" #> Script(locs_array) &
    ".entid" #> Script(id_array) &
    ".book" #> book
     
  }
  import code.lib._
  import code.comet._
  import edu.umass.ciir.proteus.protocol.ProteusProtocol._
import edu.umass.ciir.proteus._
  
  def getPageLocations(id: String): ListBuffer[JsArray] = {
    val master:ListBuffer[JsArray] = new ListBuffer[JsArray]
    val entities : List[Location] = {
      if (TheCart.get.page_map.get.contains(id.toInt)) {
        val accessID = TheCart.get.page_map.get.apply(id.toInt).item.getId
        val loc_results = Librarian.library.getContents(accessID, ProteusType.PAGE, ProteusType.LOCATION).mapTo[SearchResponse] flatMap {
          locs => Future.traverse(locs.getResultsList.toList)(result => Librarian.library.lookupLocation(result))
        }
        loc_results.mapTo[List[Location]].get
      }
      else if (TheCart.get.book_map.get.contains(id.toInt)) {
        val accessID = TheCart.get.book_map.get.apply(id.toInt).item.getId
        println("Got access id: " + accessID)
        val page_results = Librarian.library.getContents(accessID, ProteusType.COLLECTION, ProteusType.PAGE, num_requested = 10).mapTo[SearchResponse] flatMap {
          pags => 
            println("received pages...: " + pags.getResultsList.toList)
            Future.traverse(pags.getResultsList.toList)(result => Librarian.library.getContents(result.getId, ProteusType.PAGE, ProteusType.LOCATION, num_requested = 10))
        }
        val loc_results = page_results.mapTo[List[SearchResponse]] flatMap {
          locs => 
            val flattend : List[SearchResult] = locs.map(_.getResultsList.toList).flatten
            println("Got my list of responses: " + flattend + "\n....")
            println("And sample result: " + flattend(0))
            println(Librarian.library.lookupLocation(flattend(0)).get)
            Future.traverse(flattend)((result: SearchResult) => Librarian.library.lookupLocation(result))
        }
        loc_results.mapTo[List[Location]].get
      }
      else Nil
    }

   // entities.foreach(e => TheCart.addItem(e))
    var entity_count = 0
    
 
    for (entity <- entities) {    
      val full_ent = entity//Librarian.library.lookupLocation(entity).get
      val coordinates = (entity.getTitle, full_ent.getLongitude, full_ent.getLatitude)
        println(coordinates)
        master.add(JsArray(Str(coordinates._1), Num(coordinates._2.toDouble), Num(coordinates._3.toDouble)))
        idList.add(JsArray(Str(entity.getTitle), Str((entity.getId.getIdentifier + entity.getId.getResourceId).hashCode.toString)))
      
    }
    return master
  }


}

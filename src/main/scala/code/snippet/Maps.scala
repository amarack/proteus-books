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
    val entities : List[SearchResult] = {
      if (TheCart.get.page_map.get.contains(id.toInt)) {
        val accessID = TheCart.get.page_map.get.apply(id.toInt).item.getId
        Nil
      }
      else if (TheCart.get.book_map.get.contains(id.toInt))
        Nil //TheCart.get.book_map.get.apply(id.toInt).item.getLocations.getResults().get.map(i => i.asInstanceOf[LocationType])
      else Nil
    }

    entities.foreach(e => TheCart.addItem(e))
    var entity_count = 0
    
 
    for (entity <- entities) {    
      val full_ent = Librarian.library.lookupLocation(entity).get
      val coordinates = (entity.getTitle, full_ent.getLongitude, full_ent.getLatitude)
        println(coordinates)
        master.add(JsArray(Str(coordinates._1), Num(coordinates._2.toDouble), Num(coordinates._3.toDouble)))
        idList.add(JsArray(Str(entity.getTitle), Str((entity.getId.getIdentifier + entity.getId.getResourceId).hashCode.toString)))
      
    }
    return master
  }


}

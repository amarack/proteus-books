/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package code
package snippet

// TODO: CONVERT

//import org.galagosearch.core.tools.Search._
import net.liftweb._
import code.snippet._
import http._
//import org.galagosearch.tupleflow.Parameters
import scala.collection.mutable.MutableList
import util._
import Helpers._
import scala.collection.JavaConversions._
//import org.galagosearch.core.retrieval.BadOperatorException
//import org.galagosearch.core.tools.Search.SearchResult
import code.comet._
import code.lib._
object Picture extends Query {
//
//  var search = createSearch("pictures")
//
// def pictureSearch(query: String) : List[(String, String, List[String], String, String)] = {
//    val p = new Parameters
//    p.set("retrievalGroup", "pictures")
//    p.set("withSnippets", "true")
//
//    search = updateSearchIndex(search, "pictures")
//
//    if(search.isEmpty) {
//      println("Empty search, this is an error we want to handle intelligently!")
//      return Nil
//    }
//    else {
//      println("The Picture Query Term: " + query);
//      val all_results = try {
//        performSearch(query, search.open_!, p)
//      } catch {
//        case ex: BadOperatorException =>
//          S.error("picture_error_msg", ex.getMessage)
//          new SearchResult
//      }
//      val rlist = all_results.items.toList
//      return rlist.map(r => (r.identifier, r.displayTitle, 
//                             picMap(getID(r.identifier), 
//                                    r.metadata.get("coords")).map(i =>
//                                            {archDownload+getID(r.identifier)+"/page/leaf"+getPageNumber(r.identifier)+ i + ".jpg"}).toList,
//                                            {archDownload+getID(r.identifier)+"/page/leaf"+getPageNumber(r.identifier)+"_s4.jpg"},
//                                            r.metadata.get("numID"))
//            )
//
//    }
//  }

    /*
     * Not currently needed, picture results are being listed by AllPicturesPage
     *
  def listPictureResults = {
    val p = new Parameters
    p.set("retrievalGroup", "pictures")
    p.set("withSnippets", "true")
    
    search = updateSearchIndex(search, "pictures")

    if(search.isEmpty) {
      println("Empty search, this is an error we want to handle intelligently!")
      ".result" #> "Error: The selected index does not contain this content type."
    }
    else {
      //println(queryTerm.is);
      //val all_results = performSearch(queryTerm.is, search.open_!, p)
      val rlist = all_results.items.toList

      ".returnedPages" #> rlist.map(r =>
        ".picsInPage" #> picMap(getID(r.identifier), r.metadata.get("coords")).map(i =>
                ".img" #> <a class="thumbnail" href="#thumb"><img src={archDownload+getID(r.identifier)+"/page/leaf"+getPageNumber(r.identifier)+ i + ".jpg"}></img>
                          <span><img src={archDownload+getID(r.identifier)+"/page/leaf"+getPageNumber(r.identifier)+"_s4.jpg"}/></span></a> ) &
        ".docTitle" #> <u><strong>{r.displayTitle}</strong></u> &
        ".doclink" #> <a href={"/doc?d=" + r.identifier}>[see OCR output]</a> &
        ".archlink" #> <a href={archStream+getID(r.identifier)+"#page/leaf"+getPageNumber(r.identifier)+"/mode/1up"}>[read at archive]</a>
      )
    }
  }
  */

//  private def picMap(id: String, coords: String) : MutableList[String] = {
//    // parse out all coordinates, return a list
//    var coordlist = new MutableList[String]
//    val splitText = coords.split("\\|")
//    for (i <- splitText) {
//      coordlist += formatCoords(i)
//    }
//    println(coordlist)
//    return coordlist
//  }
//
//
//  private def formatCoords(coords: String) : String = {
//    if (coords.equals(""))
//      return ""
//    var splitText = coords.split(",")
//    var left = Integer.parseInt(splitText(0))
//    var top = Integer.parseInt(splitText(1))
//    var right = Integer.parseInt(splitText(2))
//    var bottom = Integer.parseInt(splitText(3))
//    var width = right - left
//    var height = bottom - top
//    return "_x"+left+"_y"+top+"_w"+width+"_h"+height+"_s8"
//  }
//
//  private def getID(text: String) : String = {
//    var splitText = text.split("_")
//    return splitText(0)
//  }

import code.lib.Cart
import code.comet.BookBag._
  
  def printPageInfo = {
    var id = S.param("d").openOr("no document")
    var doctext = TheCart.get.findPictureItem(id.toInt).item.getResultTitle
    if (doctext != null)
    ".thedoc" #> {doctext}
    else
      ".thedoc" #> "Document not found.."
  }
}

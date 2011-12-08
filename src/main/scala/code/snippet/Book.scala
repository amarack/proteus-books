/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package code
package snippet

// TODO: This file has conversions to be done

import collection.mutable.ListMap
//import org.galagosearch.core.tools.Search
//import org.galagosearch.core.tools.Search._
import net.liftweb._
import http._
//import org.galagosearch.tupleflow.Parameters
import scala.collection.mutable.HashMap
import util._
import Helpers._
/*import org.apache.commons.lang*/
import scala.collection.JavaConversions._
//import org.galagosearch.core.retrieval.BadOperatorException
import code.comet.TheCart
import code.snippet._
import proteus.base._

/*
 * This class will take a document query and return a list of books ranked the sum of their document scores
 * The top 200 results will be returned for now
 */
class Book extends Query { //with PaginatorSnippet[String] {
//
//  var search = createSearch("documents")
  var booksList:List[String] = Nil

  /*
   * Pagination put on hold for now...
   *
  override def itemsPerPage = 10
  override def count = booksList.size //books.open_!.size
  override def page = booksList.slice(first.asInstanceOf[Int],(first+itemsPerPage).asInstanceOf[Int])
  ////books.open_!.slice(first.asInstanceOf[Int],(first+itemsPerPage).asInstanceOf[Int])
  // carries the query term state in between page requests
  override def pageUrl(offset: Long) = {
    val currentState = queryTerm.is
    S.fmapFunc(S.NFuncHolder(() => queryTerm(currentState))){ name =>
      Helpers.appendParams(super.pageUrl(offset), List(name -> "_"))
    }
  }
  */
 

  def searchBook(query :String, lang: String) : List[AllType] = {
    
    val all_results = Librarian.performSearch(query, List("collection", "page", "picture", "person", "location"))
    return all_results
    
  }
//
//  def searchBook(query: String, lang: String) : List[String] = {
//
//    val all_results = performBookSearch(query, lang)
//    var tempHash:HashMap[String, Double] = new HashMap[String, Double]
//    var otherHash:HashMap[String, SearchResultItem] = new HashMap[String, AllType]
//    for (item:AllType <- all_results.toList) {
//      val score = tempHash.get(getBookFromPage(item.identifier))
//      score match {
//        case None => {
//            tempHash.put(getBookFromPage(item.identifier), item.score)
//            otherHash.put(getBookFromPage(item.identifier), item)
//        }
//        case Some(x) => tempHash.update(getBookFromPage(item.identifier), (item.score + x))
//      }
//    }
//    if (tempHash.isEmpty) return List[String]()
//    var sortMap = ListMap(tempHash.toList.sortBy{_._2}:_*)
//    booksList = sortMap.keys.toList
//    booksList
//  }
//  
/*
  def list = {
    val p = new Parameters
    p.set("retrievalGroup", "documents")
    p.set("withSnippets", "true")
    search = updateSearchIndex(search, "documents")
    val all_results = performBookSearch(queryTerm.is, search.open_!, p)
    println("The query (from all_results): " + all_results.queryAsString)
    
    var tempHash:HashMap[String, Double] = new HashMap[String, Double]
    var otherHash:HashMap[String, SearchResultItem] = new HashMap[String, SearchResultItem]
    for (item:SearchResultItem <- all_results.items.toList) {
      val score = tempHash.get(getBookFromPage(item.identifier))
      score match {
        case None => {
            tempHash.put(getBookFromPage(item.identifier), item.score)
            otherHash.put(getBookFromPage(item.identifier), item)
        }
        case Some(x) => tempHash.update(getBookFromPage(item.identifier), (item.score + x))
      }
    }
    var sortMap = ListMap(tempHash.toList.sortBy{_._2}:_*)
    booksList = sortMap.keys.toList
    
    ".result" #> page.map(b =>
      ".docImage" #> <img src={getCoverImageURL(b)} width={"90"} height={"120"} /> &
      ".docTitle" #> otherHash.get(b).get.displayTitle &
      ".docauthor" #> otherHash.get(b).get.metadata.getOrElse("creator", "none") &
      ".score" #> ("Score: "+ tempHash.get(b).get) &
      ".mapLink" #> <a href={"map?book="+getBookFromPage(otherHash.get(b).get.identifier)}>Map Locations for this book</a> &
      ".entsLink" #> <a href={"entities?book="+getBookFromPage(otherHash.get(b).get.identifier)}> Entities for this book</a>
    )
    
  }
  */

}

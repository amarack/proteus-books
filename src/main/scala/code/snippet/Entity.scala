package code
package snippet

// TODO: This file conversions needed
import proteus.base._

import java.util.HashSet
import java.util.regex.Pattern
import net.liftweb._
import code.comet.TheCart
import code.snippet._
import http._
//import org.galagosearch.tupleflow.Parameters
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer
import util._
import Helpers._
import org.slf4j.LoggerFactory
import net.liftweb.common.Logger
import net.liftweb.common.Logger._
import scala.collection.JavaConversions._
//import org.galagosearch.core.retrieval.BadOperatorException
//import org.galagosearch.core.tools.Search.SearchResult
//import org.galagosearch.core.retrieval.Retrieval

import net.liftweb.common._
import net.liftweb._
import http._
import net.liftweb.http.js.jquery.JqWiringSupport

import scala.xml.NodeSeq
import sitemap._
import util._
import Helpers._

import util._
import js._
import js.jquery._
import JsCmds._
//import org.galagosearch.tupleflow.Utility;
//import org.tartarus.snowball.SnowballStemmer;
//import org.tartarus.snowball.ext.englishStemmer;

import code.comet.BookBag._

object Entity extends Query {

  val cannedAmbiguousResponse = "This entity has several possible matches within the Wikipedia.  Click the link above to search the Wikipedia for matches.  Words that occur near the entity name are listed below along with the number of times they occur on the same page in this collection."

  val stopwords : Set[String] = try {
	  io.Source.fromInputStream(getClass().getResourceAsStream("/stopwords/inquery")).getLines().toSet

    } catch {
      case _ =>
      // Oh well
      Set()
    }

  
  def entitySearch(query :String, lang: String) : List[AllType] = {
    
    val all_results = Librarian.performSearch(query, List("person", "location", "organization"))
    return all_results
  }

  def logAnnotations = "* [onclick]" #> SHtml.ajaxInvoke(() => {logging; JsCmds.RedirectTo("/")})

  val log = LoggerFactory.getLogger("annotations")

  def logging {
    
    // Append on the user's name to the annotations and write them to a file (appending)
//    annotations.foreach(a => log.info((userName :: a ::: page_metadata).map(v => "<" + v + ">").mkString(", ")))
  }

  /*
   *  If we use this again, dont perform another search, use results from
   *
   *
   *
   */
  def listEntityResults = {
    
    // page_12357
    val page = S.param("term").open_!
    

    val rlist = Librarian.performSearch(page, List("person", "location", "organization"))
    "#entities" #> rlist.map(ent =>
      "a *" #> <strong>{ent.getResultTitle}</strong> &
      "a [href]" #> Entity.getEntityLink(ent.getAccessURI.hashCode.toString, ent.getAccessURI.hashCode.toString) &
      "@cat *"  #> {ent.getResultType} &
      "@url *"  #> {Entity.getAdditionalInfo(ent)} &
      "@ident *" #> {ent.getAccessURI.hashCode}
    ) &
    "@quantity" #> rlist.size

  }


  val NORMAL = 1
  val ESCAPE = 2
  val UNICODE_ESCAPE = 3

  def convertUnicodeEscape(s: String) : String = {
    var out = Array.fill[Character](s.length)(' ')

    var state = NORMAL
    var j = 0
    var k = 0
    var unicode = 0
    var c = ' '
    for (i <- 0 to s.length - 1) {
      c = s.charAt(i);
      if (state == ESCAPE) {
        if (c == 'u') {
          state = UNICODE_ESCAPE;
          unicode = 0;
        }
        else { // we don't care about other escapes
          out(j) = '\\';
          j+=1
          out(j) = c;
          j+=1
          state = NORMAL;
        }
      }
      else if (state == UNICODE_ESCAPE) {
        if ((c >= '0') && (c <= '9')) {
          unicode = (unicode << 4) + c - '0';
        }
        else if ((c >= 'a') && (c <= 'f')) {
          unicode = (unicode << 4) + 10 + c - 'a';
        }
        else if ((c >= 'A') && (c <= 'F')) {
          unicode = (unicode << 4) + 10 + c - 'A';
        }
        else {
          throw new IllegalArgumentException("Malformed unicode escape");
        }
        k+=1;

        if (k == 4) {
          out(j) = unicode.toChar;
          j+=1
          k = 0;
          state = NORMAL;
        }
      }
      else if (c == '\\') {
        state = ESCAPE;
      }
      else {
        out(j) = c;
        j+=1
      }
    }

    if (state == ESCAPE) {
      out(j) = c;
      j+=1
    }

    return new String(out.map(_.charValue));
  }

  
  def getAdditionalInfo(result: AllType) : String = {
    result.getResultType match {
      case "person" => {
        val ent = result.asInstanceOf[PersonType]
        val birth = ent.getBirthDate.apply(0)
        val death = ent.getDeathDate.apply(0)
        val birthStr = if(birth != -1) java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM).format(new java.util.Date(birth)) else "??"
        val deathStr = if(death != -1) java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM).format(new java.util.Date(death)) else "??"
        return "(" + birthStr + " - " + deathStr + ")"
      }
      case "location" => {
        val ent = result.asInstanceOf[LocationType]
        return "(Longitude: " + ent.getLongitude + ", Latitude: " + ent.getLatitude + ")"
      }
      case _ => {
        return "Organization"
      }
    }
  }
   
  def printEntInfo = {
    // id is a Box[String] object
    // Box is full even if the string is empty ("")...
        
    info("SESSION ID: " + S.session.openOr("NONE") + ", ENTITY DETAIL")
//    search = updateSearchIndex(search, "entities")
    var id: String = S.param("e").openOr("bad entity name")
    val numID = S.param("id").openOr("0").toInt
   // val id_check: String = getEntNameFromNumID(numID)
    val histogramBuilder = new StringBuilder
    val kwicBuilder = new StringBuilder
    //println("In printEntInfo, ent id = " + id + " " + id_check)
//    if(!id.equals(id_check)) {
//      println("ERROR: Entity identifier doesn't match looked up identifier based on numID: " + id + " : " + id_check + " : " + numID)
//      id = id_check
//    }
//    val document = search.open_!.getDocument(id_check)
    val document = TheCart.entity_map.get.apply(numID)
   // val descriptor = extractEntityParts(id)

    //println("DESCRIPTOR: " + descriptor)
    
    if (isAmbiguous(id)) {

      try {
        val details = if(document.isPerson) {
	          val pers: PersonType = document.asPerson
	          (pers.getTermHistogram.apply(0), pers.getWikipediaLink.apply(0))
	        } else {
	          val loc: LocationType = document.asLocation
	          (loc.getTermHistogram.apply(0), loc.getWikipediaLink.apply(0))
	        } //document.terms.toList
        val terms = details._1
        val wiki = details._2
        
        ".enttype" #> "Ambiguous Entity" &
        ".entterm" #>  document.item.getResultTitle &
        ".ambig" #> cannedAmbiguousResponse &
        ".searchAmb" #> <a href={"/search?q=entity_" + numID}>Search for this Entity</a> &
        //".desc" #> getEntityText(id) &
        //".desc" #> "No description information given..."
        //".entpic" #> <img src={getEntityImage(id)} /> &
        ".entwiki *" #> <a href={wiki}>Search wikipedia</a> &
        ".histTitle" #> "Histogram of surrounding terms:" &
        ".histogram *" #> terms.map { (B) => <tr><td>{B._1}</td> <td>{B._2}</td></tr>}
      } catch {
        case _: NullPointerException => {
            ".enttype" #> "Ambiguous Entity" &
            ".entterm" #> document.item.getResultTitle  &
            ".searchAmb" #> <a href={"/search?q=entity_" + numID}>Search for this Entity</a> &
            ".ambig" #> {cannedAmbiguousResponse}
            //".entpic" #> <img src={getEntityImage(id)} /> &
//            ".entwiki *" #> <a href={"http://en.wikipedia.org/w/index.php?search="+ getTitle(id)}>Search Wikipedia</a>
          }
      }
                       
    }
    // entity is disambiguated, display different items
    // to double quote string vars, ("\""*1)+getTitle(id)+("\""*1)
    else {
      val details = if(document.isPerson) {
	          val pers: PersonType = document.asPerson
	          (pers.getTermHistogram.apply(0), pers.getWikipediaLink.apply(0), getAdditionalInfo(pers) )
	        } else {
	          val loc: LocationType = document.asLocation
	          (loc.getTermHistogram.apply(0), loc.getWikipediaLink.apply(0), getAdditionalInfo(loc))
	        } //document.terms.toList
      val terms = details._1
      val wiki = details._2
      val info = details._3
      
//      println("ENTITY: " + getTitle(id))
      ".enttype" #> "Disambiguated Entity" &
      ".desc" #> document.item.getResultSummary.toString &
      ".searchWithThisEnt" #> <a href={"/search?q=entity_" + numID}>Search for this Entity</a> &
      //".findPagesWithThisEnt" #> <a href={getBooksWithEntityLink(numID)}>Show Pages mentioning this Entity</a> &
      ".entterm" #> document.item.getResultTitle &
      ".entpic" #> <img src={document.viewImg} /> &
      ".wikilink" #> <a href={wiki}>Wikipedia Article</a> &
      ".longlat" #> info &
      ".histTitle" #> (if (terms.isEmpty) "" else "Histogram of surrounding terms:") &
      ".histogram *" #> terms.map { (B) => <tr><td>{B._1}</td> <td>{B._2}</td></tr>}
    }
  }

  /**
   * Input: either [/text, CATEG], [text, CATEG], [/text_(something), CATEG], /text_more_text, /text_(something)
   * Output: boolean indicating whether or not entity is disambiguated --
   *         (starts with a "/")
   */
  def isAmbiguous(text: String) : Boolean = {
      return false
    
  }
  
  def getPageListFromEntityID(numID: String) = {
    "/dquery?term=" + ("entity_"+numID)+"&index="+S.param("index").openOr("default").toString+"&language="+S.param("language").openOr("english").toString
  }

}

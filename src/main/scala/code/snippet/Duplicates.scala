package code
package snippet

// TODO: This needs conversion

//import edu.umass.ciir.megabooks.index.EntityNameReader
//import org.galagosearch.core.tools.Search._
import net.liftweb.util.Props
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.HashMap
import java.util.Scanner
import net.liftweb._
import http._
import code.snippet._
//import org.galagosearch.tupleflow.Parameters
import scala.collection.mutable.HashSet
import scala.collection.mutable.ArrayBuffer
import scala.xml.NodeSeq
import util._
import Helpers._
import scala.collection.JavaConversions._
//import org.galagosearch.core.retrieval.BadOperatorException
//import org.galagosearch.core.tools.Search.SearchResult

object Duplicates extends Query {

  // Variable declarations
//  var search = createSearch("graph")
//
//  // The titles.txt file should contain titles for every duplicated book
//  // However, it doesn't. So as a backup we can do a search for the title using this
//  var doc_search = createSearch("documents")
//  
//  val dupeHash: HashMap[String, Int] = new HashMap
//  val swappedHash: HashMap[String, Int] = new HashMap
//  val titles = new HashMap[String, String]

  /*
   *  We are reading in temporary information from text files within the next if statement
   *  The text files are specified in the src/main/resources/props directory
   *
   */
//  if (search.isDefined) {
//    val scan = new Scanner(new File(Props.get(prefix+"pairscores", "")))
//    println(Props.get(prefix+"pairscores", ""))
//    while (scan.hasNext()) {
//      val gname = scan.next
//      val changed = Integer.parseInt(scan.next)
//      swappedHash.put(gname, changed)
//    }
//    scan.close
//
//    // This is our set of valid graphs, should only be loading once
//    val in = new Scanner(new File(Props.get(prefix+"graph.ids", "")))
//    while (in.hasNext()) {
//      val count = Integer.parseInt(in.next)
//      val identifier = in.next
//      dupeHash.put(identifier, count)
//    }
//    in.close
//
//    val br = new BufferedReader(new FileReader(Props.get(prefix+"titles", "")))
//    while (br.ready) {
//      val parts = br.readLine.split("\t")
//      titles(parts(0)) = parts(1)
//    }
//    br.close
//  }

  /*
   *  called from Documents class, in listDocumentResults()
   */
  def dupeLink(in: NodeSeq) : scala.xml.NodeSeq = {
//    if (search.isEmpty) {
//      return in
//    }

    val id = S.attr("id").open_!
    return dupeLinked(id)
  }
  
  import code.comet._
  import code.lib._
  import proteus.base._
  
  def dupeLinked(id: String) : scala.xml.NodeSeq = {
//    val rlist = try {performSearch("#neighbors:"+id+":part=postings()", search.open_!).items.toList}
//                catch { case ex: Exception =>  print("Not found in dupe index"); ex.printStackTrace; Nil}
    val thebook = TheCart.get.findBookItem(id.toInt)
    println("Getting dupes for: " + id + " " + thebook.item.getAccessURI)
    val rlist = thebook.item.getOverlappingCollections.getResults().get.map(i => i.asInstanceOf[CollectionType])

    if (rlist.length > 0) { //dupeHash.contains(id)) {
      val count = if(rlist.length == 5) "5+" else rlist.length.toString //dupeHash.get(id)
      val term = rlist.length match {
        case 1 => "duplicate"
        case _ => "duplicates"
      }
      return <a href={"/dupes?d=" + id + "&i=" + id}>{count} {term}</a>
    } else {
      return <span></span>
    }
  }
//
//  def encodeQuery(text: String) : String = {
//    return urlEncode("#neighbors:"+text+":part=postings()")
//  }

  /*
   *  The compareStrings(), whichPlace(), and getBookTitle() methods are used
   *  together to provide the correct labeling for the graph pictures
   *  
   */
//
//  def getOrder(str1: String, str2: String) : Int = {
//    val reconstruct = if (str1 < str2) {
//      str1+"_"+str2
//    } else {
//      str2+"_"+str1
//    }
//    return swappedHash.get(reconstruct)
//  }
//
//  private def compareStrings(str1: String, str2: String) : String = {
//    // this will concatenate the strings in alpahbetical order with a "_" in between, see "redordered.txt"
//    if (str1.compareTo(str2) > 0) {
//      return str2+"_"+str1
//    } else {
//      return str1+"_"+str2
//    }
//  }
//  
//  private def getBookTitle(iaid: String) : String = {
//    val pageid = iaid + "1"
//    if (titles.contains(iaid)) {
//      titles(iaid)
//    } else {
//      if (doc_search.isEmpty) {
//        iaid
//      } else {
//        val doc = doc_search.open_!.getDocument(pageid)
//        if (doc != null) {
//          doc.metadata.getOrElse("title", iaid)
//        } else {
//          "Document " + iaid + " not found"
//        }
//      }
//    }
//  }

  /*
   * This method interfaces with dupes.html and "replaces" the <scan class=".."> tags
   * with the appropriate class names
   * 
   */
  def listDuplicates = {
//    if (search.isDefined) {
      var id = S.param("i").openOr("error in listDupes")
      var dupeQuery = S.param("d").openOr("error in listDupes")
//      var decoded = urlDecode(dupeQuery.toString)
      val dList = TheCart.get.findBookItem(id.toInt).item.getOverlappingCollections.getResults().get.map(i => i.asInstanceOf[CollectionType])
      // Add these results back into the cart/book bag
      dList.foreach(d => TheCart.addItem(d))
      val rlist = dList.map(d => TheCart.get.findBookItem(d.getAccessURI.hashCode))
      info("SESSION ID: " + S.session.openOr("NONE") + ", LIST DUPLICATES")
      // this is called CSS Selector Syntax, and is a nice feature of Lift
      ".dupeResult" #> rlist.map(d => {
//          val order = getOrder(id, d.identifier)
          //".dupeTitle" #> d.displayTitle &
          ".archLink" #> "archLink here" &
          //[book cover] [title] [score] [bar image]
          ".topbar" #> <a class="thumbnail" href="#thumb"> <img src={d.thumbImg} width={"60"} height={"100"} />
            <span><img src={d.viewImg} width={"300"} height={"500"}></img></span> </a> &
          ".title1" #> {<span>(reference)</span>} &
          ".title2" #> {<a href={d.item.getAccessURI}>{d.item.getResultTitle}</a> } &
          ".graph" #>  <img width="200" height="75" src={"http://laguna.cs.umass.edu:6800/gimages/unknown.png"}></img> })
      //green, red bars (images are named alphabetically)
//    } else {
//      ".dupeResult" #> ""
//    }
  }

  def duplicateTitle = {
    var id = S.param("i").openOr("error in duplicateTitle")
    val baseDupe = TheCart.get.findBookItem(id.toInt)
    ".showTitle" #> <span>Partial duplicates of <I>{baseDupe.item.getResultTitle}</I></span> &
    ".minh" #> <a class="thumbnail" href="#thumb"> <img src ={baseDupe.thumbImg} width={"85"} height={"150"}/> </a>
  }

}

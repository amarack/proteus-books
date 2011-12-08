package code
package snippet

//import proteus.web
// TODO: NEEDS CONVERT

import java.io.FileWriter
import java.io.IOException
import net.liftweb._
import net.liftweb.common._
//import edu.umass.ciir.megabooks.tools.Search
import http._ 
//import org.galagosearch.core.index.StructuredIndex
//import org.galagosearch.core.tools.Search.SearchResult
//import org.galagosearch.tupleflow.Parameters
import scala.collection.mutable.ListBuffer
import scala.xml.NodeSeq
import util._
import Helpers._

import scala.collection.JavaConversions._
//import edu.umass.ciir.megabooks.traversal.StemmingTraversal
//import edu.umass.ciir.megabooks.index.EntityNameReader
//import org.galagosearch.core.index.DocumentNameReader

import scala.collection.mutable.HashSet
import java.io.File

import proteus.web._
import proteus.base._

object Librarian extends Logger {
  val library = new RemoteLibraryManager("mildura.cs.umass.edu", 8081)
  library.connect
  
  def performSearch(query:String, typesRequested: List[String]) : List[AllType] = {
      
        // catch illegal operator exception here
        val processed = processQuery(trace("processing query", query))
        
        info("scala.Query.performSearch => PROCESSED_QUERY: " + processed)
        Timer.go("Lower level doc search")
        val result = library.query(processed, typesRequested).get.asInstanceOf[List[AllType]]//search.runQuery(processed, p, provideSnippets)
        info(Timer.stop)
        return result
    }

    // Need to split up multi-word queries, but ignore components that may be useful
    val ignored = """has_(obj|sub)|(obj|sub)_of|entity_|page_""".r
    val stripRE = """\.""".r
    def processQuery(query: String) : String = {
        if (query.startsWith("#")) 
            return query
    
       
        val result = stripRE.replaceAllIn(query, " ").toLowerCase
        return result
    }
  
}

abstract class Query extends Logger  {
  
//    var search : Box[Search]
    val prefix = System.getProperty("label", "default") + "."
    var indexes = "default"
val selectedLanguage = "english"
  
//    var ent_name_reader = new EntityNameReader(Props.get(prefix + "index.root", "") + "/entities/names")
//    var doc_name_reader = new DocumentNameReader(Props.get(prefix + "index.root", "") + "/documents/names")

//    def createSearch(ext:String) : Box[Search] = {
//        printf("Creating search object for spec: %s\n", ext)
//        val indexes_selected = S.param("index").openOr("").toString.split(";")
//        val indexPath = Props.get(prefix+"index.root", "") + "/" + ext
//        val corpusPath = Props.get(prefix+"index.root", "") + "/" + ext + "/corpus"
//
//        val paramFile = Props.get(prefix+"global.parameters", "")
//        var parameters = new Parameters
//        if (paramFile.length > 0) {
//            parameters = new Parameters(new File(paramFile))
//        }
//
//        if(!S.param("index").isDefined || indexes_selected.length == 0) {
//            val indexPath = Props.get(prefix+"index.root", "") + "/" + ext
//            val corpusPath = Props.get(prefix+"index.root", "") + "/" + ext + "/corpus"
//            //printf("Index path: %s, corpusPath: %s, params: %s\n", indexPath, corpusPath, paramFile)
//            printf("Index path: %s, corpusPath: %s, params: %s\n", indexPath, corpusPath, "resource: parameters.xml")
//            val indexDir = new File(indexPath)
//            if (!indexDir.exists) {
//                printf("Path: %s doesn't seem to exist. Returning empty box.\n", indexPath)
//                return Empty
//            }
//            updateNameReaders(prefix)
//            parameters.add("index", indexPath)
//            // Check to see if loading a corpus would work
//            val corpusFile = new File(corpusPath)
//            if (corpusFile.exists) {
//                parameters.add("corpus", corpusPath)
//            }
//        }
//        else {
//            for(indexName <- indexes_selected) {
//                if (!indexName.equals("(all)")) {
//                    val indexPath = Props.get(indexName+".index.root", "") + "/" + ext
//                    val corpusPath = Props.get(indexName+".index.root", "") + "/" + ext + "/corpus"
//                    //printf("Index path: %s, corpusPath: %s, params: %s\n", indexPath, corpusPath, paramFile)
//                    printf("Index path: %s, corpusPath: %s, params: %s\n", indexPath, corpusPath, "resource: parameters.xml")
//                    val indexDir = new File(indexPath)
//                    if (!indexDir.exists) {
//                        printf("Path %s doesn't seem to exist. Skipping.\n", indexPath)
//                        //return Empty
//
//                    }
//                    else {
//                        updateNameReaders(indexName+".")
//                        parameters.add("index", indexPath)
//                        // Check to see if loading a corpus would work
//                        val corpusFile = new File(corpusPath)
//                        if (corpusFile.exists) {
//                            parameters.add("corpus", corpusPath)
//                        }
//                    }
//                }  
//            }
//        }
//    
//        if (parameters.list("index").length == 0)
//            return Empty
//
//        return Full(new Search(parameters))
//    }

//    
//  
//    def createSearch(indexes:String, ext:String) : Box[Search] = {
//        printf("Creating search object for spec: %s\n", ext)
//
//        val indexes_selected = indexes.split(";")
//        val paramFile = Props.get(prefix+"global.parameters", "")
//        var parameters = new Parameters
//        if (paramFile.length > 0) {
//            parameters = new Parameters(new File(paramFile))
//        }
//
//        if(!S.param("index").isDefined || indexes_selected.length == 0) {
//            val indexPath = Props.get(prefix+"index.root", "") + "/" + ext
//            val corpusPath = Props.get(prefix+"index.root", "") + "/" + ext + "/corpus"
//            printf("Index path: %s, corpusPath: %s, params: %s\n", indexPath, corpusPath, paramFile)
//            val indexDir = new File(indexPath)
//            if (!indexDir.exists) {
//                printf("Path: %s doesn't seem to exist. Returning empty box.\n", indexPath)
//                return Empty
//            }
//            updateNameReaders(prefix)
//            parameters.add("index", indexPath)
//            // Check to see if loading a corpus would work
//            val corpusFile = new File(corpusPath)
//            if (corpusFile.exists) {
//                parameters.add("corpus", corpusPath)
//            }
//        }
//        else {
//            for(indexName <- indexes_selected) {
//                if (!indexName.equals("(all)")) {
//                    val indexPath = Props.get(indexName+".index.root", "") + "/" + ext
//                    val corpusPath = Props.get(indexName+".index.root", "") + "/" + ext + "/corpus"
//                    printf("Index path: %s, corpusPath: %s, params: %s\n", indexPath, corpusPath, paramFile)
//                    val indexDir = new File(indexPath)
//                    if (!indexDir.exists) {
//                        printf("Path %s doesn't seem to exist. Skipping.\n", indexPath)
//                        //return Empty
//                    }
//                    else {
//                        updateNameReaders(indexName + ".")
//                        parameters.add("index", indexPath)
//                        // Check to see if loading a corpus would work
//                        val corpusFile = new File(corpusPath)
//                        if (corpusFile.exists) {
//                            parameters.add("corpus", corpusPath)
//                        }
//                    }
//                }
//            }
//        }
//        println("Indexes Loaded: " + parameters.list("index").toString)
//        if (parameters.list("index").length == 0)
//            return Empty
//
//        printf("Returning boxed search object.\n")
//        return Full(new Search(parameters))
//    }
//
//    def createBasicSearch(indexName:String, ext:String) : Box[Search] = {
//        val paramFile = Props.get(prefix+"global.parameters", "")
//        var parameters = new Parameters
//        if (paramFile.length > 0) {
//            parameters = new Parameters(new File(paramFile))
//        }
//        val indexPath = Props.get(indexName+".index.root", "") + "/" + ext
//        val corpusPath = Props.get(indexName+".index.root", "") + "/" + ext + "/corpus"
//        printf("Index path: %s, corpusPath: %s, params: %s\n", indexPath, corpusPath, paramFile)
//        val indexDir = new File(indexPath)
//        if (!indexDir.exists) {
//            printf("Path %s doesn't seem to exist. Skipping.\n", indexPath)
//            //return Empty
//        }
//        else {
//           
//            parameters.add("index", indexPath)
//            // Check to see if loading a corpus would work
//            val corpusFile = new File(corpusPath)
//            if (corpusFile.exists) {
//                parameters.add("corpus", corpusPath)
//            }
//        }
//        
//        println("Indexes Loaded: " + parameters.list("index").toString)
//        if (parameters.list("index").length == 0)
//            return Empty
//
//        printf("Returning boxed search object.\n")
//        return Full(new Search(parameters))
//    }
//    
    
//    def updateSearchIndex(search: Box[Search],ext: String) : Box[Search] = {
//        StemmingTraversal.language = S.param("language").openOr(StemmingTraversal.language).toString
//        val new_indexes = parseIndexSelection
//        if(!indexes.equals(new_indexes)) {
//            indexes = new_indexes
//            return createSearch(new_indexes, ext)
//        }
//        else
//            return search
//    }
//
//    def updateNameReaders(prefix : String) = {
//        val entIndexPath = Props.get(prefix+"index.root", "") + "/entities"
//        val docIndexPath = Props.get(prefix+"index.root", "") + "/documents"
//        ent_name_reader = new EntityNameReader(entIndexPath + "/names")
//        doc_name_reader = new DocumentNameReader(docIndexPath + "/names")
//    }

    def selectParts = {
        val part = whichAction
        if (part == 1) {
            ".parts" #> <input type="radio" name="part" onclick="document.searchform.action='/dquery';">Pages</input>
            <input type="radio" name="part" checked="checked" onclick="document.searchform.action='/equery';">Entities</input>
            <input type="radio" name="part" onclick="document.searchform.action='/pquery';">Pictures</input>
        }
        else if (part == 2) {
            ".parts" #> <input type="radio" name="part" onclick="document.searchform.action='/dquery';">Pages</input>
            <input type="radio" name="part" onclick="document.searchform.action='/equery';">Entities</input>
            <input type="radio" name="part" checked="checked" onclick="document.searchform.action='/pquery';">Pictures</input>
        }
        else if (part == 3) {
            ".parts" #> <input type="radio" name="part" onclick="document.searchform.action='/dquery';">Pages</input>
            <input type="radio" name="part" checked="checked" onclick="document.searchform.action='/bquery';">Books</input>
            <input type="radio" name="part" onclick="document.searchform.action='/equery';">Entities</input>
            <input type="radio" name="part" onclick="document.searchform.action='/pquery';">Pictures</input>
        }
        else {
            ".parts" #> <input type="radio" name="part" checked="checked" onclick="document.searchform.action='/dquery';">Pages</input>
            <input type="radio" name="part" onclick="document.searchform.action='/equery';">Entities</input>
            <input type="radio" name="part" onclick="document.searchform.action='/pquery';">Pictures</input>
        }
    }

    def whichAction : Int = {
        if (S.uri.length > 1 && S.uri.charAt(1) == 'e')
            return 1
        else if(S.uri.length > 1 && S.uri.charAt(1) == 'p')
            return 2
        else if(S.uri.length > 1 && S.uri.charAt(1) == 'b')
            return 3
        else
            return 0
    }
  
    def getIndexForm = {
        val names = List("/dquery", "/equery", "/pquery", "/bquery")
        ".indexform" #> <form name="searchform" action={names.get(whichAction)}>
            <input size="45" name="term" />
            <input type="hidden" name="index" value={indexes} /> <input type="hidden" name="language" value={selectedLanguage} />
                        </form>

    }

//    def parseIndexSelection() : String = {
//        if(S.param("index").openOr("").toString.equals(""))
//            return "default"
//    
//        var newindx = "";
//        for(shard <- S.param("index").openOr("").toString.split(";")) {
//            if(!shard.equals("(all)"))
//                newindx = newindx + shard + ";"
//        }
//        return newindx.substring(0,newindx.length-1)
//    }
//
//    def langIsIndexed(lang: String, count: Int) : String = {
//        val prefix = lang.toLowerCase + count
//        val indexPath = Props.get(prefix+".index.root", "")
//        if(indexPath.equals(""))
//            return ""
//        else {
//            return ";" + prefix.capitalize + langIsIndexed(lang, count+1)
//        }
//    }
//
//    def listSupportedLanguages = {StemmingTraversal.supported.toList}
//    def selectedLanguage = {StemmingTraversal.language}
//  
//    def listAvailableIndexes = {
//        var indxs = "Default"
//        for(lang <- listSupportedLanguages) {
//            indxs += langIsIndexed(lang, 1)
//        }
//        indxs.split(";").toList
//    }
//  
    def displayAvailableIndexes = {
        val rlist = List("Default")
        ".indexAvail" #> rlist.map(r =>
            if(indexes.contains(r.toLowerCase))
                ".indexTitle" #> <option selected='selected' value={r.toLowerCase}>{r}</option>
            else
                ".indexTitle" #> <option value={r.toLowerCase}>{r}</option>
        )
    }

    def displayAvailableLanguages = {
        val rlist = List("English")
        
        ".langAvail" #> rlist.map(r =>
            if(selectedLanguage.equals(r.toLowerCase))
                ".lang" #> <option selected='selected' value={r.toLowerCase}>{r}</option>
            else
                ".lang" #> <option value={r.toLowerCase}>{r}</option>
        )
    }

    val archDownload = "http://www.archive.org/download/"
    val archStream = "http://www.archive.org/stream/"

    def getEntityLink(identifier: String, numID: String) = {
        //info("SESSION ID: " + S.session.openOr("NONE") + ", GET ENTITY LINK")
        "/ent?e=" + identifier + "&id="+numID+"&index="+S.param("index").openOr("default").toString+"&language="+S.param("language").openOr("english").toString
    }
    
    protected def getBooksWithEntityLink(numID: String) = {
        //info("SESSION ID: " + S.session.openOr("NONE") + ", FIND BOOKS WITH ENTITY")
        "/dquery?term=" + ("entity_"+numID)+"&index="+S.param("index").openOr("default").toString+"&language="+S.param("language").openOr("english").toString
    }

    def getEntitySearchLink(numID: String) = {
        //info("SESSION ID: " + S.session.openOr("NONE") + ", SEARCH ENTITIES")
        // For normal entity lists on pages
        "equery?term="+("page_"+numID)+"&index="+S.param("index").openOr("default").toString+"&language="+S.param("language").openOr("english").toString
        // For doing entity annotation ground truths from users
        //"elabel?term="+("page_"+numID)+"&index="+S.param("index").openOr("default").toString+"&language="+S.param("language").openOr("english").toString
    }
//    def getArchiveLink(identifier: String) = {
//        //info("SESSION ID: " + S.session.openOr("NONE") + ", GET ARCHIVE BOOK LINK")
//        println("GET ARCHIVE LINK: " + identifier)
//        "http://www.archive.org/stream/"+getBookFromPage(identifier)/* +"#page/leaf"+getPageNumber(identifier)+"/mode/1up" */
//    }
//  
//    def getArchiveLinkPage(identifier: String) = {
//        //info("SESSION ID: " + S.session.openOr("NONE") + ", GET ARCHIVE PAGE LINK")
//        println("GET ARCHIVE LINK: " + identifier)
//        "http://www.archive.org/stream/"+getBookFromPage(identifier) +"#page/leaf"+getPageNumber(identifier)+"/mode/1up" 
//    }
    def getDocLink(identifier: String) = {
        //info("SESSION ID: " + S.session.openOr("NONE") + ", VIEW OCR TEXT")
        "/doc?d=" + identifier+"&index="+S.param("index").openOr("default").toString+"&language="+S.param("language").openOr("english").toString
    }

//    def getThumbnailURL(identifier: String) = {
//        
//        "http://www.archive.org/download/"+getBookFromPage(identifier)+"/page/leaf"+getPageNumber(identifier)+"_thumb.jpg"
//    }
//
//    def getPageImageURL(identifier: String) = {
//        "http://www.archive.org/download/"+getBookFromPage(identifier)+"/page/leaf"+getPageNumber(identifier)+"_s4.jpg"
//    }
//
//    def getCoverImageURL(identifier: String) = {
//        "http://www.archive.org/download/"+getBookFromPage(identifier)+"/page/cover_thumb.jpg"
//    }
//
//    def getCoverImageURL2(identifier: String) = {
//        "http://www.archive.org/download/"+getBookFromPage(identifier)+"/page/cover_s2.jpg"
//    }

//    def getBookFromPage(text: String) : String = {
//        var splitText = text.split("_")
//        return splitText(0)
//    }
//
//    def getPageNumber(text: String) : String = {
//        var splitText = text.split("_")
//        return splitText(1)
//    }

    // fetching document twice at this point
//    def getSnippet(ident: String, query: String): String = {
//        val queryTerms = new HashSet[String]
//        var strings = query.split(" ")
//        queryTerms.addAll(strings.toList)
//        //println("IDENT: " + ident)
//        //println("SNIPPETS:" + queryTerms.toString)
//        val document = search.open_!.getDocument(ident)
//        var snippet = search.open_!.getSummary(document, queryTerms)
//        return snippet
//    }
//
//    // Is this being used?
//    def splitQueryTerm(query: String): String = {
//        return ""
//    }
   

//    def getDocNameFromNumID(numID : String) = {
//        doc_name_reader.getDocumentName(numID.toInt)
//    }
//
//    def getEntNameFromNumID(numID : String) = {
//        ent_name_reader.getDocumentName(numID.toInt)
//    }


}

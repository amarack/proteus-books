/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package code
package snippet

import code.comet._
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
import proteus.base._
import code.lib._

object AllItemsPage {
    

    
  // define the menu item for the page that
  // will display all items
  //lazy val menu = Menu.i("Pages") / "item" >>
  //Loc.Snippet("Pages", render)
  lazy val menu = Menu("Pages "+{
      val num = TheCart.get.numPages.get
      if (num == 0) {
        ""
      } else {
        "("+ num +")"
      }
    }) / "item"


  // display the items
  def render = {

    if (TheCart.get.queryText.isEmpty)
      "*" #> <h4>Oops, it looks like you haven't run a query!</h4>
    else if (TheCart.get.numPages.get == 0)
      "*" #> <h4>No page results for your query!</h4>
    else {
      
     
      "#pages" #> (
        "tbody" #>
        Helpers.findOrCreateId(id =>  // make sure tbody has an id
          // when the cart contents updates
          WiringUI.history(TheCart.currentDocPage) {
            (old, nw, ns) => {
              // capture the tr part of the template
              val theTR = ("tr ^^" #> "**")(ns)

              def ciToId(i: PageItem): String = i.id + "_" + i.item.getAccessURI.hashCode

              // build a row out of a cart item
              def html(i: PageItem): NodeSeq = {
                ( "tr [id]" #> ciToId(i) &
                 "@name *" #> <u><strong>{scala.xml.Unparsed(i.item.getResultTitle)}</strong> (page {i.item.getPageNumber.apply(0)})</u>
                 <p>{scala.xml.Unparsed(i.item.getResultSummary.tagTerms())}</p>
                 <span>{LogServer.logLink("/doc?d=" + i.item.getAccessURI.hashCode, "[Show OCR output]",
                                          S.session.openOr("NONE").toString + " --> Show OCR on page link clicked (" + i.id + ")")}
                 <span> | </span>
                 {LogServer.logLink(Document.getEntitySearchLink(i.item.getAccessURI.hashCode().toString), "[Show entities on this page]",
                                          S.session.openOr("NONE").toString + " --> Show entities on page link clicked (" + i.id + ")")}
                 <span> | </span>S
                 {LogServer.logLink(i.item.getAccessURI, "[View page at Internet Archive]",
                                          S.session.openOr("NONE").toString + " --> Archive Link Clicked (" + i.item.getAccessURI + ")")}</span> &
                 "@img *"  #> <a class="thumbnail" href="#thumb"><img src={i.thumbImg} width={"90"} height={"120"} />
                    <span><img src={i.viewImg} width={"450"} height={"700"}></img></span></a> 
                )(theTR)

              }

                 
              // calculate the delta between the lists and
              // based on the deltas, emit the current jQuery
              // stuff to update the display
              JqWiringSupport.calculateDeltas(old, nw, id)(ciToId _, html _)
            }
          })) & 
      "@query" #> WiringUI.asText(TheCart.wiredQuery) &
      "@slice" #> ("displaying items " + (TheCart.firstDoc+1) + " to " + (TheCart.firstDoc + TheCart.docsPerPage)) &
      "@pageCount" #> WiringUI.asText(TheCart.numPages) &
      "@right [onclick]" #> SHtml.ajaxInvoke(() => TheCart.get.docPageRight) &
      "@left [onclick]" #> SHtml.ajaxInvoke(() => TheCart.get.docPageLeft) 
    } // end else statement
  } // end render()


    
  }
  
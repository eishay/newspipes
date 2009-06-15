package com.newspipes.server.core

import com.google.appengine.api.urlfetch._
import java.net.URL
import scala.xml._

class TwitterSearch {
  def search(query: String) : String = {
    val urlFetchService = URLFetchServiceFactory.getURLFetchService
    val response = urlFetchService.fetch(new URL("http://search.twitter.com/search.atom?q=" + query + "&rpp=10"))
    val stringValue = new String(response.getContent)
    println("got = " + stringValue)
    val xml = XML.loadString(stringValue)
    val urls = extractUrls(xml \ "entry")
    val results = urls.mkString
    println("results = " + results)
    results
  }
  
  def extractUrls(xml:  NodeSeq) : Seq[String] = {
    xml map (n => extractUrl(n)) flatMap (a => a) filter {case None => false; case _ => true} map {case Some(url) => url; case None => throw new RuntimeException("impossible reach")}
  }
  
  def extractUrl(element: Node) : Seq[Option[String]] = {
    element \ "content" map (a => extractUrlFromContent(a.toString)) 
  }
  
  case class ExternalLink {
    import java.util.regex.Pattern._
    
    val urlPattern = compile(""".*a href="(.*)".*""") 
    def unapply(str: String): Option[String] = {
      
    }
  }
  
  def extractUrlFromContent(content: String) : Option[String] = content match {
    case Some(content)
  }
}

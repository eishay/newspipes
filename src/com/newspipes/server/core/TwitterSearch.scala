package com.newspipes.server.core

import com.google.appengine.api.urlfetch._
import java.net.URL
import scala.xml._

class TwitterSearch {
  def search(query: String) : String = {
    val urlFetchService = URLFetchServiceFactory.getURLFetchService
    val response = urlFetchService.fetch(new URL("http://search.twitter.com/search.atom?q=" + query + "&rpp=10"))
    val stringValue = new String(response.getContent)
    val xml = XML.loadString(stringValue)
    val urls = extractUrls(xml)
    urls.mkString
  }
  
  def extractUrls(xml:  Elem) : Seq[String] = {
    xml.child map(extractUrl) filter {case None => false; case _ => true} map {case Some(url) => url}
  }
  
  def extractUrl(element: Node) : Option[String] = element match {
    case <entry><content>{ Text(content) }</content></entry> => extractUrl(content) 
    case _ => None
  }
  
  def extractUrl(content: String) : Option[String] = Some(content)
}

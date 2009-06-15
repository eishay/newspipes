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
    val urls = extractUrls(xml \ "entry")
    val results = urls.mkString
    println("results = " + results)
    results
  }
  
  def extractUrls(xml:  NodeSeq) : Seq[String] = {
    xml map (n => extractUrl(n)) flatMap (a => a) filter {case None => false; case _ => true} map {case Some(url) => url; case None => throw new RuntimeException("impossible reach")}
  }
  
  def extractUrl(element: Node) : Seq[Option[String]] = {
    element \ "content" map (a => a.child) flatMap (a => a) foreach println//map {case t: Text => extractUrlFromContent(t.text); case _ => None}
    None::Nil
  }
  
  object ExternalLink {
    import java.util.regex.Pattern._
    val urlPattern = compile("a href=\"(.*?)\"")

    def unapply(str: String): Option[String] = {
      val matcher = urlPattern.matcher(str)
      matcher.find() match {
        case true  => {
          println("matching " + str)
          Some(matcher.group(1))
        }
        case false => {
          println ("not matching " + str)
          None
        }
      }
    }
  }
  
  def extractUrlFromContent(content: String) : Option[String] = content match {
    case ExternalLink(link) => Some(link)
    case _ => None
  }
}

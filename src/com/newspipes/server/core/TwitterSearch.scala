package com.newspipes.server.core

import com.google.appengine.api.urlfetch._
import java.net.URL
import scala.xml._

class TwitterSearch {
  val urlFetchService = URLFetchServiceFactory.getURLFetchService

  def search(query: String) : Array[String] = {
    val response = urlFetchService.fetch(new URL("http://search.twitter.com/search.atom?q=" + query + "&rpp=100"))
    val stringValue = new String(response.getContent)
    val xml = XML.loadString(stringValue)
    val urls = extractUrls(xml \ "entry")
    println("results = " + urls)
    urls.toArray
  }
  
  def extractUrls(xml:  NodeSeq) : Seq[String] = {
    xml map (n => extractUrl(n)) filter {case None => false; case _ => true} map {case Some(url) => url; case None => throw new RuntimeException("impossible reach")}
  }
  
  def extractUrl(element: Node) : Option[String] = {
    val str = element \ "content" map (a => a.child) flatMap (a => a) mkString("")
    //println(str)
    extractUrlFromContent(str)
  }
  
  object ExternalLink {
    import java.util.regex.Pattern._
    val urlPattern = compile("a href=&quot;(.*?)&quot;")
    val urlTwitterPattern = compile("""twitter\.com""")

    def unapply(str: String): Option[String] = {
      val matcher = urlPattern.matcher(str)
      matcher.find() match {
        case true  => {
          //println("matching " + str)
          val url = matcher.group(1)
          urlTwitterPattern.matcher(url).find() match {
            case true => None
            case false => Some(url)
          }
        }
        case false => {
          //println ("not matching " + str)
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

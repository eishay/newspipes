package com.newspipes.server.core

import client.Article
import google.appengine.api.datastore.KeyFactory
import java.util.{ArrayList, List => JList}
import javax.jdo.annotations.{Extension, Persistent, IdGeneratorStrategy, PrimaryKey, PersistenceCapable, IdentityType}
import scala.collection.jcl.Conversions._
import ServiceUtils._

@PersistenceCapable{val identityType = IdentityType.APPLICATION}
class SearchKeyword(
  @PrimaryKey
  @Persistent{val valueStrategy = IdGeneratorStrategy.IDENTITY}
  @Extension{val vendorName="datanucleus", val key="gae.encoded-pk", val value="true"}
  var key: String,
  @Persistent var value: String,
  @Persistent var redirectedUrls: JList[RedirectedUrl],
  @Persistent var fetchedArticles: JList[String],
  @Persistent var count: Int) {

  /**
   * Adding URLs to the list of raw urls
   */
  def addUrls(urls: Seq[String]) = urls match {
    case urls if(null == urls || urls.isEmpty) =>
    case _ => for(url <- urls) if(!hasUrl(url)) internRedirectedUrls.add(getRedirectUrl(url))
  }

  def internRedirectedUrls = redirectedUrls match {
    case null => {
      redirectedUrls = new ArrayList
      redirectedUrls
    }
    case _ => redirectedUrls
  }

  private def hasUrl(url: String): Boolean = redirectedUrls match {
    case null => false
    case urls => urls exists (a => url == a.rawUrl || url == a.resolvedUrl)
  }

  def getRedirectUrl(url: String) = {
    query(PME.pmfInstance.getPersistenceManager()) { pm =>
      pm.getObjectById(classOf[RedirectedUrl], url)
    } match {
      case None => persist(new RedirectedUrl(createStringFromKey[RedirectedUrl](url), url, null, false))
      case Some(value) => value
    }
  }
}
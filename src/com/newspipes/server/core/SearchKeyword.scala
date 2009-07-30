package com.newspipes.server.core

import client.Article
import google.appengine.api.datastore.KeyFactory
import java.util.{List => JList}
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
  @Persistent var redirectedUrl: JList[RedirectedUrl],
  @Persistent var fetchedArticles: JList[String],
  @Persistent var count: Int) {

  /**
   * Adding URLs to the list of raw urls
   */
  def addUrls(urls: Seq[String]) = for(url <- urls) if(!hasUrl(url)) redirectedUrl.add(getRedirectUrl(url))

  private def hasUrl(url: String): Boolean = redirectedUrl exists (a => url == a.rawUrl || url == a.resolvedUrl)

  def getRedirectUrl(url: String) = {
    query(PME.pmfInstance.getPersistenceManager()) { pm =>
      pm.getObjectById(classOf[RedirectedUrl], url)
    } match {
      case None => persist(new RedirectedUrl(createStringFromKey[RedirectedUrl](url), url, null, false))
      case Some(value) => value
    }
  }
}
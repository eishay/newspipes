package com.newspipes.server.core


import client.{Article, NewsPipesService}
import google.appengine.api.datastore._
import google.appengine.api.datastore.Query._
import google.gwt.user.server.rpc.RemoteServiceServlet
import java.util.logging.Logger
import java.util.{ArrayList, Random, List => JList}
import javax.jdo.{JDOObjectNotFoundException, PersistenceManager, JDOEntityManager}
import ServiceUtils._
import TwitterSearch._

object NewsPipesServiceImpl{
  val logger = Logger.getLogger(classOf[NewsPipesServiceImpl].getName())
  val ds = DatastoreServiceFactory.getDatastoreService()
  val random = new Random()
}

class NewsPipesServiceImpl extends RemoteServiceServlet with NewsPipesService{
  import NewsPipesServiceImpl._

  def search(clientID: String, keyword: String): Article = {
    println("[" + clientID + "] searching for: " + keyword)
    val session = getSession
//    val serverInfo = getServletContext().getServerInfo()
    val searchKeyword = getKeyword(keyword)
    val urls = searchForUrls(searchKeyword)
    searchKeyword.addUrls(urls)
    val url = urls(random.nextInt(urls.size))
    val article = getArticle(url)
    println("article in session = " + session.getValue("articles"))
    session.putValue("articles", article)
    article
  }

  private def getArticle(url: String) = {
    var (title, fullUrl) = ArticleFetcher.fetchTitle(url)
    println("found title = " + title + " and url = " + fullUrl)

    query(PME.pmfInstance.getPersistenceManager()) { pm =>
      val existingArticle: Article = pm.getObjectById(classOf[Article], fullUrl)
      existingArticle.incrementCount()
      existingArticle
    } match {
      case None => {
        val normalizedTitle = title match {
          case None => null
          case Some(text) => trim(text)
        }
        persist(new Article(createStringFromKey[Article](fullUrl), trim(fullUrl), normalizedTitle))
      }
      case Some(value) => {
        println("article found!")
        value
      }
    }
  }

  private def trim(text: String) = if(text.length > 500) (text.substring(0, 497) + "...") else text

  /**
   *  intern a keyword from db
   */
  def getKeyword(keyword: String) = query(PME.pmfInstance.getPersistenceManager()) { pm =>
    val existingKeyword: SearchKeyword = pm.getObjectById(classOf[SearchKeyword], keyword)
    existingKeyword.count += 1
    existingKeyword
  } match {
    case None => persist(new SearchKeyword(createStringFromKey[SearchKeyword](keyword), keyword, new ArrayList, new ArrayList, 1))
    case Some(value) => value
  }


  /**
   * getting session
   */
  def getSession = {
    val session = getThreadLocalRequest.getSession(true)
    println("session id = [" + session.getId + "]")
    session
  }
}
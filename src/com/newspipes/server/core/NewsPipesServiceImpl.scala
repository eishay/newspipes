package com.newspipes.server.core


import client.{Article, NewsPipesService}
import google.appengine.api.datastore._
import google.appengine.api.datastore.Query._
import google.gwt.user.server.rpc.RemoteServiceServlet
import java.util.logging.Logger
import java.util.{ArrayList, Random, List => JList}
import javax.jdo.{JDOObjectNotFoundException, PersistenceManager, JDOEntityManager}

object NewsPipesServiceImpl{
  val logger = Logger.getLogger(classOf[NewsPipesServiceImpl].getName())
  val ds = DatastoreServiceFactory.getDatastoreService()
  val random = new Random()
}

class NewsPipesServiceImpl extends RemoteServiceServlet with NewsPipesService{
  import NewsPipesServiceImpl._

  def search(clientID: String, keyword: String): Article = {
    println("[" + clientID + "] searching for: " + keyword)
    val session = getThreadLocalRequest.getSession(true)
    println("session id = [" + session.getId + "]")

    val serverInfo = getServletContext().getServerInfo()

    val searchKeyword = getKeyword(keyword)
    val urls = TwitterSearch.search(searchKeyword)

    val url = urls(random.nextInt(urls.size))
    val article = getArticle(url)
    println("article in session = " + session.getValue("articles"))
    session.putValue("articles", article)
    article
  }

  private def getArticle(url: String) = {
    var (title, fullUrl) = ArticleFetcher.fetchTitle(url)
    println("found title = " + title + " and url = " + fullUrl)
    val key = KeyFactory.createKey(classOf[Article].getSimpleName, fullUrl)
    val keyAsString = KeyFactory.keyToString(key)

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
        val article = new Article(keyAsString, trim(fullUrl), normalizedTitle)
        execute(PME.pmfInstance.getPersistenceManager()) { pm =>
          pm.makePersistent(article)
        }
        article
      }
      case Some(value) => {
        println("article found!")
        value
      }
    }
  }

  private def trim(text: String) = {
    if(text.length > 500) (text.substring(0, 497) + "...") else text
  }

  /**
   * intern a keyword from db
   */
  def getKeyword(keyword: String) = {
    val key = KeyFactory.createKey(classOf[SearchKeyword].getSimpleName, keyword)
    query(PME.pmfInstance.getPersistenceManager()) { pm =>
      val existingKeyword: SearchKeyword = pm.getObjectById(classOf[SearchKeyword], keyword)
      existingKeyword.count += 1
      existingKeyword
    } match {
      case None => {
        val searchKeyword = new SearchKeyword(KeyFactory.keyToString(key), keyword, new ArrayList, new ArrayList, 1)
        execute(PME.pmfInstance.getPersistenceManager()) { pm =>
          pm.makePersistent(searchKeyword)
        }
        searchKeyword
      }
      case Some(value) => value
    }
  }

  def execute(pm: PersistenceManager)(block: PersistenceManager => Unit): Unit = {
    try {
      block(pm)
    }
    finally{
      pm.close
    }
  }

  def query[A](pm: PersistenceManager)(block: PersistenceManager => A): Option[A] = {
    try {
      pm.setDetachAllOnCommit(true)
      val value = block(pm)
      println("found " + value)
      Some(value)
    }
    catch {
      case e: JDOObjectNotFoundException => {
        println("object not found")
        None
      }
      case any => {
        any.printStackTrace()
        throw any
      }
    }
    finally{
      pm.close
    }
  }
}
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
    val twitter = new TwitterSearch()
    val urls = twitter.search(keyword)

    var key = KeyFactory.createKey(classOf[SearchKeyword].getSimpleName, keyword)
    var searchKeyword = query(PME.pmfInstance.getPersistenceManager()) { pm =>
      pm.setDetachAllOnCommit(true)
      val existingKeyword: SearchKeyword = pm.getObjectById(classOf[SearchKeyword], keyword)
      existingKeyword.count += 1
      existingKeyword
    } match {
      case None => null
      case Some(value) => value
    }
    if(null == searchKeyword){
      searchKeyword = new SearchKeyword(KeyFactory.keyToString(key), keyword, new ArrayList, new ArrayList, 1)
      execute(PME.pmfInstance.getPersistenceManager()) { pm =>
        pm.makePersistent(searchKeyword)
      }
    }


    val url = urls(random.nextInt(urls.size))
    val (title, fullUrl) = ArticleFetcher.fetchTitle(url)
    println("found title = " + title + " and url = " + fullUrl + " from url " + url)
    key = KeyFactory.createKey(classOf[Article].getSimpleName, fullUrl)
    val keyAsString = KeyFactory.keyToString(key)
    
    var article = query(PME.pmfInstance.getPersistenceManager()) { pm =>
      pm.setDetachAllOnCommit(true)
      val existingArticle: Article = pm.getObjectById(classOf[Article], fullUrl)
      existingArticle.incrementCount()
      existingArticle
    } match {
      case None => null
      case Some(value) => value
    }

    if(null == article){
      article = new Article(keyAsString, fullUrl, title)
      execute(PME.pmfInstance.getPersistenceManager()) { pm =>
        pm.makePersistent(article)
      }
    }
    else println("article found!")
    println("article in session = " + session.getValue("articles"))
    session.putValue("articles", article)
    article
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
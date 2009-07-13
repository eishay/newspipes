package com.newspipes.server.core


import client.{Article, NewsPipesService}
import google.appengine.api.datastore._
import google.appengine.api.datastore.Query._
import google.gwt.user.server.rpc.RemoteServiceServlet
import java.util.logging.Logger
import java.util.{Random, List => JList}
import javax.jdo.{JDOObjectNotFoundException, PersistenceManager, JDOEntityManager}
import org.datanucleus.exceptions.NucleusObjectNotFoundException

object NewsPipesServiceImpl{
  val logger = Logger.getLogger(classOf[NewsPipesServiceImpl].getName())
  val ds = DatastoreServiceFactory.getDatastoreService()
  val random = new Random()
}

class NewsPipesServiceImpl extends RemoteServiceServlet with NewsPipesService{
  import NewsPipesServiceImpl._

  def search(sessionID: String, keyword: String): Article = {
    println("[" + sessionID + "] searching for: " + keyword)
    val session = Session.getSession(sessionID)

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
      searchKeyword = new SearchKeyword(KeyFactory.keyToString(key), keyword, Nil, Nil, 1)
      execute(PME.pmfInstance.getPersistenceManager()) { pm =>
        pm.makePersistent(searchKeyword)
      }
    }


    val url = urls(random.nextInt(urls.size))
    val (title, fullUrl) = ArticleFetcher.fetchTitle(url)
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
    println("1. session = " + session.getValue)
    session.updateValue(article.getUrl)
    println("2. session = " + session.getValue)
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
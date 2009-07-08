package com.newspipes.server.core


import client.{Article, NewsPipesService}
import google.appengine.api.datastore._
import google.appengine.api.datastore.Query._
import google.gwt.user.server.rpc.RemoteServiceServlet
import java.util.logging.Logger
import java.util.Random
import javax.jdo.{JDOObjectNotFoundException, PersistenceManager, JDOEntityManager}
import org.datanucleus.exceptions.NucleusObjectNotFoundException
object NewsPipesServiceImpl{
  val logger = Logger.getLogger(classOf[NewsPipesServiceImpl].getName())
  val ds = DatastoreServiceFactory.getDatastoreService()
  val random = new Random()
}

class NewsPipesServiceImpl extends RemoteServiceServlet with NewsPipesService{
  import NewsPipesServiceImpl._

  def search(keyword: String): Article = {
    println("searching for: " + keyword)

    val serverInfo = getServletContext().getServerInfo()
    val twitter = new TwitterSearch()
    val urls = twitter.search(keyword)

    val url = urls(random.nextInt(Math.min(urls.size, 3)))
    val title = ArticleFetcher.fetchTitle(url)
    val key = KeyFactory.createKey(classOf[Article].getSimpleName, url)
    var article = query(PME.pmfInstance.getPersistenceManager()) { pm =>
      pm.setDetachAllOnCommit(true)
      val existingArticle: Article = pm.getObjectById(classOf[Article], url)
      existingArticle.incrementCount()
      existingArticle
    }
    if(null == article){
      article = new Article(KeyFactory.keyToString(key), url, title)
      execute(PME.pmfInstance.getPersistenceManager()) { pm =>
        pm.makePersistent(article)
      }
    }
    else println("article found!")
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

  def query(pm: PersistenceManager)(block: PersistenceManager => Article): Article = {
    try {
      block(pm)
    }
    catch {
      case e: JDOObjectNotFoundException => {
        println("object not found")
        null
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
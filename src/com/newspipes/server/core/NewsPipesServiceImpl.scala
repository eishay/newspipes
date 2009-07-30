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


  /**
   * getting session
   */
  def getSession = {
    val session = getThreadLocalRequest.getSession(true)
    println("session id = [" + session.getId + "]")
    session
  }
}
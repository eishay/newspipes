package com.newspipes.server.core


import client.{Article, NewsPipesService}
import google.appengine.api.datastore._
import google.appengine.api.datastore.Query._
import google.gwt.user.server.rpc.RemoteServiceServlet
import java.util.logging.Logger

object NewsPipesServiceImpl{
  val logger = Logger.getLogger(classOf[NewsPipesServiceImpl].getName())
  val ds = DatastoreServiceFactory.getDatastoreService()
}

class NewsPipesServiceImpl extends RemoteServiceServlet with NewsPipesService{
  import NewsPipesServiceImpl.{logger, ds}

  def search(keyword: String): Article = {
    println("searching for: " + keyword)

    val keywordKey = KeyFactory.
    keywordQuery.addFilter("keyword", FilterOperator.EQUAL, keyword)
    val it = ds.prepare(keywordQuery).asIterator
    while(it.hasNext) println (it.next.toString)

    val keywordEntity = new Entity("Keyword")
    keywordEntity.setProperty("keyword", keyword)
    keywordEntity.
    ds.put(keywordEntity)
    println("added entity " + keywordEntity + " with key " + keywordEntity.getKey.gettoString)
    val serverInfo = getServletContext().getServerInfo()
    val twitter = new TwitterSearch()
    val urls = twitter.search(keyword)

    ArticleFetcher.fetch(urls(0))
  }
}
package com.newspipes.server.core


import client.{Article, NewsPipesService}
import google.gwt.user.server.rpc.RemoteServiceServlet

class NewsPipesServiceImpl extends RemoteServiceServlet with NewsPipesService
{
  def search(input: String): Article = {
    val serverInfo = getServletContext().getServerInfo()
    //getThreadLocalRequest().getHeader("User-Agent");
    val twitter = new TwitterSearch()
    val urls = twitter.search(input)
//    val sb = new StringBuilder()
//    sb.append("For keyword [" + input + "] we found the following URLs:<br>")
//    urls foreach (url => sb.append("<a href=\"" + url + "\">" + url + "</a><br/>"))
//    sb.toString()
    new Article(urls(0), 1)
  }
}
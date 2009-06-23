package com.newspipes.server.core


import client.Article
import google.appengine.api.urlfetch.URLFetchServiceFactory
import java.net.URL
import java.util.regex.Pattern

object ArticleFetcher {
  val urlFetchService = URLFetchServiceFactory.getURLFetchService
  val titlePattern = Pattern.compile("""<title.*>(.*?)(</title>""", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE)
  val headerPattern = Pattern.compile("""<h.*>(.*?)</h?>""", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE)

  def fetch(url: String) : Article = {
    val response = urlFetchService.fetch(new URL(url))
    //extract Content-Type / charset from header
    val content = new String(response.getContent)
    var matcher = titlePattern.matcher(content)
    val title = matcher.matches match {
      case true => matcher.group(1)
      case false => {
        matcher = headerPattern.matcher(content)
        matcher.matches match {
          case true => matcher.group(1)
          case false => url
        }
      }
    }
    new Article(url, title)
  }
}
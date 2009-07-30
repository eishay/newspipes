package com.newspipes.server.core


import client.Article
import google.appengine.api.datastore.KeyFactory
import javax.jdo.{PersistenceManager, JDOObjectNotFoundException}
import reflect.Manifest
import java.util.{ArrayList, Random, List => JList}

object ServiceUtils {

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

  def persist[A](a: A): A = {
    execute(PME.pmfInstance.getPersistenceManager()) { pm =>
      pm.makePersistent(a)
    }
    a
  }

  def createStringFromKey[T](v: String)(implicit m: Manifest[T]) : String = KeyFactory.keyToString(KeyFactory.createKey(m.erasure.getSimpleName, v))

  def getArticle(url: String) = {
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
}
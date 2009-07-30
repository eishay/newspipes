package com.newspipes.server.core


import google.appengine.api.datastore.KeyFactory
import javax.jdo.{PersistenceManager, JDOObjectNotFoundException}
import reflect.Manifest

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
}
package com.newspipes.server.core


import google.appengine.api.memcache.{MemcacheService, Expiration, MemcacheServiceFactory}
import java.io.Serializable

/**
 * Http Session implementation using memcache
 */
object Session {
  private[Session] val memcache = MemcacheServiceFactory.getMemcacheService()
  private val expiration = Expiration.byDeltaSeconds(60 * 24)

  def getSession(id: String) = memcache.get(id) match {
    case null => {
      val session = new Session(id)
      session.updateValue(null)
      session
    }
    case obj => obj.asInstanceOf[Session]
  }
}

class Session(val id: String) extends Serializable{
  import Session._
  
  private def updateSession = memcache.put(id, this, expiration, MemcacheService.SetPolicy.SET_ALWAYS)

  private var value: String = null

  def updateValue(newVal: String) {
    value = newVal
    updateSession
  }

  def getValue = value
}
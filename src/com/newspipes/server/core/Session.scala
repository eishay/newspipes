package com.newspipes.server.core


import google.appengine.api.memcache.{MemcacheService, Expiration, MemcacheServiceFactory}
import java.io.Serializable

class Session(val id: String) extends Serializable{
}
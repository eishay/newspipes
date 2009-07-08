package com.newspipes.server.core


import javax.jdo.{JDOHelper, PersistenceManagerFactory}

object PME {
    val pmfInstance = JDOHelper.getPersistenceManagerFactory("transactions-optional")
}
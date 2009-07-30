package com.newspipes.server.core


import java.util.{List => JList}
import javax.jdo.annotations.{Extension, Persistent, IdGeneratorStrategy, PrimaryKey, PersistenceCapable, IdentityType}

@PersistenceCapable{val identityType = IdentityType.APPLICATION}
class RedirectedUrl(
  @PrimaryKey
  @Persistent{val valueStrategy = IdGeneratorStrategy.IDENTITY}
  @Extension{val vendorName="datanucleus", val key="gae.encoded-pk", val value="true"}
  var key: String,
  @Persistent var rawUrl: String,
  @Persistent var resolvedUrl: String,
  @Persistent var resolved: boolean)
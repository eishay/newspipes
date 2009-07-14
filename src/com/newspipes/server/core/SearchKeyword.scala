package com.newspipes.server.core

import java.util.{List => JList}
import javax.jdo.annotations.{Extension, Persistent, IdGeneratorStrategy, PrimaryKey, PersistenceCapable, IdentityType}

@PersistenceCapable{val identityType = IdentityType.APPLICATION}
class SearchKeyword(
  @PrimaryKey
  @Persistent{val valueStrategy = IdGeneratorStrategy.IDENTITY}
  @Extension{val vendorName="datanucleus", val key="gae.encoded-pk", val value="true"}
  var key: String,
  @Persistent var value: String,
  @Persistent var unfetchedUrls: JList[String],
  @Persistent var fetchedArticles: JList[String],
  @Persistent var count: Int)
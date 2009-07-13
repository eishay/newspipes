package com.newspipes.server.core

import javax.jdo.annotations.{Extension, Persistent, IdGeneratorStrategy, PrimaryKey, PersistenceCapable, IdentityType}

@PersistenceCapable{val identityType = IdentityType.APPLICATION}
class SearchKeyword(
  @PrimaryKey
  @Persistent{val valueStrategy = IdGeneratorStrategy.IDENTITY}
  @Extension{val vendorName="datanucleus", val key="gae.encoded-pk", val value="true"}
  var key: String,
  @Persistent var value: String,
  @Persistent var unfetchedUrls: List[String],
  @Persistent var fetchedArticles: List[String],
  @Persistent var count: Int)
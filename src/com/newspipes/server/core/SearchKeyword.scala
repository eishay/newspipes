package com.newspipes.server.core


import google.gwt.user.client.rpc.IsSerializable
import javax.jdo.annotations.{Extension, Persistent, IdGeneratorStrategy, PrimaryKey, PersistenceCapable, IdentityType}

@PersistenceCapable{val identityType = IdentityType.APPLICATION}
class SearchKeyword{
  @PrimaryKey
  @Persistent{val valueStrategy = IdGeneratorStrategy.IDENTITY}
  @Extension{val vendorName="datanucleus", val key="gae.encoded-pk", val value="true"}
  var key: String = null

  @Persistent var value: String = null

  @Persistent var count: Int = 1
}
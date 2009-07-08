package com.newspipes.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import javax.jdo.annotations.*;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Article implements IsSerializable {
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
  private String _key;

  @Persistent
  private String _title;

  @Persistent
  private String _url;

  @Persistent
  private int _count = 1;

  public Article() {}

  public Article(String key, String url, String title) {
    _url = url;
    _title = title;
    _key = key;
  }

  public int getCount() {
    return _count;
  }

  public int incrementCount() {
    _count++;
    return _count;
  }

  public String getUrl() {
    return _url;
  }

  public String getTitle() {
    return _title;
  }
}

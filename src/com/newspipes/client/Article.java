package com.newspipes.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Article implements IsSerializable {

  @Persistent
  private String _title;

  @Persistent
  private String _url;

  @Persistent
  private int _count = 1;

  public Article() {}

  public Article(String url, String title) {
    _url = url;
    _title = title;
  }

  public int getCount() {
    return _count;
  }

  public String getUrl() {
    return _url;
  }

  public String getTitle() {
    return _title;
  }
}

package com.newspipes.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Article implements IsSerializable {
  private String _url;
  private int _count;
  private int _id;

  public Article() {}

  public Article(int id, String url, int count) {
    _url = url;
    _count = count;
  }

  public int getCount() {
    return _count;
  }

  public String getUrl() {
    return _url;
  }

  public int getId() {
    return _id;
  }
}

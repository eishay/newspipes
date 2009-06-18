package com.newspipes.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Article implements IsSerializable {
  private String _url;
  private int _count;

  public Article() {}

  public Article(String url, int count) {
    _url = url;
    _count = count;
  }

  public int getCount() {
    return _count;
  }

  public String getUrl() {
    return _url;
  }
}

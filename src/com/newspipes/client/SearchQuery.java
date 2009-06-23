package com.newspipes.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import java.util.List;
import java.util.ArrayList;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SearchQuery implements IsSerializable{

  @Persistent
  private String _query;

  @Persistent
  private List<Article> _articles = new ArrayList<Article>();

  public SearchQuery(){}

  public SearchQuery(String query){
    _query = query;
  }

  public List<Article> getArticles()
  {
    return _articles;
  }

  public void addArticle(Article a)
  {
    _articles.add(a);
  }
}

package com.newspipes.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>NewsPipesService</code>.
 */
public interface NewsPipesServiceAsync
{
  void search(String input, AsyncCallback<Article> callback);
}

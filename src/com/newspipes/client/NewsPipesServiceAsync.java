package com.newspipes.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/**
 * The async counterpart of <code>NewsPipesService</code>.
 */
public interface NewsPipesServiceAsync
{
  void search(String sessionID, String input, AsyncCallback<Article> callback);
}

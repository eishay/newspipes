package com.newspipes.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("search")
public interface NewsPipesService extends RemoteService {
  Article search(String sessionID, String name);
}

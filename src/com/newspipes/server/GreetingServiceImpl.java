package com.newspipes.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.newspipes.client.GreetingService;
import com.newspipes.server.core.*;
/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService
{
  public String greetServer(String input)
  {
    String serverInfo = getServletContext().getServerInfo();
    String userAgent = getThreadLocalRequest().getHeader("User-Agent");
    TwitterSearch twitter = new TwitterSearch();
    return "Hello, " + input + "!<br><br>I am running " + serverInfo + ".<br><br>It looks like you are using:<br>" + userAgent
     + twitter.search(input);
  }
}

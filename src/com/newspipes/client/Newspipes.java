package com.newspipes.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Random;

import java.util.ArrayList;
import java.util.List;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Newspipes implements EntryPoint
{
  /**
   * The message displayed to the user when the server cannot be reached or
   * returns an error.
   */
  private static final String SERVER_ERROR =
      "An error occurred while " + "attempting to contact the server. Please check your network " + "connection and try again.";

  /**
   * Create a remote service proxy to talk to the server-side NewsPipesService service.
   */
  private final NewsPipesServiceAsync newsPipesService = GWT.create(NewsPipesService.class);

  private String textToServer = null;

  final private VerticalPanel mainPanel = new VerticalPanel();
  final private Button sendButton = new Button("Send");
  final private TextBox nameField = new TextBox();

  final private List existingArticleKeys = new ArrayList();
  private String id = null;

  /**
   * This is the entry point method.
   */
  public void onModuleLoad()
  {
    if(null == id){
      id = Cookies.getCookie("SESSION_ID");
      if(null == id) {
        id = "SESSION_ID." + System.currentTimeMillis() + "." + Random.nextInt() + "." + Random.nextDouble();
        Cookies.setCookie("SESSION_ID", id);
      }
    }
    RootPanel.get("mainPanel").add(mainPanel);
    // http://www.linkedin.com/rss/nus?key=swWAPb7ehmtUSIbXVS0B4DdwJ4MyuH87ubX1-1QUv1Sc4CJ73eJHJ9FkoQmUTJIT
    //nameField.setText("http://www.linkedin.com/rss/nus?key=...");
    nameField.setText("scala");

    // We can add style names to widgets
    sendButton.addStyleName("sendButton");

    // Add the nameField and sendButton to the RootPanel
    // Use RootPanel.get() to get the entire body element
    RootPanel.get("linkedinRssFieldContainer").add(nameField);
    RootPanel.get("sendButtonContainer").add(sendButton);

    // Focus the cursor on the name field when the app loads
    nameField.setFocus(true);
    nameField.selectAll();


    // Create a handler for the sendButton and nameField
    class MyHandler implements ClickHandler, KeyUpHandler
    {
      /**
       * Fired when the user clicks on the sendButton.
       */
      public void onClick(ClickEvent event)
      {
        getContentAndExecute();
      }

      /**
       * Fired when the user types in the nameField.
       */
      public void onKeyUp(KeyUpEvent event)
      {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
        {
          getContentAndExecute();
        }
      }

      private void getContentAndExecute()
      {
        String tmp = nameField.getText();
        if(null == tmp || tmp.trim().length() == 0) return;
        textToServer = tmp.trim();
        sendNameToServer();
      }
    }

    // Add a handler to send the name to the server
    MyHandler handler = new MyHandler();
    sendButton.addClickHandler(handler);
    nameField.addKeyUpHandler(handler);

    Timer timer = new Timer() {
      public void run() {
        if(textToServer == null) return;
        sendNameToServer();
      }
    };
    timer.scheduleRepeating(40000);
  }

  /**
   * Send the name from the nameField to the server and wait for a response.
   */
  private void sendNameToServer()
  {
    sendButton.setEnabled(false);
    newsPipesService.search(id, textToServer, new AsyncCallback<Article>()
    {
      public void onFailure(Throwable caught)
      {
        sendButton.setEnabled(true);
        sendButton.setFocus(true);
      }

      public void onSuccess(Article article)
      {
        addArticleToPanel(article);
        existingArticleKeys.add(article.getKey());
        sendButton.setEnabled(true);
        sendButton.setFocus(true);
      }
    });
  }

  private void addArticleToPanel(Article article) {
    FlowPanel articleWidget = new FlowPanel();
    String times = article.getCount() > 1 ? "times" : "time";
    articleWidget.add(new HTML("Viewd " + article.getCount() + " " + times + " : <a href=\"" + article.getUrl() + "\">" + article.getTitle() + "</a> at <a href=\"" + article.getUrl() + "\">" + article.getUrl() + "</a>"));
    mainPanel.add(articleWidget);

  }
}

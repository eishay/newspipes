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

  final VerticalPanel mainPanel = new VerticalPanel();
  final Button sendButton = new Button("Send");
  final TextBox nameField = new TextBox();

  /**
   * This is the entry point method.
   */
  public void onModuleLoad()
  {
    RootPanel.get("mainPanel").add(mainPanel);
    nameField.setText("scala");

    // We can add style names to widgets
    sendButton.addStyleName("sendButton");

    // Add the nameField and sendButton to the RootPanel
    // Use RootPanel.get() to get the entire body element
    RootPanel.get("nameFieldContainer").add(nameField);
    RootPanel.get("sendButtonContainer").add(sendButton);

    // Focus the cursor on the name field when the app loads
    nameField.setFocus(true);
    nameField.selectAll();

    // Create the popup dialog box
//    final DialogBox dialogBox = new DialogBox();
//    dialogBox.setText("Remote Procedure Call");
//    dialogBox.setAnimationEnabled(true);
//    final Button closeButton = new Button("Close");
//    // We can set the id of a widget by accessing its Element
//    closeButton.getElement().setId("closeButton");
//    final Label textToServerLabel = new Label();
//    final HTML serverResponseLabel = new HTML();
//    VerticalPanel dialogVPanel = new VerticalPanel();
//    dialogVPanel.addStyleName("dialogVPanel");
//    dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
//    dialogVPanel.add(textToServerLabel);
//    dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
//    dialogVPanel.add(serverResponseLabel);
//    dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
//    dialogVPanel.add(closeButton);
//    dialogBox.setWidget(dialogVPanel);

//    // Add a handler to close the DialogBox
//    closeButton.addClickHandler(new ClickHandler()
//    {
//      public void onClick(ClickEvent event)
//      {
//        dialogBox.hide();
//        sendButton.setEnabled(true);
//        sendButton.setFocus(true);
//      }
//    });

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
    timer.scheduleRepeating(10000);
  }

  /**
   * Send the name from the nameField to the server and wait for a response.
   */
  private void sendNameToServer()
  {
    sendButton.setEnabled(false);
    newsPipesService.search(textToServer, new AsyncCallback<Article>()
    {
      public void onFailure(Throwable caught)
      {
        sendButton.setEnabled(true);
        sendButton.setFocus(true);
      }

      public void onSuccess(Article article)
      {
        addArticleToPanel(article);
        sendButton.setEnabled(true);
        sendButton.setFocus(true);
      }
    });
  }

  private void addArticleToPanel(Article article) {
    FlowPanel articleWidget = new FlowPanel();
    articleWidget.add(new Label("[" + article.getCount() + "]"));
    articleWidget.add(new HTML("<a href=\"" + article.getUrl() + "\">" + article.getUrl() + "</a>"));
    mainPanel.add(articleWidget);
  }

}

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.PrintWriter;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");

    boolean isLoggedIn = false;

    PrintWriter out = response.getWriter();
    UserService userService = UserServiceFactory.getUserService();
    String loginUrl = userService.createLoginURL("/");

    if (userService.isUserLoggedIn()) {
      isLoggedIn = true;
      String userEmail = userService.getCurrentUser().getEmail();

      String nickname = getUserNickname(userService.getCurrentUser().getUserId());
      out.println("<p>Set your nickname here:</p>");
      out.println("<form method=\"POST\" action=\"/nickname\">");
      out.println("<input name=\"nickname\" value=\"" + nickname + "\" />");
      out.println("<br/>");
      out.println("<button>Submit</button>");
      out.println("</form>");

    
      response.getWriter().println("<p>Hello " + userEmail + "!</p>");
      response.getWriter().println("<p>Logout <a href=\"" + loginUrl + "\">here</a>.</p>");
    } else {

      response.getWriter().println("<p>Hello stranger.</p>");
      response.getWriter().println("<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>");
    }

  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/login");
      return;
    }

    String nickname = request.getParameter("nickname");
    String id = userService.getCurrentUser().getUserId();
    String userEmail = userService.getCurrentUser().getEmail();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity entity = new Entity("UserInfo", id);
    entity.setProperty("id", id);
    entity.setProperty("nickname", nickname);
    entity.setProperty("email", userEmail);
    // The put() function automatically inserts new data or updates existing data based on ID
    datastore.put(entity);

    response.sendRedirect("/");
  }

  /**
   * Returns the nickname of the user with id, or empty String if the user has not set a nickname.
   */
  private String getUserNickname(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("blogMessage").addSort("time", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      return "";
    }
    String nickname = (String) entity.getProperty("nickname");
    return nickname;
  }
}

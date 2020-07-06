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

  private final static String NICKNAME = "nickname";

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
    
      if (nickname.isEmpty()) { 
        out.println("<p>You don't have a nickname</p>");
      } else {
        out.println("<p>Your current nickname is " + nickname + ". If you'd like to change it, enter a new one below.</p>");}   

      out.println("<p>Set your nickname here:</p>");
      out.println("<form method=\"POST\" action=\"/login\">");
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

    String nickname = request.getParameter(NICKNAME);
    String id = userService.getCurrentUser().getUserId();
    String userEmail = userService.getCurrentUser().getEmail();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity entity = new Entity("blogMessage", id);
    entity.setProperty("id", id);
    entity.setProperty("nickname", nickname);
    entity.setProperty("email", userEmail);
    datastore.put(entity);

    response.sendRedirect("/");
  }

  private String getUserNickname(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
        new Query("blogMessage")
            .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      return "";
    }
    String nickname = (String) entity.getProperty(NICKNAME);
    return nickname;
  }
}

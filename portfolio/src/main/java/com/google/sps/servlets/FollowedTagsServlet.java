// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@WebServlet("/follow-tags")
public class FollowedTagsServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    String userEmail = (String) userService.getCurrentUser().getEmail();
    
    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(LoadFollowedTags.getFollowedTags(userEmail)));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    String userEmail = (String) userService.getCurrentUser().getEmail();

    String[] receivedTags = request.getParameterValues(BlogConstants.TAG_PARAMETER);
    if (receivedTags == null || receivedTags.length == 0) {
      return;
    }
    List<String> receivedTagsList = new LinkedList<String>(Arrays.asList(receivedTags));

    // Prevent multiple instances of a followed tag in datastore.
    removeFollowedTags(receivedTagsList);

    putTagsInDatastore(receivedTagsList, userEmail);

    response.setContentType("text/html");
    if (receivedTagsList.size() == 1) {
      response.getWriter().println("<h2>Success! You now follow this tag.</h2>");
    } else if (receivedTagsList.size() > 1) {
      response.getWriter().println("<h2>Success! You now follow these tags.</h2>");
    } else {
      response.getWriter().println("<h2>Failed! You follow the tag(s) already.</h2>");
    }
  }

  private void putTagsInDatastore(List<String> tags, String email) {
    for (String tag : tags) {
      Entity followedTag = new Entity(BlogConstants.TAG_QUERY);
      followedTag.setProperty("tag", tag);
      followedTag.setProperty("email", email);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(followedTag);
    }
  }

  private void removeFollowedTags(List<String> tagsReceived) {
    UserService userService = UserServiceFactory.getUserService();
    String userEmail = (String) userService.getCurrentUser().getEmail();

    List<String> userFollowedTags = new ArrayList<String>();
    for (FollowedTag tagObject : LoadFollowedTags.getFollowedTags(userEmail)) {
      userFollowedTags.add(tagObject.getTag());
    }

    List<String> duplicateBin = new ArrayList<String>();
    for (String tag : tagsReceived) {
      if (userFollowedTags.contains(tag)) {
        duplicateBin.add(tag);
      }
    }
    tagsReceived.removeAll(duplicateBin);
  }
}
/* End Of File */

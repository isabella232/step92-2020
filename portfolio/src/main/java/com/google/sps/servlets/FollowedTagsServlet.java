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

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.BlogMessage;
import com.google.sps.data.BlogHashMap;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/follow-tags")
public class FollowedTagsServlet extends HttpServlet {
  List<String> followedPosts= new ArrayList<>();
  public static String tagQuery = "followedTag";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

  }
    
  /**
  * Converts a ServerStats instance into a JSON string using the Gson library
  */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
     
    String commentType = request.getParameter("tags");
    if (commentType == null) {
      return;
    }
    String email = (String) userService.getCurrentUser().getEmail();

    followedPosts.add(commentType);

    Entity followedTag = new Entity(tagQuery);
    followedTag.setProperty("tag", commentType);
    followedTag.setProperty("email", email);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(followedTag);
  }
}
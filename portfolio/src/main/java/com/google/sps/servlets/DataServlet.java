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
import com.google.sps.data.InternalTags;
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
import java.util.Iterator;

@WebServlet("/data")
public class DataServlet extends HttpServlet {
    int numberOfCommentsToDisplay = 0;
    List<String> tagsToSearch = new ArrayList<String>();
    List<String> messageReplies = new ArrayList<String>(); // TODO: Handle replies later.
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException { 
      List<BlogMessage> messages = new ArrayList<>();
      Query query = new Query("blogMessage").addSort("time", SortDirection.ASCENDING);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      PreparedQuery results = datastore.prepare(query);

      UserService userService = UserServiceFactory.getUserService();

      for (Entity entity : results.asIterable()) {
        long messageId = entity.getKey().getId();
        long timestamp = (long) entity.getProperty("time");
        String tag = (String) entity.getProperty("tag");
        String comment = (String) entity.getProperty("text");
        String nickname = (String) entity.getProperty("nickname");
        String email = (String) userService.getCurrentUser().getEmail();
        //String image = (String) entity.getProperty("imgUrl");
        ArrayList<String> message_Replies = (ArrayList) entity.getProperty("replies");
        BlogMessage message = new BlogMessage(messageId, tag, comment, nickname, email, message_Replies, timestamp);
        messages.add(message);
      }
      
      // Create BlogHashMap Object and put BlogMessages in the map.
      BlogHashMap blogMap = new BlogHashMap();
      blogMap.putInMap(messages);

       // Load messages from BlogHashMap and respond with gson.
      LinkedList<BlogMessage> loadedBlogMessages = blogMap.getMessages(tagsToSearch, numberOfCommentsToDisplay);
    
      Gson gson = new Gson();
      response.setContentType("application/json;");
      
      response.getWriter().println(gson.toJson(limitedBlogMessages));
      return;
    }
    
   @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      // Get Post parameters.
      String message = request.getParameter("text-input");
      String nickname = request.getParameter("sender");
      String loadFactor = request.getParameter("load_factor");
      String postTag = getTagParameter(request, "tags", InternalTags.defaultTag());
      
      //TODO: Handle image file sent with FormData.
      //String imageUrl = getUploadedFileUrl(request, imagePath);
      
      // Only put BlogMessages with a message and tag in datastore.
      if (message.equals("") || message.equals(null) || postTag == null) {
        return;
      }

      long timestamp = System.currentTimeMillis();
      Entity blogMessageEntity = new Entity("blogMessage");
      blogMessageEntity.setProperty("nickname", nickname);
      blogMessageEntity.setProperty("text", message);
      // blogMessageEntity.setProperty("imgUrl", imageUrl);
      blogMessageEntity.setProperty("time", timestamp);
      blogMessageEntity.setProperty("tag", postTag);
      blogMessageEntity.setProperty("replies", messageReplies);
    
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(blogMessageEntity);

      // To get the recently posted message, add its tag to the |tagsToSearch| list.
      // If the list has tags already, clear them before adding the tag.
      // Our goal is to get the recently posted message.
      if (!tagsToSearch.isEmpty()) {
        tagsToSearch.clear();
      }
      tagsToSearch.add(postTag);

      // Everytime a post comes through, the javascript function that handles the Post assigns
      // a load_factor value of 1.
      // We assign that to the |numberOfCommentsToDisplay|. 
      if (loadFactor != null) {
        // we don't catch since this is always 1.
        numberOfCommentsToDisplay = Integer.parseInt(loadFactor); 
      }
      
      // Now we call |doGet| to load and respond with the message.
      // |doGet| passes the |tagsToSearch| and |numberOfCommentsToDisplay| to the 
      // BlogHashMap's getMessages method, which responds with the recent post.
      doGet(request, response);
    }

    // Returns the requested parameter, or the default value if the parameter is null.
    private String getTagParameter(HttpServletRequest request, String name, String defaultValue) {
      String value = request.getParameter(name);
      if (value == null) {
        return defaultValue;
      }
      return value;
    }

    // Returns a URL that points to the uploaded file, or null if the user didn't upload a file.
    private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
      BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
      Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
      List<BlobKey> blobKeys = blobs.get(formInputElementName);

      if (blobKeys == null || blobKeys.isEmpty()) {
        return null;
      }

      // Our form only contains a single file input, so get the first index.
      BlobKey blobKey = blobKeys.get(0);

      // User submitted form without selecting a file, so we can't get a URL. (live server)
      BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
      if (blobInfo.getSize() == 0) {
        blobstoreService.delete(blobKey);
        return null;
      }

      // We could check the validity of the file here, e.g. to make sure it's an image file
      // https://stackoverflow.com/q/10779564/873165

      // Use ImagesService to get a URL that points to the uploaded file.
      ImagesService imagesService = ImagesServiceFactory.getImagesService();
      ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);

      // To support running in Google Cloud Shell with AppEngine's dev server, we must use the relative
      // path to the image, rather than the path returned by imagesService which contains a host.
      try {
        URL url = new URL(imagesService.getServingUrl(options));
        return url.getPath();
      } catch (MalformedURLException e) {
        return imagesService.getServingUrl(options);
      }
    }

}
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
import com.google.gson.Gson;
import com.google.sps.data.BlogMessage;
import com.google.sps.data.BlogHashMap;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    int numberOfCommentsToDisplay = 0;
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {      
      if (numberOfCommentsToDisplay < 1 || numberOfCommentsToDisplay > 100) {
        response.setContentType("text/html");
        response.getWriter().println("Please enter an integer between 1 and 100.");
        return;
      }

      List<BlogMessage> messages = new ArrayList<>();
      Query query = new Query("blogMessage").addSort("time", SortDirection.DESCENDING);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      PreparedQuery results = datastore.prepare(query);

      for (Entity entity : results.asIterable()) {
        long messageId = entity.getKey().getId();
        long timestamp = (long) entity.getProperty("time");
        String tags = (String) entity.getProperty("tag");
        String comment = (String) entity.getProperty("text");
        String sender = (String) entity.getProperty("sender");
        String image = (String) entity.getProperty("imgUrl");
        ArrayList<String> messageReplies = (ArrayList) entity.getProperty("replies");
        BlogMessage message = new BlogMessage(messageId, tags, comment, image, sender, messageReplies, timestamp);
        messages.add(message);
      }
      
      // Create BlogHashMap Object and put BlogMessages in the map.
      BlogHashMap blogMap = new BlogHashMap();
      blogMap.putInMap(messages);

      // If (user loads all BlogMessages) 
      LinkedList<BlogMessage> allBlogMessages = blogMap.getMessages();

      // If (user loads BlogMessages for a specific tag)
      String tagToSearch = ""; // we'll get the input later.
      LinkedList<BlogMessage> BlogMessagesForTag = blogMap.getMessages(tagToSearch);

      // If (user loads all BlogMessages for a list of tags)
      List<String> tagsToSearch = new ArrayList<String>();
      tagsToSearch.add(""); // we'll get inputs later.

      LinkedList<BlogMessage> BlogMessagesForTags = blogMap.getMessages(tagsToSearch);
    

      Gson gson = new Gson();
      response.setContentType("application/json;");

      if(numberOfCommentsToDisplay == 0){
        response.getWriter().println(gson.toJson(allBlogMessages)); // set a default amount later.
        return;
      }

      /** TODO: 
            add functionality for next cases => 
            1. user specifies amount for all messages
            2. user specifies amount for messages under a tag
            3. user specifies amount for all messages under a list of tags
        */ 

    }
    

    /**
   * Converts a ServerStats instance into a JSON string using the Gson library
   */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      // Get the message entered by the user.
      String message = request.getParameter("text-input");

      // Get sender.
      String sender = getParameter(request, "sender", "Steven");

      // Get type of comment.
      String commentType = getParameter(request, "tags", "Default");
      
      //Get number of comments.
      numberOfCommentsToDisplay = getNumberOfCommentsToDisplay(request);

      // Get the URL of the image that the user uploaded to Blobstore.
      String imageUrl = getUploadedFileUrl(request, "image");

      //Get replies.
      String messageRepliesString = getParameter(request, "replies", "");
	    String messageRepliesArray[] = messageRepliesString.split(",");
	    List<String> messageReplies = new ArrayList<String>();
	    messageReplies = Arrays.asList(messageRepliesArray);
      
      // Get system time.
      long timestamp = System.currentTimeMillis();

      // Store image and comment in datastore.
      Entity blogMessageEntity = new Entity("blogMessage");
      blogMessageEntity.setProperty("sender", sender);
      blogMessageEntity.setProperty("text", message);
      blogMessageEntity.setProperty("imgUrl", imageUrl);
      blogMessageEntity.setProperty("time", timestamp);
      blogMessageEntity.setProperty("tag", commentType);
      blogMessageEntity.setProperty("replies", messageReplies);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(blogMessageEntity);

      // Redirect back to the HTML page.
      response.sendRedirect("/index.html");
    }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client.
   */
    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
      String value = request.getParameter(name);
      if (value == null) {
        return defaultValue;
      }
      return value;
    }

    /* Returns number of comments to display */
    private int getNumberOfCommentsToDisplay(HttpServletRequest request) {
      // Get the input from the form.
      String numberOfCommentsString = getParameter(request, "comments-choice", "0");
      // Convert the input to an int.
      int numberOfComments;
      try {
        numberOfComments = Integer.parseInt(numberOfCommentsString);
      } catch (NumberFormatException e) {
        System.err.println("Could not convert to int: " + numberOfCommentsString);
        return 1;
      }
      return numberOfComments;
    }

    /** Returns a URL that points to the uploaded file, or null if the user didn't upload a file. */
    private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
      BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
      Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
      List<BlobKey> blobKeys = blobs.get(formInputElementName);

      // User submitted form without selecting a file, so we can't get a URL. (dev server)
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

    //Get data out of hash table.
    /*private List<BlogMessage> getInfoFromHashTable(String tag, BlogHashMap<String,LinkedList<String>> table){
      List<BlogMessage> messages = new ArrayList<>();
      Set<String> keys = table.keySet();
      for(String key: keys){
        messages.add(table.get(key));
      }
      return messages;
    }*/
}
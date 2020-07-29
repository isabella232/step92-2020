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
import com.google.sps.data.BlogHashMap;
import com.google.sps.data.BlogMessage;
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
 
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    response.setContentType("application/json;");
 
    response.getWriter().println(gson.toJson(DatastoreUtils.doGetFromDatastore()));
  }
 
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get Post parameters.
    // We can't process an empty message.
    // Also a post without a tag will be assigned the default tag.
    String message = request.getParameter(BlogConstants.MESSAGE_PARAMETER);
    if (message == null || message.isEmpty()) {
      response.setContentType("text/html");
      response.getWriter().println("<h2> Cannot post empty message.</h2>");
      return;
    }
 
    String nickname = request.getParameter(BlogConstants.SENDER_PARAMETER);
    String postTag = request.getParameter(BlogConstants.TAG_PARAMETER);
    String parentIDString = request.getParameter(BlogConstants.PARENTID_PARAMETER);
    long parentID = Long.parseLong(parentIDString);
    if (postTag == null || postTag.isEmpty()) {
      postTag = InternalTags.defaultTag();
    } 
 
    // TODO: Handle image file sent with FormData.
 
    putBlogsInDatastore(postTag, message, nickname, parentID);
 
    // Respond with the recent post.
    // |LoadAllBlogsOrLast| returns the recent post if false is passed.
    Gson gson = new Gson();
 
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(DatastoreUtils.LoadAllBlogsOrLast(/*all=*/false)));   
  }

  // Takes BlogMessage details and puts in datastore.
  private void putBlogsInDatastore(
        String tag, String message, String nickname, long parentID) {
    // Only put BlogMessages with a message in datastore.
    if (message == null || message.isEmpty()) {
      return;
    }
    Entity blogMessageEntity = new Entity(BlogConstants.BLOG_ENTITY_KIND);
    blogMessageEntity.setProperty("nickname", nickname);
    blogMessageEntity.setProperty("text", message);
    blogMessageEntity.setProperty("time", System.currentTimeMillis());
    blogMessageEntity.setProperty("tag", tag);
    blogMessageEntity.setProperty("parentID", parentID);
 
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(blogMessageEntity);
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

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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.BlogMessage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class DatastoreUtils {
  public static LinkedList<BlogMessage> doGetFromDatastore () {
    // Get BlogMessages from Datastore.
    List<BlogMessage> blogMessages = RepliesUtils.putRepliesWithPosts(LoadAllBlogsOrLast(/*all=*/ true));
    // TODO: Get these from client.
    int numberOfCommentsToDisplay = 0;
    List<String> tagsToSearch = new ArrayList<String>();
    TagsUtils.updateTagsToSearch(tagsToSearch);

    return BlogHashMapUtils.sortAndLoadFromBlogHashMap(
        blogMessages, tagsToSearch, numberOfCommentsToDisplay);
  }

  // Loads all BlogMessages from Datastore if true is passed,
  // Otherwise if false is passed only the recent post is loaded.
  // This is useful because each time a user posts, we only load the last BlogMessage
  // Which reduces the time to load datastore.
  public static List<BlogMessage> LoadAllBlogsOrLast(boolean all) {
    List<BlogMessage> blogMessages = new ArrayList<BlogMessage>();
    Query query = new Query(BlogConstants.BLOG_ENTITY_KIND);
 
    if (all) {
      query.addSort("time", SortDirection.ASCENDING);
    } else {
      query.addSort("time", SortDirection.DESCENDING);
    }
 
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
      long parentID = (long) entity.getProperty("parentID");
      ArrayList<BlogMessage> messageReplies = new ArrayList<BlogMessage>();
 
      BlogMessage message = new BlogMessage(
            messageId, tag, comment, nickname, email, messageReplies, timestamp, parentID);
      blogMessages.add(message);
      if (!all) {
        break;
      }
    }
    return blogMessages;
  }
}
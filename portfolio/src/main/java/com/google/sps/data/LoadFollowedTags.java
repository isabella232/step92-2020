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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class LoadFollowedTags {
  public static String tagQuery = "followedTag";
  public static List<String> getFollowedTags (String email) {
    List<String> followedTags = new ArrayList<String>();
    PreparedQuery results = getResults(email);

    for (Entity entity : results.asIterable()) {
      String tag = (String) entity.getProperty("tag");
      followedTags.add(tag);
    }
    return followedTags;
  }

  public static Boolean hasFollowedTags (String email) {
    return getFollowedTags(email).isEmpty();
  }

  private static PreparedQuery getResults (String email) {
    Filter tagFilter = new FilterPredicate("email", FilterOperator.EQUAL, email);
    Query query = new Query(tagQuery).setFilter(tagFilter);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    return results;
  }
}
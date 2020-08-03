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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/** Servlet responsible for deleting entities from datastore. */
@WebServlet("/delete-data")
public class DeleteServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long entityID = Long.parseLong(request.getParameter(BlogConstants.ENTITY_ID_PARAMETER));
    String entityKind = request.getParameter(BlogConstants.ENTITY_KIND_PARAMETER);

    Key EntityKey = KeyFactory.createKey(entityKind, entityID);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.delete(EntityKey);

    response.setContentType("text/html");
    if (entityKind.equals(BlogConstants.BLOG_ENTITY_KIND)) {
      response.getWriter().println("Success! Post Deleted.");
    }
    if (entityKind.equals(BlogConstants.TAG_QUERY)) {
      response.getWriter().println("Success! You unfollow this tag.");
    }
  }

}
/* End Of File */

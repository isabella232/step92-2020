package com.google.sps.servlets;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.assertEquals;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;



public class CuratedPostsTest {

  private final LocalServiceTestHelper helper =
    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  private void createDatastoreEntities() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity UserEntity = new Entity("UserLogin");
    UserEntity.setProperty("id", "246810");
    UserEntity.setProperty("nickname", "hasTags");
    UserEntity.setProperty("email", "hasTags@testing.com");
    datastore.put(UserEntity);

    Entity blogMessageEntity = new Entity("blogMessage");
    blogMessageEntity.setProperty("nickname", "test1");
    blogMessageEntity.setProperty("text","test text business" );
    blogMessageEntity.setProperty("time", System.currentTimeMillis());
    blogMessageEntity.setProperty("tag", "#business");
    blogMessageEntity.setProperty("parentID", 0);
    datastore.put(blogMessageEntity);

    Entity followedTag = new Entity("followedTag");
    followedTag.setProperty("tag", "#business");
    followedTag.setProperty("email", "hasTags@testing.com");
    datastore.put(followedTag);
    
  }

  @Test
  public void testFollowsOneTag() {
    createDatastoreEntities();
    List<String> expectedFollowedTags = new ArrayList<String>();
    expectedFollowedTags.add("#business");
    assertEquals(expectedFollowedTags, LoadFollowedTags.getFollowedTags("hasTags@testing.com"));
  }

  @Test
  public void testFollowsNoTags() {
  }
}
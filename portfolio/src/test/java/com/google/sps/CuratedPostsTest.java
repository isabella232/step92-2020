package com.google.sps.servlets;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.assertEquals;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
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

    Entity noTagEntity = new Entity("UserLogin");
    noTagEntity.setProperty("id", "357911");
    noTagEntity.setProperty("nickname", "noTags");
    noTagEntity.setProperty("email", "noTags@testing.com");
    datastore.put(noTagEntity);

    Entity hasTagEntity = new Entity("UserLogin");
    hasTagEntity.setProperty("id", "246810");
    hasTagEntity.setProperty("nickname", "hasTags");
    hasTagEntity.setProperty("email", "hasTags@testing.com");
    datastore.put(hasTagEntity);

    Entity hasMultipleTagsEntity = new Entity("UserLogin");
    hasMultipleTagsEntity.setProperty("id", "123456");
    hasMultipleTagsEntity.setProperty("nickname", "hasMultipleTags");
    hasMultipleTagsEntity.setProperty("email", "hasMultipleTags@testing.com");
    datastore.put(hasMultipleTagsEntity);

    Entity blogMessageEntity = new Entity("blogMessage");
    blogMessageEntity.setProperty("nickname", "test1");
    blogMessageEntity.setProperty("text","test text business" );
    blogMessageEntity.setProperty("time", System.currentTimeMillis());
    blogMessageEntity.setProperty("tag", "#business");
    blogMessageEntity.setProperty("parentID", 0);
    datastore.put(blogMessageEntity);

    Entity hasTagFollowedTags = new Entity("followedTag");
    hasTagFollowedTags.setProperty("tag", "#business");
    hasTagFollowedTags.setProperty("email", "hasTags@testing.com");
    datastore.put(hasTagFollowedTags);

    List<String> hasMultipleTagsFollowed = new ArrayList<String>();
    hasMultipleTagsFollowed.add("#business");
    hasMultipleTagsFollowed.add("#education");
    hasMultipleTagsFollowed.add("#music");
    
    for (int i=0; i<hasMultipleTagsFollowed.size(); i++) {
      Entity hasMultipleTagsFollowedTags = new Entity("followedTag");
      hasMultipleTagsFollowedTags.setProperty("tag", hasMultipleTagsFollowed.get(i));
      hasMultipleTagsFollowedTags.setProperty("email", "hasMultipleTags@testing.com");
      datastore.put(hasMultipleTagsFollowedTags);
    }

  }

  @Test
  public void testFollowsNoTags() {
    createDatastoreEntities();
    List<String> expectedFollowedTags = new ArrayList<String>();
    assertEquals(expectedFollowedTags, LoadFollowedTags.getFollowedTags("noTags@testing.com"));
  }

  @Test
  public void testFollowsOneTag() {
    createDatastoreEntities();
    List<String> expectedFollowedTags = new ArrayList<String>();
    expectedFollowedTags.add("#business");
    assertEquals(expectedFollowedTags, LoadFollowedTags.getFollowedTags("hasTags@testing.com"));
    assertEquals(1, LoadFollowedTags.getFollowedTags("hasTags@testing.com").size());
  }

    @Test
  public void testFollowsMultipleTag() {
    createDatastoreEntities();
    List<String> expectedFollowedTags = new ArrayList<String>();
    expectedFollowedTags.add("#business");
    expectedFollowedTags.add("#education");
    expectedFollowedTags.add("#music");
    assertEquals(expectedFollowedTags, LoadFollowedTags.getFollowedTags("hasMultipleTags@testing.com"));
    assertEquals(3, LoadFollowedTags.getFollowedTags("hasMultipleTags@testing.com").size());
  }

  @Test
  public void testFollowsTagsFalse() {
    createDatastoreEntities();
    Boolean expectedFollowedTags = false;
    assertEquals(expectedFollowedTags, LoadFollowedTags.hasFollowedTags("noTags@testing.com"));
  }

  @Test
  public void testFollowsTagsTrue() {
    createDatastoreEntities();
    Boolean expectedFollowedTags = true;
    assertEquals(expectedFollowedTags, LoadFollowedTags.hasFollowedTags("hasTags@testing.com"));
    assertEquals(expectedFollowedTags, LoadFollowedTags.hasFollowedTags("hasMultipleTags@testing.com"));
  }
}
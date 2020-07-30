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
  public static String TAG = "tag";
  public static String NO_TAGS_EMAIL = "noTags@testing.com";
  public static String HAS_TAGS_EMAIL = "hasTags@testing.com";
  public static String HAS_MULTIPLE_TAGS_EMAIL = "hasMultipleTags@testing.com";
  public static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

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

  public void createUserEntity(String id, String nickname, String email) {
    Entity userEntity = new Entity(BlogConstants.BLOG_USER);
    userEntity.setProperty(BlogConstants.ID, id);
    userEntity.setProperty(BlogConstants.NICKNAME, nickname);
    userEntity.setProperty(BlogConstants.EMAIL, email);
    datastore.put(userEntity);
  }

  public void createBlogMessageEntity(String nickname, String text, String tag, long parentID) {
    Entity blogMessageEntity = new Entity(BlogConstants.BLOG_ENTITY_KIND);
    blogMessageEntity.setProperty(BlogConstants.NICKNAME, nickname);
    blogMessageEntity.setProperty("text", text);
    blogMessageEntity.setProperty(BlogConstants.TIME, System.currentTimeMillis());
    blogMessageEntity.setProperty(TAG, tag);
    blogMessageEntity.setProperty(BlogConstants.PARENTID_PARAMETER, parentID);
    datastore.put(blogMessageEntity);
  }

  public void addEntityTags(String tag, String email) {
    Entity followedTags = new Entity("followedTag");
    followedTags.setProperty(BlogConstants.EMAIL, email);
    followedTags.setProperty(TAG, tag);
    datastore.put(followedTags);
  }

  private void createDatastoreEntities() {
    createUserEntity("357911", "noTags", NO_TAGS_EMAIL);
    createUserEntity("246810", "hasTags", HAS_TAGS_EMAIL);
    createUserEntity("123456", "hasMultipleTags", HAS_MULTIPLE_TAGS_EMAIL);
    createBlogMessageEntity("test1", "test text business", "#business", 0);
    addEntityTags("#business", HAS_TAGS_EMAIL);

    List<String> multipleTagsFollowed = new ArrayList<String>();
    multipleTagsFollowed.add("#business");
    multipleTagsFollowed.add("#education");
    multipleTagsFollowed.add("#music");
    
    for (int i=0; i<multipleTagsFollowed.size(); i++) {
      addEntityTags(multipleTagsFollowed.get(i), HAS_MULTIPLE_TAGS_EMAIL);
    }
  }

  @Test
  public void testFollowsNoTags() {
    createDatastoreEntities();
    List<String> expectedFollowedTags = new ArrayList<String>();
    assertEquals(expectedFollowedTags, LoadFollowedTags.getFollowedTags(NO_TAGS_EMAIL));
  }

  @Test
  public void testFollowsOneTag() {
    createDatastoreEntities();
    List<String> expectedFollowedTags = new ArrayList<String>();
    expectedFollowedTags.add("#business");
    assertEquals(expectedFollowedTags, LoadFollowedTags.getFollowedTags(HAS_TAGS_EMAIL));
    assertEquals(1, LoadFollowedTags.getFollowedTags(HAS_TAGS_EMAIL).size());
  }

  @Test
  public void testFollowsMultipleTag() {
    createDatastoreEntities();
    List<String> expectedFollowedTags = new ArrayList<String>();
    expectedFollowedTags.add("#business");
    expectedFollowedTags.add("#education");
    expectedFollowedTags.add("#music");
    assertEquals(expectedFollowedTags, LoadFollowedTags.getFollowedTags(HAS_MULTIPLE_TAGS_EMAIL));
    assertEquals(3, LoadFollowedTags.getFollowedTags(HAS_MULTIPLE_TAGS_EMAIL).size());
  }

  @Test
  public void testFollowsTagsFalse() {
    createDatastoreEntities();
    Boolean expectedFollowedTags = false;
    assertEquals(expectedFollowedTags, LoadFollowedTags.hasFollowedTags(NO_TAGS_EMAIL));
  }

  @Test
  public void testFollowsTagsTrue() {
    createDatastoreEntities();
    Boolean expectedFollowedTags = true;
    assertEquals(expectedFollowedTags, LoadFollowedTags.hasFollowedTags(HAS_TAGS_EMAIL));
    assertEquals(expectedFollowedTags, LoadFollowedTags.hasFollowedTags(HAS_MULTIPLE_TAGS_EMAIL));
  }
}
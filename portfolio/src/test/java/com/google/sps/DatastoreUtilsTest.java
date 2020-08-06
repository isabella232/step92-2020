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

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.assertEquals;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RunWith(JUnit4.class)
public final class DatastoreUtilsTest {
  public static String TAG = "tag";
  public static String HAS_TAGS_EMAIL = "hasTags@testing.com";
  public static String HAS_MULTIPLE_TAGS_EMAIL = "hasMultipleTags@testing.com";
  public static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  private final LocalServiceTestHelper helper =
    new LocalServiceTestHelper(
        new LocalDatastoreServiceTestConfig()).setEnvAuthDomain(
        "example.com").setEnvIsLoggedIn(true).setEnvEmail(HAS_MULTIPLE_TAGS_EMAIL);

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
    createUserEntity("123456", "hasMultipleTags", HAS_MULTIPLE_TAGS_EMAIL);
    createBlogMessageEntity("test1", "test text business", "#business", 0);
    createBlogMessageEntity("test2", "test text business", "#business", 0);

    List<String> multipleTagsFollowed = new ArrayList<String>();
    multipleTagsFollowed.add("#business");
    multipleTagsFollowed.add("#education");
    multipleTagsFollowed.add("#music");
    
    for (int i=0; i<multipleTagsFollowed.size(); i++) {
      addEntityTags(multipleTagsFollowed.get(i), HAS_MULTIPLE_TAGS_EMAIL);
    }
  }

  @Test
  public void createDatastoreEntitiesWithResponse() {
    BlogMessage parent = new BlogMessage (
        1, "#business", "test text business", "nickname", HAS_TAGS_EMAIL, new ArrayList<BlogMessage>(), 0, 0);
    BlogMessage child = new BlogMessage (
        2, "#business", "test text business", "nickname", HAS_TAGS_EMAIL, new ArrayList<BlogMessage>(), 0, parent.getMessageId());
    List<BlogMessage> testMessages = new ArrayList<BlogMessage>();
    testMessages.add(parent);
    testMessages.add(child);
    List<BlogMessage> actual = RepliesUtils.putRepliesWithPosts(testMessages);

    Assert.assertEquals(1, actual.size());
  }

  @Test
  public void loadAllBlogsOrLastTestTrue() {
    createDatastoreEntities();
    List<BlogMessage> actual = DatastoreUtils.LoadAllBlogsOrLast(true);

    Assert.assertEquals(2, actual.size());
  }

  @Test
  public void loadAllBlogsOrLastTestFalse() {
    createDatastoreEntities();
    List<BlogMessage> actual = DatastoreUtils.LoadAllBlogsOrLast(false);

    Assert.assertEquals("test2", actual.get(0).getSender());
  }

  @Test
  public void updateTagsToSearchTest() {
    List<String> tagsToSearch = new ArrayList<String>();
    createDatastoreEntities();
    TagsUtils.updateTagsToSearch(tagsToSearch);
   
    Assert.assertEquals(true, tagsToSearch.contains("#music"));
    Assert.assertEquals(true, tagsToSearch.contains("#education"));
    Assert.assertEquals(true, tagsToSearch.contains("#business"));
  }

  @Test
  public void sortAndLoadFromBlogHashMapTest() {
    List<BlogMessage> blogMessages = new ArrayList<BlogMessage>();
    List<String> tagsToSearch = new ArrayList<String>();
    tagsToSearch.add("#music");
    BlogMessage testBusiness = new BlogMessage (
        1, "#business", "test text business", "nickname", HAS_TAGS_EMAIL, new ArrayList<BlogMessage>(), 0, 0);
    BlogMessage testMusic = new BlogMessage (
        1, "#music", "test text music", "nickname", HAS_TAGS_EMAIL, new ArrayList<BlogMessage>(), 0, 0);
    BlogMessage testEducation = new BlogMessage (
        1, "#education", "test text education", "nickname", HAS_TAGS_EMAIL, new ArrayList<BlogMessage>(), 0, 0);
    blogMessages.add(testBusiness);
    blogMessages.add(testMusic);
    blogMessages.add(testEducation);
    LinkedList<BlogMessage> actual = BlogHashMapUtils.sortAndLoadFromBlogHashMap(
        blogMessages, tagsToSearch, 1);

    Assert.assertEquals(1, actual.size());
  }
  
  @Test
  public void PutBlogsInDatastoreTest_withPost() {
    String postTag = "#test";
    String post = "I am testing this function";
    String nickname = "tester";
    long parentID = 0;
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    assertEquals(0, ds.prepare(new Query(BlogConstants.BLOG_ENTITY_KIND)).countEntities(withLimit(5)));
    
    DatastoreUtils.putBlogsInDatastore(postTag, post, nickname, parentID);
    assertEquals(1, ds.prepare(new Query(BlogConstants.BLOG_ENTITY_KIND)).countEntities(withLimit(5)));
  }

  // |DatastoreUtils.putBlogsInDatastore| shouldn't put anything without a post in datastore.
  @Test
  public void PutBlogsInDatastoreTest_withoutPost() {
    String postTag = "#test";
    String post = null;
    String nickname = "tester";
    long parentID = 0;
    
    // Null posts aren't allowed.
    DatastoreUtils.putBlogsInDatastore(postTag, post, nickname, parentID);
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    assertEquals(0, ds.prepare(new Query(BlogConstants.BLOG_ENTITY_KIND)).countEntities(withLimit(5)));

    // Empty posts aren't allowed.
    String newPost = "";
    DatastoreUtils.putBlogsInDatastore(postTag, newPost, nickname, parentID);
    assertEquals(0, ds.prepare(new Query(BlogConstants.BLOG_ENTITY_KIND)).countEntities(withLimit(5)));
  }

 // Should load and return everything if |loadAll = true|.
  @Test
  public void testDoGetFromDatastore_LoadAll() {
    DatastoreUtils.putBlogsInDatastore("#general", "Hi", "tester", 0);
    DatastoreUtils.putBlogsInDatastore("#education", "Hi", "tester", 0);
    DatastoreUtils.putBlogsInDatastore("#games", "Hi", "tester", 0);

    List<String> tagsToSearch = new ArrayList<String>();
    Assert.assertEquals(3, DatastoreUtils.doGetFromDatastore(0, tagsToSearch, /*loadAll=*/true).size());
    
    // Load everything even if user follows tags.
    // TagUtils uses current user's email...
    UserService userService = UserServiceFactory.getUserService();
    String userEmail = (String) userService.getCurrentUser().getEmail();

    // Follow #games.
    addEntityTags("#games", userEmail);
    
    // Should ignore followed tags and load everything...
    Assert.assertEquals(3, DatastoreUtils.doGetFromDatastore(0, tagsToSearch, true).size());
  }

  @Test
  public void testDoGetFromDatastore_LoadFollowedTagsPosts() {
    DatastoreUtils.putBlogsInDatastore("#general", "Hi", "tester", 0);
    DatastoreUtils.putBlogsInDatastore("#education", "Hi", "tester", 0);
    DatastoreUtils.putBlogsInDatastore("#games", "Hi", "tester", 0);

    List<String> tagsToSearch = new ArrayList<String>();

    // No followed tags, load all 3 posts.
    Assert.assertEquals(3, DatastoreUtils.doGetFromDatastore(0, tagsToSearch, /*loadAll=*/ false).size());

    UserService userService = UserServiceFactory.getUserService();
    String userEmail = (String) userService.getCurrentUser().getEmail();

    // Follow these 2 tags.
    addEntityTags("#general", userEmail);
    addEntityTags("#games", userEmail);

    // Load posts for only the tags followed. In this case, 2.
    Assert.assertEquals(2, DatastoreUtils.doGetFromDatastore(0, tagsToSearch, false).size());

    // New posts for unfollowed tags, still load posts for only followed tags - 2 in this case.
    DatastoreUtils.putBlogsInDatastore("#music", "Hello", "tester2", 0);
    Assert.assertEquals(2, DatastoreUtils.doGetFromDatastore(0, tagsToSearch, false).size());

    // New posts for followed tags, include those in the loaded posts - 3 in this case.
    DatastoreUtils.putBlogsInDatastore("#games", "Let's play", "gamer", 0);
    Assert.assertEquals(3, DatastoreUtils.doGetFromDatastore(0, tagsToSearch, false).size());
  }

  // Should load posts for only the requested tag.
  @Test
  public void testDoGetFromDatastore_LoadRequestedTagPosts() {
    DatastoreUtils.putBlogsInDatastore("#general", "Hi", "tester", 0);
    DatastoreUtils.putBlogsInDatastore("#education", "Hi", "tester", 0);
    DatastoreUtils.putBlogsInDatastore("#games", "Hi", "tester", 0);
    DatastoreUtils.putBlogsInDatastore("#games", "Let's play", "gamer", 0);

    UserService userService = UserServiceFactory.getUserService();
    String userEmail = (String) userService.getCurrentUser().getEmail();
    // Follow a tag.
    addEntityTags("#general", userEmail);
    
    // If a tag is requested, ignore followed tags and load posts for only the requested tag.
    List<String> tagsToSearch = new ArrayList<String>();
    tagsToSearch.add("#games");
    // Shoud load the 2 posts for #games.
    Assert.assertEquals(2, DatastoreUtils.doGetFromDatastore(0, tagsToSearch, false).size());
    
    // No requested tag, load everything or use followed tags if there's any.
    tagsToSearch.remove("#games");
    // Should load the post for the followed tag (#general).
    Assert.assertEquals(1, DatastoreUtils.doGetFromDatastore(0, tagsToSearch, false).size());
  }
}

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

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class HashTableTest {
  @Test
  public void noComments() {
    //Should return an empty hash map.
    Hashtable actual = new Hashmap<>();
    int expected = 0;

    Assert.assertEquals(expected, actual.size());
  }

  @Test
  public void oneBlogPostForEachTag() {
    //Return a Hashmap with LinkedList of size one for each tag.
    Hashmap<String, LinkedList<BlogMessage>> actual = new Hashmap<>();
    actual("#Pop") = BlogMessage(1, "#Pop", "I enjoy pop music", "", "Steven", ArrayList<String>(), System.currentTimeMillis());
    actual("#Country") = BlogMessage(2, "#Country", "Country music is the best music", "", "Tayyaba", ArrayList<String>(), System.currentTimeMillis());
    actual("#Classical") = BlogMessage(3, "#Classical", "Classical music makes you smarter", "", "Andrew", ArrayList<String>(), System.currentTimeMillis());

    Set<String> keys = actual.keySet();
    for(String key : keys){
      Assert.assertEquals(1, actual.get(key).size());
    }
  }

  @Test
  public void multipleBlogPostsForFewTags() {
    /*should return a Hashmap with the appropriate amount of LinkedLists, 
    whose size is equal to the amount of posts per tag.*/
    Hashmap<String,LinkedList<String>> actual = new Hashmap<>();
    for(int i = 0; i < 4; i++){
      actual("#Pop") = BlogMessage(i, "#Pop", "Test Message", "", "Steven", ArrayList<String>(), System.currentTimeMillis());
    }
    for(int i = 0; i < 4; i++){
      actual("#Country") = BlogMessage(i, "#Country", "Test Message", "", "Tayyaba", ArrayList<String>(), System.currentTimeMillis());
    }
    for(int i = 0; i < 4; i++){
      actual("#Classical") = BlogMessage(i, "#Classical", "Test Message", "", "Andrew", ArrayList<String>(), System.currentTimeMillis());
    }
    
    Set<String> keys = actual.keySet();
    for(String key : keys){
      Assert.assertEquals(3, actual.get(key).size);
    }
  }

  @Test
  public void multipleBlogPostsInOrder(){
    //Should return blog posts in descending order by timestamp.
    Hashmap<String,LinkedList<String>> actual = new Hashmap<>();
    List<String> expected = new ArrayList<>();
    for(int i = 0; i < 4; i++){
      long int time = System.currentTimeMillis();
      actual("#Pop") = BlogMessage(i, "#Pop", "Test Message", "", "Steven", ArrayList<String>(), time);
      expected.add(BlogMessage(i, "#Pop", "Test Message", "", "Steven", ArrayList<String>(), time));
    }
    
    Set<String> keys = actual.keySet();
    for(int i = 0; i < expected.size(); i++){
      Assert.assertEquals(expected(i).timestamp, actual.get("#Pop").pop().timestamp);
    }
  }

  @Test
  public void invalidTag(){
    //Tag should not be supported so should return empty hash table.
    Hashmap<String,LinkedList<String>> actual = new Hashmap<>();
    actual("#Edm") = BlogMessage(1, "#Edm", "I listen to Edm while I work.", "", "Steven", ArrayList<String>(), System.currentTimeMillis());

    Assert.assertEquals(0, actual.size());
  }
}
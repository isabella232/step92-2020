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

package com.google.sps.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class BlogHashMap {
  private final String[] INTERNAL_TAGS = {"#general", "#wellbeing", "#music", "#games"};
  private Map<String, LinkedList<BlogMessage>> map;

  public int size(){
    return map.size();
  }
  // Returns whether a message's tag is supported internally.
  private boolean tagIsSupported(BlogMessage message) {
    boolean isTagSupported = false;
    for (int i = 0; i < INTERNAL_TAGS.length; i++){
      if (message.getTag().equals(INTERNAL_TAGS[i])){
        isTagSupported = true;
      }
    }
    return isTagSupported;
  }

  // Takes and puts a list of BlogMessage type in map.
  // Each BlogMessage type should contain tag that matches tags in the internal System. 
  public void putInMap(List<BlogMessage> messages) {
    for (BlogMessage message : messages) {
      if (tagIsSupported(message)) {
        if (map.containsKey(message.getTag())){
          map.get(message.getTag()).addLast(message);
        } else {
          // No such mapping with specified tag,
          // Create first mapping with specified tag.
          LinkedList<BlogMessage> firstValue = new LinkedList<BlogMessage>();
          firstValue.addFirst(message); 
          map.put(message.getTag(), firstValue);
        }
      } else {
        System.err.println("Tag not supported. Tag: " + message.getTag());
      }
    }
  }
  
  /** FOR ALL getMessages() METHODS: 
        In a case where different users assign different tags to equivalent messages,
        Returned values could have duplicate messages - but for different tags.
    */

  // Returns a linkedList of all values of BlogMessage type in the map.
  public LinkedList<BlogMessage> getMessages() {
    Set<String> keys = map.keySet();
    LinkedList<BlogMessage> values = new LinkedList<BlogMessage>();
    for (String key : keys) {
      values.addAll(map.get(key));
    }
    return values;
  }

  // Returns a requested amount of all BlogMessages in map
  // Returned values contain the most recent messages.
  public LinkedList<BlogMessage> getMessages(int limit) {
    if (limit < 0) {
      return new LinkedList<>();
    }

    Set<String> keys = map.keySet();
    LinkedList values = new LinkedList();

    for (String key : keys) {
      if (limit <= 0) {break;}
      Iterator iterator;
      int tagMessagesSize = (map.get(key)).size(); 

      // startPoint is from (n0.of.messages in each tag - limit) if the list contains more messages
      // than the limit,
      // That way, returned values contain the most recent messages.
      if (tagMessagesSize > limit) {
        int startPoint = tagMessagesSize - limit;
        iterator = (map.get(key)).listIterator(startPoint);
      } 
      // Otherwise the n0.of messages in the tag is less than or equal to the limit, hence
      // we return all of them.
      else {
        iterator = (map.get(key)).iterator();
      }
      while(iterator.hasNext() && limit >= 0){
        values.addLast(iterator.next());
        limit--;
      }
    }
    return values;
  }

  // Takes a tag, and
  // Returns a linkedList of all values of BlogMessage type for the specified tag.
  public LinkedList<BlogMessage> getMessages(String tag) {
    LinkedList<BlogMessage> values = new LinkedList<BlogMessage>();
    if (map.containsKey(tag)) {
      values.addAll(map.get(tag));
    } else {
      System.err.println("Tag not found. Tag : " + tag);
    }    
    return values;
  }

  // Returns a requested amount of all BlogMessages for a specified tag.
  public LinkedList<BlogMessage>getMessages(String tag, int limit) {
    if (limit < 0) {
      return new LinkedList<>();
    }

    LinkedList values = new LinkedList();

    if (map.containsKey(tag)){
      Iterator iterator;
      int tagMessagesSize = (map.get(tag)).size();
      if (tagMessagesSize > limit) {
        int startPoint = tagMessagesSize - limit;
        iterator = (map.get(tag)).listIterator(startPoint);
      } else {
        iterator = (map.get(tag)).iterator();
      }
      while (iterator.hasNext() && limit >= 0) {
        values.addLast(iterator.next());
        limit--;
      }
    } else {
      System.err.println("Tag not found. Tag: " + tag);
    }
    return values;
  } 

  // Takes a list of tags, and
  // Returns a linkedList of all values of BlogMessage type for the specified tags.
  public LinkedList<BlogMessage> getMessages(List<String> tags) {
    LinkedList<BlogMessage> values = new LinkedList<BlogMessage>();
    for (String tag : tags){
      if (map.containsKey(tag)) {
        values.addAll(map.get(tag));
      } else {
        System.err.println("Tag not found. Tag : " + tag);
      }    
    }
    return values;
  }

  // Returns a requested amount of all BlogMessages for specified tags.
  public LinkedList<BlogMessage> getMessages(List<String> tags, int limit) {
    if (limit < 0) {
      return new LinkedList<>();
    }

    LinkedList values = new LinkedList();

    for (String tag : tags) {
      if (limit <= 0) {break;}
      Iterator iterator;

      if (map.containsKey(tag)) {
        int tagMessagesSize = (map.get(tag)).size(); 
        if (tagMessagesSize > limit) {
          int startPoint = tagMessagesSize - limit;
          iterator = (map.get(tag)).listIterator(startPoint);
        } else {
          iterator = (map.get(tag)).iterator();
        }
        while(iterator.hasNext() && limit >= 0) {
          values.addLast(iterator.next());
          limit--;
        }
      } else {
        System.err.println("Tag not found. Tag: " + tag);
      }
    }
    return values;
  }

  public BlogHashMap() {
    map = new LinkedHashMap<String, LinkedList<BlogMessage>>();
  }

}
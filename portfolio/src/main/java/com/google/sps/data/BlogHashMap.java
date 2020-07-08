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
  private final int DEFAULT_LOAD_AMOUNT = 10;
  private final InternalTags internal_tags;
  private Map<String, LinkedList<BlogMessage>> map;

  // Takes and puts a list of BlogMessage type in map.
  // Each BlogMessage type should contain tag that matches tags in the internal System. 
  public void putInMap(List<BlogMessage> messages) {
    for (BlogMessage message : messages) {
      if (internal_tags.tagIsSupported(message)) {
        if (!map.containsKey(message.getTag())){
          LinkedList<BlogMessage> firstValue = new LinkedList<BlogMessage>();
          firstValue.addFirst(message); 
          map.put(message.getTag(), firstValue);
          continue;
        }
        map.get(message.getTag()).addLast(message);
      } else {
        System.err.println("Tag not supported. Tag: " + message.getTag());
      }
    }
  }
  
  // FOR ALL getMessages() METHODS: 
  //      Each method takes a 'limit', an argument for the amount of messages requested.
  //      Returned messages will not be greater than this amount, but could be less,
  //      For instance, when the number of messages in the map is less than that requested.
  //        In a case where different users assign different tags to equivalent messages,
  //      Returned values could have duplicate messages - but for different tags.
  //
  //      Returned values contain the most recent BlogMessages.

  // Returns a requested amount of all BlogMessages in map
  private LinkedList<BlogMessage> getMessages(int limit) {
    LinkedList values = new LinkedList();

    for (LinkedList<BlogMessage> tagMessages : map.values()){
      if (limit <= 0) 
      {break;}

      Iterator iterator;
      int tagMessagesSize = tagMessages.size();

      // If a tag contains more messages than requested
      // Iterate from (number of messages - limit) to return the most recent BlogMessages. 
      if (tagMessagesSize > limit) {
        iterator = tagMessages.listIterator(tagMessagesSize - limit);
      } else {
        iterator = tagMessages.iterator();
      }

      while(iterator.hasNext() && limit >= 0) {
        values.addLast(iterator.next());
        limit--;
      }
    }
    return values;
  }

  // Returns a requested amount of all BlogMessages for a specified tag.
  private LinkedList<BlogMessage> getMessages(String tag, int limit) {
    LinkedList values = new LinkedList();

    if (map.containsKey(tag)) {
      Iterator iterator;
      int tagMessagesSize = map.get(tag).size();
      if (tagMessagesSize > limit) {
        iterator = map.get(tag).listIterator(tagMessagesSize - limit);
      } else {
        iterator = map.get(tag).iterator();
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

  // This method takes a list of tags and a number of messages to load, and
  // Returns a requested (or default) amount of BlogMessages under specified tag/tags.
  // If tag/tags are not specified, a requested (or default) amount of all BlogMessages will be returned.
  public LinkedList<BlogMessage> getMessages(List<String> tags, int limit) {
    if (limit <= 0) {
      limit = DEFAULT_LOAD_AMOUNT;
    }
    if (tags.isEmpty()) {
      return getMessages(limit);
    }
    if (tags.size() == 1) {
      return getMessages(tags.get(0), limit);
    }

    LinkedList values = new LinkedList();
    for (String tag : tags) {
      if (limit <= 0) 
      {break;}

      Iterator iterator;
      if (map.containsKey(tag)) {
        int tagMessagesSize = map.get(tag).size(); 
        if (tagMessagesSize > limit) {
          iterator = map.get(tag).listIterator(tagMessagesSize - limit);
        } else {
          iterator = map.get(tag).iterator();
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
    internal_tags = new InternalTags();
  }

}
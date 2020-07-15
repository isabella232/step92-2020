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
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public final class BlogHashMap {
  private final int DEFAULT_LOAD_AMOUNT = 10;
  private Map<String, LinkedList<BlogMessage>> map;

  public int size(){
    return map.size();
  }
  
  // Takes and puts a list of BlogMessage type in map.
  // Each BlogMessage type should contain tag that matches tags in the internal System. 
  public void putInMap(List<BlogMessage> messages) {
    for (BlogMessage message : messages) {
      if (InternalTags.tagIsSupported(message)) {
        if (!map.containsKey(message.getTag())) {
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
    LinkedList<BlogMessage> values = new LinkedList<BlogMessage>();

    List<LinkedList> valuesList = new ArrayList(map.values());
    ListIterator<LinkedList> reverseItr = valuesList.listIterator(valuesList.size());
     
    while (reverseItr.hasPrevious()) {
      if (limit <= 0) {
        break;
      }

      LinkedList<BlogMessage> tagMessages = reverseItr.previous();

      // Use LinkedList's |descendingIterator()| to iterate BlogMessages from the back,
      // And addFirst to return the most recent messages in the order they were received.
      // This way, if [limit] is 1 we return the last message in the last tag entered. 
      Iterator<BlogMessage> iterateFromBack = tagMessages.descendingIterator();
      while(iterateFromBack.hasNext() && limit > 0) {
        values.addFirst(iterateFromBack.next());
        limit--;
      }
    }
    return values;
  }

  // Returns a requested amount of all BlogMessages for a specified tag.
  private LinkedList<BlogMessage> getMessages(String tag, int limit) {
    LinkedList<BlogMessage> values = new LinkedList<BlogMessage>();

    if (map.containsKey(tag)) {
      Iterator<BlogMessage> iterator;
      int tagMessagesSize = map.get(tag).size();

      // If the tag contains more messages than requested
      // Iterate from (number of messages - limit) to return the most recent BlogMessages. 
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
  // If |tags| are not specified, a requested (or default) amount of all BlogMessages will be returned.
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

    LinkedList<BlogMessage> values = new LinkedList<BlogMessage>();
    for (String tag : tags) {
      if (limit <= 0) {
        break;
      }

      Iterator<BlogMessage> iterator;
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
  }

}
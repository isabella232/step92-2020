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
  private static final String[] internalTags = {"#general", "#wellbeing", "#music", "#games"};

  // Takes and puts a list of BlogMessage type in map.
  // Each BlogMessage type should contain tag that matches tags in the internal System. 
  public static void putInMap(List<BlogMessage> messages, Map<String, LinkedList<BlogMessage>> map) {
    for (BlogMessage message : messages) {
      boolean isTagSupported = false;
      for (int i = 0; i < internalTags.length; i++){
        if (message.getTag().equals(internalTags[i])){
          isTagSupported = true;
          if (map.containsKey(message.getTag())){
            map.get(message.getTag()).addLast(message);
          } else {
            // No such mapping with specified tag,
            // Create first mapping with specified tag.
            LinkedList<BlogMessage> firstValue = new LinkedList<BlogMessage>();
            firstValue.addFirst(message); 
            map.put(message.getTag(), firstValue);
          }
        } 
      }
      if (!isTagSupported) {
        System.out.println("Tag not supported. Tag: " + message.getTag());
        System.out.println("For BlogMessage@ " + message);
      }
    }

  }

  public static int getSize(Map<String, LinkedList<BlogMessage>> map) {
    return map.size();
  }

  // Takes map, and
  // Returns a linkedList of all values of BlogMessage type in the map. 
  public static LinkedList<BlogMessage> getMessages(Map<String, LinkedList<BlogMessage>> map) {
    System.out.println("Fetching all [LinkedList] Values in map...");

    Set<String> keys = map.keySet();
    LinkedList<BlogMessage> values = new LinkedList<BlogMessage>();
    for (String key : keys) {
      values.addAll(map.get(key));
    }
    return values;
  }

  // Returns a requested amount of all BlogMessages in map
  public static LinkedList<BlogMessage> getMessages(Map<String, LinkedList<BlogMessage>> map, int limit) {
    if (limit < 0) {
      System.out.println("Cannot load this amount.");
      return new LinkedList<>();
    }

    LinkedList<BlogMessage> values = getMessages(map);
    if (limit > values.size()) {
      limit = values.size();
    } 

    System.out.println("Fetching requested amount of [LinkedList] Values for all tags...");

    Iterator<BlogMessage> iterateList = values.iterator();
    LinkedList<BlogMessage> limitedValues = new LinkedList<BlogMessage>();
    int i = 0;
    while (iterateList.hasNext() && i < limit){
      limitedValues.addLast(iterateList.next());
      i++;
    }
    return limitedValues;
  }

  // Takes a tag, and
  // Returns a linkedList of all values of BlogMessage type for the specified tag.
  public static LinkedList<BlogMessage> getMessages(String tag, Map<String, LinkedList<BlogMessage>> map) {
    System.out.println("Fetching all [LinkedList] Value(s) for specified tag...");

    LinkedList<BlogMessage> values = new LinkedList<BlogMessage>();
    if (map.containsKey(tag)) {
      values.addAll(map.get(tag));
    } else {
      System.out.println("Tag not in map. Tag : " + tag);
    }    
    return values;
  }

  // Returns a requested amount of all BlogMessages for a specified tag.
  public static LinkedList<BlogMessage>getMessages(String tag, Map<String, LinkedList<BlogMessage>> map, int limit) {
    if (limit < 0) {
      System.out.println("Cannot load this amount.");
      return new LinkedList<>();
    }

    LinkedList<BlogMessage> values = getMessages(tag, map);
    if (limit > values.size()) {
      limit = values.size();
    } 

    System.out.println("Fetching requested amount of [LinkedList] Values for specified tag...");

    Iterator<BlogMessage> iterateList = values.iterator();
    LinkedList<BlogMessage> limitedValues = new LinkedList<BlogMessage>();
    int i = 0;
    while (iterateList.hasNext() && i < limit){
      limitedValues.addLast(iterateList.next());
      i++;
    }
    return limitedValues;
  } 

  // Takes a list of tags, and
  // Returns a linkedList of all values of BlogMessage type for the specified tags.
  public static LinkedList<BlogMessage>getMessages(List<String> tags, Map<String, LinkedList<BlogMessage>> map) {
    System.out.println("Fetching all [LinkedList] Values for specified tags...");

    LinkedList<BlogMessage> values = new LinkedList<BlogMessage>();
    for (String tag : tags){
      if (map.containsKey(tag)) {
        values.addAll(map.get(tag));
      } else {
        System.out.println("Tag not in map. Tag : " + tag);
      }    
    }
    return values;
  }

  // Returns a requested amount of all BlogMessages for specified tags.
  public static LinkedList<BlogMessage>getMessages(List<String> tags, Map<String, LinkedList<BlogMessage>> map, int limit) {
    if (limit < 0) {
      System.out.println("Cannot load this amount.");
      return new LinkedList<>();
    }

    LinkedList<BlogMessage> values = getMessages(tags, map);
    if (limit > values.size()) {
      limit = values.size();
    } 

    System.out.println("Fetching requested amount of all [LinkedList] Values for specified tags...");

    Iterator<BlogMessage> iterateList = values.iterator();
    LinkedList<BlogMessage> limitedValues = new LinkedList<BlogMessage>();
    int i = 0;
    while (iterateList.hasNext() && i < limit){
      limitedValues.addLast(iterateList.next());
      i++;
    }
    return limitedValues;
  }

}
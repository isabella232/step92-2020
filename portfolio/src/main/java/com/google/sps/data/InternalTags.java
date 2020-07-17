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
import java.util.List;

public final class InternalTags {
  private static List<String> INTERNAL_TAGS = new ArrayList<String>();
  private static String DEFAULT_TAG = "#general";
  private static String ANIMAL_TAG = "#animal";
  private static String ART_TAG = "#art";
  private static String BUSINESS_TAG = "#business";
  private static String EDUCATION_TAG = "#education";
  private static String ENVIRONMENT_TAG = "#environment";
  private static String GAMES_TAG = "#games";
  private static String MUSIC_TAG = "#music";
  private static String POLITICS_TAG = "#politics";
  private static String WELLBEING_TAG = "#wellbeing";
  
  private static void setTags() {
    INTERNAL_TAGS.add(DEFAULT_TAG);
    INTERNAL_TAGS.add(ANIMAL_TAG);
    INTERNAL_TAGS.add(ART_TAG);
    INTERNAL_TAGS.add(BUSINESS_TAG);
    INTERNAL_TAGS.add(EDUCATION_TAG);
    INTERNAL_TAGS.add(ENVIRONMENT_TAG);
    INTERNAL_TAGS.add(GAMES_TAG);
    INTERNAL_TAGS.add(MUSIC_TAG);
    INTERNAL_TAGS.add(POLITICS_TAG);
    INTERNAL_TAGS.add(WELLBEING_TAG);
  }

  public static boolean tagIsSupported(BlogMessage message) {
    if (INTERNAL_TAGS.isEmpty()) {
      setTags();
    }

    for (String internalTag : INTERNAL_TAGS) {
      if (message.getTag().equals(internalTag)){
        return true;
      }
    }
    return false;
  }

  // For check in DataServlet
  public static String defaultTag(){
    return DEFAULT_TAG;
  }

  // TODO:
  //    Create hashMap for Internal tags for search in constant time.

}
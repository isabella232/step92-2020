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
  private final List<String> INTERNAL_TAGS = new ArrayList<String>();
  private final String DEFAULT_TAG = "#general";
  private final void setTags() {
    INTERNAL_TAGS.add(DEFAULT_TAG);
    INTERNAL_TAGS.add("#business");
    INTERNAL_TAGS.add("#education");
    INTERNAL_TAGS.add("#games");
    INTERNAL_TAGS.add("#music");
    INTERNAL_TAGS.add("#wellbeing");
  }

  public final boolean tagIsSupported(BlogMessage message) {
    for (String internalTag : INTERNAL_TAGS) {
      if (message.getTag().equals(internalTag)){
        return true;
      }
    }
    return false;
  }

  // For check in DataServlet
  public String defaultTag(){
    return DEFAULT_TAG;
  }
  
  public InternalTags() {
    setTags();
  }

  // TODO:
  //    Create hashMap for Internal tags for search in constant time.

}
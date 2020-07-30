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

import com.google.sps.data.BlogHashMap;
import com.google.sps.data.BlogMessage;
import java.util.LinkedList;
import java.util.List;

public final class BlogHashMapUtils {
  // Takes a list of BlogMessages and 2 load parameters: tags to Search for and a load amount.
  // Puts BlogMessages in BlogHashMap and loads the requested parameters.
  public static LinkedList<BlogMessage> sortAndLoadFromBlogHashMap(
        List<BlogMessage> blogMessages, List<String> tagsToSearch, int loadAmount) {
    BlogHashMap blogMap = new BlogHashMap();
    blogMap.putInMap(blogMessages);
 
    return blogMap.getMessages(tagsToSearch, loadAmount);
  }
}
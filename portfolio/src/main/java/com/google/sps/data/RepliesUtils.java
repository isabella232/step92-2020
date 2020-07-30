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

import com.google.sps.data.BlogMessage;
import java.util.ArrayList;
import java.util.List;

public final class RepliesUtils {
  public static List<BlogMessage> putRepliesWithPosts(List<BlogMessage> blogMessagesAll) {
    // Separate posts from replies from |blogMessages|.
    // Add replies to messageReplies for the respective posts.
    List<BlogMessage> blogMessagesParents = new ArrayList<BlogMessage>();
    List<BlogMessage> blogMessagesReplies = new ArrayList<BlogMessage>();
    for (BlogMessage message : blogMessagesAll) {
      if (message.getParentID() == 0) {
        blogMessagesParents.add(message);
      } else {
        blogMessagesReplies.add(message);
      }
    }
 
    for (BlogMessage post : blogMessagesParents) {
      for (BlogMessage reply : blogMessagesReplies) {
        if (reply.getParentID() == post.getMessageId()) {
          post.addReply(reply);
        }
      } 
    }
    return blogMessagesParents;
  }
}
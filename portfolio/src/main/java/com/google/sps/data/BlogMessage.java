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

import java.lang.String;
import java.util.ArrayList;

public final class BlogMessage {
  private final long id;
  private final String message;
  private final String nickname;
  private final long timestamp;
  private final String email;
  private final String tag;
  private final long parentID;
  private final ArrayList<BlogMessage> messageReplies;
  
  public BlogMessage(long id, String tag, String message,
        String nickname, String email, ArrayList<BlogMessage> messageReplies, long timestamp, long parentID) {
    this.id = id;
    this.tag = tag;
    this.message = message;
    this.nickname = nickname;
    this.email = email;
    this.messageReplies = messageReplies;   
    this.timestamp = timestamp;
    this.parentID = parentID;
  }

  public long getMessageId() {
    return id;
  }

  public String getTag() {
    return tag;
  }

  public String getMessage() {
    return message;
  }

  public String getSender() {
    return nickname;
  }

  public ArrayList<BlogMessage> getReplies() {
    return messageReplies;
  }

  public void addReply(BlogMessage reply) {
    if (reply != null) {
      messageReplies.add(reply);
    }
  }

  public long getTimestamp() {
    return timestamp;
  }

  public long getParentID() {
    return parentID;
  }
  
}
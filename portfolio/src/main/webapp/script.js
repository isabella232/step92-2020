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
 
 
// These variables will be used to track whenever a reply button is clicked
// And whether the same button was clicked again in a row.
var replyClickCounter = 0;
var currentReplyBtnClicked = 0;
 
const postInputArea = document.getElementById('post-text-input');
postInputArea.addEventListener('keyup', () => {
  if (postInputArea.value.length >= 1) {
    document.getElementById('post-submit').style.display = 'block';
    postInputArea.style.backgroundColor = 'DimGrey';
  } else {
    document.getElementById('post-submit').style.display = 'none';
    postInputArea.style.backgroundColor = 'white';
  }
});
 
// Post event listener to submit posts with Fetch on form submit. 
document.getElementById('post-form').addEventListener('submit', function(postSubmitEvent) {
  // Prevent form from redirecting to the server.
  postSubmitEvent.preventDefault();
  
  // Get a reference to all the elements on the form.
  const formData = new FormData(this);
  const postFormParams = new URLSearchParams();
  
  // Append a pair [parameter name: value] of the form's elements
  // to a URLSearchParams.
  for (const pair of formData) {
    postFormParams.append(pair[0], pair[1]);
  }
 
  // Make a POST request with the new post and fetch it to add to the DOM.
  fetch('/data', {method: 'POST', body: postFormParams}).then(
        response => response.json()).then((msgs) => {
    const statsListElement = document.getElementById('posts-list');
    msgs.forEach((msg) => {
      statsListElement.appendChild(createPostsElements(msg));
    });
 
    // Empty the post form after a post is submitted.
    resetForm('post-form');
  }).catch (function(error) {
    console.log(error);
  });
});
 
// Reply event listener to submit replies with Fetch on form submit.
document.getElementById('reply-form').addEventListener('submit', function(replySubmitEvent) {
  replySubmitEvent.preventDefault();
 
  const formData = new FormData(this);
  const replyFormParams = new URLSearchParams();
  
  for (const pair of formData) {
    replyFormParams.append(pair[0], pair[1]);
  }
 
  fetch('/data', {method: 'POST', body: replyFormParams})
        .then(response => response.json()).then((replies) => {
    replies.forEach((reply) => {
      let postRepliesContainer = document.getElementById(`repliesli${reply.parentID}`);
      postRepliesContainer.appendChild(createRepliesElements(reply));
    });
    resetForm('reply-form');
  }).catch (function(error) {
      console.log(error);
  });
});
 
function openPage(pageName, elmnt, color) {
  // Hide all elements with class="tabcontent" by default */
  var i, tabcontent, tablinks;
  tabcontent = document.getElementsByClassName("tabcontent");
  for (i = 0; i < tabcontent.length; i++) {
    tabcontent[i].style.display = "none";
  }
 
  // Remove the background color of all tablinks/buttons
  tablinks = document.getElementsByClassName("tablink");
  for (i = 0; i < tablinks.length; i++) {
    tablinks[i].style.backgroundColor = "";
  }
 
  // Show the specific tab content
  document.getElementById(pageName).style.display = "block";
 
  // Add the specific color to the button used to open the tab content
  elmnt.style.backgroundColor = color;
  
}
 
function defaultPage() {
  document.getElementById("defaultOpen").click();
}
 
// Makes a Get request and loads Posts to the Blog page on body load
// Also loads 5 display-only posts to be displayed on the home page.
async function loadPosts() {
  const HOME_LOAD_AMOUNT = 5;
  fetch('/data').then(response => response.json()).then((msgs) => {
    const statsListElement = document.getElementById('posts-list');
    const homeListElement = document.getElementById('home-comments-tagselectcontainer');
    var i = 1;
    msgs.forEach((msg) => {
      statsListElement.appendChild(createPostsElements(msg));
      let msgReplies = msg.messageReplies;
      let repliesContainer = document.querySelector(`#repliesli${CSS.escape(msg.id)}`);
      if (!isEmpty(msgReplies)) {
        msgReplies.forEach((reply) => {
          repliesContainer.appendChild(createRepliesElements(reply));
        });
      }
 
      if (i > HOME_LOAD_AMOUNT) {
        return true;
      }
      homeListElement.appendChild(createPostsElementsHome(msg));
      i++;
    });
  }).catch (function(error) {
    console.log(error);
  });
}

function loadAllPosts() {
  fetch('/data?load-all=all').then(response => response.json()).then((msgs) => {
    const statsListElement = document.getElementById('posts-list');
    if (isEmpty(msgs)) {
      return; 
    }
    statsListElement.innerHTML = '';
    msgs.forEach((msg) => {
      statsListElement.appendChild(createPostsElements(msg));
      let msgReplies = msg.messageReplies;
      let repliesContainer = document.querySelector(`#repliesli${CSS.escape(msg.id)}`);
      if (!isEmpty(msgReplies)) {
        msgReplies.forEach((reply) => {
          repliesContainer.appendChild(createRepliesElements(reply));
        });
      }
    });
  }).catch(function(error) {
    console.log(error);
  });
}
 
function getCommentsTag(tag) {
  fetch('/data?' + new URLSearchParams({
    'tag': tag})).then(response => response.json()).then((msgs) => {
    const statsListElement = document.getElementById('home-comments-tagselectcontainer');
    if (isEmpty(msgs)) {
      statsListElement.innerHTML = '<b>There are no posts for this tag!</b>'; 
      return;
    }
    statsListElement.innerHTML = '';
    msgs.forEach((msg) => {
      statsListElement.appendChild(createPostsElementsHome(msg));
    });   
  });
}
 
// Creates an <li> element containing message details.
function createPostsElements(msg) {
  const postElement = document.createElement('li');
  postElement.id = 'li' + msg.id;
 
  // Add container for posts
  let divEle = document.createElement('span');
  divEle.className = 'post';
  divEle.id = msg.id;
 
  const tagElement = document.createElement('span');
  tagElement.className = 'postTag';
  tagElement.innerHTML = `<b><i>${msg.tag}</i></b>`;
 
  const messageElement = document.createElement('span');
  messageElement.className = 'postText';
  messageElement.innerText = msg.message;
  
  const userElement = document.createElement('span');
  userElement.className = 'postSender';
  if (msg.nickname === undefined || msg.nickname === null) {
    userElement.innerHTML = "<i>_Anonymous</i>";
  } else {
    userElement.innerHTML = `<i>_${msg.nickname}</i>`;
  }
 
  const timeElement = document.createElement('span');
  timeElement.className = 'postDate';
  var date = new Date(msg.timestamp);
  timeElement.innerText = date.toLocaleString().slice(0, 24);
 
  const deleteMsgElement = document.createElement('button');
  deleteMsgElement.className = 'postDelBtn';
  deleteMsgElement.innerText = 'Delete';
  deleteMsgElement.addEventListener('click', () => {
    const POST_ENTITY_KIND = 'blogMessage';
    deleteElement(msg, POST_ENTITY_KIND, postElement);
  });
 
  const replyMsgElement = document.createElement('button');
  replyMsgElement.className = 'postRepBtn';
  replyMsgElement.innerText = 'Reply';
  replyMsgElement.addEventListener('click', () => {
    handleReplies(msg.id, msg.nickname, msg.tag, postElement.id);
    replyClickCounter++;
  });
 
  divEle.appendChild(tagElement);
  divEle.appendChild(userElement);
  divEle.appendChild(messageElement);
  divEle.appendChild(timeElement);
  divEle.appendChild(replyMsgElement);
  divEle.appendChild(deleteMsgElement);
 
  postElement.appendChild(divEle);
 
  const replyListEle = document.createElement('ul');
  replyListEle.id = 'replies' + postElement.id;
 
  postElement.appendChild(replyListEle);
  return postElement;
}
 
// TODO: Show top 3 trending posts(ie. with the most replies)
// Creates a list of posts to be displayed on the homepage.
// Display-only. Non-Interactive (ie. No reply or delete button). 
function createPostsElementsHome(msg) {
  const postElement = document.createElement('li');
  postElement.className = 'post';
  
  let divEle = document.createElement('span');
 
  const tagElement = document.createElement('span');
  tagElement.className = 'postTag';
  tagElement.innerHTML = `<b><i>${msg.tag}</i></b>`;
 
  const messageElement = document.createElement('span');
  messageElement.className = 'postText';
  messageElement.innerText = msg.message;
  
  const userElement = document.createElement('span');
  userElement.className = 'postSender';
  if (msg.nickname === undefined || msg.nickname === null) {
    userElement.innerHTML = "<i>_Anonymous</i>";
  } else {
    userElement.innerHTML = `<i>_${msg.nickname}</i>`;
  }
 
  const timeElement = document.createElement('span');
  timeElement.className = 'postDate';
  var date = new Date(msg.timestamp);
  timeElement.innerText = date.toLocaleString().slice(0, 24);
  
  divEle.appendChild(tagElement);
  divEle.appendChild(userElement);
  divEle.appendChild(messageElement);
  divEle.appendChild(timeElement);
 
  postElement.appendChild(divEle);
  return postElement;
}
 
// Returns an <li> element containing reply details.
function createRepliesElements(reply) {
  const replyElement = document.createElement('li');
  replyElement.id = `reply${reply.id}`;
  replyElement.className = 'reply';
  
  const replyContainer = document.createElement('span');
 
  const messageElement = document.createElement('span');
  messageElement.className = 'replyText';
  messageElement.innerText = reply.message;
  
  const userElement = document.createElement('span');
  userElement.className = 'postSender';
  if (reply.nickname === undefined || reply.nickname === null) {
    userElement.innerHTML = "<i>_Anonymous</i>";
  } else {
    userElement.innerHTML = "<i>_" + reply.nickname + "</i>";
  }
 
  const timeElement = document.createElement('span');
  timeElement.className = 'postDate';
  var date = new Date(reply.timestamp);
  timeElement.innerText = date.toLocaleString().slice(0, 24);
 
  const deleteReplyBtn = document.createElement('button');
  deleteReplyBtn.className = 'replyDelBtn';
  deleteReplyBtn.innerText = 'Delete Reply';
  deleteReplyBtn.addEventListener('click', () => {
    const REPLY_ENTITY_KIND = 'blogMessage';
    deleteElement(reply, REPLY_ENTITY_KIND, replyElement);
  });
  
  replyContainer.appendChild(userElement);
  replyContainer.appendChild(messageElement);
  replyContainer.appendChild(timeElement);
  replyContainer.appendChild(deleteReplyBtn);
 
  replyElement.appendChild(replyContainer);
 
  return replyElement;
}
 
/** Creates an <img> element containing text. */
function createImgElement(text) {
  const imgElement = document.createElement('img');
  imgElement.src = text;
  return imgElement;
}
 
// Deletes an entity from datastore and sends a confirmation message for 5 seconds.
async function deleteElement(entityObj, entityKind, elemContainer) { 
  const params = new URLSearchParams();
  params.append('entityId', entityObj.id);
  params.append('entity_name', entityKind);
 
  fetch('/delete-data', {method: 'POST', body: params})
        .then(response => response.text()).then((text) => {
    const confirmationBox = document.getElementById('confirm-box');
    const confirmationElement = document.getElementById('confirm-text');
 
    // Delete failed due to unexpected client/server error...
    if (!text.includes('Success!')) {
      confirmationBox.style.backgroundColor = 'FireBrick';
      confirmationElement.innerHTML = '';
      confirmationElement.innerHTML = `<i>Oops! Please try again</i>`;
      confirmationBox.style.display = 'block';
      setTimeout(function () {
        confirmationBox.style.display = 'none';}, 5000);
      return;
    }
    confirmationBox.style.backgroundColor = 'DarkSlateGray';
    confirmationElement.innerHTML = '';
    confirmationElement.innerHTML = `<i>${text}</i>`;
    confirmationBox.style.display = 'block';
    setTimeout(function () {
        confirmationBox.style.display = 'none';}, 5000);
    elemContainer.remove();
  }).then(function () {
    if (entityKind == 'followedTag') {
      checkEmptyFollowedTagsStatus();
    }
  }); 
}
 
// Displays the reply form under a post when its reply button is clicked.
// Removes the reply form if button is clicked again, and so on...
function handleReplies(msgID, msgSender, tag, postEleID) { 
  // The form appears when a reply button is clicked an even number of times, so
  // If a different reply button is clicked, form is reset; counter is set to 0.
  if (clickedDifferentReplyButton(msgID)) {
    resetForm('reply-form');
  }
  
  // Remove the form when a button is clicked an odd number of times in a row.
  if (replyClickCounter % 2 != 0) {
    document.getElementById('reply-form').style.display = 'none';
    return
  }
  
  // Put reply form under the post.
  let replyNode = document.getElementById('reply-form');
  var referenceNode = document.querySelector(`#${CSS.escape(postEleID)}`);
  referenceNode.parentNode.insertBefore(replyNode, referenceNode.nextSibling);
  replyNode.style.display = 'block';
  
  // Show submit button only if a user types, 
  // Otherwise hide submit button.
  let replyArea = document.getElementById('reply-text-input');
  replyArea.placeholder = `<reply to @${msgSender}>`;
  replyArea.addEventListener('keyup', () => {
    if (replyArea.value.length >= 1) {
      document.getElementById('reply-submit').style.display = 'block';
      document.getElementById('reply-submit').innerHTML = '<i>Reply Post</i>';
      replyArea.style.backgroundColor = 'LightGray';
    } else {
      document.getElementById('reply-submit').style.display = 'none';
      replyArea.style.backgroundColor = 'white';
    }
  });
 
  // On submit, a reply's parentID and tag is set to those of its post.
  document.getElementById('reply-parentID').value = msgID;
  document.getElementById('reply-tag').value = tag;
}
 
// Returns true if the clicked reply button is different from the last reply button clicked.
// Otherwise; returns false.
function clickedDifferentReplyButton(clickedId) {
  let isDif = false;
  if (clickedId != currentReplyBtnClicked) {
    isDif = true;
  }
  currentReplyBtnClicked = clickedId;
  return isDif;
}
 
// Empties the form whose Id is passed.
function resetForm(formID) { 
  // For reply form, first revert all modifications, and
  // Set counter to 0.
  if (formID == 'reply-form') {
    document.getElementById('reply-form').style.display = 'none';
    document.getElementById('reply-text-input').style.backgroundColor = 'white';
    document.getElementById('reply-submit').style.display = 'none';
    replyClickCounter = 0;
  }
  document.getElementById(formID).reset();
}
 
function fetchBlobstoreUrlAndShowForm() {
  fetch('/blobstore-upload-url')
      .then((response) => {
        return response.text();
      })
      .then((imageUploadUrl) => {
        const messageForm = document.getElementById('my-form');
        messageForm.action = imageUploadUrl;
        messageForm.classList.remove('hidden');
      });
}
 
function showPostForm() {
  fetch('/login_status').then(response => response.json()).then((isLoggedIn) => {
    if (isLoggedIn) {
      document.getElementById("blogcontent").style.display = "block";
    } else {
    window.open("/login")}
  });
}
 
function setTag(tag) {
  document.getElementById("comments-tag").value = tag;
}
 
function setNicknameForm() {
    window.open("/login");
}
 
let followSectionBtn = document.getElementById('show-Ft-Btn');
followSectionBtn.addEventListener('click', () => {
  const followTagsSection = document.getElementById('follow-tags-section');
  if (followTagsSection.style.display == 'none') {
    showTagsToFollow();
  } else {
    hideTagsToFollow();
  }
});
 
function showTagsToFollow() {
  document.getElementById('follow-tags-section').style.display = 'block';
  followSectionBtn.innerText = 'Hide Tags to Follow';
}
 
function hideTagsToFollow() {
  document.getElementById('follow-tags-section').style.display = 'none';
  followSectionBtn.innerText = 'Show Tags to Follow';
}
 
// If a user selects tag(s) to follow, this function posts them to the server, and 
// Sends the appropriate confirmation message for 5 seconds.
function sendFollowedTags() {
  let params = new URLSearchParams();
 
  const tagElems = document.getElementsByClassName('followtagcheck');
  for (var i = 0; i < tagElems.length; i++) {
    if (tagElems[i].checked === true) {
      params.append('tags', tagElems[i].value);
    }
  }
  fetch('/follow-tags', {method: 'POST', body: params}).then(
        response => response.text()).then((text) => {
    const confirmationBox = document.getElementById('confirm-box');
    const confirmationElement = document.getElementById('confirm-text');
 
    // In case of unexpected server errors...
    if (!text.includes('Success!') && !text.includes('Failed!')) {
      confirmationBox.style.backgroundColor = 'FireBrick';
      confirmationElement.innerHTML = '';
      confirmationElement.innerHTML = `<i>Oops! Please try again</i>`;
      confirmationBox.style.display = 'block';
      setTimeout(function () {
        confirmationBox.style.display = 'none';}, 5000);
      return;
    }
    
    // This server message means user tried to re-follow tag(s) they already follow.
    if (text.includes('Failed!')) {
      confirmationBox.style.backgroundColor = 'FireBrick';
      confirmationElement.innerHTML = "";
      confirmationElement.innerHTML = `${text}`;
      confirmationBox.style.display = 'block';
      setTimeout(function () {
            confirmationBox.style.display = 'none';}, 5000);
      return;
    } 
    
    confirmationBox.style.backgroundColor = 'DarkSlateGray';
    confirmationElement.innerHTML = "";
    confirmationElement.innerHTML = `${text}`;
    confirmationBox.style.display = 'block';
    setTimeout(function () {
        confirmationBox.style.display = 'none';}, 5000);
        
    // Post was successful, so
    // Hide the tags to follow section and load the recently followed tag(s).
    hideTagsToFollow();
    getFollowedTags();
  });
}
 
// Loads user followed tags to the FollowTags page...
function getFollowedTags() {
  fetch('/follow-tags').then(response => response.json()).then((userFollowedTags) => {
    const userFollowedTagsBox = document.getElementById('user-followed-tags');
    const followTagsStatusElem = document.getElementById('follow-tags-status');
 
    if (isEmpty(userFollowedTags)) {
      followTagsStatusElem.innerHTML = '<Strong>You do not follow any tag.</Strong>';
      return;
    }
 
    userFollowedTagsBox.innerHTML = '';
    followTagsStatusElem.innerHTML = '<Strong>You follow the following tag(s).</Strong>';
    userFollowedTags.forEach((followedTag) => {
      userFollowedTagsBox.appendChild(createFollowedTagContainer(followedTag));
    });
  });
}
 
// If a user unfollows all tags, change paragraph text to show the appropriate message.
function checkEmptyFollowedTagsStatus() {
  const userFollowedTagsBox = document.getElementById('user-followed-tags');
  const followTagsStatusElem = document.getElementById('follow-tags-status');
  if (!userFollowedTagsBox.firstElementChild) {
    followTagsStatusElem.innerHTML = '<Strong>You do not follow any tag.</Strong>';
  }
}
 
// Checks if an object (specifically a response object) is empty.
function isEmpty(obj) {
  for(var key in obj) {
    if(obj.hasOwnProperty(key)) {
      return false;
    }
  }
  return true;
}
 
// Returns a container for a followedTag, including a button to unfollow the tag.
function createFollowedTagContainer(tagObject) {
  const tagContainer = document.createElement('div');
  tagContainer.className = 'followtagbox';
 
  // The style defined in our css file is the [Ft_tagName] without the '#' sign, so slice it...
  tagContainer.id = `Ft_${tagObject.tag.slice(1)}`;
  tagContainer.innerHTML = `<strong>${tagObject.tag}</strong>`;
 
  const unfollowBtn = document.createElement('button');
  unfollowBtn.className = 'Ft-Btns';
  unfollowBtn.innerText = 'Unfollow tag';
  unfollowBtn.addEventListener('click', () => {
    const FTAG_ENTITY_KIND  = 'followedTag';
    deleteElement(tagObject, FTAG_ENTITY_KIND, tagContainer);
  });
 
  tagContainer.appendChild(unfollowBtn);
  return tagContainer;
}

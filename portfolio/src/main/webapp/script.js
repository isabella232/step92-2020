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
 
// Posts and fetches messages from the server and adds them to the DOM.
async function sendAndGet(sender, text, tag) {
  // Get post parameters.
  const textEle = document.getElementById(text);
  const nameEle = document.getElementById(sender);
  const tagEle = document.getElementById(tag);
  const parentidEle = document.getElementById('parentID');

  // TODO: use FormData function to post image file.
  
  // Create a |URLSearchParams()| and append parameters to it. 
  const params = new URLSearchParams();
  params.append('text-input', textEle.value);
  params.append('sender', nameEle.value);
  params.append('parentID', parentidEle.value);

  console.log("In SendAndGet");
  console.log("This is the text value", textEle.value);
  console.log("This is the sender value", nameEle.value);
  console.log("This is the tag value", tagEle.value);
  console.log("This is the parentID value", parentidEle.value);

  // Post parameters to the server and fetch instantly to build the page.
  fetch('/data', {method: 'POST', body: params})
        .then(response => response.json()).then((msgs) => {
    const statsListElement = document.getElementById('posts-list');
    msgs.forEach((msg) => {
      statsListElement.appendChild(createListElement(msg));
    })
  });
}

// Makes a Get request and loads Posts to the Blog page on body load
// Also loads 5 display-only posts to be displayed on the home page.
async function loadPosts(){
  const HOME_LOAD_AMOUNT = 5;
  fetch('/data').then(response => response.json()).then((msgs) => {
    const statsListElement = document.getElementById('posts-list');
    const homeListElement = document.getElementById('home-comments-container');
    var i = 1;
    msgs.forEach((msg) => {
      statsListElement.appendChild(createListElement(msg));
      if (i > HOME_LOAD_AMOUNT) {
        return true;
      }
      homeListElement.appendChild(createListElementHome(msg));
      i++;
    })
  });
}

function getCommentsTag(tag) {
  fetch('/data').then(response => response.json()).then((msgs) => {
   
  const statsListElement = document.getElementById('home-comments-container');
  statsListElement.innerHTML = '';
  msgs.forEach((msg) => {
    if (msg.tag == tag || tag == "") {
      statsListElement.appendChild(
        createListElement(msg.nickname + ': ' + msg.message));
      statsListElement.appendChild(
        createImgElement(msg.image));}
    })   
  });
}
 
// Creates an <li> element containing message details.
function createListElement(msg) {
  const postElement = document.createElement('li');
  postElement.className = 'post';

  const messageElement = document.createElement('span');
  messageElement.innerText = msg.message;
  
  const userElement = document.createElement('span');
  if (msg.nickname === undefined || msg.nickname === null) {
    userElement.innerHTML = "<b><i>_Anonymous</i></b>";
  } else {
    userElement.innerHTML = "<b><i>_" + msg.nickname + "</i></b>";
  }
  userElement.style.marginLeft = "15px";

  // TODO: move styles to style.css
  const timeElement = document.createElement('span');
  var date = new Date(msg.timestamp);
  timeElement.innerText = date.toString().slice(0, 24);
  timeElement.style.marginTop = "5px";
  timeElement.style.float = "right";
  timeElement.style.clear ="left";

  const deleteMsgElement = document.createElement('button');
  deleteMsgElement.innerText = 'Delete';
  deleteMsgElement.addEventListener('click', () => {
    deleteMessage(msg);
    postElement.remove();
  });

  const replyMsgElement = document.createElement('button');
  replyMsgElement.innerText = 'Reply';
  replyMsgElement.style.marginTop = "5px";
  replyMsgElement.style.float = "right";
  replyMsgElement.addEventListener('click', () => {
    openReplies(msg.timestamp, msg.tag);
  });

  postElement.appendChild(userElement);
  postElement.appendChild(messageElement);
  postElement.appendChild(timeElement);
  postElement.appendChild(replyMsgElement);
  postElement.appendChild(deleteMsgElement);
  return postElement;
}

// Creates a list of posts to be displayed on the homepage.
// Display-only.(ie. No reply or delete button). 
function createListElementHome(msg) {
  const postElement = document.createElement('li');
  postElement.className = 'post';

  const messageElement = document.createElement('span');
  messageElement.innerText = msg.message;
  
  const userElement = document.createElement('span');
  if (msg.nickname === undefined || msg.nickname === null) {
    userElement.innerHTML = "<b><i>_Anonymous</i></b>";
  } else {
    userElement.innerHTML = "<b><i>_" + msg.nickname + "</i></b>";
  }
  userElement.style.marginLeft = "15px";

  // TODO: move styles to style.css
  const timeElement = document.createElement('span');
  var date = new Date(msg.timestamp);
  timeElement.innerText = date.toString().slice(0, 24);
  timeElement.style.marginTop = "5px";
  timeElement.style.float = "right";
  timeElement.style.clear = "left";
  
  postElement.appendChild(userElement);
  postElement.appendChild(messageElement);
  postElement.appendChild(timeElement);

  return postElement;
}

/** Creates an <img> element containing text. */
function createImgElement(text) {
  const imgElement = document.createElement('img');
  imgElement.src = text;
  return imgElement;
}

// Deletes a post and sends a confirmation message for 5 seconds.
async function deleteMessage(msg) { 
  const params = new URLSearchParams();
  params.append('messageId', msg.id);
  fetch('/delete-data', {method: 'POST', body: params})
        .then(response => response.text()).then((text) => {
    const confirmationElement = document.getElementById('confirm');
    confirmationElement.innerHTML="";
    confirmationElement.style.display='inline-block';
    confirmationElement.innerHTML = `<i>${text}</i>`;
    setTimeout(function () {
        document.getElementById('confirm').style.display='none';}, 5000);
  });
}

function openReplies(timestamp, tag) { 
  document.getElementById("blogcontent").style.display = "none";
  document.getElementById("blogreplycontent").style.display = "block";
  document.getElementById('parentID').value = timestamp;
  document.getElementById('tags-reply').value = tag;
}

function closeReplies() { 
  document.getElementById("blogcontent").style.display = "block";
  document.getElementById("blogreplycontent").style.display = "none";
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

function sendFollowedTags() {
  const tagElem = document.getElementById('followtags');

  const params = new URLSearchParams();
  params.append('tags', tagElem.value);

  fetch('/follow-tags', {method: 'POST', body: params});
}
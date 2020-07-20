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
async function sendAndGet() {
  // Get post parameters.
  const textEle = document.getElementById('text-input');
  const nameEle = document.getElementById('sender');
  const tagEle = document.getElementById('tags');

  // TODO: use FormData function to post image file.
  
  // Create a |URLSearchParams()| and append parameters to it. 
  const params = new URLSearchParams();
  params.append('text-input', textEle.value);
  params.append('sender', nameEle.value);
  params.append('tags', tagEle.value);

  // Post parameters to the server and fetch instantly to build the page.
  fetch('/data', {method: 'POST', body: params})
        .then(response => response.json()).then((msgs) => {
    const statsListElement = document.getElementById('posts-list');
    msgs.forEach((msg) => {
      statsListElement.appendChild(createListElement(msg));
    })
  });
}

function getCommentsHome() {
  fetch('/data').then(response => response.json()).then((msgs) => {
    const statsListElement = document.getElementById('home-comments-container');
    statsListElement.innerHTML = '';
    msgs.forEach((msg) => {
      statsListElement.appendChild(
        createListElement(msg.nickname + ': ' + msg.message));
      statsListElement.appendChild(
        createImgElement(msg.image));
    })
  });  
}

async function loadPosts(){
  fetch('/data').then(response => response.json()).then((msgs) => {
    const statsListElement = document.getElementById('posts-list');
    msgs.forEach((msg) => {
      statsListElement.appendChild(createListElement(msg));
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
  postElement.style.margin = "10px";

  const messageElement = document.createElement('span');
  messageElement.innerText = msg.message;
  
  const userElement = document.createElement('span');
  if (msg.nickname === undefined || msg.nickname === null) {
    userElement.innerHTML = "<b><i>_Anonymous</i></b>";
  } else {
    userElement.innerHTML = "<b><i>_" + msg.nickname + "</i></b>";
  }
  userElement.style.marginLeft = "15px";

  const timeElement = document.createElement('span');
  var date = new Date(msg.timestamp);
  timeElement.innerText = date.toString().slice(0, 24);
  timeElement.style.marginTop = "5px";
  timeElement.style.float = "right";
  timeElement.style.clear ="left";

  const deleteMsgElement = document.createElement('button');
  deleteMsgElement.innerText = 'Delete';
  deleteMsgElement.style.marginTop = "5px";
  deleteMsgElement.style.float = "right";
  deleteMsgElement.addEventListener('click', () => {
    deleteMessage(msg);
    postElement.remove();
  });

  postElement.appendChild(messageElement);
  postElement.appendChild(userElement);
  postElement.appendChild(deleteMsgElement);
  postElement.appendChild(timeElement);

  return postElement;
}

/** Creates an <img> element containing text. */
function createImgElement(text) {
  const imgElement = document.createElement('img');
  imgElement.src = text;
  return imgElement;
}
 
async function deleteMessage(msg) {
  const params = new URLSearchParams();
  params.append('messageId', msg.id);
  fetch('/delete-data', {method: 'POST', body: params})
        .then(response => response.text()).then((text) => {
    const confirmationElement = document.getElementById('confirm');
    confirmationElement.innerHTML = text;
  });
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


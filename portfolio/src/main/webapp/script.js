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

/**
 * Fetches messages from the servers and adds them to the DOM.
 */
async function getComments() {
    fetch('/data').then(response => response.json()).then((msgs) => {
   
    const statsListElement = document.getElementById('comments-container');
    statsListElement.innerHTML = '';
    msgs.forEach((msg) => {
        statsListElement.appendChild(
            createListElement(msg.sender + ': ' + msg.message));
        statsListElement.appendChild(
            createImgElement(msg.imgUrl));
    })
    
  });
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}
/** Creates an <img> element containing text. */
function createImgElement(text) {
  const imgElement = document.createElement('img');
  imgElement.src = text;
  return imgElement;
}

function deleteData(){
    fetch('/delete-data', {method: 'POST'}).then(getComments());
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
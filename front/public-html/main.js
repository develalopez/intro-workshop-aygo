var messages = []

var redrawMessages = function() {
  var ul = document.createElement('ul')
  ul.setAttribute('class', 'messages')

  messages.forEach(function (message) {
    var li = document.createElement('li')

    ul.appendChild(li);
    li.appendChild(document.createTextNode(message.content))
  })

  var messagesElement = document.getElementById("messages")
  if (messagesElement.hasChildNodes()) {
    messagesElement.removeChild(messagesElement.childNodes[0]);
  }
  messagesElement.appendChild(ul)
}

var xhttp = new XMLHttpRequest()

xhttp.onreadystatechange = function () {
  if (this.readyState == 4 && this.status == 200) {
    messages = JSON.parse(this.responseText)
    redrawMessages()
  }
}

xhttp.open("GET", "http://roundrobin:35000/messages", true)
xhttp.setRequestHeader("Origin", "http://front:8080")
xhttp.setRequestHeader("Referer", "http://front:8080/")
xhttp.send()

var sendToServer = function (message) {
  var xhttpForPost = new XMLHttpRequest()
  xhttpForPost.onreadystatechange = function () {
    if (this.readyState == 4 && this.status == 201) {
      console.log(this.responseText)
    }
  }

  xhttpForPost.open("POST", 'http://roundrobin:35000/messages', true)
  xhttpForPost.send(message);
}

window.onload = function (e) {
  var form = document.getElementById("messages_form")
  form.onsubmit = function (e) {
    e.preventDefault()

    var message = e.target.elements[0].value

    messages.push(message)
    e.target.elements[0].value = ""
    redrawMessages()
    sendToServer(message)
  }
}
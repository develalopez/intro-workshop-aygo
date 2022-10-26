var xhttp = new XMLHttpRequest()
  
xhttp.onreadystatechange = function () {
  if (this.readyState == 4 && this.status == 200) {
    const data = JSON.parse(this.responseText)
    console.log(data)

    var ul = document.createElement('ul')
    ul.setAttribute('class', 'messages')

    data.forEach(function(message){
      var li = document.createElement('li')
      
      ul.appendChild(li);
      li.appendChild(document.createTextNode(message.message))
    })

    document.getElementById("messages").appendChild(ul)
  }
}

xhttp.open("GET", "/messages", true)
xhttp.send()
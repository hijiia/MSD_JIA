// Chat logic and WebSocket handling
let ws;
let username;
let roomname;

const joinBtn = document.getElementById("joinBtn");
const sendBtn = document.getElementById("sendBtn");
const leaveBtn = document.getElementById("leaveBtn");
const chatArea = document.getElementById("chatArea");
const loginArea = document.getElementById("loginArea");
const chatBox = document.getElementById("chatBox");
const messageInput = document.getElementById("messageInput");
const roomLabel = document.getElementById("roomLabel");

// Connect to the WebSocket when the user joins the chat
joinBtn.addEventListener("click", function() {
    username = document.getElementById("username").value.trim();
    roomname = document.getElementById("roomname").value.trim();

    if (username && roomname.match(/^[a-z]+$/)) {
        ws = new WebSocket("ws://" + location.host);

        ws.onopen = function() {
            ws.send(`join ${username} ${roomname}`);
            chatBox.value = `You have joined the room ${roomname}.\n`;
            chatArea.style.display = "block";
            loginArea.style.display = "none";
            roomLabel.textContent = roomname;
        };

        ws.onmessage = function(event) {
            const message = JSON.parse(event.data);

            if (message.type === "message") {
                chatBox.value += `${message.user}: ${message.message}\n`;
            } else if (message.type === "join") {
                chatBox.value += `${message.user} has joined the room.\n`;
            } else if (message.type === "leave") {
                chatBox.value += `${message.user} has left the room.\n`;
            }
            chatBox.scrollTop = chatBox.scrollHeight;
        };

        ws.onerror = function(error) {
            alert("WebSocket error: " + error);
        };

        ws.onclose = function() {
            chatBox.value += "Connection closed.\n";
        };
    } else {
        alert("Invalid username or room name (only lowercase letters are allowed for room name).");
    }
});

// Send a message to the room
sendBtn.addEventListener("click", function() {
    const message = messageInput.value.trim();
    if (message && ws) {
        ws.send(`message ${message}`);
        messageInput.value = "";
    }
});

// Leave the chat room
leaveBtn.addEventListener("click", function() {
    if (ws) {
        ws.send("leave");
        ws.close();
        chatArea.style.display = "none";
        loginArea.style.display = "block";
        chatBox.value = "";
    }
});
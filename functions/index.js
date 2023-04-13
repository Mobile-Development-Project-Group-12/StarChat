const express = require("express");
const app = express();
const http = require("http").createServer(app);
const io = require("socket.io")(http);
const functions = require("firebase-functions");

http.listen(3000, () => {
  console.log("listening on *:3000");
});

io.on("connection", (socket) => {
  console.log("a user connected");

  socket.on("send_message", (msg) => {
    console.log("message: " + msg);
    socket.emit("receive_message', 'Hello, client!");
  });
});

// function that returns something to the client
exports.function7 = functions.https.onRequest((req, res) => {
  console.log("function7 said TESTING HELLO WORLD");
});

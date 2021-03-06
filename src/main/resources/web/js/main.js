var dwws = dwws || {};

(function (dwws, $) {
   "use strict";

   var $status = $("#status");
   var socket = null;

   function connectSocket() {
      if(socket == null) {
         socket = new WebSocket("ws://localhost:8091/ws/");

         socket.onopen = function() {
            console.log("Connected!");
            $status.prepend("<p>成功连接 websocket!</p>");
         };

         socket.onclose = function() {
            console.log("Closed!");
            $status.prepend("<p>关闭链接 websocket</p>");
         };

         socket.onmessage = function(msg) {
            console.log("Gots message", msg, this);
            $status.prepend("<p>" + msg.data + "</p>");
         };
      }
   }

   $("#broadcastBtn").click(function() {
      var msg = $("#msg").val();
      $.ajax({
         type: "POST",
         url: "/broadcast",
         data: msg,
         contentType: "text/plain"
      });
   });

   $("#sendBtn").click(function() {
      if(socket != null) {
         socket.send($("#msg").val());
      }
   });

   $("#connectBtn").click(connectSocket);

   $("#disconnectBtn").click(function() {
      if(socket != null) {
         socket.close();
         socket = null;
      }
   });
}(dwws, jQuery));

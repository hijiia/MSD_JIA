let x = document.getElementById("xVal");
let y = document.getElementById("yVal");
let submit = document.getElementById("calculate");
let result = document.getElementById("result");

let ws = new WebSocket("ws://localhost:8080");
let isConnected = false;

ws.onopen = function(){
    isConnected = true;
    console.log("Connection is established");
};

ws.onmessage = function(messageEvent){
    result.textContent = messageEvent.data; // Update result with the data from WebSocket
};

ws.onerror = function(e){
    console.error("WebSocket error:", e);
};

ws.onclose = function (e){
    console.log("Connection is now closed");
};

submit.addEventListener("click", function() {
    let xValue = Number(x.value);
    let yValue = Number(y.value);

    if (!(isNaN(xValue) || isNaN(yValue))) {
        fetch("http://localhost:8080/calculate?x=" + x.value + "&y=" + y.value)
            .then(response => {
                if (!response.ok) {
                    throw new Error("Network response was not ok");
                }
                return response.text();
            })
            .then(data => {
                result.textContent = data; // Update result with the fetched data
            })
            .catch(error => {
                console.error("Fetch error:", error);
            });
    }
});
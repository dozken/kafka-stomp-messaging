let stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    const notifications = $("#notifications");
    if (connected) {
        notifications.show();
    } else {
        notifications.hide();
    }
    notifications.html("");
}

function connect(recipient = "") {
    const socket = new SockJS('/ws-notification');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, frame => {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe(`/topic/notification/${recipient}`, notificationMessage => {
            console.log('notificationMessage',notificationMessage)
            showGreeting(notificationMessage.body);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    connect($("#recipient").val());
}

function showGreeting(message) {
    $("#notifications").append("<pre>" + message + "</pre>");
}

$(function () {
    $("form").on('submit', e => e.preventDefault());
    $("#disconnect").click(() => disconnect());
    $("#connect").click(() => sendName());
});


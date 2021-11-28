let stompClient = null;

function connect(recipients) {
    const socket = new SockJS('/ws-notification');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, frame => {
        setConnected(true);
        console.log('Connected: ' + frame);
        recipients.forEach(recipient => {
            stompClient.subscribe(`/topic/notification/${recipient}`, notificationMessage => {
                console.log('notificationMessage', notificationMessage)
                showGreeting(recipient, notificationMessage.body);
            });
        })
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    $("#notifications").toggle(connected);
}

function useRecipients() {
    const recipients = $("#recipients").val().split(`,`);
    console.log('recipients', recipients);
    recipients.forEach(recipient => {
        $("#notifications").append(`
            <div class="col-xs-12">
                <label for="recipient-log-${recipient}">Recipient: ${recipient}</label>
                <div id="recipient-log-${recipient}" class="recipient-log"></div>
            </div>
        `);
    });

    connect(recipients);
}

function showGreeting(recipient, message) {
    $(`#recipient-log-${recipient}`).append("<pre>" + message + "</pre>");
}

$(() => {
    $("form").on('submit', e => e.preventDefault());
    $("#disconnect").click(() => disconnect());
    $("#connect").click(() => useRecipients());
});


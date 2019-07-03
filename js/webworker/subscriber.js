importScripts("../lib/eftl.patched.js");

var connection = null;

onmessage = function (e) {
    console.log(e.data);
    switch (e.data.type) {
        case 'printVersion':
            addInfo(eFTL.getVersion());
            break;

        case 'connect':
            doConnect(
                e.data.url,
                e.data.username,
                e.data.password,
                e.data.clientId
            );
            break;

        case 'disconnect':
            doDisconnect();
            break;

        case 'subscribe':
            if (connection !== null) {
                doSubscribe(
                    e.data.dureable,
                    e.data.matcher
                );
            }
            break;
    }
};

function doConnect(url, username, password, clientId) {
    // Asynchronously connect to the eFTL server.

    eFTL.connect(url, {
        username: username,
        password: password,
        clientId: clientId,
        onConnect: function (eFTLConnection) {

            // Successufully connected to the eFTLserver.
            connection = eFTLConnection;
            addSuccess("Connected to eFTL server");
            postMessage({
                'type': 'connection'
            });
        },
        onDisconnect: function (eFTLConnection, code, reason) {

            // Lost connection to the eFTL server.
            postMessage({
                'type': 'disconnected'
            });
            addWarning("Disconnected: " + reason);
        },
        onReconnect: function (eFTLConnection) {

            // Successufully reconnected to the eFTLserver.
            addSuccess("Reconnected");
            postMessage({
                'type': 'reconnected'
            });
        },
        onError: function (eFTLConnection, code, reason) {

            // The eFTL server has reported an error.
            addError("Error: " + reason);
        }
    });
}

function doDisconnect() {
    if (connection !== null) {

        // Asynchronously disconnect from the eFTL server.
        connection.disconnect();
    }
    return false;
}

function doSubscribe(dureable, matcher) {
    connection.subscribe({
        matcher: matcher,
        durable: dureable,
        onSubscribe: function (id) {
            addSuccess("Subscribed: " + matcher);
        },
        onError: function (id, code, reason) {
            addError(reason);
        },
        onMessage: function (message) {
            // addInfo("Got message: " + message.get("payload").substr(0, 12));
            try {
                let now = Date.now();
                let attachment = message.get("payload");
                let splits = attachment.split(';');
                let timeWhenSent = splits[0].substr(4);
                let elapsedTime = now - parseInt(timeWhenSent);
                console.log("Elapsed Time in ms: " + elapsedTime + " | " + splits[1].substr(0, 40) + " | " + attachment.length + " bytes");
                addInfo("Elapsed Time in ms: " + elapsedTime + " | " + splits[1].substr(0, 40) + " | " + attachment.length + " bytes");
            } catch (e) {
                addInfo("Got message: " + message.toString());
            }
        }
    });
}

function addError(message) {
    log(message, 'error')
}

function addWarning(message) {
    log(message, 'warning');
}

function addInfo(message) {
    log(message, 'info');
}

function addSuccess(message) {
    log(message, 'success');
}

function log(message, type) {
    postMessage({
        'type': 'msg',
        'msg': message,
        'serenity': type
    });
}
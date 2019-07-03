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

        case 'sendMessage':
            if (connection !== null) {
                sendFtlMessage(
                    e.data.msgTemplate,
                    e.data.data
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

function sendFtlMessage(msgTemplate, data) {
    var m = new eFTLMessage(msgTemplate);

    // Publish messages with a destination field of "sample".
    //
    // When connected to an EMS channel the destination field  must
    // be present and set to the EMS topic on which to publish the
    // message.
    for (var property in data) {
        if (data.hasOwnProperty(property)) {
            m.set(property, data[property]);
        }
      }

    // Asynchronously publish the message.
    connection.publish(m, {
        onComplete: function (message) {
            addInfo("Published: " + message.get("payload"));
        },
        onError: function (message, code, reason) {
            addError("Error publishing: " + reason);
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
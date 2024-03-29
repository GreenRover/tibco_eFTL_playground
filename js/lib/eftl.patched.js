var WebSocket = WebSocket || require("ws");
! function() {
    function _isArray(a) {
        return Array.isArray ? Array.isArray(a) : "[object Array]" == Object.prototype.toString.call(a)
    }

    function _isDate(d) {
        return "[object Date]" == Object.prototype.toString.call(d)
    }

    function _isFunction(f) {
        return "[object Function]" == Object.prototype.toString.call(f)
    }

    function _isNumber(n) {
        return "[object Number]" == Object.prototype.toString.call(n)
    }

    function _isBoolean(b) {
        return !0 === b || !1 === b || "[object Boolean]" == Object.prototype.toString.call(b)
    }

    function _isObject(o) {
        return o === Object(o)
    }

    function _isString(s) {
        return "[object String]" == Object.prototype.toString.call(s)
    }

    function _isUndefined(o) {
        return void 0 === o
    }

    function _isNull(o) {
        return null === o
    }

    function _isInteger(n) {
        return n % 1 == 0
    }

    function _each(obj, iterator, context) {
        if (null != obj)
            if (arrayForEach && obj.forEach === arrayForEach) obj.forEach(iterator, context);
            else if (obj.length === +obj.length) {
            for (var i = 0, l = obj.length; i < l; i++)
                if (i in obj && iterator.call(context, obj[i], i, obj) === {}) return
        } else
            for (var key in obj)
                if (Object.prototype.hasOwnProperty.call(obj, key) && iterator.call(context, obj[key], key, obj) === {}) return
    }

    function _map(obj, iterator, context) {
        if (null == obj) return [];
        if (arrayMap && obj.map === arrayMap) return obj.map(iterator, context);
        var retVal = [];
        return _each(obj, function(value, index, list) {
            retVal[retVal.length] = iterator.call(context, value, index, list)
        }), obj.length === +obj.length && (retVal.length = obj.length), retVal
    }

    function _safeExtend(o) {
        return _each(Array.prototype.slice.call(arguments, 1), function(source) {
            var v;
            for (var key in source)
                if (source.hasOwnProperty(key)) {
                    if (v = source[key], _isNull(v) || _isUndefined(v) || _isFunction(v)) continue;
                    _isBoolean(v) && (v = v.toString()), o[key] = v
                }
        }), o
    }

    function parseURL(str) {
        if ("object" == typeof module && module.exports) {
            return (url = require("url")).parse(str)
        }
        if ("function" == typeof URL) {
            return new URL(str);
        }
        var url = document.createElement("a");
        return url.href = str, url.query = url.search && "?" === url.search.charAt(0) ? url.search.substring(1) : url.search, -1 !== str.indexOf("://") && -1 !== str.indexOf("@") && (url.auth = str.substring(str.indexOf("://") + 3, str.indexOf("@"))), url
    }

    function doubleToJson(value, key, list) {
        var retVal = {};
        return isFinite(value) ? retVal[DOUBLE_FIELD] = value : retVal[DOUBLE_FIELD] = value.toString(), retVal
    }

    function jsonToDouble(raw) {
        return _isNumber(raw[DOUBLE_FIELD]) ? raw[DOUBLE_FIELD] : parseFloat(raw[DOUBLE_FIELD])
    }

    function dateToJson(value, key, list) {
        var millis = value.getTime(),
            retVal = {};
        return retVal[MILLISECOND_FIELD] = millis, retVal
    }

    function jsonToDate(raw) {
        if (void 0 !== raw[MILLISECOND_FIELD]) {
            var millis = raw[MILLISECOND_FIELD];
            return new Date(millis)
        }
    }

    function eFTLMessage() {
        1 === arguments.length && _safeExtend(this, arguments[0])
    }

    function eFTLConnection() {
        this.connectOptions = null, this.accessPointURL = null, this.webSocket = null, this.state = CLOSED, this.clientId = null, this.reconnectToken = null, this.timeout = 6e5, this.heartbeat = 24e4, this.maxMessageSize = 0, this.lastMessage = 0, this.timeoutCheck = null, this.subscriptions = {}, this.sequenceCounter = 0, this.subscriptionCounter = 0, this.reconnectCounter = 0, this.autoReconnectAttempts = 5, this.autoReconnectMaxDelay = 3e4, this.reconnectTimer = null, this.connectTimer = null, this.isReconnecting = !1, this.isOpen = !1, this.qos = !0, this.sendList = {}, this.lastSequenceNumber = 0
    }
    _isUndefined(console) && (console = {}, console.log = function(arg) {}, console.info = function(arg) {}, console.error = function(arg) {});
    var trustCertificates, openConnections = [],
        arrayForEach = Array.prototype.forEach,
        arrayMap = Array.prototype.map,
        _keys = Object.keys || function(o) {
            if (o !== Object(o)) throw new TypeError("Invalid object");
            var retVal = [];
            for (var k in o) Object.prototype.hasOwnProperty.call(o, k) && (retVal[retVal.length] = k);
            return retVal
        };
    "undefined" == typeof btoa && (btoa = function(str) {
        return new Buffer(str).toString("base64")
    }), "undefined" == typeof atob && (atob = function(str) {
        return new Buffer(str, "base64").toString()
    });
    var eFTL = function() {};
    eFTL.prototype.connect = function(url, options) {
        if (_isUndefined(url) || _isNull(url)) throw new TypeError("The URL is null or undefined.");
        if (!(url = url.trim()).startsWith("ws:") && !url.startsWith("wss:")) throw new SyntaxError("The URL's scheme must be either 'ws' or 'wss'.");
        (new eFTLConnection).open(url, options)
    }, eFTL.prototype.setTrustCertificates = function(certs) {
        trustCertificates = certs
    }, eFTL.prototype.getVersion = function() {
        return "TIBCO eFTL Version " + EFTL_VERSION
    };
    var EFTL_VERSION = "3.4.0   V10",
        DOUBLE_FIELD = "_d_",
        MILLISECOND_FIELD = "_m_",
        OPAQUE_FIELD = "_o_";
    eFTLMessage.prototype.set = function(fieldName, value) {
        var val = void 0;
        if (value instanceof eFTLMessage) val = value;
        else if (_isArray(value))
            if (value.length > 0)
                if (value[0] instanceof eFTLMessage) val = _map(value, function(o) {
                    return o
                });
                else if (_isDate(value[0])) val = _map(value, dateToJson);
        else if (_isString(value[0])) val = value;
        else {
            if (!_isNumber(value[0])) throw new TypeError("Unsupported object type: " + typeof value);
            val = _isInteger(value[0]) ? value : _map(value, doubleToJson)
        } else val = value;
        else if (_isDate(value)) val = dateToJson(value);
        else if (_isString(value)) val = value;
        else if (_isNumber(value)) val = _isInteger(value) ? value : doubleToJson(value);
        else if (!1 === _isNull(value) && !1 === _isUndefined(value)) throw new TypeError("Unsupported object type: " + typeof value);
        _isUndefined(val) ? delete this[fieldName] : this[fieldName] = val
    }, eFTLMessage.prototype.setAsOpaque = function(fieldName, value) {
        _isNull(value) || _isUndefined(value) ? delete this[fieldName] : this[fieldName] = function(value, key, list) {
            var retVal = {};
            return retVal[OPAQUE_FIELD] = btoa(value), retVal
        }(value)
    }, eFTLMessage.prototype.setAsLong = function(fieldName, value) {
        var val = void 0;
        if (_isArray(value))
            if (value.length > 0) {
                if (!_isNumber(value[0])) throw new TypeError("Unsupported object type: " + typeof value);
                val = _map(value, Math.floor)
            } else val = value;
        else if (_isNumber(value)) val = Math.floor(value);
        else if (!1 === _isNull(value) && !1 === _isUndefined(value)) throw new TypeError("Unsupported object type: " + typeof value);
        _isUndefined(val) ? delete this[fieldName] : this[fieldName] = val
    }, eFTLMessage.prototype.setAsDouble = function(fieldName, value) {
        var val = void 0;
        if (_isArray(value))
            if (value.length > 0) {
                if (!_isNumber(value[0])) throw new TypeError("Unsupported object type: " + typeof value);
                val = _map(value, doubleToJson)
            } else val = value;
        else if (_isNumber(value)) val = doubleToJson(value);
        else if (!1 === _isNull(value) && !1 === _isUndefined(value)) throw new TypeError("Unsupported object type: " + typeof value);
        _isUndefined(val) ? delete this[fieldName] : this[fieldName] = val
    }, eFTLMessage.prototype.get = function(fieldName) {
        var raw = this[fieldName];
        return _isArray(raw) ? raw.length > 0 && _isObject(raw[0]) ? raw[0][DOUBLE_FIELD] ? _map(raw, jsonToDouble) : raw[0][MILLISECOND_FIELD] ? _map(raw, jsonToDate) : _map(raw, function(o) {
            return new eFTLMessage(o)
        }) : raw : _isObject(raw) ? void 0 !== raw[DOUBLE_FIELD] ? jsonToDouble(raw) : void 0 !== raw[MILLISECOND_FIELD] ? jsonToDate(raw) : void 0 !== raw[OPAQUE_FIELD] ? function(raw) {
            if (void 0 !== raw[OPAQUE_FIELD]) return atob(raw[OPAQUE_FIELD])
        }(raw) : new eFTLMessage(raw) : raw
    }, eFTLMessage.prototype.getFieldNames = function() {
        var buf = [];
        return _each(_keys(this), function(kn) {
            buf.push(kn)
        }), buf
    }, eFTLMessage.prototype.toString = function() {
        var msg = this,
            str = "{";
        return _each(_keys(this), function(kn) {
            var val = msg.get(kn);
            str.length > 1 && (str += ", "), str += kn, str += "=", _isArray(val) ? (str += "[", str += val, str += "]") : str += val
        }), str += "}"
    };
    var CLOSED = 3;
    eFTLConnection.prototype._caller = function(func, context, args) {
        try {
            func && func.apply(context, args)
        } catch (ex) {
            console.error("Exception while calling client callback", ex, ex.stack)
        }
    }, eFTLConnection.prototype.isConnected = function() {
        return 1 === this.state
    }, eFTLConnection.prototype.createMessage = function() {
        return new eFTLMessage
    }, eFTLConnection.prototype._send = function(message, seqNum, forceSend) {
        var data = 4 === arguments.length ? arguments[3] : JSON.stringify(message);
        if (this._debug(">>", message), this.webSocket && (this.state < 2 || forceSend)) try {
            this.webSocket.send(data), !this.qos && seqNum > 0 && this._pendingComplete(seqNum)
        } catch (err) {}
    }, eFTLConnection.prototype._subscribe = function(subId, options) {
        var subscription = {};
        subscription.options = options, subscription.id = subId, subscription.pending = !0;
        var subMessage = {};
        subMessage.op = 3, subMessage.id = subId, subMessage.matcher = options.matcher, subMessage.durable = options.durable;
        var opts = _safeExtend({}, options);
        return delete opts.matcher, delete opts.durable,
            function(o) {
                _each(Array.prototype.slice.call(arguments, 1), function(source) {
                    for (var key in source) source.hasOwnProperty(key) && (o[key] = source[key])
                })
            }(subMessage, opts), this.subscriptions[subId] = subscription, this._send(subMessage, 0, !1), subId
    }, eFTLConnection.prototype._ack = function(sequenceNumber) {
        if (this.qos && sequenceNumber) {
            var envelope = {};
            envelope.op = 9, envelope.seq = sequenceNumber, this._send(envelope, 0, !1)
        }
    }, eFTLConnection.prototype._sendMessage = function(message, type, options) {
        type = type || ".m.";
        var envelope = {};
        envelope.op = 8;
        var body = new eFTLMessage(message);
        envelope.body = body;
        var sequence = this._nextSequence();
        this.qos && (envelope.seq = sequence);
        var publish = {};
        publish.options = options, publish.message = message, publish.envelope = envelope, publish.sequence = sequence;
        var data = JSON.stringify(envelope);
        if (this.maxMessageSize > 0 && data.length > this.maxMessageSize) throw new Error("Message exceeds maximum message size of " + this.maxMessageSize);
        this.sendList[sequence] = publish, 1 === this.state && this._send(envelope, sequence, !1, data)
    }, eFTLConnection.prototype._disconnect = function() {
        this.reconnectTimer && (clearTimeout(this.reconnectTimer), this.reconnectTimer = null, this._clearPending(11, "Disconnected"), this._caller(this.connectOptions.onDisconnect, this.connectOptions, [this, 1e3, "Disconnect"])), this._close("Connection closed by user.", !0)
    }, eFTLConnection.prototype._close = function(reason, notifyServer) {
        if (0 === this.state || 1 === this.state) {
            if (this.state = 2, this.timeoutCheck && (clearInterval(this.timeoutCheck), this.timeoutCheck = null), notifyServer) {
                reason = reason || "User Action";
                var dcMessage = {};
                dcMessage.op = 11, dcMessage.reason = reason, this._send(dcMessage, 0, !0)
            }
            this.webSocket.close()
        }
    }, eFTLConnection.prototype._handleSubscribed = function(message) {
        var subId = message.id,
            sub = this.subscriptions[subId];
        if (null != sub && sub.pending) {
            sub.pending = !1;
            var options = sub.options;
            this._caller(options.onSubscribe, options, [subId])
        }
    }, eFTLConnection.prototype._handleUnsubscribed = function(message) {
        var subId = message.id,
            errCode = message.err,
            reason = message.reason,
            sub = this.subscriptions[subId];
        if (null != sub) {
            delete this.subscriptions[subId];
            var options = sub.options;
            this._caller(options.onError, options, [subId, errCode, reason])
        }
    }, eFTLConnection.prototype._handleEvent = function(message) {
        var to = message.to,
            seqNum = message.seq,
            data = message.body,
            sub = this.subscriptions[to];
        if (!this.qos || !seqNum || seqNum > this.lastSequenceNumber) {
            if (sub && null != sub.options) {
                var wsMessage = new eFTLMessage(data);
                this._debug("<<", data), this._caller(sub.options.onMessage, sub.options, [wsMessage])
            }
            this.qos && seqNum && (this.lastSequenceNumber = seqNum)
        }
        this._ack(seqNum)
    }, eFTLConnection.prototype._handleEvents = function(messages) {
        for (var i = 0, max = messages.length; i < max; i++) {
            var message = messages[i];
            this._handleEvent(message)
        }
    }, eFTLConnection.prototype._initWS = function(webSocket) {
        var connection = this;
        webSocket.onopen = function(arg) {
            var url = parseURL(connection.accessPointURL),
                auth = url.auth ? url.auth.split(":") : [],
                username = auth[0],
                password = auth[1],
                clientId = function(url, name) {
                    var query = url.query;
                    if (query)
                        for (var vars = query.split("&"), i = 0; i < vars.length; i++) {
                            var pair = vars[i].split("=");
                            if (pair[0] == name) return pair[1]
                        }
                }(url, "clientId"),
                loginMessage = {};
            loginMessage.op = 1, loginMessage.client_type = "js", loginMessage.client_version = EFTL_VERSION, username ? loginMessage.user = username : connection.connectOptions.hasOwnProperty("username") ? loginMessage.user = connection.connectOptions.username : loginMessage.user = connection.connectOptions.user, loginMessage.password = password || connection.connectOptions.password, connection.clientId && connection.reconnectToken ? (loginMessage.client_id = connection.clientId, loginMessage.id_token = connection.reconnectToken) : loginMessage.client_id = clientId || connection.connectOptions.clientId, _isBoolean(connection.connectOptions._qos) || (connection.connectOptions._qos = !0), connection.qos = connection.connectOptions._qos, _isNumber(connection.connectOptions.autoReconnectAttempts) && (connection.autoReconnectAttempts = connection.connectOptions.autoReconnectAttempts), _isNumber(connection.connectOptions.autoReconnectMaxDelay) && (connection.autoReconnectMaxDelay = connection.connectOptions.autoReconnectMaxDelay);
            var options = _safeExtend({}, connection.connectOptions);
            connection.isReconnecting && (options._resume = "true");
            _each(["user", "username", "password", "clientId"], function(value) {
                delete options[value]
            }), loginMessage.login_options = options, connection._send(loginMessage, 0, !1)
        }, webSocket.onerror = function(error) {}, webSocket.onclose = function(evt) {
            if (null !== webSocket && connection.state !== CLOSED) {
                connection.state = CLOSED, connection.connectTimer && (clearTimeout(connection.connectTimer), connection.connectTimer = null), connection.timeoutCheck && (clearInterval(connection.timeoutCheck), connection.timeoutCheck = null), connection.webSocket = null;
                var index = openConnections.indexOf(this); - 1 !== index && openConnections.splice(index, 1), evt.wasClean || 1006 != evt.code ? (connection.isOpen = !1, connection._clearPending(11, "Closed"), connection._caller(connection.connectOptions.onDisconnect, connection.connectOptions, [connection, evt.code, evt.reason ? evt.reason : "Connection failed"])) : connection.isOpen && connection._scheduleReconnect() || (connection.isOpen = !1, connection._clearPending(11, "Closed"), connection._caller(connection.connectOptions.onDisconnect, connection.connectOptions, [connection, evt.code, evt.reason ? evt.reason : "Connection failed"]))
            }
        }, webSocket.onmessage = function(rawMessage) {
            var msg = JSON.parse(rawMessage.data);
            if (connection._debug("<<", msg), connection.lastMessage = (new Date).getTime(), _isArray(msg)) try {
                connection._handleEvents(msg)
            } catch (err) {
                console.error("Exception on onmessage callback.", err, err.stack)
            } else try {
                if (!_isUndefined(msg.op)) {
                    var opCode = msg.op;
                    switch (opCode) {
                        case 0:
                            connection._send.apply(connection, [msg, 0, !1, rawMessage.data]);
                            break;
                        case 2:
                            connection._handleWelcome(msg);
                            break;
                        case 4:
                            connection._handleSubscribed(msg);
                            break;
                        case 6:
                            connection._handleUnsubscribed(msg);
                            break;
                        case 7:
                            connection._handleEvent(msg);
                            break;
                        case 10:
                            connection._handleError(msg);
                            break;
                        case 12:
                            connection._handleGoodbye(msg);
                            break;
                        case 9:
                            connection._handleAck(msg);
                            break;
                        default:
                            console.log("Received unknown/unexpected op code - " + opCode, rawMessage.data)
                    }
                }
            } catch (err) {
                console.error(err, err.stack)
            }
        }
    }, eFTLConnection.prototype._handleWelcome = function(message) {
        var reconnectId = null != this.reconnectToken,
            resume = "true" === ((message._resume || "") + "").toLowerCase();
        this.clientId = message.client_id, this.reconnectToken = message.id_token, this.timeout = 1e3 * message.timeout, this.heartbeat = 1e3 * message.heartbeat, this.maxMessageSize = message.max_size, resume || (this._clearPending(11, "Reconnect"), this.lastSequenceNumber = 0), this.qos = "true" === ((message._qos || "") + "").toLowerCase();
        var connection = this;
        this.heartbeat > 0 && (this.timeoutCheck = setInterval(function() {
            var now = (new Date).getTime(),
                diff = now - connection.lastMessage;
            diff > connection.timeout && (connection._debug("lastMessage in timer ", {
                id: connection.clientId,
                now: now,
                lastMessage: connection.lastMessage,
                timeout: connection.timeout,
                diff: diff,
                evaluate: diff > connection.timeout
            }), connection._close("Connection timeout", !1))
        }, this.heartbeat)), this.state = 1, this.reconnectCounter = 0, _keys(this.subscriptions).length > 0 && !resume && _each(this.subscriptions, function(value, key) {
            value.pending = !0
        }), _keys(this.subscriptions).length > 0 && _each(this.subscriptions, function(value, key) {
            value.pending && connection._subscribe(key, value.options)
        }), this._sendPending(), reconnectId && !this.isReconnecting ? this._caller(this.connectOptions.onReconnect, this.connectOptions, [this]) : this.isReconnecting || this._caller(this.connectOptions.onConnect, this.connectOptions, [this]), this.isReconnecting = !1, this.isOpen = !0
    }, eFTLConnection.prototype._handleError = function(message) {
        var errCode = message.err,
            reason = message.reason;
        this._caller(this.connectOptions.onError, this.connectOptions, [this, errCode, reason]), 0 === this.state && this._close(reason, !1)
    }, eFTLConnection.prototype._handleGoodbye = function(message) {
        message = message || "Server Goodbye", this._close(message, !1)
    }, eFTLConnection.prototype._handleAck = function(message) {
        var sequence = message.seq,
            errCode = message.err,
            reason = message.reason;
        _isUndefined(sequence) || this._ackPending(sequence, errCode, reason)
    }, eFTLConnection.prototype._ackPending = function(seqNumber, errCode, reason) {
        var i, max, sequences = _keys(this.sendList);
        if (sequences.length > 0)
            for (sequences.sort(function(a, b) {
                    return a - b
                }), i = 0, max = sequences.length; i < max && sequences[i] <= seqNumber; i++) {
                var sequence = sequences[i];
                _isNull(errCode) || _isUndefined(errCode) ? this._pendingComplete(sequence) : this._pendingError(sequence, errCode, reason)
            }
    }, eFTLConnection.prototype._sendPending = function() {
        var sequences = _keys(this.sendList);
        if (sequences.length > 0) {
            var self = this;
            sequences.sort(function(a, b) {
                return a - b
            }), _each(sequences, function(obj) {
                var seq = obj,
                    pub = self.sendList[seq];
                self._send(pub.envelope, pub.sequence, !1)
            })
        }
    }, eFTLConnection.prototype._pendingComplete = function(sequence) {
        var pub = this.sendList[sequence];
        null != pub && (delete this.sendList[sequence], null != pub.options && this._caller(pub.options.onComplete, pub.options, [pub.message]))
    }, eFTLConnection.prototype._pendingError = function(sequence, errCode, reason) {
        var pub = this.sendList[sequence];
        null != pub && (delete this.sendList[sequence], null != pub.options ? this._caller(pub.options.onError, pub.options, [pub.message, errCode, reason]) : this._caller(this.connectOptions.onError, this.connectOptions, [this, errCode, reason]))
    }, eFTLConnection.prototype._clearPending = function(errCode, reason) {
        var sequences = _keys(this.sendList);
        if (sequences.length > 0) {
            var self = this;
            sequences.sort(function(a, b) {
                return a - b
            }), _each(sequences, function(obj) {
                var seq = obj,
                    pub = self.sendList[seq];
                null != pub.options && self._caller(pub.options.onError, pub.options, [pub.message, errCode, reason]), delete self.sendList[seq]
            })
        }
    }, eFTLConnection.prototype._nextSequence = function() {
        return ++this.sequenceCounter
    }, eFTLConnection.prototype._nextSubscriptionSequence = function() {
        return ++this.subscriptionCounter
    }, eFTLConnection.prototype.open = function(url, options) {
        this.connectOptions = options, this.accessPointURL = url, this.state = 0, url.indexOf("?") > 0 && (url = url.substring(0, url.indexOf("?")));
        try {
            options = {};
            void 0 !== trustCertificates && (options.ca = trustCertificates), _isBoolean(this.connectOptions.trustAll) && this.connectOptions.trustAll && (options.rejectUnauthorized = !1), this.webSocket = new WebSocket(url, ["v1.eftl.tibco.com"], options), this._initWS(this.webSocket), openConnections.push(this);
            var timeout = 15e3;
            _isNumber(this.connectOptions.timeout) && (timeout = 1e3 * this.connectOptions.timeout);
            var webSocket = this.webSocket;
            this.connectTimer = setTimeout(function() {
                webSocket.readyState === WebSocket.CONNECTING && webSocket.close()
            }, timeout)
        } catch (err) {
            throw console.error(err, err.stack), err
        }
    }, eFTLConnection.prototype.subscribe = function(options) {
        var subId = this.clientId + ".s." + this._nextSubscriptionSequence();
        return this._subscribe(subId, options), subId
    }, eFTLConnection.prototype.unsubscribe = function(subscriptionId) {
        var unsubMessage = {};
        unsubMessage.op = 5, unsubMessage.id = subscriptionId, this._send(unsubMessage, 0, !1), delete this.subscriptions[subscriptionId]
    }, eFTLConnection.prototype.unsubscribeAll = function() {
        var self = this;
        _keys(this.subscriptions).length > 0 && _each(this.subscriptions, function(value, key) {
            self.unsubscribe(key)
        })
    }, eFTLConnection.prototype.getClientId = function() {
        return this.clientId
    }, eFTLConnection.prototype.publish = function(message, options) {
        ! function(message, variableName) {
            var varName = variableName || "message";
            if (_isUndefined(message)) throw new Error(varName + " cannot be undefined");
            if (_isNull(message)) throw new Error(varName + " cannot be null");
            if (message instanceof eFTLMessage == 0) throw new Error(varName + " must be an instance of eFTLMessage")
        }(message, "Message"), this._sendMessage(message, ".m.", options)
    }, eFTLConnection.prototype._scheduleReconnect = function() {
        if (this.reconnectCounter < this.autoReconnectAttempts) {
            var ms = Math.min(1e3 * Math.pow(2, this.reconnectCounter++), this.autoReconnectMaxDelay),
                connection = this;
            return this.reconnectTimer = setTimeout(function() {
                connection.reconnectTimer = null, connection.isReconnecting = !0, connection.open(connection.accessPointURL, connection.connectOptions)
            }, ms), !0
        }
        return !1
    }, eFTLConnection.prototype.reconnect = function() {
        this.state === CLOSED && (this.isReconnecting = !1, this.open(this.accessPointURL, this.connectOptions))
    }, eFTLConnection.prototype.disconnect = function() {
        this._disconnect()
    }, eFTLConnection.prototype._debug = function(arg) {
        if (this.connectOptions && this.connectOptions.debug) {
            var args = Array.prototype.slice.call(arguments);
            console.log(args)
        }
    }, String.prototype.startsWith || (String.prototype.startsWith = function(searchString, position) {
        return position = position || 0, this.substr(position, searchString.length) === searchString
    }), String.prototype.trim || (String.prototype.trim = function() {
        return this.replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g, "")
    }), "undefined" != typeof exports ? "undefined" != typeof module && module.exports ? exports = module.exports = new eFTL : exports = new eFTL : (this.eFTL = new eFTL, this.eFTLMessage = eFTLMessage)
}();
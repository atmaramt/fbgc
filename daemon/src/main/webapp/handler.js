dojo.require("dojox.cometd");

var onlineUsers = [];

dojo.addOnLoad(function()
{
    var cometd = dojox.cometd;

    // Idempotent function called every time the connection
    // with the Bayeux server is (re-)established
    var _subscription;
    function _connectionSucceeded()
    {
        dojo.byId('body').innerHTML = '<div>Cometd Connection Succeeded</div>';

        cometd.batch(function()
        {
            if (_subscription)
            {
                cometd.unsubscribe(_subscription);
            }
            _subscription = cometd.subscribe('/service/outgoing', function(message)
            {
            	if(message.data.action == 'received')
            		dojo.byId('messages').innerHTML += '<div><b>' + message.data.id + ': </b>' + message.data.message + '</div>';
            	
            	if(message.data.action == 'presence'){
            		dojo.byId('friends').innerHTML += '<div><b>' + message.data.id + '</b> is ' + message.data.presence + '</div>';
            		
            		if(message.data.presence == 'online' || message.data.presence == 'away'){
            			alert("trueee");
            			onlineUsers[message.data.id] = "true";
            		}else{
            			onlineUsers[message.data.id] = "false";
            		}
            		_refreshUsersBox();
            	}
            });
            
            // Publish on a service channel since the message is for the server only
            // cometd.publish('/service/incoming', { name: 'World' });
        });
    }
    
    function _connectionBroken()
    {
        dojo.byId('body').innerHTML = 'Cometd Connection Broken';
    }

    // Function that manages the connection status with the Bayeux server
    var _connected = false;
    function _metaConnect(message)
    {
        var wasConnected = _connected;
        _connected = message.successful === true;
        if (!wasConnected && _connected)
        {
            _connectionSucceeded();
        }
        else if (wasConnected && !_connected)
        {
            _connectionBroken();
        }
    }

    // Disconnect when the page unloads
    dojo.addOnUnload(function()
    {
        cometd.disconnect();
    });

    var cometURL = location.protocol + "//" + location.host + config.contextPath + "/cometd";
    cometd.configure({
        url: cometURL,
        logLevel: 'debug'
    });

    cometd.addListener('/meta/connect', _metaConnect);

    cometd.handshake();
});

function _sendMessage()
{
	var cometd = dojox.cometd;
	cometd.batch(function()
        {
            cometd.publish('/service/incoming', { action: 'send', message: dojo.byId('phrase').value, id: '1' });
        });
}
function _login(session_key, session_secret)
{
	var cometd = dojox.cometd;
	cometd.batch(function()
        {
            cometd.publish('/service/incoming', { action: 'login', key : session_key, secret : session_secret});
        });
}
function _logout()
{
	var cometd = dojox.cometd;
	cometd.batch(function()
        {
            cometd.publish('/service/incoming', { action: 'logout'});
        });
}

function _refreshUsersBox(){
	var len=onlineUsers.length;
	
	alert('length is' + onlineUsers.length);
	
	dojo.byId('users').innerHTML = '';
	
	for(var key in onlineUsers) {
		var value = arr[key];
		
		dojo.byId('users').innerHTML += key + ' ';
	}
}
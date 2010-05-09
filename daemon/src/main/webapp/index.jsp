<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
	    <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
	    <script type="text/javascript" src="${pageContext.request.contextPath}/dojo/dojo.js.uncompressed.js"></script>
	    <script type="text/javascript" src="application.js"></script>
	    <script type="text/javascript">
	    	dojo.require("dojox.cometd");
	    
	        var config = {
	            contextPath: '${pageContext.request.contextPath}'
	        };
	        function _sendMessage()
	        {
	        	var cometd = dojox.cometd;
	        	cometd.batch(function()
	    	        {
	    	            cometd.publish('/service/incoming', { action: 'send', message: dojo.byId('phrase').value, id: '1' });
	    	        });
	        }
	        function _login()
	        {
	        	var cometd = dojox.cometd;
	        	cometd.batch(function()
	    	        {
	    	            cometd.publish('/service/incoming', { action: 'login'});
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
	    </script>
	</head>
	<body>
	
	    <div id="body"></div>
		
		<h1>facebookGroupChat.com</h1>
		
		<div>
			<a href="#" onClick="_login();">Login!</a>
			<a href="#" onClick="_logout();">Logout!</a>
		</div>
		
		<div id="messages"></div>
	
		<div id="friends"></div>
		
		<div>
			<input id="phrase" type="text"></input>
	        <a href="#" onClick="_sendMessage();return true">Send!</a>
	    </div>
	</body>
</html>

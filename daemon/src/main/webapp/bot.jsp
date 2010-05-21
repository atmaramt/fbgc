<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://www.facebook.com/2008/fbml">
	<head>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
	    <script type="text/javascript" src="${pageContext.request.contextPath}/dojo/dojo.js.uncompressed.js"></script>
	    <script type="text/javascript" src="handler.js"></script>
	    <script type="text/javascript" src="connect.js"></script>
	    <script type="text/javascript">
	    	var config = {
	            contextPath: '${pageContext.request.contextPath}'
	        };
	    </script>
	</head>
	<body>
		<div id="user">
			<fb:login-button onlogin="update_user_box();"></fb:login-button> 
		</div> 
	
		<div id="userid"></div>
		
		<div id="body"></div>
		
		<h3>Console</h3>
		
		<div id="messages"></div>
	
		<div id="users"></div>
	
		<div>
			<input id="phrase" type="text"></input>
	        <a href="#" onClick="_sendMessage();return true">Send!</a>
	    </div>

		<script type="text/javascript" src="http://static.ak.connect.facebook.com/js/api_lib/v0.4/FeatureLoader.js.php"></script> 
		<script type="text/javascript"> 
			FB.init("11b0bfb6cbf4a5dfe69227d7b2972f2a","xd_receiver.htm", {"ifUserConnected":_onConnected, "ifUserNotConnected":_onNotConnected}); 
		</script> 
	</body> 
</html>
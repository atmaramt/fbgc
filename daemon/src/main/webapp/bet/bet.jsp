<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://www.facebook.com/2008/fbml">
	<head>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
	    <script type="text/javascript" src="connect.js"></script>
	    <script type="text/javascript">
	    	var config = {
	            contextPath: '${pageContext.request.contextPath}'
	        };
	    </script>
	</head>
	<body>
		<div id="user">
			<span>
				<fb:profile-pic uid=loggedinuser facebook-logo=true size='normal'></fb:profile-pic>
				<h3>
					<fb:name uid=loggedinuser useyou=false></fb:name>
				</h3>
			</span>
		</div>
		<div id="userid"></div>

		<script type="text/javascript" src="http://static.ak.connect.facebook.com/js/api_lib/v0.4/FeatureLoader.js.php"></script> 
		<script type="text/javascript"> 
			FB.init("73ad346a92f9560feec31a72ef7fcd98","xd_receiver.htm", {"ifUserConnected":_onConnected, "ifUserNotConnected":_onNotConnected}); 
		</script> 
	</body> 
</html>
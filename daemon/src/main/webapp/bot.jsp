<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://www.facebook.com/2008/fbml">
	<head>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
	    <script type="text/javascript" src="${pageContext.request.contextPath}/dojo/dojo.js.uncompressed.js"></script>
	    <script type="text/javascript" src="handler.js"></script>
	    <script type="text/javascript">
	    	dojo.require("dojox.cometd");
	    
	        var config = {
	            contextPath: '${pageContext.request.contextPath}'
	        };
	    </script>
	    <script type="text/javascript"> 
			function update_user_box() { 
				var user_box = document.getElementById("user"); 

				// add in some XFBML. note that we set useyou=false so it doesn't display "you" 
				user_box.innerHTML = "<span>" + 
									"<h2>Your bot is</h2>" +
									"<fb:profile-pic uid=loggedinuser facebook-logo=true></fb:profile-pic>" + 
									"<fb:name uid=loggedinuser useyou=false></fb:name>" + 
									"<a href='#' onclick='_loginEx();'>Login</a>" +
									"</span>"; 

				var userid_box = document.getElementById("userid");
				var api = FB.Facebook.apiClient;
				userid_box.innerHTML = "" + api.get_session().uid; 
				
				// because this is XFBML, we need to tell Facebook to re-process the document 
				FB.XFBML.Host.parseDomTree(); 
			}

			function _loginEx()
			{
			    facebook_prompt_permission('offline_access', function(accepted)
			    {
			        if(accepted) {
			        	_loginExP();
			        }
			        else
			        {
			            //  User does not have permission
			            alert(' not granted');
			        }
			    });

			    
			}
			
			function _loginExP()
			{
			    facebook_prompt_permission('xmpp_login', function(accepted)
			    {
			        if(accepted) {
			            // User (already) has permission
			            var api = FB.Facebook.apiClient;
			            _login(api.get_session().session_key);
			        }
			        else
			        {
			            //  User does not have permission
			            alert(' not granted');
			        }
			    });
			}

			function facebook_prompt_permission(permission, callbackFunc)
			{
			    // Check if user has permission, if not invoke dialog.
			    FB.ensureInit(function() {
			        FB.Connect.requireSession(function(){
			            //check is user already granted for this permission or not
			            FB.Facebook.apiClient.users_hasAppPermission(permission,
			            function(result) {
			                // prompt offline permission
			                if (result == 0) {
			                    // render the permission dialog
			                    FB.Connect.showPermissionDialog(permission,
			                    function(result){
			                        if (!result){
			                        	callbackFunc(true);
			                        }else{
			                        	callbackFunc(false);
			                        }
			                    });
			                } else {
			                    // permission already granted.
			                    callbackFunc(true);
			                }
			            });
			        });
			    });
			}
		</script>
	</head>
	<body>
		<h1>Activate bot:</h1>
		<div id="user">
			<fb:login-button onlogin="update_user_box();"></fb:login-button> 
		</div> 
	
		<div id="userid"></div>
		
		<div id="body"></div>
		
		<h2>Console</h2>
		
		<div id="messages"></div>
	
		<div id="friends"></div>
		
		<div>
			<input id="phrase" type="text"></input>
	        <a href="#" onClick="_sendMessage();return true">Send!</a>
	    </div>

		<script type="text/javascript" src="http://static.ak.connect.facebook.com/js/api_lib/v0.4/FeatureLoader.js.php"></script> 
		<script type="text/javascript"> FB.init("11b0bfb6cbf4a5dfe69227d7b2972f2a","xd_receiver.htm", {"ifUserConnected" : update_user_box}); </script> 
	</body> 
</html>
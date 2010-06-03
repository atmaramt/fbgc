function _onConnected(){
	_updateUserBox();
}

function _onNotConnected(){
	alert('not connected');
}
 
function _updateUserBox() {
	var user_box = document.getElementById("user");

	// add in some XFBML. note that we set useyou=false so it doesn't display
	// "you"
	user_box.innerHTML = 
			"<span>" + 
				"<fb:profile-pic uid=loggedinuser facebook-logo=true size='normal'></fb:profile-pic>" +
				"<h3>" +
					"<fb:name uid=loggedinuser useyou=false></fb:name>" +
				"</h3>" +
			
				"<ul>" +
					"<li><a href='#' onclick='alert(\"Not yet!\");'>Bet with cagri</a></li>" +
				"</ul>" +
			"</span>";

	var api = FB.Facebook.apiClient;

	// because this is XFBML, we need to tell Facebook to re-process the
	// document
	FB.XFBML.Host.parseDomTree();
}

function _disconnectFromFacebook(){
	FB.Connect.logoutAndRedirect("bet.jsp");
}

function _checkForPermissions() {
	_checkForOfflineAccessPermission();
}

function _checkForOfflineAccessPermission() {
	facebookPromptPermissionAndReturnResult('offline_access', function(accepted) {
		if (accepted) {
			_checkForXmppLoginPermission();
		} else {
			// User does not have permission
			alert(' not granted');
		}
	});

}

function _checkForXmppLoginPermission() {
	facebookPromptPermissionAndReturnResult('xmpp_login', function(accepted) {
		if (accepted) {
			// User (already) has permission
			var api = FB.Facebook.apiClient;
			_login(api.get_session().session_key);
		} else {
			// User does not have permission
			alert(' not granted');
		}
	});
}

function facebookPromptPermissionAndReturnResult(permission, callbackFunc) {
	facebookPromptPermission(permission, function(accepted) {
		FB.Facebook.apiClient.users_hasAppPermission(permission,
				function(result) {
					if (result != 0) {
						// permission granted.
						callbackFunc(true);
					}else{
						alert('aq not granted');
					}
			});
	});
}

function facebookPromptPermission(permission, callbackFunc) {
	// Check if user has permission, if not invoke dialog.
	FB.ensureInit(function() {
		FB.Connect.requireSession(function() {
			// check is user already granted for this permission or not
				FB.Facebook.apiClient.users_hasAppPermission(permission,
						function(result) {
							// prompt offline permission
						if (result == 0) {
							// render the permission dialog
							FB.Connect.showPermissionDialog(permission,
									function(result) {
										if (!result) {
											callbackFunc(true);
										} else {
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

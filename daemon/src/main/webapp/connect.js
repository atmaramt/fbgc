function update_user_box() {
	var user_box = document.getElementById("user");

	// add in some XFBML. note that we set useyou=false so it doesn't display
	// "you"
	user_box.innerHTML = "<span>"
			+ "<h2>Your bot is</h2>"
			+ "<fb:profile-pic uid=loggedinuser facebook-logo=true></fb:profile-pic>"
			+ "<fb:name uid=loggedinuser useyou=false></fb:name>"
			+ "<a href='#' onclick='_loginEx();'>Login</a>" + "</span>";

	var userid_box = document.getElementById("userid");
	var api = FB.Facebook.apiClient;
	userid_box.innerHTML = "" + api.get_session().uid;

	// because this is XFBML, we need to tell Facebook to re-process the
	// document
	FB.XFBML.Host.parseDomTree();
}

function _loginEx() {
	facebook_prompt_permission('offline_access', function(accepted) {
		if (accepted) {
			_loginExP();
		} else {
			// User does not have permission
			alert(' not granted');
		}
	});

}

function _loginExP() {
	facebook_prompt_permission('xmpp_login', function(accepted) {
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

function facebook_prompt_permission(permission, callbackFunc) {
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

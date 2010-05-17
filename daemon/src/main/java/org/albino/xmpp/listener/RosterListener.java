package org.albino.xmpp.listener;

import java.util.Collection;

import org.albino.util.PresenceUtil;
import org.albino.xmpp.FacebookPresence;
import org.albino.xmpp.RosterManager;
import org.albino.xmpp.SessionHandlerImpl;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

public class RosterListener implements org.jivesoftware.smack.RosterListener {
	RosterManager rosterManager;
	SessionHandlerImpl sessionHandler;

	public RosterListener(SessionHandlerImpl sessionHandler,
			RosterManager rosterManager) {
		this.rosterManager = rosterManager;
		this.sessionHandler = sessionHandler;
	}

	public void entriesAdded(Collection<String> arg0) {
		for(String id : arg0){
			sessionHandler.deliverPresenceToClient(id, PresenceUtil.getFacebookPresenceFromSmackPresence(rosterManager.getPresence(id)));
		}
	}

	public void entriesDeleted(Collection<String> arg0) {
		for(String id : arg0){
			sessionHandler.deliverPresenceToClient(id, FacebookPresence.OFFLINE);
			//TODO and destroy chat
		}
	}

	public void entriesUpdated(Collection<String> arg0) {
		// TODO Auto-generated method stub

	}

	public void presenceChanged(Presence presence) {
		String id = StringUtils.parseBareAddress(presence.getFrom());
		sessionHandler.deliverPresenceToClient(id, PresenceUtil.getFacebookPresenceFromSmackPresence(presence));
	}
}

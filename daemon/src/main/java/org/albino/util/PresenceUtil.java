package org.albino.util;

import org.albino.xmpp.FacebookPresence;
import org.jivesoftware.smack.packet.Presence;

public class PresenceUtil {
	public static String getPresenceStringFromFacebookPresence(FacebookPresence presence){
		if(FacebookPresence.ONLINE.equals(presence))
			return "online";
		if(FacebookPresence.AWAY.equals(presence))
			return "away";
		if(FacebookPresence.OFFLINE.equals(presence))
			return "offline";
		
		return null;
	}
	
	public static FacebookPresence getFacebookPresenceFromSmackPresence(Presence presence){
		if(Presence.Mode.available.equals(presence.getMode()))
			return FacebookPresence.ONLINE;
		if(Presence.Mode.away.equals(presence.getMode()))
			return FacebookPresence.AWAY;
		if(Presence.Type.unavailable.equals(presence.getType()))
			return FacebookPresence.OFFLINE;
		return null;
	}
}
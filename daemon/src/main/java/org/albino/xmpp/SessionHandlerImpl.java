package org.albino.xmpp;

import java.util.HashMap;
import java.util.Map;

import org.albino.DojoCommunicationHandler;
import org.albino.mechanisms.FacebookConnectSASLMechanism;
import org.albino.util.PresenceUtil;
import org.albino.xmpp.listener.ChatManagerListener;
import org.albino.xmpp.listener.ConnectionListener;
import org.albino.xmpp.listener.MessageListener;
import org.albino.xmpp.listener.PacketListener;
import org.albino.xmpp.listener.RosterListener;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;

public class SessionHandlerImpl implements SessionHandler {
	Logger logger = Logger.getLogger(SessionHandlerImpl.class);

	RosterManager rosterManager = new RosterManager();
	RosterListener rosterListener = new RosterListener(this, rosterManager);
	ConnectionListener connectionListener = new ConnectionListener();
	PacketListener packetListener = new PacketListener();
	MessageListener messageListener = new MessageListener(this);
	ChatManagerImpl chatManager = new ChatManagerImpl(messageListener);
	ChatManagerListener chatManagerListener = new ChatManagerListener(this,
			messageListener, chatManager);

	protected final String sessionIdentifier;
	protected final DojoCommunicationHandler handler;

	String sessionKey;
	String apiKey = "11b0bfb6cbf4a5dfe69227d7b2972f2a";
	String apiSecret = "f33825fb56bd37cb7b4861ea90085eef";

	String host = "69.63.181.104";
	int port = 5222;
	String domain = "chat.facebook.com";
	String resource = "Facebook Group Chat";
	SecurityMode securityMode = SecurityMode.enabled;
	boolean isSaslAuthenticationEnabled = true;
	boolean isCompressionEnabled = false;
	boolean isReconnectionAllowed = false;

	XMPPConnection xmppConnection;

	public SessionHandlerImpl(String identifier,
			DojoCommunicationHandler handler) {
		sessionIdentifier = identifier;
		this.handler = handler;
	}

	public void startSession(String sessionKey) {
		this.sessionKey = sessionKey;

		SASLAuthentication.registerSASLMechanism("X-FACEBOOK-PLATFORM",
				FacebookConnectSASLMechanism.class);
		SASLAuthentication.supportSASLMechanism("X-FACEBOOK-PLATFORM", 0);

		logger.debug("startSession: Starting session... key: " + sessionKey);

		ConnectionConfiguration config = null;

		config = new ConnectionConfiguration(host, port, domain);

		config.setSecurityMode(securityMode);
		config.setSASLAuthenticationEnabled(isSaslAuthenticationEnabled);
		config.setCompressionEnabled(isCompressionEnabled);
		config.setReconnectionAllowed(isReconnectionAllowed);

		xmppConnection = new XMPPConnection(config);

		try {
			xmppConnection.connect();
		} catch (XMPPException e) {
			logger.error("Exception occured while connecting: ", e);
		}

		try {
			xmppConnection
					.login(apiKey + "|" + sessionKey, apiSecret, resource);
		} catch (XMPPException e) {
			logger.error("Exception occured while logging in: ", e);
		}

		Roster roster = xmppConnection.getRoster();
		roster.setSubscriptionMode(SubscriptionMode.manual);
		roster.addRosterListener(rosterListener);

		rosterManager.setRoster(roster);
		logger.debug("Roster arrived: " + roster);

		ChatManager chatManager = xmppConnection.getChatManager();
		chatManager.addChatListener(chatManagerListener);
		this.chatManager.setChatManager(chatManager);

		xmppConnection.addConnectionListener(connectionListener);

		// xmppConnection.addPacketListener(this);
		// this.xmppCon.addPacketWriterListener(new WritePacketListener(),
		// anyFilter);

		xmppConnection.addPacketListener(packetListener,
				new SubscriptionsRequestPacketFilter());

		goOnline();
		
		for(RosterElement rosterElement : rosterManager.getRosterElements()){
			FacebookPresence facebookPresence = PresenceUtil.getFacebookPresenceFromSmackPresence(rosterManager.getPresence(rosterElement.getId()));
			if(facebookPresence!=null && !facebookPresence.equals(FacebookPresence.OFFLINE)){
				deliverPresenceToClient(rosterElement.getId(), facebookPresence);
			}
		}
	}

	public void stopSession() {
		if (xmppConnection == null)
			return;

		xmppConnection.removeConnectionListener(connectionListener);

		if (rosterManager.getRoster() != null) {
			rosterManager.getRoster().removeRosterListener(rosterListener);
			rosterManager = null;
		}

		if (chatManager != null) {
			chatManager.getChatManager()
					.removeChatListener(chatManagerListener);
			chatManager = null;
		}

		if (xmppConnection != null && xmppConnection.isConnected()) {
			xmppConnection.removePacketListener(packetListener);

			xmppConnection.disconnect();
			xmppConnection = null;
		}
	}

	public void deliverMessageToClient(String id, String message) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("action", "received");
		data.put("id", id);
		data.put("message", message);

		try {
			handler.processOutgoing(sessionIdentifier, data);
		} catch (Exception e) {
			logger.error("Exception delivering message", e);
		}

	}
	
	public void deliverPresenceToClient(String id, FacebookPresence presence) {
		RosterElement sender = rosterManager.getElement(id);
		String senderName = id;

		if (sender != null)
			senderName = sender.getDisplayName();
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("action", "presence");
		data.put("id", senderName);
		data.put("presence", PresenceUtil
				.getPresenceStringFromFacebookPresence(presence));

		try {
			handler.processOutgoing(sessionIdentifier, data);
		} catch (Exception e) {
			logger.error("Exception delivering presence", e);
		}

	}

	public void sendMessage(String messageContent) {
		sendMessage("Bot", messageContent);
	}

	public void sendMessage(String id, String messageContent) {
		RosterElement sender = rosterManager.getElement(id);
		String senderName = id;

		if (sender != null)
			senderName = sender.getDisplayName();

		for (RosterElement rosterElement : rosterManager.getRosterElements()) {
			try {
				sendMessageToXmpp(senderName + " says " + messageContent,
						rosterElement.getId());
			} catch (XMPPException e) {
				logger.error("Exception when sending message", e);
			}
		}

		deliverMessageToClient(senderName, messageContent);
	}

	private void sendMessageToXmpp(String messageContent, String userId)
			throws XMPPException {
		logger.debug("Sending message " + messageContent + " to " + userId);

		Chat chat = chatManager.getChat(userId);
		chat.sendMessage(messageContent);
	}

	protected void goOnline() {
		Presence packet = new Presence(Type.available);

		xmppConnection.sendPacket(packet);
	}

	public String getSessionIdentifier() {
		return sessionIdentifier;
	}

	private static class SubscriptionsRequestPacketFilter implements
			PacketFilter {
		public boolean accept(Packet packet) {
			boolean accept = false;

			if (packet instanceof Presence) {
				Presence presence = (Presence) packet;

				accept = (presence.getType() == Type.subscribe);
			}
			return accept;
		}
	}

	public ChatManagerImpl getChatManager() {
		return chatManager;
	}
}

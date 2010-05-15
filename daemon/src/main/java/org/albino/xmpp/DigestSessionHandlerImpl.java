package org.albino.xmpp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.albino.DojoCommunicationHandler;
import org.albino.mechanisms.FacebookSASLDigestMD5Mechanism;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;

public class DigestSessionHandlerImpl implements RosterListener, ChatManagerListener,
		ConnectionListener, PacketListener, MessageListener {
	Logger logger = Logger.getLogger(DigestSessionHandlerImpl.class);

	protected final String sessionIdentifier;
	protected final DojoCommunicationHandler handler;

	String username;
	String password;

	String host = "69.63.181.104";
	int port = 5222;
	String domain = "chat.facebook.com";
	String resource = "Facebook Group Chat";
	SecurityMode securityMode = SecurityMode.enabled;
	boolean isSaslAuthenticationEnabled = true;
	boolean isCompressionEnabled = false;
	boolean isReconnectionAllowed = false;

	XMPPConnection xmppConnection;
	Roster roster;
	ChatManager chatManager;

	Map<String, Chat> chats = new HashMap<String, Chat>();

	public DigestSessionHandlerImpl(String identifier,
			DojoCommunicationHandler handler) {
		sessionIdentifier = identifier;
		this.handler = handler;
	}

	protected Chat getChat(String userId) {
		if (!chats.containsKey(userId)) {
			addChat(userId, chatManager.createChat(userId, this));
		}

		return chats.get(userId);
	}

	protected void addChat(String userId, Chat chat) {
		if (!chats.containsKey(userId)) {
			chats.put(userId, chat);
		}
	}

	public void startSession(String username, String password) {
		this.username = username;
		this.password = password;

		SASLAuthentication.registerSASLMechanism("DIGEST-MD5",
				FacebookSASLDigestMD5Mechanism.class);
		SASLAuthentication.supportSASLMechanism("DIGEST-MD5", 0);

		logger.debug("startSession: Starting session...");

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
			xmppConnection.login(username, password, resource);
		} catch (XMPPException e) {
			logger.error("Exception occured while logging in: ", e);
		}

		roster = xmppConnection.getRoster();
		roster.setSubscriptionMode(SubscriptionMode.manual);
		roster.addRosterListener(this);

		logger.debug("Roster arrived: " + roster);

		chatManager = xmppConnection.getChatManager();
		chatManager.addChatListener(this);

		xmppConnection.addConnectionListener(this);

		// xmppConnection.addPacketListener(this);
		// this.xmppCon.addPacketWriterListener(new WritePacketListener(),
		// anyFilter);

		xmppConnection.addPacketListener(this,
				new SubscriptionsRequestPacketFilter());
	}

	public void stopSession() {
		if (xmppConnection == null)
			return;

		xmppConnection.removeConnectionListener(this);

		if (roster != null) {
			roster.removeRosterListener(this);
			roster = null;
		}

		if (chatManager != null) {
			chatManager.removeChatListener(this);
			chatManager = null;
		}

		if (xmppConnection != null && xmppConnection.isConnected()) {
			xmppConnection.removePacketListener(this);

			xmppConnection.disconnect();
			xmppConnection = null;
		}
	}

	public void handleIncomingMessage(String id, String message) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("action", "received");
		data.put("id", id);
		data.put("message", message);

		handler.processOutgoing(sessionIdentifier, data);

		for (RosterEntry rosterEntry : roster.getEntries()) {
			try {
				sendMessageToXmpp(id + " says " + message, rosterEntry.getUser());
			} catch (XMPPException e) {
				logger.error("Exception when sending message", e);
			}
		}
	}

	public void sendMessage(String messageContent) {
		handleIncomingMessage("Bot:", "Sending this message: " + messageContent);
	}

	private void sendMessageToXmpp(String messageContent, String userId)
			throws XMPPException {
		logger.debug("Sending message " + messageContent + " to " + userId);

		Chat chat = getChat(userId);
		chat.sendMessage(messageContent);
	}

	public String getSessionIdentifier() {
		return sessionIdentifier;
	}

	// Methods to implement for RosterListener interface
	public void entriesAdded(Collection<String> arg0) {
		// TODO Auto-generated method stub

	}

	public void entriesDeleted(Collection<String> arg0) {
		// TODO Auto-generated method stub

	}

	public void entriesUpdated(Collection<String> arg0) {
		// TODO Auto-generated method stub

	}

	public void presenceChanged(Presence arg0) {
		// TODO Auto-generated method stub

	}//

	// Methods to implement for ChatManagerListener interface
	public void chatCreated(Chat chat, boolean arg1) {
		addChat(chat.getParticipant(), chat);

		chat.addMessageListener(this);

	}//

	// Methods to implement for ConnectionListener interface
	public void connectionClosed() {
		// TODO Auto-generated method stub

	}

	public void connectionClosedOnError(Exception arg0) {
		// TODO Auto-generated method stub

	}

	public void reconnectingIn(int arg0) {
		// TODO Auto-generated method stub

	}

	public void reconnectionFailed(Exception arg0) {
		// TODO Auto-generated method stub

	}

	public void reconnectionSuccessful() {
		// TODO Auto-generated method stub

	}//

	// Methods to implement for PacketListener interface
	public void processPacket(Packet arg0) {
		// TODO Auto-generated method stub

	}//

	// Methods to implement for MessageListener interface
	public void processMessage(Chat chat, Message message) {
		handleIncomingMessage(chat.getParticipant(), message.getBody());
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
}

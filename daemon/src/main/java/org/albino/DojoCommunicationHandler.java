package org.albino;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.albino.session.SessionHandlerImpl;
import org.apache.log4j.Logger;
import org.cometd.Bayeux;
import org.cometd.Client;
import org.cometd.Message;
import org.cometd.server.BayeuxService;

public class DojoCommunicationHandler extends BayeuxService {
	Logger logger = Logger.getLogger(DojoCommunicationHandler.class);
	
	public enum Action {
		LOGIN, LOGOUT, SEND, RECEIVED, LOGGEDIN, LOGGEDOUT
	};

	Set<String> knownIds = new HashSet<String>();
	Bayeux bayeux;

	Map<String, SessionHandlerImpl> sessions = new HashMap<String, SessionHandlerImpl>();

	public DojoCommunicationHandler(final Bayeux bayeux) {
		super(bayeux, "dojo");
		this.bayeux = bayeux;
		subscribe("/service/incoming", "processIncoming");
	}

	@SuppressWarnings("unchecked")
	public void processIncoming(Client remote, Message message) {
		String id = remote.getId();
		Action action = getAction(message);

		logger.debug("Incoming action = " + action);

		if (action.equals(Action.LOGIN)) {
			// Assign session handler
			if (!sessions.containsKey(id))
				sessions.put(id, new SessionHandlerImpl(id, this));
		}

		SessionHandlerImpl sessionHandler = getSession(id);

		if (action.equals(Action.LOGIN)) {
			Map<String, Object> data = (Map<String, Object>) message.getData();
			String sessionKey = (String) data.get("key");
			sessionHandler.startSession(sessionKey);
		} else if (action.equals(Action.LOGOUT)) {
			sessionHandler.stopSession();
		} else if (action.equals(Action.SEND)) {
			Map<String, Object> data = (Map<String, Object>) message.getData();
			String messageContent = (String) data.get("message");
			sessionHandler.sendMessage(messageContent);
		}

		if (action.equals(Action.LOGOUT)) {
			// Remove session handler
			if (sessions.containsKey(id))
				sessions.remove(id);
		}
	}

	public void processOutgoing(String id, Map<String, Object> data) {
		Client remoteClient = bayeux.getClient(id);

		System.out.println("Processing outgoing data = " + data);

		remoteClient.deliver(getClient(), "/service/outgoing", data, null);
	}

	private SessionHandlerImpl getSession(String id) {
		SessionHandlerImpl sessionHandler = sessions.get(id);

		if (sessionHandler == null)
			throw new IllegalStateException("Session not found with id = " + id);

		return sessionHandler;
	}

	@SuppressWarnings("unchecked")
	private Action getAction(Message message) {
		Map<String, Object> data = (Map<String, Object>) message.getData();
		String action = (String) data.get("action");

		if ("login".equals(action))
			return Action.LOGIN;

		if ("logout".equals(action))
			return Action.LOGOUT;

		if ("send".equals(action))
			return Action.SEND;

		throw new IllegalStateException("Action type not recognized = "
				+ action);
	}
}

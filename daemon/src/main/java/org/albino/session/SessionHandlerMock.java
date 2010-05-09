package org.albino.session;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.albino.DojoCommunicationHandler;

public class SessionHandlerMock {
	private final String sessionIdentifier;
	private final DojoCommunicationHandler handler;

	public SessionHandlerMock(String identifier, DojoCommunicationHandler handler) {
		sessionIdentifier = identifier;
		this.handler = handler;
	}

	public void startSession() {
		System.out.println("Starting execution");
		ExecutorService executorService = Executors.newFixedThreadPool(1);
		executorService.execute(new FacebookServiceMock(this));
		System.out.println("Done");
	}

	public void stopSession() {

	}

	public void handleIncomingMessage(String id, String message) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("action", "received");
		data.put("id", id);
		data.put("message", message);

		handler.processOutgoing(sessionIdentifier, data);
	}

	public void sendMessage(String messageContent) {
		handleIncomingMessage("fuck face:", "I saw this: " + messageContent);
	}

	public String getSessionIdentifier() {
		return sessionIdentifier;
	}

	private class FacebookServiceMock implements Runnable {

		SessionHandlerMock handler;

		public FacebookServiceMock(SessionHandlerMock handler) {
			this.handler = handler;
		}

		public void run() {
			try {
				for (int i = 0; i < 20; i++) {
					Thread.sleep(5000);
					handler.handleIncomingMessage("1", "Hellaaaaa");
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}

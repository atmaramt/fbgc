package org.albino.xmpp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;

public class RosterManager {
	private Map<String, RosterElement> knownEntries = new HashMap<String, RosterElement>();
	private Roster roster;

	public void setRoster(Roster roster) {
		this.roster = roster;
		for (RosterEntry rosterEntry : roster.getEntries()) {
			System.out.println("Added element: " + rosterEntry.getUser() + ", "
					+ rosterEntry.getName() + ", " + rosterEntry.getStatus());
			addElement(rosterEntry.getUser(), rosterEntry.getName());
		}
	}

	public Roster getRoster() {
		return roster;
	}

	public List<RosterElement> getRosterElements() {
		return new ArrayList<RosterElement>(knownEntries.values());
	}

	public void addElement(String id, String displayName) {
		if (!knownEntries.containsKey(id)) {
			RosterElement rosterElement = new RosterElement();
			rosterElement.setId(id);
			rosterElement.setDisplayName(displayName);
			knownEntries.put(id, rosterElement);
		}
	}

	public RosterElement getElement(String id) {
		return knownEntries.get(id);
	}

	public void removeElement(String id) {
		knownEntries.remove(id);
	}
}

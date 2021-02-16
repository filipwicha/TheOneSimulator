package routing.customRouter;

import java.util.ArrayList;

import core.DTNHost;
import core.SimClock;

public class RoutingTable {

	private ArrayList<RoutingTableEntry> routingTable;
	public int DEFAULT_TIMEOUT = 1000000;

	public RoutingTable() {
		this.routingTable = new ArrayList<>();
		
		//addRoute(host, host, 0);
	}

	public void addRoute(DTNHost destinationID, DTNHost nextHop, int hops) {
		if (getRoute(destinationID) == null) {
			routingTable.add(new RoutingTableEntry(destinationID, nextHop, hops));
		}
		
	}

	public RoutingTableEntry getRoute(DTNHost destinationID) {
		
		
		for (RoutingTableEntry entry : routingTable) {
			if (entry.destinationID == destinationID) {
				return entry;
			}
		}
		return null;
	}

	public void removeExpiredRoutes() {
		for (RoutingTableEntry entry : routingTable) {
			if (SimClock.getIntTime() - entry.sequenceNumber >= DEFAULT_TIMEOUT) {
				routingTable.remove(entry);
			}
		}
	}
}

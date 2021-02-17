package routing.customRouter;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import core.DTNHost;
import core.SimClock;

public class RoutingTable {

	int DEFAULT_TIMEOUT = 1000000;
	private Stream<RoutingTableEntry> routingTable;

	public RoutingTable() {
		this.routingTable = Stream.empty();
		
		//addRoute(host, host, 0);
	}

	public static<T> Stream<T> addToBeg(Stream<T> stream, T element) {
        return Stream.concat(Stream.of(element), stream);
    }

	public void addRoute(DTNHost destinationID, DTNHost nextHop, int hops) {
		if (getRoute(destinationID) == null) {
			this.routingTable = addToBeg(this.routingTable, new RoutingTableEntry(destinationID, nextHop, hops)); //higher probability of choosing the first entry than the last one (for optimalization purpose and starvation)
		}
		
	}

	public RoutingTableEntry getRoute(DTNHost destinationID) {

		Optional<RoutingTableEntry> result = this.routingTable.parallel().filter(entry -> entry.destinationID == destinationID ).findAny();

		if(result.isPresent()){
			return result.get();
		}
		
		return null;
	}

	public void removeExpiredRoutes() {
		int time = SimClock.getIntTime();

		this.routingTable = this.routingTable.parallel().filter(entry -> SimClock.getIntTime() - entry.sequenceNumber >= DEFAULT_TIMEOUT );

		// for (RoutingTableEntry entry : routingTable) {
		// 	if (SimClock.getIntTime() - entry.sequenceNumber >= DEFAULT_TIMEOUT) {
		// 		routingTable.remove(entry);
		// 	}
		// }
	}
}

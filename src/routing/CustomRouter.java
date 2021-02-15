package routing;

import java.util.ArrayList;
import java.util.List;

import core.Message;
import core.Settings;
import core.Connection;
import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.NetworkInterface;
import core.Settings;
import core.SimClock;

import routing.customRouter.*;

public class CustomRouter extends ActiveRouter {

	public RoutingTable routingTable = new RoutingTable();
	public ArrayList<RREQ> toBroadcast = new ArrayList<RREQ>();

	public CustomRouter(Settings s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	public CustomRouter(ActiveRouter r) {
		super(r);
		// TODO Auto-generated constructor stub
	}

	@Override
	public MessageRouter replicate() {
		// TODO Auto-generated method stub
		return new CustomRouter(this);
	}

	public boolean isInToBroadcast(RREQ rreq) {
		for (RREQ value : toBroadcast) {
			if (value.destinationID == rreq.destinationID) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void update() {
		super.update();
		if (isTransferring() || !canStartTransfer()) {
			return; // transferring, don't try other connections yet
		}

		// Try first the messages that can be delivered to final recipient
		if (exchangeDeliverableMessages() != null) {
			System.out.println();
			return; // started a transfer, don't try others (yet)
		}
		///////////////////////////////////////////////////////////

		List<Message> messages = new ArrayList<Message>(this.getMessageCollection());

		for (Message message : messages) {
			RoutingTableEntry route = routingTable.getRoute(message.getTo());
			if (route == null) {
				// If I do not know the route for this message
				RREQ newRreq = new RREQ(message.getFrom(), message.getTo());
				if (isInToBroadcast(newRreq) == false) {
					// Put RREQ on the broadcast list
					toBroadcast.add(newRreq);
				}

			} else {
				System.out.println("I know a route for this message!!!!111");
				// TODO: send the message somehow
			}
		}

		// Broadcast all RREQ messages:
		List<Connection> connections = getConnections();
		for (RREQ rreq : toBroadcast) {
			for (Connection connection : connections) {
				DTNHost peer = connection.getOtherNode(getHost());
				peer.getRouter().passRREQ(connection, rreq);
			}
		}

	}

	@Override
	public void passRREQ(Connection con, RREQ rreq) {
		routingTable.addRoute(rreq.sourceID, con.getOtherNode(getHost()), rreq.hopCount);

		RoutingTableEntry route = routingTable.getRoute(rreq.destinationID);
		if (route != null) {
			con.getOtherNode(getHost()).getRouter().passRREPL(con, new RREPL(rreq.sourceID, rreq.destinationID));
		} else {
			if (isInToBroadcast(rreq) == false) {
				rreq.hopCount++;
				toBroadcast.add(rreq);
			}
		}
		return;
	}

	@Override
	public void passRREPL(Connection con, RREPL rrepl) {
		DTNHost peer = con.getOtherNode(getHost());
		routingTable.addRoute(rrepl.destinationID, peer, rrepl.hopCount);
		return;
	}

	@Override
	public void changedConnection(Connection con) {
		super.changedConnection(con);
	}

}

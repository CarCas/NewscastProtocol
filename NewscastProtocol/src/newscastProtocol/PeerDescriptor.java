package newscastProtocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PeerDescriptor implements Comparable<PeerDescriptor> {
	List<NeighbourDescriptor> peerView;
	Integer peerID;
	Integer cachelines;
	
	public PeerDescriptor(int id, int cachelines) {
		this.peerID = id;
		this.peerView = new ArrayList<NeighbourDescriptor>();
		this.cachelines = cachelines;
	}

	/** This function eliminates the possible duplicates by removing the oldest ones among them.
	 * Afterwards, (if found) it removes the peer with peerID equal to the peerID of the current peer.
	 * Finally, it sorts the peer's view by using the timestamp of the saved peers and removes from
	 * the list all the elements except for the cachelines first.
	 * */
	public void UpdateState() {
		/* removing duplicates scanning through each set of equal peerIDs. */
		Collections.sort(this.peerView, (p1, p2) -> p1.compareTo(p2.peerID));
		for(int i = 0; i < this.peerView.size(); i++) {
			int pID = this.peerView.get(i).peerID;
			while(i+1 < this.peerView.size() && (pID == this.peerView.get(i+1).peerID)) {
				if(this.peerView.get(i).timestamp > this.peerView.get(i+1).timestamp)
					this.peerView.remove(i+1);
				else
					this.peerView.remove(i);
			}
			if(this.peerView.get(i).peerID == this.peerID)
				this.peerView.remove(i);
		}
		/* removing all the older entries, on the base of the number of the available cachelines */
		Collections.sort(this.peerView, (p1, p2) -> - p1.compareTo(p2.timestamp));
		int i = this.peerView.size() - 1;
		while (i >= cachelines){
			this.peerView.remove(i);
			i--;
		}
	}
	
	@Override
	public boolean equals(Object curr) {
		if ((curr != null) && (this.peerID == ((PeerDescriptor)curr).peerID))
			return true;
		return false;
	}
	
	/** This function provides the exchanging of informations between a randomly selected neighbor
	 * and the current peer. The function, afterwards, makes the current peer to exchange its own
	 * descriptor paired with the timestamp obtained as current time in milliseconds, with the system
	 * method, to the neighbor, and the same is done by the neighbor itself. Finally, both the current
	 * peer and the neighbor call the updateState function.
	 * */
	public void selectPeer() {
		if(!this.peerView.isEmpty()) {
			PeerDescriptor neigh = NewscastProtocol.net.get( peerView.get( (new Random()).nextInt(peerView.size()) ).peerID );
			ArrayList<NeighbourDescriptor> tmp = new ArrayList<NeighbourDescriptor>();
			for(NeighbourDescriptor nd : neigh.peerView) tmp.add(new NeighbourDescriptor(nd.timestamp, nd.peerID));
			for(NeighbourDescriptor nd : peerView) 		 neigh.peerView.add(new NeighbourDescriptor(nd.timestamp, nd.peerID));
			for(NeighbourDescriptor nd : tmp) 			 peerView.add(new NeighbourDescriptor(nd.timestamp, nd.peerID));
			neigh.peerView.add(new NeighbourDescriptor(System.currentTimeMillis(), peerID));
			peerView.add(new NeighbourDescriptor(System.currentTimeMillis(), neigh.peerID));
			this.UpdateState();
			neigh.UpdateState();
		}
	}
	
	@Override
	public int compareTo(PeerDescriptor p) {
		return this.peerID.compareTo(p.peerID);
	}
}

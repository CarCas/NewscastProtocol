package newscastProtocol;

/* This class is used to save the characteristics of the neighbour
 * of the current peer, that is, its timestamp and its peerID.
 */
public class NeighbourDescriptor {
	Long timestamp;
	Integer peerID;
	
	public NeighbourDescriptor(Long timestamp, Integer peer) {
		this.timestamp = timestamp;
		this.peerID = peer;
	}

	public int compareTo(Long timestamp) {
		return this.timestamp.compareTo(timestamp);
	}
	
	public int compareTo(Integer peerID) {
		return this.peerID.compareTo(peerID);
	}
	
	@Override
    public boolean equals(Object o) {
        NeighbourDescriptor curr = (NeighbourDescriptor) o;
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (this.peerID.equals(curr.peerID))
        	if(this.timestamp > curr.timestamp)
        		return true;
    	return false;
    }
}

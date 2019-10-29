package newscastProtocol;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class NewscastProtocol {

	static HashMap<Integer, PeerDescriptor> net = new HashMap<Integer, PeerDescriptor>();

	/**
	 * The function initialize the scale free network and all the peers that are
	 * added have all the same initial time. The number of peers that are connected
	 * randomly among themselves, using the Erdos-Renyi Model, are the initial number
	 * of cachelines neighbours, afterwards, the approach follows the Barabasi-Albert
	 * algorithm, this means that all the edges that belongs to the simulation are
	 * sorted and than for each new vertex that has to be added, the algorithm chooses
	 * randomly two vertex that already belong to the network and it connects them to
	 * the new vertex to add.
	 */
	private static void initScaleFreeNetwork(int noPeers, Float prob, int cachelines) {
		net = new HashMap<Integer, PeerDescriptor>();
		/* filling the network */
		for (int i = 0; i < noPeers; i++)
			net.put(i, new PeerDescriptor(i, cachelines));
		long currentTime = System.currentTimeMillis();
		Random p = new Random();
		ArrayList<PeerDescriptor> edges = new ArrayList<PeerDescriptor>();
		/* creation of the initial random network. */ 
		for (int i = 0; i < cachelines; i++) {
			System.out.print("Peer: " + net.get(i).peerID + " - Adding: ");
			for(int j = 0; j < cachelines; j++) {
				/* All the peers are connected with AT MOST cachelines value to them neighbours */
				if ((net.get(i).peerID != net.get(j).peerID)) {
					if (p.nextFloat() <= prob) {
						System.out.print(net.get(j).peerID + " ");
						net.get(i).peerView.add(new NeighbourDescriptor(currentTime, net.get(j).peerID));
						/* registration of the two vertices of each edge in the array */
						edges.add(net.get(i));
						edges.add(net.get(j));
					} else
						System.out.print("[[" + net.get(j).peerID + "]] ");
				}
			}
			System.out.println();
		}
		/* ordering the array */
		Collections.sort(edges, (e1, e2) -> e1.compareTo(e2));
		Random r = new Random();
		/* registering for each edge the vertices involved in an array */
		for(int i = cachelines; i < net.size(); i++) {
			int edgei = r.nextInt(edges.size());
			int edgej = r.nextInt(edges.size());
			
			net.get(i).peerView.add(new NeighbourDescriptor(currentTime, edges.get(edgei).peerID));
			net.get(i).peerView.add(new NeighbourDescriptor(currentTime, edges.get(edgej).peerID));
			edges.add(edges.get(edgei));
			edges.add(net.get(i));
			edges.add(edges.get(edgej));
			edges.add(net.get(i));
			Collections.sort(edges, (e1, e2) -> e1.compareTo(e2));

		}
	}

	/**
	 * The function initialize the graph in a grid shape fashion, so all the elements
	 * are treated in a separate way, by considering the elements in position
	 * (0, 0), (n-1, 0), (0, m-1), (n-1, m-1) connecting them to their own neighbours
	 * depending on the element; afterwards all the elements in the first and last
	 * row and column without considering the first and last element; finally,
	 * all the elements inside, from position (1, 1) up to (n-1, m-1). 
	 *  */
	private static void initGridNetwork(HashMap<Integer, PeerDescriptor> grid, Integer height, Integer width,
			Integer cacheLines) {
		/* Given a grid n*m */
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				PeerDescriptor pd = new PeerDescriptor(width * j + i, cacheLines);
				grid.put(width * j + i, pd);
			}
		}
		long currentTime = System.currentTimeMillis();

		/* I update the element (0, 0) */
		grid.get(0).peerView.add(new NeighbourDescriptor(currentTime, grid.get(1).peerID));
		grid.get(0).peerView.add(new NeighbourDescriptor(currentTime, grid.get(width).peerID));
		/*
		 * I update the first row without the first and last element (the interval
		 * [(0, 1), (0, m-2)])
		 */
		for (int i = 1; i < width - 1; i++) {
			int idx = width * 0 + i;
			grid.get(idx).peerView.add(new NeighbourDescriptor(currentTime, grid.get(idx + 1).peerID));
			grid.get(idx).peerView.add(new NeighbourDescriptor(currentTime, grid.get(idx - 1).peerID));
			grid.get(idx).peerView.add(new NeighbourDescriptor(currentTime, grid.get(idx + width).peerID));
		}
		/* I update the element (0, m-1) */
		grid.get(width - 1).peerView.add(new NeighbourDescriptor(currentTime, grid.get(width - 2).peerID));
		grid.get(width - 1).peerView.add(new NeighbourDescriptor(currentTime, grid.get((width - 1) * 2 + 1).peerID));

		/* I update the element (n-1, 0) */
		grid.get(width * (height - 1)).peerView
				.add(new NeighbourDescriptor(currentTime, grid.get(width * (height - 2)).peerID));
		grid.get(width * (height - 1)).peerView
				.add(new NeighbourDescriptor(currentTime, grid.get(width * (height - 1) + 1).peerID));
		/*
		 * I update the last row without the first and last element (the interval
		 * [(n-1, 1), (n-1, m-2)] )
		 */
		for (int i = width * (height - 1) + 1; i < width * (height - 1) + (width - 1); i++) {
			int idx = width * 0 + i;
			grid.get(idx).peerView.add(new NeighbourDescriptor(currentTime, grid.get(idx + 1).peerID));
			grid.get(idx).peerView.add(new NeighbourDescriptor(currentTime, grid.get(idx - 1).peerID));
			grid.get(idx).peerView.add(new NeighbourDescriptor(currentTime, grid.get(idx - width).peerID));
		}
		/* I update the element (n-1, m-1) */
		grid.get(width * height - 1).peerView
				.add(new NeighbourDescriptor(currentTime, grid.get(width * (height - 1) - 1).peerID));
		grid.get(width * height - 1).peerView
				.add(new NeighbourDescriptor(currentTime, grid.get(width * height - 2).peerID));

		/* I update the first column without the first and last element */
		for (int j = width; j < width * (height - 1); j += width) {
			grid.get(j).peerView.add(new NeighbourDescriptor(currentTime, grid.get(j + 1).peerID));
			grid.get(j).peerView.add(new NeighbourDescriptor(currentTime, grid.get(j - width).peerID));
			grid.get(j).peerView.add(new NeighbourDescriptor(currentTime, grid.get(j + width).peerID));
		}

		/* I update the last column without the first and last element */
		for (int j = (width - 1) * 2 + 1; j < width * height - 1; j += width) {
			grid.get(j).peerView.add(new NeighbourDescriptor(currentTime, grid.get(j - 1).peerID));
			grid.get(j).peerView.add(new NeighbourDescriptor(currentTime, grid.get(j - width).peerID));
			grid.get(j).peerView.add(new NeighbourDescriptor(currentTime, grid.get(j + width).peerID));
		}

		/* I update all the central elements */
		for (int i = 1; i < width - 1; i++)
			for (int j = 1; j < height - 1; j++) {
				int idx = width * j + i;
				grid.get(idx).peerView.add(new NeighbourDescriptor(currentTime, grid.get(idx - 1).peerID));
				grid.get(idx).peerView.add(new NeighbourDescriptor(currentTime, grid.get(idx + 1).peerID));
				grid.get(idx).peerView.add(new NeighbourDescriptor(currentTime, grid.get(idx - width).peerID));
				grid.get(idx).peerView.add(new NeighbourDescriptor(currentTime, grid.get(idx + width).peerID));
			}
	}

	/**
	 * This function executes the algorithm for the free scale network, for each of the
	 * peers in the network; before the execution and after all the executions it saves
	 * the state of the graph.
	 *  */
	private static void newscastProtocolScaleFreeNetwork(Integer noPeers, Float prob, Integer cacheLines) {
		initScaleFreeNetwork(noPeers, prob, cacheLines);
		try {
			fillFileNeighboursScaleFreeNetwork(noPeers, cacheLines, true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for (int i = 0; i < noPeers; i++)
			net.get(i).selectPeer();
		try {
			fillFileNeighboursScaleFreeNetwork(noPeers, cacheLines, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	
	/**
	 * This function executes the algorithm for the grid network, for each of the
	 * peers in the network; before the execution and after all the executions it saves
	 * the state of the graph.
	 *  */
	private static void newscastProtocolGridNetwork(Integer height, Integer width, Integer cacheLines) {
		initGridNetwork(net, height, width, cacheLines);
		try {
			fillFileNeighboursGrid(net, height, width, cacheLines, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int j = 0; j < height; j++)
			for (int i = 0; i < width; i++)
				net.get(width * j + i).selectPeer();
		try {
			fillFileNeighboursGrid(net, height, width, cacheLines, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * This function fills the file for the scale free networks,
	 * in which the list of neighbours is saved for each peer
	 * */
	private static void fillFileNeighboursScaleFreeNetwork(Integer noPeers, Integer cacheLines, Boolean init)
			throws IOException {
		String path = "." + File.separator + "results" + File.separator + "freeScaleResults" + File.separator + "cacheLines"
			+ cacheLines + File.separator + "peers" + noPeers;
		new File(path).mkdirs();
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				new File(path + File.separator + "neighbours_" + ((init) ? "initial" : "final") + ".sif")));
		for (Integer k : net.keySet()) {
			if (!net.get(k).peerView.isEmpty()) {
				bw.write(net.get(k).peerID.toString() + " link ");
				for (int i = 0; i < net.get(k).peerView.size() - 1; i++)
					bw.write(net.get(k).peerView.get(i).peerID + " ");
				bw.write(net.get(k).peerView.get(net.get(k).peerView.size() - 1).peerID.toString() + "\n");
			}
		}
		bw.close();
		return;
	}

	/**
	 * This function fills the file for the grid networks,
	 * in which the list of neighbours is saved for each peer
	 * */
	private static void fillFileNeighboursGrid(HashMap<Integer, PeerDescriptor> net, Integer height, Integer width,
			Integer cacheLines, Boolean init) throws IOException {
		String path = "." + File.separator + "results" + File.separator + "gridResults" + File.separator + "peers"
				+ height + "x" + width + File.separator + "cacheLines" + cacheLines;
		new File(path).mkdirs();
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				new File(path + File.separator + "neighbours_" + ((init) ? "initial" : "final") + ".sif")));
		for (Integer k : net.keySet()) {
			if (!net.get(k).peerView.isEmpty()) {
				bw.write(net.get(k).peerID.toString() + " link ");
				for (int i = 0; i < net.get(k).peerView.size() - 1; i++)
					bw.write(net.get(k).peerView.get(i).peerID + " ");
				bw.write(net.get(k).peerView.get(net.get(k).peerView.size() - 1).peerID.toString() + "\n");
			}
		}
		bw.close();
		return;
	}

	/**
	 * This function is the main: it takes in input 3 arguments, that is:
	 * the number of peers that the scale free network algorithm requires in input
	 * and the width and height of the rectangle that will compose the network
	 * in a grid fashion, in which every element is a peer that participate in
	 * the experiments. The experiments are iterated for the number of cache lines
	 * used in the simulation, by using values equal to: 20, 35 and 50; the
	 * probability that is needed in the Barabasi-Albert Model, for creating
	 * the initial graph on which the BA algorithm is applied, is defined randomly.
	 * */
	public static void main(String[] args) {
		if(args.length != 3) {
			System.err.println("Inserted a non valid number of parameters, 3 required:"
					+ " number of peers in the free scale network,"
					+ " width and height in the grid network");
			return;
		}
		int noPeers = Integer.parseInt(args[0]);
		int width = Integer.parseInt(args[1]);
		int height = Integer.parseInt(args[2]);
		float prob = (new Random()).nextFloat();
		prob = (prob == 0F) ? 0.2F : prob;
		for (int cacheLines = 20; cacheLines <= 50; cacheLines += 15)
			newscastProtocolScaleFreeNetwork(noPeers, prob, cacheLines);
		for (int cacheLines = 20; cacheLines <= 50; cacheLines += 15)
			newscastProtocolGridNetwork(height, width, cacheLines);
		return;
	}

}
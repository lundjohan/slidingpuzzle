package com.slidingPuzzleGoogle;

import java.util.List;

public abstract class Agent {
	protected double time = 0;
	protected int nodesChecked;

	public abstract List<Character> solve(Tablet tablet);

	public abstract String getName();

	public abstract void clear();

	public double getTime() {
		return time;
	}

	public int getNodesChecked() {
		return nodesChecked;
	}

	// -----------------------------------------------------------------------------
	// Inner class Node
	// -----------------------------------------------------------------------------
	public class Node {

		protected Tablet tablet;
		protected Node up;
		protected Node down;
		protected Node right;
		protected Node left;
		protected String hashNr;
		protected int heuristics;

		public Node(Tablet aTablet) {
			tablet = aTablet;
			heuristics = tablet.getPoints() + tablet.getMovesMade();
			hashNr = tablet.calcHashNr();
			up = null;
			down = null;
			right = null;
			left = null;
		}

		// gets
		public Tablet getTablet() {
			return tablet;
		}

		public void expand() {
			if (tablet.move('u')) {
				up = new Node(new Tablet(tablet));
				tablet.moveBack();
			}
			if (tablet.move('d')) {
				down = new Node(new Tablet(tablet));
				tablet.moveBack();
			}
			if (tablet.move('r')) {
				right = new Node(new Tablet(tablet));
				tablet.moveBack();
			}
			if (tablet.move('l')) {
				left = new Node(new Tablet(tablet));
				tablet.moveBack();
			}
		}
	};
}

package com.slidingPuzzleGoogle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tablet {
	private Tab[][] board; // i c++ ***board
	private int points;
	private int emptyLocRow;
	private int emptyLocCol;
	private ArrayList<Character> oldMoves = new ArrayList<>();

	public static int side;
	public static Heuristics heuristics;
	public static int lowerRightCorner; // true == 1, false == 0

	public Tablet() {
		board = new Tab[side][side];

		// fyll en vektor med tal
		ArrayList<Integer> tmp1 = new ArrayList<>(side * side);
		for (int i = 0; i < side * side; i++)
			tmp1.add(i);
		boolean unsolvable = true;
		do {
			// shuffle ArrayListn
			Collections.shuffle(tmp1);

			// räkna antalet inversions
			// se
			// www.cs.princeton.edu/courses/archive/fall12/cos226/assignments/8puzzle.htm
			// för förklaring av inversions
			// OBS!!! En 2x2 matris kan vara olöslig i alla fall! Betrakta
			// serien 0 2 1 3, den är ok enligt principen för inversions men
			// serien är ändå olöslig.
			int inversions = 0;
			for (int i = 1; i < tmp1.size(); i++) {
				if (tmp1.get(i) != 0)
					for (int j = 0; j < i; j++) {
						if (tmp1.get(j) != 0)
							if (tmp1.get(i) < tmp1.get(j))
								inversions++;
					}
			}

			// vektor lösbar?
			if (tmp1.size() % 2 != inversions % 2)
				unsolvable = false;
		} while (unsolvable);
		customVec(tmp1);
	}

	public Tablet(Tablet t) { // kopieringskonstruktor
		points = t.points;
		emptyLocRow = t.emptyLocRow;
		emptyLocCol = t.emptyLocCol;
		oldMoves = (ArrayList<Character>) t.oldMoves.clone();
		board = new Tab[side][side];
		for (int row = 0; row < side; row++) {
			for (int col = 0; col < side; col++) {
				board[row][col] = t.board[row][col];
				System.out.print(t.board[row][col].getNr()+" ");
			}System.out.println("\n");
		}
	}

	public void customVec(List<Integer> serie) {
		// lägg in i board och passa samtidigt på att finna den tomma rutan
		// (nollan).
		for (int row = 0; row < side; row++)
			for (int col = 0; col < side; col++) {
				board[row][col] = new Tab(serie.get(row * side + col),
						Tablet.side, Tablet.lowerRightCorner);
				if (board[row][col].getNr() == 0) {
					emptyLocRow = row;
					emptyLocCol = col;
				}
			}
		points = Tablet.heuristics.calc(board, Tablet.side);
	}

	public boolean customStr(String serie) {
		boolean ok = false;
		String[] tokens = serie.split(" ");
		ArrayList<Integer> tmp2 = new ArrayList<>(side * side);
		for (int i = 0; i < side * side; i++) {
			int nr = Integer.parseInt(tokens[i]);
			tmp2.add(nr);
		}
		if (isSolvable(tmp2)) {
			customVec(tmp2);
			ok = true;
		}
		return ok;

	}

	boolean isSolvable(ArrayList<Integer> serie) {
		// räkna antalet inversions
		// se
		// www.cs.princeton.edu/courses/archive/fall12/cos226/assignments/8puzzle.htm
		// för förklaring av inversions
		// OBS!!! En 2x2 matris kan vara olöslig i alla fall! Betrakta serien 0
		// 2 1 3, den är ok enligt principen för inversions men serien är ändå
		// olöslig.
		int inversions = 0;
		for (int i = 1; i < serie.size(); i++) {
			if (serie.get(i) != 0)
				for (int j = 0; j < i; j++) {
					if (serie.get(j) != 0)
						if (serie.get(i) < serie.get(j))
							inversions++;
				}
		}
		// vektor lösbar?
		return (serie.size() % 2 != inversions % 2);
	}

	// gets
	public Tab getTab(int row, int col) {
		return board[row][col];
	}

	public int getPoints() {
		return points;
	}

	public int getMovesMade() {
		return oldMoves.size();
	}

	public int getEmptyLocRow() {
		return emptyLocRow;
	}

	public int getEmptyLocCol() {
		return emptyLocCol;
	}

	// moves
	public boolean moveNr(int nr) { // används externt.
		boolean ok = false;
		int rowLoc = -1;
		int colLoc = -1;
		// hämta platsen för tab med nr
		for (int i = 0; i < side; i++) {
			for (int j = 0; j < side; j++) {
				if (board[i][j].getNr() == nr) {
					rowLoc = i;
					colLoc = j;
					ok = true;
					break;
				}
			}
		}
		// kontrollera om tab angränsar (rakt) till nollan.
		if ((Math.abs(rowLoc - emptyLocRow) + Math.abs(colLoc - emptyLocCol)) == 1) {

			// ligger tomma rutan över (värdet på row är lägre högre upp)?
			if (emptyLocRow < rowLoc)
				move('d'); // flytta tomma rutan down.

			// under
			else if (emptyLocRow > rowLoc)
				move('u');

			// till vänster
			else if (emptyLocCol < colLoc)
				move('r');

			// till höger
			else if (emptyLocCol > colLoc)
				move('l');
		}
		return ok;
	}

	public int getNrThatShallBePressed(char ch) { // används externt
		int number = -1;
		if (ch == 'u')
			number = board[emptyLocRow - 1][emptyLocCol].getNr();
		else if (ch == 'd')
			number = board[emptyLocRow + 1][emptyLocCol].getNr();
		else if (ch == 'l')
			number = board[emptyLocRow][emptyLocCol - 1].getNr();
		else if (ch == 'r')
			number = board[emptyLocRow][emptyLocCol + 1].getNr();
		return number;
	}

	public boolean move(char ch) {
		// up
		if (ch == 'u' && emptyLocRow > 0) {
			oldMoves.add('d');
			moveHelper(ch);
		}
		// down
		else if (ch == 'd' && emptyLocRow < side - 1) {
			oldMoves.add('u');
			moveHelper(ch);
		}
		// left
		else if (ch == 'l' && emptyLocCol > 0) {
			oldMoves.add('r');
			moveHelper(ch);
		}
		// right
		else if (ch == 'r' && emptyLocCol < side - 1) {
			oldMoves.add('l');
			moveHelper(ch);
		} else
			return false;
		return true;

	}

	public void moveBack() {
		if (!oldMoves.isEmpty()) {
			char ch = oldMoves.get(oldMoves.size() - 1);
			oldMoves.remove(oldMoves.size() - 1);
			moveHelper(ch);
		}
	}

	public void moveHelper(char ch) {
		int newEmptyLocRow = 0;
		int newEmptyLocCol = 0;
		if (ch == 'u') {
			newEmptyLocRow = emptyLocRow - 1;
			newEmptyLocCol = emptyLocCol;
		} else if (ch == 'd') {
			newEmptyLocRow = emptyLocRow + 1;
			newEmptyLocCol = emptyLocCol;
		} else if (ch == 'l') {
			newEmptyLocRow = emptyLocRow;
			newEmptyLocCol = emptyLocCol - 1;
		} else if (ch == 'r') {
			newEmptyLocRow = emptyLocRow;
			newEmptyLocCol = emptyLocCol + 1;
		}

		// byt plats på rutorna.
		Tab tmp = board[newEmptyLocRow][newEmptyLocCol];
		board[newEmptyLocRow][newEmptyLocCol] = board[emptyLocRow][emptyLocCol];
		board[emptyLocRow][emptyLocCol] = tmp;

		// uppdatera emptyLoc
		emptyLocRow = newEmptyLocRow;
		emptyLocCol = newEmptyLocCol;

		points = heuristics.calc(board, Tablet.side);
	}

	// oldMoves
	public void clearOldMoves() {
		oldMoves.clear();
	}

	public List<Character> getOldMoves() {
		return oldMoves;
	}

	public List<Character> forwardBackMoves() {
		ArrayList<Character> oldies = oldMoves;
		ArrayList<Character> forwardMoves = new ArrayList<>();
		char ch;
		while (!oldies.isEmpty()) {
			ch = oldies.get(oldMoves.size() - 1);;
			oldies.remove(oldies.size() - 1);
			if (ch == 'u') {
				ch = 'd';
			} else if (ch == 'd') {
				ch = 'u';
			} else if (ch == 'l') {
				ch = 'r';
			} else if (ch == 'r') {
				ch = 'l';
			}
			forwardMoves.add(ch);
		}
		return forwardMoves;
	}

	public void updatePoints() {
		points = Tablet.heuristics.calc(board, Tablet.side);
	}

	// övrigt
	public void changeZeroCorner() {
		if (lowerRightCorner == 1) // 1 == true
			lowerRightCorner = 0;
		else
			lowerRightCorner = 1;
		for (int row = 0; row < side; row++)
			for (int col = 0; col < side; col++)
				board[row][col].setGoalLoc(side, lowerRightCorner);
		points = Tablet.heuristics.calc(board, Tablet.side);

	}

	public String calcHashNr() {
		StringBuilder hash = new StringBuilder(side * side);
		for (int row = 0; row < side; row++)
			for (int col = 0; col < side; col++)
				hash.append((char) (board[row][col].getNr() + 48));
		return hash.toString();
	}
}

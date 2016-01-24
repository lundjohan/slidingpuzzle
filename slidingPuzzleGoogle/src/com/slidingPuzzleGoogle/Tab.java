package com.slidingPuzzleGoogle;

public class Tab{
	private int nr;
	private	int goalRow;
	private	int goalCol;

	public	Tab(int aNr, int side, int lowerRightCorner){	//lowerRightCorner == 1 => true, == 0 => false (zero up in left corner.)
		if (aNr!=0){nr = aNr; goalRow = (nr-lowerRightCorner)/side; goalCol = (nr-lowerRightCorner)%side;}}				
	public	int getNr(){return nr;}
	public	int getGoalRow(){return goalRow;}
	public	int getGoalCol(){return goalCol;}
	public	void setGoalLoc(int side, int corner){goalRow = (nr-corner)/side; goalCol = (nr-corner)%side;}
}

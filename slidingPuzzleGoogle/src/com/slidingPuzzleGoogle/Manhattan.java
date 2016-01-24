package com.slidingPuzzleGoogle;

public class Manhattan extends Heuristics{
	public String getName(){return "Manhattan Distance";}

	public int calc(Tab[][]board, int side){
		int points=0;
		//calculate manhattan-distance for each "tab" (alla utom nollan).
		for (int row=0;row<side;row++){
			for (int col=0;col<side;col++){
				if(board[row][col].getNr()!=0){
					//räkna ut Delta-Y och Delta-X => manhattan-distance
					points += Math.abs(row-board[row][col].getGoalRow()) + Math.abs(col - board[row][col].getGoalCol());
				}
			}
		}
		return points;
	}
	
}

package com.slidingPuzzleGoogle;

public class LinearConflict extends Heuristics{
	public String getName(){return "Linear Conflict";}
	public int calc(Tab[][]board, int side){
		int points = 0;						
		int rowJ =-1, rowK=-1;	//om rowK �r till h�ger om rowJ och om rowK:s goalCol �r till v�nster om rowJ:s => points+= 2;
		int colJ =-1, colK=-1;	//om colK har h�gre radnummer �n colJ och om colK:s goalRow �r mindre �n rowJ:s => points+= 2;
		for (int row=0;row<side;row++){
			for (int col=0;col<side;col++){
				if(board[row][col].getNr()!=0){
					//r�kna ut Delta-Y och Delta-X => manhattan-distance
					points += Math.abs(row-board[row][col].getGoalRow()) + Math.abs(col - board[row][col].getGoalCol());

					//utr�kning av linear conflict - rowwise(horisontellt) ----.
					if(board[row][col].getGoalRow()==row){	//p� r�tt rad?
						if (rowJ ==-1)							//om den f�rsta p� raden som �r p� r�tt rad
							rowJ=board[row][col].getGoalCol();
						else if(rowK ==-1){						//om den andra ---||---
							rowK=board[row][col].getGoalCol();
							if(rowK<rowJ){						//om rowK:s goal till v�nster om rowJ:s goal
								points+=2;
							}
						}
						else{	//varken rowJ eller RowK �r -1.
							rowJ = rowK;
							rowK = board[row][col].getGoalCol();
							if(rowK<rowJ){
								points+=2;
							}
						}	
					}		
				}//!= 0
				//utr�kning av linear conflict - colwise(vertikalt) ^ Detta ser ologiskt ut i samma loop som ovan, men t�nk p� att jag v�nder till [col][row] nedan f�r att det ska funka.
				if(board[col][row].getNr()!=0){
					if(board[col][row].getGoalCol()==row){	//i r�tt kolumn? (Observera att jag m�ste byta col mot row efter ==)
						if (colJ ==-1)
							colJ=board[col][row].getGoalRow();
						else if(colK ==-1){
							colK=board[col][row].getGoalRow();
							if(colK<colJ){
								points+=2;
							}
						}
						else{	//varken colJ eller colK �r -1.
							colJ = colK;
							colK = board[col][row].getGoalRow();;
							if(colK<colJ){
								points+=2;
							}
						}	
					}
				}
			}
			//nollst�ll
			rowJ =-1; rowK =-1;
			colJ =-1; colK =-1;	
		}
		return points;
		
	}
}

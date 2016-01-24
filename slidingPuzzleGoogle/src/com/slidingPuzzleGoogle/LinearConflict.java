package com.slidingPuzzleGoogle;

public class LinearConflict extends Heuristics{
	public String getName(){return "Linear Conflict";}
	public int calc(Tab[][]board, int side){
		int points = 0;						
		int rowJ =-1, rowK=-1;	//om rowK är till höger om rowJ och om rowK:s goalCol är till vänster om rowJ:s => points+= 2;
		int colJ =-1, colK=-1;	//om colK har högre radnummer än colJ och om colK:s goalRow är mindre än rowJ:s => points+= 2;
		for (int row=0;row<side;row++){
			for (int col=0;col<side;col++){
				if(board[row][col].getNr()!=0){
					//räkna ut Delta-Y och Delta-X => manhattan-distance
					points += Math.abs(row-board[row][col].getGoalRow()) + Math.abs(col - board[row][col].getGoalCol());

					//uträkning av linear conflict - rowwise(horisontellt) ----.
					if(board[row][col].getGoalRow()==row){	//på rätt rad?
						if (rowJ ==-1)							//om den första på raden som är på rätt rad
							rowJ=board[row][col].getGoalCol();
						else if(rowK ==-1){						//om den andra ---||---
							rowK=board[row][col].getGoalCol();
							if(rowK<rowJ){						//om rowK:s goal till vänster om rowJ:s goal
								points+=2;
							}
						}
						else{	//varken rowJ eller RowK är -1.
							rowJ = rowK;
							rowK = board[row][col].getGoalCol();
							if(rowK<rowJ){
								points+=2;
							}
						}	
					}		
				}//!= 0
				//uträkning av linear conflict - colwise(vertikalt) ^ Detta ser ologiskt ut i samma loop som ovan, men tänk på att jag vänder till [col][row] nedan för att det ska funka.
				if(board[col][row].getNr()!=0){
					if(board[col][row].getGoalCol()==row){	//i rätt kolumn? (Observera att jag måste byta col mot row efter ==)
						if (colJ ==-1)
							colJ=board[col][row].getGoalRow();
						else if(colK ==-1){
							colK=board[col][row].getGoalRow();
							if(colK<colJ){
								points+=2;
							}
						}
						else{	//varken colJ eller colK är -1.
							colJ = colK;
							colK = board[col][row].getGoalRow();;
							if(colK<colJ){
								points+=2;
							}
						}	
					}
				}
			}
			//nollställ
			rowJ =-1; rowK =-1;
			colJ =-1; colK =-1;	
		}
		return points;
		
	}
}

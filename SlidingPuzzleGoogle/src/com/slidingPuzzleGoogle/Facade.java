package com.slidingPuzzleGoogle;

import java.util.List;



public class Facade {
	public	Facade(){
		System.out.println("Inuti Facade 1");
		//************************************
		//Options
		//************************************						
		Tablet.lowerRightCorner = 0;				//true(1) f�r m�lnollan l�ngst ner till h�ger, falskt(0) f�r uppe till v�nster.
		Tablet.heuristics = new LinearConflict();	//Manhattan, LinearConflict, PatternDb
	}
	public List<Character> solve(List<Integer>nrs){
		System.out.println("Inuti Facade 2");
		Tablet.side = (int)Math.sqrt(nrs.size());							//3 = 8-puzzle, 4 = 15-puzzle
		Tablet tablet = new Tablet();
		tablet.customVec(nrs);
		Agent agent = new AgentIDA();
		System.out.println("Inuti Facade 3");
		return agent.solve(tablet);
	}
}

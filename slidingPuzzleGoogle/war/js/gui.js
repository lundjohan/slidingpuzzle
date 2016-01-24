/*
 * Global values used by Gui
 */

var SIDE = 3;
var TABWIDTH = 10;
var TABGAP = .3;
var puzzle;
var moveBlocked = 0;	//false
// synchronize(java) moves

function Gui() {
	this.toMoveQueue = [];
	this.locZero;
	this.tabs = [];

}

Gui.prototype.restart = function() {
	var self = this;
	this.toMoveQueue.length = 0;
	// här ligger felet.
	for (var element = 0; element < self.tabs.length; element++) {
		var tab = document.getElementById(element);
		tab.removeEventListener("click", function() {
			self.moveTab(tab);
		}, false);
		tab.removeEventListener('transitionend', function() {
			'webkitTransitionEnd OTransitionEnd transitionend'
			self.move;
		}, false);

	}

	// nollställ arrayens längd.
	this.tabs.length = 0;

	// rensa puzzles innehåll. Snabbare än puzzle.innerHTML = "";
	while (puzzle.hasChildNodes())
		puzzle.removeChild(puzzle.childNodes[0]);
	// Start New Game
	this.newPuzzle();
};
/*
 * newPuzzle randomizes and elects only solvable puzzles
 */
Gui.prototype.newPuzzle = function() {
	for (var i = 0; i < SIDE * SIDE; i++)
		this.tabs.push(i);

	// skapa storlek för puzzle
	puzzle.style.width = TABWIDTH * SIDE - TABGAP + "em";
	puzzle.style.height = TABWIDTH * SIDE - TABGAP + "em";

	// shuffle this.tabsay
	this.shuffle();

	/*
	 * placera ut dem i puzzle. Gör varje nummer till en tab, och varje tab utom
	 * nollan innehåller class="tabVisible", varje tab har också ett unikt
	 * id-värde, samma som nr, t ex "4".
	 */
	for (var row = 0; row < SIDE; ++row) {
		for (var col = 0; col < SIDE; ++col) {

			var element = row * SIDE + col;
			var tab = document.createElement("tab");
			tab.id = (element);
			tab.style.cursor = "pointer";
			var nr = document.createTextNode(this.tabs[element]);
			var span = document.createElement("span");
			span.className = "nr";
			span.appendChild(nr);
			tab.appendChild(span);

			/*
			 * see
			 * http://stackoverflow.com/questions/8909652/adding-click-event-listeners-in-loop.
			 * Have to do like this, otherwise its only last tab that gets
			 * eventListener
			 */
			var that = this;
			// finns snyggare sätt att spara this, se här:
			// http://www.smashingmagazine.com/2014/01/23/understanding-javascript-function-prototype-bind/
			if (this.tabs[element] != 0) {
				(function(_tab) {
					tab.addEventListener("click", function() {
						that.moveTab(_tab);
					}, false);
					/* Nedan för Detecting the completion of a transition of tab */
					tab.addEventListener('transitionend', function() {
						that.move();
					}, false);

				})(tab);
			}
			puzzle.appendChild(tab);
			tab.style.width = TABWIDTH - TABGAP + "em";
			tab.style.height = TABWIDTH - TABGAP + "em";

			// place tabs on place according to order in tabs
			tab.style.webkitTransform = "translateX(" + TABWIDTH * col + "em)";
			tab.style.transform = "translateX(" + TABWIDTH * col + "em)";

			tab.style.webkitTransform += "translateY(" + TABWIDTH * row + "em)";
			tab.style.transform += "translateY(" + TABWIDTH * row + "em)";

			if (this.tabs[element] == 0) {
				locZero = element;
				tab.style.visibility = "hidden";
			}

		}
	}
};
// shuffle function that also check if serie solvable (only half is!).
Gui.prototype.shuffle = function() {// v1.0
	var unsolvable = true;
	do {
		// make the shuffle, hämtad från
		// http://stackoverflow.com/questions/6274339/how-can-i-shuffle-an-this.tabsay-in-javascript
		for (var j, x, i = this.tabs.length; i; j = Math.floor(Math.random()
				* i), x = this.tabs[--i], this.tabs[i] = this.tabs[j], this.tabs[j] = x)
			;

		// räkna antalet inversions
		// se
		// www.cs.princeton.edu/courses/archive/fall12/cos226/assignments/8puzzle.html
		// för förklaring av inversions
		// OBS!!! En 2x2 matris kan vara olöslig i alla fall! Betrakta serien 0
		// 2 1 3, den är ok enligt principen för inversions men serien är ändå
		// olöslig.
		var inversions = 0;
		for (var i = 1; i < this.tabs.length; i++) {

			// ta fram locZero
			if (this.tabs[i] == 0)
				this.locZero = i;

			if (this.tabs[i] != 0)
				for (var j = 0; j < i; j++) {
					if (this.tabs[j] != 0)
						if (this.tabs[i] < this.tabs[j]) {
							inversions++;
						}
				}
		}
		// om tabs är jämnt nummer lägg till raden för nollan (första raden =
		// 0):
		if (SIDE % 2 == 0)
			inversions += Math.floor(this.locZero / SIDE);
		// vektor lösbar?
		/*
		 * Even sized tables (e.g. 16) shall have odd number of inversions for
		 * being solvable Odd sized tables (e.g. 9) shall have even number of
		 * inversions for being solvable
		 */

		if (Math.floor(this.tabs.length % 2) != Math.floor(inversions % 2)) {
			unsolvable = false;
		}
	} while (unsolvable);

	// ta fram locZero
	for (var i = 0; i < this.tabs.length; i++) {
		if (this.tabs[i] == 0) {
			this.locZero = i;
			break;
		}
	}
};
Gui.prototype.moveTab = function(tabToMove) {
	if (moveBlocked != 0)
		return;
	moveBlocked = true;
	var locTab = parseInt(tabToMove.id);
	// locZero just above tabToMove?
	if (this.locZero + SIDE == locTab)
		this.toMoveQueue.push("d"); // d = down
	// move zero down (counterutive but the engine thinks that the tab that is
	// moving is the zero)
	// locZero just under tabToMove?
	else if (locTab + SIDE == this.locZero)
		this.toMoveQueue.push("u");
	// move zero up.
	// locZero just to the left of tabToMove?
	else if (locTab % SIDE != 0 && locTab - 1 == this.locZero)
		this.toMoveQueue.push("r");
	// move zero right.
	// locZero just to the right of tabToMove?
	else if (locTab % SIDE != SIDE - 1 && locTab + 1 == this.locZero)
		this.toMoveQueue.push("l");
	// move zero left.
	this.move();
	moveBlocked = 0;
};

// skiss.
Gui.prototype.move = function() {
	var locTab;
	// locTab refers to the tab that shall change place with tab with zero
	// (invisible tab).
	// while (this.toMoveQueue.length > 0) {
	if (this.toMoveQueue.length > 0) {
		var direction = this.toMoveQueue.pop();
		if (direction == "u") {// Its the empty square that is moving, not tab
			// to empty square.
			locTab = this.locZero - SIDE;
		} else if (direction == "d") {
			locTab = this.locZero + SIDE;
		} else if (direction == "r") {
			locTab = this.locZero + 1;
		} else if (direction == "l") {
			locTab = this.locZero - 1;
		}
		// 1. Change place graphically.
		var tmp = document.getElementById(locTab).style.webkitTransform;
		document.getElementById(locTab).style.webkitTransform = document
				.getElementById(this.locZero).style.webkitTransform;
		document.getElementById(this.locZero).style.webkitTransform = tmp;

		var tmp = document.getElementById(locTab).style.transform;
		document.getElementById(locTab).style.transform = document
				.getElementById(this.locZero).style.transform;
		document.getElementById(this.locZero).style.transform = tmp;
		// 2. Change id.

		var tmpId = -1;
		document.getElementById(this.locZero).id = tmpId;
		// temporaly, so that there are not two tabs with same id.
		document.getElementById(locTab).id = this.locZero;
		document.getElementById(tmpId).id = locTab;

		// 3. Change place in tabs.
		var tmpNr = this.tabs[this.locZero]
		this.tabs[this.locZero] = this.tabs[locTab];
		this.tabs[locTab] = tmpNr;

		// new location for this.locZero
		this.locZero = locTab;
	}
};
Gui.prototype.solve = function() {
	if (moveBlocked!=0)
		return;
	moveBlocked = true;

	// clear array.
	this.toMoveQueue = [];

	/*
	 * AJAX
	 */
	var http_request;
	try {/* Opera 8.0+, Firefox, Chrome, Safari */
		http_request = new XMLHttpRequest();
	} catch (e) {/* Internet Explorer Browsers */
		try {
			http_request = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			try {
				http_request = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (e) {// Something went wrong
				alert("Your browser broke!");
			}
		}
	}
	var self = this;
	http_request.onreadystatechange = function() {
		if (http_request.readyState == 4 && http_request.status == 200) {/*
																			 * Javascript
																			 * function
																			 * JSON.parse
																			 * to
																			 * parse
																			 * JSON
																			 * data
																			 */
			var jsonObj = JSON.parse(http_request.responseText);
			self.toMoveQueue = jsonObj.toMoveArray.slice(); /*
															 * toMoveArray är
															 * arrayen inne i
															 * JSON-objektet.
															 *//*
				 * Make moves on client
				 */
			// while(self.toMoveQueue.length>0){
			self.move();
			// window.setTimeout(self.move,500 ); Funkar ej.
			// }

		}
		moveBlocked = 0;
	};

	/* Send this.tabs (array with numbers) as a JSON-array to server */
	var jsonArray = {
		"nrsArray" : this.tabs
	}; // eller ska jag använda
	// stringify????
	http_request.open("POST", "/facade", true);
	http_request.send(JSON.stringify(jsonArray));

};

function start() {
	puzzle = document.getElementById("puzzle");
	var gui = new Gui();
	gui.newPuzzle();
	var random = document.getElementById("random");
	random.addEventListener("click", function() {
		gui.restart();
	}, false);
	var solve = document.getElementById("solve");
	solve.addEventListener("click", function() {
		gui.solve();
	}, false);

}

window.addEventListener("load", start, false);

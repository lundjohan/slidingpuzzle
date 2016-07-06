/*
 * Note: this script was earlier without jQuery. Some of the functions of the javascript code went obesolete so I included jQuery. 
 * jQuery is not used consistently in code, so its a bit fragmented. If I get more time I will change file to have more jQuery.
 */

/*
 * Global values used by Gui
 */

var SIDE = 3;
var TABWIDTH = 10;
var TABGAP = .3;
var puzzle;
var moveBlocked = 0; // false

$(function() {
	start();
});

// synchronize(java) moves

function Gui() {
	this.toMoveQueue = [];
	this.locZero;
	this.tabs = [];

}

Gui.prototype.restart = function() {
	var self = this;
	this.toMoveQueue.length = 0;

	// remove event listeners through implicit iteration:
	$('tab').off('click').off('transitionend');

	// nollställ arrayens längd.
	this.tabs.length = 0;

	// rensa puzzles innehåll.
	$('#puzzle').empty();
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
	var side = (TABWIDTH * SIDE - TABGAP) + "em";
	$('#puzzle').width(side).height(side);

	// shuffle this.tabs
	this.shuffle();

	/*
	 * placera ut dem i puzzle. Gör varje nummer till en tab, och varje tab utom
	 * nollan innehåller class="tabVisible", varje tab har också ett unikt
	 * id-värde, samma som nr, t ex "4".
	 */
	var that = this;
	for (var row = 0; row < SIDE; ++row) {
		for (var col = 0; col < SIDE; ++col) {

			var element = row * SIDE + col;
			var tabRef = $('<tab></tab>').appendTo('#puzzle').attr('id',
					element).css('cursor', 'pointer');

			(function(tabRef) {
				$('<span class = "nr"></span>').appendTo('#' + element).append(
						document.createTextNode(that.tabs[element]));

				if (that.tabs[element] != 0) {
					tabRef.click(function() {
						that.moveTab(tabRef.attr('id'));	
					}).bind('transitionend', function() {
						that.move();
					}).width(TABWIDTH - TABGAP + "em")
					.height(TABWIDTH - TABGAP + "em");
				}

				// place tabs on place according to order in tabs
				tabRef.css({
					transform : "translateX(" + TABWIDTH * col + "em)"
							+ "translateY(" + TABWIDTH * row + "em)"
				});

				if (that.tabs[element] == 0) {
					locZero = element;
					tabRef.css("visibility", "hidden")
				}
			})(tabRef);
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
Gui.prototype.moveTab = function(tabId) {

	if (moveBlocked != 0)
		return;
	moveBlocked = true;
	var locTab = parseInt(tabId);
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
		var tmp = $('#' + locTab).css('transform');
		$('#' + locTab).css({
			transform : $('#' + this.locZero).css('transform')
		});
		$('#' + this.locZero).css({
			transform : tmp
		});

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
	if (moveBlocked != 0)
		return;
	moveBlocked = true;
	// clear array.
	this.toMoveQueue = [];

	/*
	 * AJAX
	 */

	/* Send this.tabs (array with numbers) as a JSON-array to server */
	var jsonArray = {
		"nrsArray" : this.tabs
	};

	var self = this;
	$.ajax({
		type : "POST",
		url : "/sliding",
		contentType : "application/json",
		dataType : "json",
		data : JSON.stringify(jsonArray),
		success : function(response) {
			var jsonObj = response;
			self.toMoveQueue = jsonObj.toMoveArray.slice();// toMoveArray är
															// arrayen inne i
															// JSON-objektet.*/
			self.move();
		},
	});
	moveBlocked = 0; // denna block fungerar ej.
};

function start() {
	var gui = new Gui();
	gui.newPuzzle();
	$('#random').click(function() {
		gui.restart();
	});
	$('#solve').click(function() {
		gui.solve();
	});
}

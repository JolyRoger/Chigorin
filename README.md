# Chigorin

This is a chess application written in [Play](https://www.playframework.com) framework to play and analyze a chess game. It's named in honor of great Russian chess master Mikhail Chigorin. 

## I just want to run it

This is not deployed, but Docker can help. Try 
```
> docker run -p 9000:9000 daniilmonakhov/chigorin 
```
Docker image exposes port 9000, so after downloading and running, the application will be active on http://localhost:9000 in your browser. 

## Getting Started

Just download the application:
```
> git clone https://github.com/JolyRoger/Chigorin.git
```

### Prerequisites

The simplest way to run it locally is to download and install [sbt]( https://www.scala-sbt.org/) to your machine. Actually, this and Internet connection are all that you need to run the application. 

### Running

Then go to the game directory: 
```
> cd Chigorin
```
and run it with sbt:
```
> sbt run
```
It takes some time, but when sbt downloads, resolves, updates, and compiles everything you will see 
```
(Server started, use Ctrl+D to stop and go back to the console....)
```
It means Chigorin has been started. Load the application page in address [http://localhost:9000](http://localhost:9000). 

![alt text](https://ic.pics.livejournal.com/mjol1nir/16493210/6955/6955_900.png)

### Control

Now you can play with a chess engine and/or analyse your position. Select desired chess engine with help of gray combobox in left top corner of screen. 

![alt text](https://ic.pics.livejournal.com/mjol1nir/16493210/7257/7257_900.png)

By default it is Stockfish. Well known Komodo and Mediocre by Jonatan Pettersson are also available. 

To start playing just do move by dragging and dropping a piece on the board 

![alt text](https://ic.pics.livejournal.com/mjol1nir/16493210/7483/7483_900.png)

or press orange "MOVE" button on the panel above.

![alt text](https://ic.pics.livejournal.com/mjol1nir/16493210/8393/8393_900.png)

In this case selected chess engine does the best from its point od view move.

To start new game press button "New game" and select how you wish to enter the position. 

![alt text](https://ic.pics.livejournal.com/mjol1nir/16493210/8500/8500_900.png)

There are four options:

* **Start position**. Game starts from initial chess position.

* **From PGN**. Paste correct PGN. The application will do all pgn moves and show them in black notation panel

![alt text](https://ic.pics.livejournal.com/mjol1nir/16493210/8965/8965_900.png)

* **From FEN**. Paste correct FEN string. Fen position will be shown on the board.

![alt text](https://ic.pics.livejournal.com/mjol1nir/16493210/9346/9346_900.png)

* **Set pieces**. Drag and drop pieces to the pop-up board and press "OK".

![alt text](https://ic.pics.livejournal.com/mjol1nir/16493210/8841/8841_900.png)

Green "Show FEN" button 

![alt text](https://ic.pics.livejournal.com/mjol1nir/16493210/9529/9529_900.png)

shows fen code of position on board below the board. 

![alt text](https://ic.pics.livejournal.com/mjol1nir/16493210/9867/9867_900.png)

Further you can copy fen to clipboard pressing "Copy" button, past anywhere (Ctrl+V works quite well), and close fen text box by "Hide" button.

Orange "MOVE" button in any cases tries to get the best move from server and do it on board

When some moves have been done and represented on the black notation panel you can move position back and forward by pressing ?, ?, ?, and ? buttons on the panel down the board. 

![alt text](https://ic.pics.livejournal.com/mjol1nir/16493210/10107/10107_900.png)

They moves to start position, one move back, one move forward, or to the end of position respectively. Button ? reverts the board with no affection to your game or analysis.

To analyse position on the board move switcher to "ON" position. To stop analysis get it back to "OFF"

![alt text](https://ic.pics.livejournal.com/mjol1nir/16493210/10391/10391_900.png)

When the switcher is on you see on the light right panel different variations which chess engine considers as best, three by default. Playing blue "Fix" button, arising only when analysis is up, you can temporarily stop analysis, fix current variations, and play them on board by pressing to moves (they are clickable) or to arrows ?/?. Back and forward, respectively.

Pressing "Setting" in the top right corner of screen you may want to tune some application options.

Select players. Select by radio buttons who plays.

Computer vs. Human by default. To every your move on the board selected chess engine responds by the the best move from its side.

Computer vs. Computer. Just to watch how chess engine plays with itself.

Human vs. Human. If you don't want chess engine to respond to your dragging pieces on board.

Pondering time in seconds. How long chess engine thinks of the next move.

Analysis lines. How many analysis lines chess engine shows when analysis is on. Keep and mind some chess engines does not support multiline analysis feature. E.g. Mediocre permanently will issue only one line an analysis mode.


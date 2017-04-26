var board, statusEl, fenEl, pgnEl, game = new Chess()
var fenPgn = {}
var oldFen
var startFen = 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1'

function onChange(oldPos, newPos) {
    //console.log("Position changed:");
    //console.log("Old position: " + ChessBoard.objToFen(oldPos));
    //console.log("New position: " + ChessBoard.objToFen(newPos));
    //console.log("--------------------")

    $('#fencopybtn').children().attr('src', '/assets/images/copy.png')
}


// do not pick up pieces if the game is over
// only pick up pieces for the side to move
var onDragStart = function(source, piece, position, orientation) {
    if (game.game_over() === true ||
        (game.turn() === 'w' && piece.search(/^b/) !== -1) ||
        (game.turn() === 'b' && piece.search(/^w/) !== -1)) {
        return false;
    }
};

var onDrop = function(source, target) {
    // see if the move is legal
    var move = game.move({
        from: source,
        to: target,
        promotion: 'q' // NOTE: always promote to a queen for example simplicity
    });

    // illegal move
    if (move === null) return 'snapback';
    console.log('MOVE!')
    if (analysis) {
        continueAnalysis(true, clearAndAnalyse)
    } else {
        getBestMoveFromServer()
    }
    updateStatus();
};

// update the board position after the piece snap
// for castling, en passant, pawn promotion
var onSnapEnd = function() {
    board.position(game.fen());
};

var updateStatus = function() {
    var status = '';

    var moveColor = 'White';
    if (game.turn() === 'b') {
        moveColor = 'Black';
    }

    // checkmate?
    if (game.in_checkmate() === true) {
        status = 'Game over, ' + moveColor + ' is in checkmate.';
    }

    // draw?
    else if (game.in_draw() === true) {
        status = 'Game over, drawn position';
    }

    // game still on
    else {
        status = moveColor + ' to move';

        // check?
        if (game.in_check() === true) {
            status += ', ' + moveColor + ' is in check';
        }
    }

    var fen = game.fen()
    var pgn = game.pgn({ with_header: false })
    fenPgn[pgn] = oldFen
    oldFen = fen
    statusEl.html(status);
    fenEl.html(fen);
    pgnEl.html(addClickToMove2(pgn));
    $('#fencopybtn').children().attr('src', '/assets/images/copy.png')
}

function addClickToMove2(pgn) {
    var i = 0
    return pgn.replace(/[a-zA-Z][^\s\.]+/g,function (moveStr) {
        return '<span value="' + (i++) + '" class="clicked-move" onclick="clickMove(this)">' + moveStr + '</span>'
    })
}

function clickMove(element) {
    console.info($(element).attr('value') + " :: " + element.innerText)
    game.load(startFen)
    var moveNumber = parseInt($(element).attr('value'))
    $('#notation').children().each(function(index) {
        var curN = parseInt($(this).attr('value'))
        if (curN <= moveNumber) game.move($(this).text())
    })
    board.position(game.fen())
}

function unload() {
    $.ajax( {
        url : '/deleteID/',
        type: 'GET',
        async: false
    })
}

function init() {
    $(document).ready(function() {
        $('#fencontainer').hide()
        $('.fen-paste-element').hide()

        var cfg = {
            draggable: true,
            position: 'start',
            onChange: onChange,
            onDragStart: onDragStart,
            onDrop: onDrop,
            onSnapEnd: onSnapEnd
        }
        board = ChessBoard('board', cfg)
        //statusEl = $('#analysis')
        statusEl = $('#status')
        fenEl = $('#fencontent')
        pgnEl = $('#notation')

        initEngineServer(updateStatus)
    })
}

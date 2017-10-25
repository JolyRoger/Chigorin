var board, statusEl, fenEl, pgnEl, game = new Chess()
var startFen = 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1'

function onChange(oldPos, newPos) {
    if (analysis) continueAnalysis()
    $('#fencopybtn').children().attr('src', '/assets/images/copy.png')
}

// do not pick up pieces if the game is over
// only pick up pieces for the side to move
function onDragStart(source, piece, position, orientation) {
    if (game.game_over() === true ||
        (game.turn() === 'w' && piece.search(/^b/) !== -1) ||
        (game.turn() === 'b' && piece.search(/^w/) !== -1)) {
        return false;
    }
}

function onDrop(source, target) {
    // see if the move is legal
    /* NOTE: always promote to a queen for example simplicity*/
    var move = game.move({ from: source, to: target, promotion: 'q' })

    // illegal move
    if (move === null) return 'snapback'
    if (analysis) {
        var analPgn = getPgnFromNotation($('#a-cont' + analIndex))
        var notationPgn = getPgnFromNotation()
        var gamePgnArr = getMoveArrayFromPgn(analPgn)
        game.load(getFenFromNotation())
        deleteGrayMoves()
        doMoves(game, gamePgnArr, addClickToLastMove)
    }

    game.move({ from: source, to: target, promotion: 'q' })

    addClickToLastMove()
    updateStatus()
    if (!analysis && getCheckedValue($('#players')) < 2 && !game.in_checkmate()) getBestMoveFromServer()
}

// update the board position after the piece snap
// for castling, en passant, pawn promotion
function onSnapEnd() {
    board.position(game.fen())
}

function updateStatus() {
    var status = ''

    var moveColor = 'White'
    if (game.turn() === 'b') {
        moveColor = 'Black'
    }

    // checkmate?
    if (game.in_checkmate() === true) {
        status = 'Game over, ' + moveColor + ' is in checkmate.'
    }

    // draw?
    else if (game.in_draw() === true) {
        status = 'Game over, drawn position'
    }

    // game still on
    else {
        status = moveColor + ' to move'

        // check?
        if (game.in_check() === true) {
            status += ', ' + moveColor + ' is in check'
        }
    }
    unfix()
    statusEl.html(status)
    fenEl.html(game.fen())
    $('#fencopybtn').children().attr('src', '/assets/images/copy.png')
}

function reset() {
    game.reset()
    pgnEl.html('')
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

        var cfg = {
            draggable: true,
            position: 'start',
            onChange: onChange,
            onDragStart: onDragStart,
            onDrop: onDrop,
            onSnapEnd: onSnapEnd
        }
        board = ChessBoard('board', cfg)
        statusEl = $('#status')
        fenEl = $('#fencontent')
        pgnEl = $('#notation-show')

        initEngineServer(updateStatus)
    })
}

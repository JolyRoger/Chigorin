var notation = 0

function turnSide() {
    board.flip()
}

function newPositionFromPGN(pgn) {
    reset()
    startFen = 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1'
    getMoveArrayFromPgn(pgn).forEach(function(move) {
        game.move(move)
        addClickToLastMove()
    })
    board.position(game.fen())
    updateStatus()
}

function newPosition(fen) {
    if (fen == undefined) {
        reset()
        startFen = 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1'
        board.start()
        updateStatus()
    } else {
        var validateResult = game.validate_fen(fen)
        if (validateResult.valid) {
            reset()
            startFen = fen
            game.load(fen)
            board.position(fen)
            newPositionServer(updateStatus)
        } else {
            $('#fenstringpaste').val(validateResult.error)
            console.log($('#fenstringpaste').val() + ' — ' + validateResult.error)
        }
    }
}

function setAnalysisLines() {
    if (analysis) setAnalysisLinesServer()
}

var piecesBoard

function closePosition() {
    $('#fb-modal-Position').fadeOut('slow')
    piecesBoard.destroy()
}

function setPiecesOk() {
    var whiteToMove = $('#white-to-move').attr('checked') ? 'w' : 'b'
    var whiteShortCastling = $('#white-short-castling').attr('checked') ? 'K' : ''
    var whiteLongCastling = $('#white-long-castling').attr('checked') ? 'Q' : ''
    var blackShortCastling = $('#black-short-castling').attr('checked') ? 'k' : ''
    var blackLongCastling = $('#black-long-castling').attr('checked') ? 'q' : ''
    var castling = whiteShortCastling + whiteLongCastling + blackShortCastling + blackLongCastling
    if (castling.length == 0) castling = '-'
    var enPassant = whiteToMove == 'b' ? $('#enpassant-white').val() : $('#enpassant-black').val()
    var halfmoves = $('#half-moves-number').val()
    var fullmoves = $('#full-moves-number').val()

    reset()
    startFen = piecesBoard.fen() + ' ' + whiteToMove + ' ' + castling + ' ' + enPassant + ' ' + halfmoves + ' ' + fullmoves
    board.position(startFen, true)
    game.load(startFen)
    closePosition()
    updateStatus()
}

function setPieces() {
    piecesBoard = ChessBoard('set-pieces-board', {
        dropOffBoard: 'trash',
        sparePieces: true
    });
    $('#fb-close-Position').click(close)
    $('#fb-modal-Position').fadeIn('slow')
    $('img').last().css('z-index', 102)
    $('#pos-settings-btn').css('left')
}

function moveBtnClick() {
    getBestMoveFromServer()
}

function showLoadPgnBlock() {
    //$('#notation-paste-container').show()
    $('#notation-paste-container').css('display', 'flex')
    pgnEl.hide()
}
function showLoadFenBlock() {
    $('#fencontainer').show()
    $('.fen-copy-element').hide()
    $('.fen-paste-element').show()
    $('#fenstringpaste').focus()
}
function showReadFenBlock() {
    $('#fencontainer').show()
    $('.fen-paste-element').hide()
    $('.fen-copy-element').show()
}
function hideFenBlock() {
    $('#fencontainer').hide()
}
function hidePgnBlock() {
    $('#notation-paste-container').val('')
    $('#notation-paste-container').hide()
    pgnEl.show()
}

function positionOk() {
    startFen = board.fen() + ' w - - 0 1'
    game.load(startFen)
    var cfg = {
        draggable: true,
        position: startFen,
        onChange: onChange,
        onDragStart: onDragStart,
        onDrop: onDrop,
        onSnapEnd: onSnapEnd
    }
    $('#board-container').removeClass('shifted-up')
    $('#showMoves').show()
    $('.set-board-btn').hide()
    board = new ChessBoard('board', cfg)
}
function positionSettings() {
    alert('position settings')
}

function changeNotation(_notation) {
    console.log(notation + ' :: ' + $('input.select-notation-input:checked').val())
    $('.clicked-move').each(function() {
        $(this).html(getNotationText($(this).html(), notation, _notation))
    })
    notation = _notation
}

function selectEngine(engine) {
    $('#engine-name').text(engine)
    $('#engine-name').css('color', 'darkgray')
    changeEngineServer(engine)
}

function copyFen() {
    var clipboard = new Clipboard('#fencopybtn')
    clipboard.on('success', function(e) {
        $('#fencopybtn').children().attr('src', '/assets/images/check.png')
        window.getSelection().removeAllRanges();
    })
    clipboard.on('error', function(e) {
        $('#fencopybtn').children().attr('src', '/assets/images/cancel.png')
    })
}

function loadFen() {
    var str = $('#fenstringpaste').val()
    var newstr = str.replace(/\s{2,}/g, ' ')
    newPosition(newstr.trim())
}
function loadPgn() {
    newPositionFromPGN($('#notation-paste').val())
    hidePgnBlock()
}

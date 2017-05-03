function turnSide() {
    board.flip()
}

function newPositionFromPGN(pgn) {
    startFen = 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1'
    game.reset()
    pgn.split(/\d+\.|\s+/).forEach(function(move) {
        if (move.length > 0) {
            var moveRes = game.move(move)
            if (moveRes === null) return 'snapback';
        }
    })
    board.position(game.fen())
    updateStatus()
}

function newPosition(fen) {
    if (fen == undefined) {
        startFen = 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1'
        game.reset()
        board.start()
        updateStatus()
    } else {
        var validateResult = game.validate_fen(fen)
        if (validateResult.valid) {
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

function changeEngine() {
    changeEngineServer()
}

function setAnalysisLines() {
    if (analysis) setAnalysisLinesServer()
}

function setPieces() {
    var board = ChessBoard('board', {
        dropOffBoard: 'trash',
        sparePieces: true
    });
    var offset = $('#board').width() / 8
    $('#board-container').css('bottom', offset + 'px')
    $('#showMoves').hide()
    console.log($('.board-b72b1').width())
    $('#pos-settings-btn').css('left')
    //$('#fb-trigger-Position').click()
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
    alert('position OK')

}
function positionSettings() {
    alert('position settings')
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
    //else newPositionFromPGN(newstr.trim())
}
function loadPgn() {
    console.log('load PGN')
    newPositionFromPGN($('#notation-paste').val())
    hidePgnBlock()
}

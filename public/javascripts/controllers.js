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
    $.get('/changeEngine/' + $('#select-engine').val(), function(data) {
        $('#welcome').html('<span>' + data + '</span>')
    })
}

function setAnalysisLines() {
    if (analysis) $.get('/setAnalysisLines/' + parseInt($('input#analysis-lines').val()))
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
    $('#fenstringpaste').show()
    $('#fenstringpaste').focus()
    //$('button[name="showfen"]').html('Hide FEN')
}
function showReadFenBlock() {
    $('#fencontainer').show()
    $('.fen-paste-element').hide()
    $('#fenstringcopy').show()
    $('#fenstringcopy').focus()
    //$('button[name="showfen"]').html('Hide FEN')
}
function hideFenBlock() {
    $('#fencontainer').hide()
    $('#notation-paste-container').hide()
}
function hidePgnBlock() {
    $('#notation-paste-container').val('')
    $('#notation-paste-container').hide()
    pgnEl.show()
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

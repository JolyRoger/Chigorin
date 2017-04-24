function turnSide() {
    board.flip()
    //newPosition('5nk1/2p5/1p3R2/2b1NNp1/p3Pp2/2P2P2/6r1/3K4 w - - 0 1')
}

function newPosition(fen) {
    if (fen == undefined) {
        game.reset()
        board.start()
    } else {
        game.load(fen)
        board.position(fen)
    }
    newPositionServer(updateStatus)
}

function showFen() {
    showFenBlock(! $('#fencontainer').is(':visible'))
}

function readLoadFen() {
    var fenstr = $('#fencontainer')
    if (fenstr.is(':visible')) {
        showCopyPaste($('#fenstringcopy').is(':visible'))
    } else {
        fenstr.show()
        $('button[name="showfen"]').html('Hide FEN')
        showCopyPaste(true)
    }
}

function moveBtnClick() {
    if (analysis) {
        continueAnalysis(true, function(move) {
            doMove(move)
            clearAndAnalyse()
        })
    } else {
        getBestMoveFromServer()
    }
}



















function showFenBlock(enable) {
    if (enable) {
        $('button[name="showfen"]').html('Hide FEN')
        $('#fencontainer').show()
    } else {
        showCopyPaste(false)
        $('button[name="showfen"]').html('Show FEN')
        $('#fencontainer').hide()
    }
}

function showCopyPaste(enable) {
    if (enable) {
        $('.fen-copy-element').hide()
        $('.fen-paste-element').show()
        $('button[name="loadfen"]').html('Read FEN')
        $('#fenstringpaste').focus()
    } else {
        $('.fen-paste-element').hide()
        $('.fen-copy-element').show()
        $('button[name="loadfen"]').html('Load FEN')
    }
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

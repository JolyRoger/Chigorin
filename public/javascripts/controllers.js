function turnSide() {
    board.flip()
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

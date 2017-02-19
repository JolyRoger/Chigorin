var counter = 0

function getFenFromPosition() {
    var k = 0
    var out = ''
    var abcdefgh = 'abcdefgh'
    for(j = 8; j>0; j--) {
        if (k != 0) { out += k; k=0 }
        if (out.length != 0) out += '/'
        for(i = 0; i<abcdefgh.length; i++) {
            var piece = Squares.get(abcdefgh[i],j).piece
            if (piece !== undefined) {
                if (k != 0) { out += k; k=0 }
                out += (piece.isWhite ? piece.type.toUpperCase() : piece.type)
            } else {
                k++
            }
        }
    }
    if (k != 0) { out += k; k=0 }
    var castling = (whiteCastling + blackCastling).replace('-', '')

    out += ' ' + (whiteToMove ? 'w' : 'b')
    out += ' ' + (castling == '' ? '-' : castling)
    out += ' ' + enPassantSquare
    out += ' ' + halfmoveCounter
    out += ' ' + fullmoveCounter
    return out
}

function setPositionFromFen(fen) {
    fenFinished = false
    var	horIndex = 0
    var	verIndex = 0
    var k = 0
    fen = fen.trim()
    Pieces.length = 0

    for(i=0; !fenFinished && i < fen.length; i++) {
        var currentChar = fen[i];
        var digit = parseInt(currentChar)
        if (!isNaN(digit)) {
            verIndex += digit;
        } else if (currentChar == '/') {
            horIndex++
            verIndex = 0
        } else if (currentChar == ' ') {
            fenFinished = true;
        } else {
            var isWhite = currentChar == currentChar.toUpperCase()
            Pieces[k] = new Piece(currentChar.toLowerCase(), isWhite)
            Pieces[k].setPosition(String.fromCharCode(verIndex+97), 8 - horIndex)
            k++; verIndex++
        }
    }
}

function getCastlingFromFen(fenCastling, forWhite) {
    var kingSymbol = forWhite ? 'K' : 'k'
    var queenSymbol = forWhite ? 'Q' : 'q'
    var out = ""
    if (fenCastling.indexOf(kingSymbol) > -1) out += kingSymbol
    if (fenCastling.indexOf(queenSymbol) > -1) out += queenSymbol
    if (fenCastling == "") return '-'; else return out
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
        $('button[name="showfen"]').html('Hide FEN Block')
        showCopyPaste(true)
    }
}

function showCopyPaste(enable) {
    if (enable) {
        $('.fen-copy-element').hide()
        $('.fen-paste-element').show()
        $('button[name="loadfen"]').html('Read Board FEN')
        $('#fenstringpaste').focus()
    } else {
        $('.fen-paste-element').hide()
        $('.fen-copy-element').show()
        $('button[name="loadfen"]').html('Load FEN')
    }
}
function showFenBlock(enable) {
    if (enable) {
        $('button[name="showfen"]').html('Hide FEN Block')
        $('#fencontainer').show()
    } else {
        showCopyPaste(false)
        $('button[name="showfen"]').html('Show FEN Block')
        $('#fencontainer').hide()
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

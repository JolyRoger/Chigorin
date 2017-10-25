function normalize(text) {
    function replacer(str, offset, s) {
        var strArr = str.split("")
        return strArr[0] + 'x' + strArr[1]
    }
    return text.replace(/[хХ]/, 'x')/*.replace(/[оО0]/g, 'O')*/.replace(/[!\?]/g, '').
        replace(/^[abcdefgh]{2}/, replacer)
}

function getNotationText(text, _from, _to) {
    var from = _from == undefined ? notation : _from
    var to = _to == undefined ? 0 : _to
    var out = normalize(text)
    if (from == to) return out

    var notFrom = [['K', 'Q', 'R', 'B', 'N'],
                   ['Кр', 'Ф', 'Л', 'С|C', 'К|K'],
                    ['♔', '♕', '♖', '♗', '♘']]

    var notTo = [['K', 'Q', 'R', 'B', 'N'],
                ['Кр', 'Ф', 'Л', 'С', 'К'],
                ['♔', '♕', '♖', '♗', '♘']]

    for (var i=0; i<notFrom[from].length; i++) {
        var newOut = out.replace(new RegExp(notFrom[from][i]), notTo[to][i])
        if (out != newOut) return newOut
    }
    return out
}

function convertPgn(pgn, from, to) {
    var _from = from == undefined ? notation : from
    var _to = to == undefined ? 0 : to
    var elements = pgn.split(/\s/)
    for (var i=0; i<elements.length; i++) {
        elements[i] = getNotationText(elements[i], _from, _to)
    }
    return elements.join(' ')
}

function getCheckedValue(element) {
    return parseInt(element.children('input').filter(function() {
        return $(this).prop('checked')
    }).attr('value'))
}

function pgnToFen(pgn) {
    if (!pgn) return startFen
    var chess = new Chess()
    chess.load(startFen)
    doPgnMoves(chess, pgn)
    var fen = chess.fen()
    chess = null
    return fen
}

function getFenFromNotation() {
    var fen = pgnEl.children('.last-move').attr('fen')
    return fen ? fen : startFen
}

function getPgnFromNotation(_element) {
    var element = _element == undefined ? pgnEl : _element
    return element.children('span:not(.gray-move)').text()
}

function doMoves(chess, movesArr, f) {
    movesArr.forEach(function(move) {
        var moveRes = chess.move(move)
        if (moveRes === null) return 'snapback';
        f && (typeof f === 'function') && f()
    })
}

function doPgnMoves(chess, pgn) {
    doMoves(chess, getMoveArrayFromPgn(pgn))
}

function loadFenPgn(game, fen, pgn) {
    game.load(fen)
    doPgnMoves(game, pgn)
}

function getMoveArrayFromPgn(pgn) {
    return pgn.split(/\d+\.|\s+|\.+/).filter(function(element) {
        return element.length > 0
    })
}

function doSmth() {
    console.log('do smth')
}
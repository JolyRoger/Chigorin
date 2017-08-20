function normalize(text) {
    return text.replace(/[хХ]/, 'x').replace(/[оО0]/g, 'O').replace(/[!\?]/g, '')
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

function getFenFromNotation(element) {
    return pgnToFen(getPgnFromNotation(element))
}

function getPgnFromNotation(element) {
    var _element = element == undefined ? pgnEl : element
    var out = ''
    var movesElements = _element.children()
    var lastMove = parseInt(_element.children('.last-move').attr('value'))
    movesElements.each(function() {
        if (parseInt($(this).attr('value')) <= lastMove)
            out += $(this).html().replace('&nbsp;', ' ')
    })
    return out
}

function doPgnMoves(chess, pgn) {
    pgn.split(/\d+\.|\s+/).forEach(function(move) {
        if (move.length > 0) {
            var moveRes = chess.move(getNotationText(move, notation, 0))
            if (moveRes === null) return 'snapback';
        }
    })
}

function loadFenPgn(game, fen, pgn) {
    game.load(fen)
    doPgnMoves(game, pgn)
}

function doSmth() {
    console.log('do smth')
}
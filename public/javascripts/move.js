function doMove(move) {
    var from = move[0] + move[1]
    var to = move[2] + move[3]
    game.move({ from: from, to: to,
        promotion: move[4] == undefined ? 'q' : move[4] })

    var fen = game.fen()
    board.position(fen)
    hideThinking()
    addClickToLastMove(fen)
    updateStatus()
    if (!analysis && getCheckedValue($('#players')) == 0 && !game.in_checkmate()) getBestMoveFromServer()
}

function doAnalysisMoves() {
    var analPgn = getPgnFromNotation($('#a-cont' + analIndex))
    var notationPgn = getPgnFromNotation()
    var gamePgnArr = getMoveArrayFromPgn(analPgn)
    game.load(getFenFromNotation())
    deleteGrayMoves()
    doMoves(game, gamePgnArr, addClickToLastMove)
}

function addClickToLastMove(fen) {
    function setMoveNumber(dots) {
        lastValue += 1
        var $moveNumber = $('<span>').attr('value', lastValue)
        $moveNumber.html(moveNumber + dots)
        pgnEl.append($moveNumber)
    }

    var pgnNodes = pgnEl.children()
    var pgnArr = game.pgn().trim().split(/\s+/)
    var move = pgnArr[pgnArr.length - 1]
    var newFen = fen ? fen : game.fen()

    var newFenArr = newFen.split(/\s+/)
    var whiteMoves = newFenArr[1] == 'b'                    // Because of it is new fen. Prev move was of opposite side.
    var moveNumber = parseInt(newFenArr[5])
    moveNumber = whiteMoves ? moveNumber : moveNumber - 1   // Because this is new fen and new move number for white. We should show move number of already done move.
    var lastValue = parseInt(pgnNodes.last().attr('value'))
    if (!lastValue) lastValue = 0

    if (whiteMoves) setMoveNumber('.&nbsp;')
     else if (pgnNodes.size() == 0)
        setMoveNumber('.&nbsp;...&nbsp;')

    var $move = $('<span>').attr('value', lastValue + 1)
    $move.attr('fen', newFen)
    $('.last-move').removeClass('last-move')
    $move.addClass('clicked-move').addClass('last-move')
    $move.click(function() {
        clickMove(this, pgnEl);
        unfix();
    })
    $move.html(move + ' ')
    pgnEl.append($move)
}

function moveBeginShow() {
    game.load(startFen)
    pgnEl.children().addClass('gray-move')
    $('.last-move').removeClass('last-move')
    board.position(startFen)
    fenEl.html(startFen)
}

function moveDownShow() {
    var $prevMoves = pgnEl.children('.clicked-move.last-move').prevAll('span.clicked-move')
    if ($prevMoves && $prevMoves.length > 0) $prevMoves[0].click()
    else moveBeginShow()
}

function moveUpShow() {
    var $nextMoves = pgnEl.children('span.clicked-move.last-move').nextAll('span.clicked-move.gray-move')
    if ($nextMoves && $nextMoves.length > 0) $nextMoves[0].click()
    else {
        var $first = pgnEl.children('span.clicked-move.gray-move').first()
        $first && $first.length > 0 && $first[0].click()
    }
}

function moveEndShow() {
    pgnEl.children('.clicked-move').last()[0].click()
}

var moveMap = []

function clickToMove(pgn, clickFunction, space) {
    var pgnArr =  pgn.split(/\s+/g)
    var chess = new Chess()
    chess.load(startFen)
    var fen = ""

    pgnArr.forEach(function(element, i, arr) {
        if (/\.+/.test(element)) pgnArr[i] = '<span value="' + i + '">' + element + '&nbsp;</span>'
        else if (/[a-zA-Z][^\s\.]+/g.test(element)) {
            chess.move(element)
            fen = chess.fen()
            moveMap[i] = fen
            pgnArr[i] = '<span value="' + i + '" class="clicked-move" onclick="' + clickFunction + '">' +
                getNotationText(element, 0, notation) + (space == undefined ? '' : space) + '</span>'
        }
    })
    chess = undefined
    return pgnArr.join('')
}

function deleteGrayMoves() {
    pgnEl.children('.gray-move').remove()
}

function clickMove(element, $target, _fen) {
    $target.children('.last-move').removeClass('last-move')
    $(element).addClass('last-move')
    var lastChildIndex = parseInt($(element).attr('value'))
    $target.children().each(function() {
        if (parseInt($(this).attr('value')) > lastChildIndex) $(this).addClass('gray-move')
        else $(this).removeClass('gray-move')
    })

    var fen = _fen ? _fen : $(element).attr('fen')
    board.position(fen)
    game.load(fen)
    fenEl.html(fen)
}

function hideThinking() {
    pgnEl.children('#thinking').remove()
}

function showThinking() {
    if (analysis || $('#thinking').length) return
    pgnEl.append('<img id="thinking" src="/assets/images/thinking.gif">')
}
function doMove(move) {
    var from = move[0] + move[1]
    var to = move[2] + move[3]
    var move = { from: from, to: to,
        promotion: move[4] == undefined ? 'q' : move[4] }
    game.move(move)
    board.position(game.fen())
    updateStatus()
    if (!analysis && getCheckedValue($('#players')) == 0) getBestMoveFromServer()
}

function moveShow(calculateMove, indexExitCriteria) {
    var allMovesLength = pgnEl.children().size()
    var valueIndex = parseInt(pgnEl.children('.clicked-move.last-move').attr('value')) || 0
    var prevMove

    do {
        valueIndex = calculateMove(valueIndex)
        prevMove = pgnEl.children('.clicked-move[value="' + valueIndex + '"]')
    } while(!/[a-zA-Z][^\s\.]+/g.test(prevMove.text().trim()) && indexExitCriteria(valueIndex, allMovesLength))

    prevMove && prevMove.length > 0 && prevMove[0].onclick(this)
}

function moveBeginShow() {
    game.load(startFen)
    pgnEl.children().each(function() {
        $(this).addClass('gray-move')
    })
    $('.last-move').removeClass('last-move')
    board.position(game.fen())
}

function moveDownShow() {
    moveShow(
        function(valueIndex) { return valueIndex - 1 },
        function(valueIndex, movesLength) { return valueIndex > 0 })
}

function moveUpShow() {
    moveShow(
        function(valueIndex) { return valueIndex + 1 },
        function(valueIndex, movesLength) { return valueIndex < movesLength-1 })
}

function moveEndShow() {
    pgnEl.children('.clicked-move').last()[0].onclick()
}

function clickToMove(pgn) {
    var pgnArr =  pgn.split(/\s+/g)
    pgnArr.forEach(function(element, i, arr) {
        if (/\.+/.test(element)) pgnArr[i] = '<span value="' + i + '">' + element + '&nbsp;</span>'
        else if (/[a-zA-Z][^\s\.]+/g.test(element))
            pgnArr[i] = '<span value="' + i + '" class="clicked-move" onclick="clickMove(this)">' + element + ' </span>'
    })
    return pgnArr.join('')
}

function clickMove(element) {
    game.load(startFen)
    $('.last-move').removeClass('last-move')
    $(element).addClass('last-move')
    pgnEl.children().each(function() {
        var lastChildIndex = parseInt(pgnEl.children('.last-move').attr('value'))
        if (parseInt($(this).attr('value')) > lastChildIndex) $(this).addClass('gray-move')
        else $(this).removeClass('gray-move')
    })

    var moveNumber = parseInt($(element).attr('value'))
    pgnEl.children('.clicked-move').each(function() {
        var curN = parseInt($(this).attr('value'))
        if (curN <= moveNumber) game.move($(this).text().trim())
    })
    board.position(game.fen())
}

function showThinking(show) {
    if (analysis || $('#thinking').length) return
    $('#notation-show').append('<img id="thinking" src="/assets/images/thinking.gif">')
}
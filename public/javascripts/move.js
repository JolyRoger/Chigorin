function doMove(move) {
    var from = move[0] + move[1]
    var to = move[2] + move[3]
    var move = { from: from, to: to,
        promotion: move[4] == undefined ? 'q' : move[4] }
    game.move(move)
    board.position(game.fen())
    updateStatus()
}

function moveShow(calculateMove, indexExitCriteria) {
    var allMovesLength = $('#notation').children().size()
    var valueIndex = parseInt($('#notation').children('.clicked-move.last-move').attr('value'))
    var prevMove

    do {
        valueIndex = calculateMove(valueIndex)
        prevMove = $('#notation').children('.clicked-move[value="' + valueIndex + '"]')
    } while(!/[a-zA-Z][^\s\.]+/g.test(prevMove.text().trim()) && indexExitCriteria(valueIndex, allMovesLength))

    prevMove && prevMove.length > 0 && prevMove[0].onclick(this)
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

function clickToMove(pgn) {
    var i = 0
    var pgnArr =  pgn.split(/\s+/g)
    pgnArr.forEach(function(element, i, arr) {
        if (/\.+/.test(element)) pgnArr[i] = '<span value="' + i + '">' + element + '&nbsp;</span>'
        else if (/[a-zA-Z][^\s\.]+/g.test(element))
            pgnArr[i] = '<span value="' + i + '" class="clicked-move" onclick="clickMove(this)">' + element + ' </span>'
    })
    var out = pgnArr.join('')
    return out
}

function clickMove(element) {
    game.load(startFen)
    $('.last-move').removeClass('last-move')
    $(element).addClass('last-move')
    var movesToGray = $('#notation').children().each(function(index) {
        var lastChildIndex = parseInt($('#notation').children('.last-move').attr('value'))
        if (parseInt($(this).attr('value')) > lastChildIndex) $(this).addClass('gray-move')
        else $(this).removeClass('gray-move')
    })

    var moveNumber = parseInt($(element).attr('value'))
    $('#notation').children('.clicked-move').each(function(index) {
        var curN = parseInt($(this).attr('value'))
        if (curN <= moveNumber) game.move($(this).text().trim())
    })
    board.position(game.fen())
}
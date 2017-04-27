function doMove(move) {
    var from = move[0] + move[1]
    var to = move[2] + move[3]
    var move = { from: from, to: to,
        promotion: move[4] == undefined ? 'q' : move[4] }
    game.move(move)
    board.position(game.fen())
    updateStatus()
}

function moveDownShow() {
    //var lastChildValue = parseInt($('#notation').children('.last-move').attr('value'))
    //var prev = $('#notation').find('.clicked-move')[lastChildValue-1]
    //if (prev) prev.onclick(this)
    //var moveArr = $('#notation').children('.clicked-move')
    var valueIndex = parseInt($('#notation').children('.clicked-move.last-move').attr('value'))
    var prevMove

    do {
        prevMove = $('#notation').children('.clicked-move[value="' + (--valueIndex) + '"]')
    } while(!/[a-zA-Z][^\s\.]+/g.test(prevMove.text().trim()) && valueIndex > 0)

    prevMove[0].onclick(this)

    //var prev = $('#notation').children('.last-move').prev('.clicked-move')
    //if (prev) prev[0].onclick(this)
//	var $spanArray = $('div#notation').find('span').not('.toDelete')
//	spanIndex = $spanArray.index($spanArray.last()) - 1
//	if (spanIndex >= 0) $spanArray[spanIndex].click()
//    continueAnalysis(clearAndAnalyse)
}

function moveUpShow() {
    //var lastChildValue = parseInt($('#notation').children('.last-move').attr('value'))
    //var next = $('#notation').find('span.clicked-move[value=' + + ']')

    //var next = $('#notation').children('.last-move').next('.clicked-move')
    //if (next) next[0].onclick(this)
    var allMovesLength = $('#notation').children('.clicked-move').size()
    var valueIndex = parseInt($('#notation').children('.clicked-move.last-move').attr('value'))
    var prevMove

    do {
        prevMove = $('#notation').children('.clicked-move[value="' + (++valueIndex) + '"]')
    } while(!/[a-zA-Z][^\s\.]+/g.test(prevMove.text().trim()) && valueIndex < allMovesLength-1)

    prevMove[0].onclick(this)


//	var $spanArray = $('div#notation').find('span.toDelete')
//	if ($spanArray.length > 0) $spanArray[0].click()
//    continueAnalysis(clearAndAnalyse)
}

function clickToMove(pgn) {
    var i = 0
    var pgnArr =  pgn.split(/\s+/g)
    pgnArr.forEach(function(element, i, arr) {
        if (/\.+/.test(element)) pgnArr[i] = '<span value="' + i + '">' + element + '&nbsp;</span>'
        else if (/[a-zA-Z][^\s\.]+/g.test(element))
            pgnArr[i] = '<span value="' + i + '" class="clicked-move" onclick="clickMove(this)">' + element + ' </span>'
        //console.log(element + ' :: ' + i + ' ::' + arr)
    })
    var out = pgnArr.join('')

    //console.info(out)
    return out
    //var pgnHtml =  pgn.replace(/[a-zA-Z][^\s\.]+/g, function (moveStr) {
    //    return '<span value="' + (i++) + '" class="clicked-move" onclick="clickMove(this)">' + moveStr + '</span>'
    //})
    //
    //return pgnHtml
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
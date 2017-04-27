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
    var lastChildValue = parseInt($('#notation').children('.last-move').attr('value'))
    var prev = $('#notation').find('.clicked-move')[lastChildValue-1]
    if (prev) prev.onclick(this)
//	var $spanArray = $('div#notation').find('span').not('.toDelete')
//	spanIndex = $spanArray.index($spanArray.last()) - 1
//	if (spanIndex >= 0) $spanArray[spanIndex].click()
//    continueAnalysis(clearAndAnalyse)
}

function moveUpShow() {
    var lastChildValue = parseInt($('#notation').children('.last-move').attr('value'))
    var next = $('#notation').find('.clicked-move')[lastChildValue+1]
    if (next) next.onclick(this)

//	var $spanArray = $('div#notation').find('span.toDelete')
//	if ($spanArray.length > 0) $spanArray[0].click()
//    continueAnalysis(clearAndAnalyse)
}

function clickToMove(pgn) {
    var i = 0
    var pgnHtml =  pgn.replace(/[a-zA-Z][^\s\.]+/g, function (moveStr) {
        return '<span value="' + (i++) + '" class="clicked-move" onclick="clickMove(this)">' + moveStr + '</span>'
    })

    return pgnHtml
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
    $('#notation').children().each(function(index) {
        var curN = parseInt($(this).attr('value'))
        if (curN <= moveNumber) game.move($(this).text())
    })
    board.position(game.fen())
}
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
	var $spanArray = $('div#notation').find('span').not('.toDelete')
	spanIndex = $spanArray.index($spanArray.last()) - 1
	if (spanIndex >= 0) $spanArray[spanIndex].click()
    continueAnalysis(clearAndAnalyse)
}

function moveUpShow() {
	var $spanArray = $('div#notation').find('span.toDelete')
	if ($spanArray.length > 0) $spanArray[0].click()
    continueAnalysis(clearAndAnalyse)
}
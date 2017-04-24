function doMove(move) {
    var from = move[0] + move[1]
    var to = move[2] + move[3]
    board.move(from + '-' + to)
    var move = { from: from, to: to,
        promotion: move[4] == undefined ? 'q' : move[4] }
    console.log('Move: ' + move.from + ' - ' + move.to + ', ' + move.promotion)
    game.move(move)
    updateStatus()
}
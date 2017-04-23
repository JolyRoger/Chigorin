function getBestMoveFromServer() {
    $.ajax({
        url: '/next/' + true,
        data: JSON.stringify({history: '', fen: game.fen()}),
        type: 'POST',
        contentType: 'application/json',
        success: function (json) {
            doMove(json.bestmove)
        }
    })
}

function newPositionServer(success) {
    $.ajax({
        url : '/new/' + encodeURIComponent(game.fen()),
        dataType: "json",
        contentType: 'application/json',
        success: success
    })
}

function initEngineServer(success) {
    $.get('/initEngine/' + 3, success)
}

function startAnalysisServer(success) {
    $.get('/startAnalysis/' + encodeURIComponent(game.fen()), success)
}
function analysisServer(success) {
    $.getJSON('/analysis', success)
}

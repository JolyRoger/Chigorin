function getBestMoveFromServer() {
    deleteGrayMoves()
    showThinking()
    $.ajax({
        url: '/next/',
        data: JSON.stringify(
            { fen: analysis ? '' : game.fen(),
              analysis: analysis,
              time: analysis ? '' : parseInt($('input#pondering-time').val()) }),
        type: 'POST',
        contentType: 'application/json',
        success: function (move) {
            doMove(move)
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
    var time = parseInt($('input#pondering-time').val())
    var lines = parseInt($('input#analysis-lines').val())
    $.get('/initEngine/' + time + '/' + lines, success)
}

function startAnalysisServer(success) {
    $.get('/startAnalysis/' + encodeURIComponent(game.fen()), success)
}
function analysisServer(success) {
    $.getJSON('/analysis', success)
}

function setAnalysisLinesServer() {
    $.get('/setAnalysisLines/' + parseInt($('input#analysis-lines').val()))
}

function changeEngineServer(engine) {
    $.get('/changeEngine/' + engine, function(data) {
        $('#engine-name').css('color', 'currentColor')
    })
}

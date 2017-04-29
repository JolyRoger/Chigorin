function getBestMoveFromServer() {
    var time = parseInt($('#ponderTime').children('input').filter(function() {
        return $(this).prop('checked')
    }).attr('value'))

    $.ajax({
        url: '/next/',
        data: JSON.stringify(
            { fen: analysis ? '' : game.fen(),
              analysis: analysis,
              time: analysis ? '' : time }),
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
    $.get('/initEngine/' + 3, success)
}

function startAnalysisServer(success) {
    $.get('/startAnalysis/' + encodeURIComponent(game.fen()), success)
}
function analysisServer(success) {
    $.getJSON('/analysis', success)
}

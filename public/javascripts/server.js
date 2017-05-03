function getBestMoveFromServer() {
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
function showThinking(show) {
    if (analysis || $('#thinking').length) return
    $('#notation-show').append('<img id="thinking" src="/assets/images/thinking.gif">')
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

var analysis = false
var intervalId


function transform(pv) {

    var fen = game.fen()
    var varArr = new Chess(fen)

    pv.forEach(function(move, i, arr) {
        move = move.trim()
        var from = move[0] + move[1]
        var to = move[2] + move[3]
        varArr.move({ from: from, to: to })
    })

    return varArr.pgn( { with_header: false, pgn_move_number: parseInt(fen.split(' ')[5]) } )
}

function stopAnalysis(f) {
    $.get("/stopAnalysis/false", f)
}

function continueAnalysis() {
    $.get("/stopAnalysis/true", clearAndAnalyse)
}

function clearAndAnalyse() {
    clearInterval(intervalId)
    analysePosition()
}

function createAnal(index, value) {
    var avariant = $('<span>').attr('id', 'a-variant' + index).addClass('a-variant')
    var amoven = $('<span>').attr('id', 'a-moven' + index).addClass('a-moven')
    var abest = $('<span>').attr('id', 'a-best' + index).addClass('a-best')
    var aconttitle = $('<span>').attr('id', 'a-cont-title' + index).addClass('a-cont-title')
    var acont = $('<span>').attr('id', 'a-cont' + index).addClass('a-cont')
    var ascoretitle = $('<span>').attr('id', 'a-score-title' + index).addClass('a-score-title')
    var ascore = $('<span>').attr('id', 'a-score' + index).addClass('a-score')

    var scoreTypeCp = value.scoreType == 'cp'
    var sc = scoreTypeCp ? parseInt(value.score) / 100 : value.score
    var pv = value.pv.slice(1, -1)
    var newPv = transform(pv.split(','))
    var best = newPv.match(/[a-zA-Z][^\s\.]+/)

    amoven.html('Move №' + (index+1) + ':&nbsp;')
    abest.html(best + '&emsp;')
    ascoretitle.html('Score:&nbsp;')
    ascore.html(scoreTypeCp ? (game.turn() === 'b' ? -sc : sc) + '<br>' : ('Mate in ' + Math.abs(sc) + '<br>'))
    aconttitle.html('Variant:&nbsp;')
    acont.html(newPv + '<br>')

    avariant.append(amoven)
    avariant.append(abest)
    avariant.append(ascoretitle)
    avariant.append(ascore)
    avariant.append(aconttitle)
    avariant.append(acont)

    return avariant
}

function startAnalysis() {
    if (analysis) {
        stopAnalysis(function() {
            $('#analysis').html('')
            $('#a-start').attr('src', '/assets/images/analoff.png')
            $('.fix-analysis-btn').hide()
            analysis = false
        })
        return
    }
    analysis = true
    $('.fix-analysis-btn').show()
    $('#a-start').attr('src', '/assets/images/analon.png')
    analysePosition()
}

function analysePosition() {
    startAnalysisServer(function() {
        intervalId = setInterval(analysisServer, 1000, function(result) {
            $('#analysis').html("")
            if (!analysis) {
                clearInterval(intervalId)
            } else {
                $.each(result, function(index, value) {
                    var cmp = createAnal(index, value)
                    $('#analysis').append(cmp.html() + '<br>')
                });
            }
        })
    })
}

function fixAnalysis() {

}

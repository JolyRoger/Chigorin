var analysis = false
var intervalId

function doSmth() {
    console.log('do smth')
    //var pgn1 = '7. Re1+ Be7 8. Nbd2 Nf6 9. h3 O-O 10. Nb3 Bd6 11. Nbxd4 Re8 12. Rxe8+ Qxe8 13. Be3 Bd7'
    //var pgn2 = '7. ... Be7 8. Nbd2 Nf6 9. h3 O-O 10. Nb3 Bd6 11. Nbxd4'
}

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

function continueAnalysis(doMove, f) {
    $.get("/stopAnalysis/" + doMove, f)
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

    var sc = parseInt(value.score) / 100
    var pv = value.pv.slice(1, -1)
    var newPv = transform(pv.split(','))
    var best = newPv.match(/[a-zA-Z][^\s\.]+/)

    amoven.html('Move №' + (index+1) + ':&nbsp;')
    abest.html(best + '&emsp;')
    ascoretitle.html('Score:&nbsp;')
    ascore.html((game.turn() === 'b' ? -sc : sc) + '<br>')
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
        continueAnalysis(false, function() {
            $('#analysis').html('')
            $('#a-start').attr('src', '/assets/images/analoff.png')
            analysis = false
        })
        return
    }
    analysis = true
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

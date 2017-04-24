var analysis = false
var intervalId

function doSmth() {
    console.log('do smth')
    //game.load('5nk1/2p5/1p3R2/2b1NNp1/p3Pp2/2P2P2/6r1/3K4 b - - 10 15')
    game.load('r1bqk2r/ppp2ppp/1bnp1n2/4p3/4P3/1BNP1N2/PPP2PPP/R1BQK2R w KQkq - 2 7')

    //var str = "g1f3, e7e6, d2d4, d7d5, c2c4, g8f6, b1c3, f8b4, e2e3, c7c5, f1e2, e8g8, e1g1, b8c6, d4c5, b4c5, d1d3, d5c4, d3c4, c5b6, f1d1, d8e7, c1d2, f8d8"
    //var str = "g8h7, f6f7, h7h8"
    var str = "0-0"
    var move = { from: 'e1', to: 'g1', promotion: 'q'}
    console.log('Move: ' + move.from + ' - ' + move.to + ', ' + move.promotion)
    game.move(move)
    board.position(game.fen())
    updateStatus()
    var arr = str.split(',')
    console.log('transformed: ' + transform(arr))
}

function transform(pv) {

    var fen = game.fen()
    var varArr = new Chess(fen)

    pv.forEach(function(move, i, arr) {
        move = move.trim()
        var from = move[0] + move[1]
        var to = move[2] + move[3]
        varArr.move({ from: from, to: to })

        //console.log(i + ": " + item + " (массив:" + arr + ")" )
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
    var acont = $('<span>').attr('id', 'a-cont' + index).addClass('a-cont')
    var ascore = $('<span>').attr('id', 'a-score' + index).addClass('a-score')

    var sc = parseInt(value.score) / 100
    var pv = value.pv.slice(1, -1)
    var best = pv.substring(0, pv.indexOf(','))
    var continuation = pv.substring(best.length + 1, pv.length)

    var newPv = transform(pv.split(','))

    //console.log('pv: ' + pv)
    //console.log('newPv: ' + newPv)

    amoven.html('Move №' + (index+1) + ':&nbsp;')
    abest.html(pv.substring(0, pv.indexOf(',')) + '&emsp;')
    ascore.html('Score: ' + (game.turn() === 'b' ? -sc : sc) + '<br>')
    acont.html('Variant: ' + newPv + '<br>')

    avariant.append(amoven)
    avariant.append(abest)
    avariant.append(ascore)
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

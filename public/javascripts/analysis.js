var analysis = false
var fix = false
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
    var acont = $('<span>').attr('id', 'a-cont' + index).addClass('a-cont').attr('value', index)
    var ascoretitle = $('<span>').attr('id', 'a-score-title' + index).addClass('a-score-title')
    var ascore = $('<span>').attr('id', 'a-score' + index).addClass('a-score')
    ascore.attr('value', index)

    var scoreTypeCp = value.scoreType == 'cp'
    var sc = scoreTypeCp ? parseInt(value.score) / 100 : value.score
    var pv = value.pv.slice(1, -1)
    var newPv = transform(pv.split(','))
    var best = newPv.match(/[a-zA-Z][^\s\.]+/)

    amoven.html('Move №' + (index+1) + ':&nbsp;')
    abest.html(best + '&emsp;')
    ascoretitle.html('Score:&nbsp;')
    ascore.html(scoreTypeCp ? (game.turn() === 'b' ? -sc : sc) : ('Mate in ' + Math.abs(sc)))
    aconttitle.html('<br>Variant:&nbsp;')
    acont.html(newPv)

    avariant.append(amoven)
    avariant.append(abest)
    avariant.append(ascoretitle)
    avariant.append(ascore)
    avariant.append(aconttitle)
    avariant.append(acont)
    avariant.append('<br>')

    return avariant
}

function startAnalysis() {
    if (analysis) {
        stopAnalysis(function() {
            $('#analysis').html('')
            $('#a-start').attr('src', '/assets/images/analoff.png')
            $('.fix-analysis-btn').hide()
            fix = analysis = false
        })
        return
    }
    analysis = true
    $('.fix-analysis-btn').html('Fix')
    $('.fix-analysis-btn').show()
    $('#a-start').attr('src', '/assets/images/analon.png')
    analysePosition()
}

function analysePosition() {
    startAnalysisServer(function() {
        intervalId = setInterval(analysisServer, 1000, function(result) {
            if (!fix) $('#analysis').html("")
            if (!analysis) {
                clearInterval(intervalId)
            } else {
                if (!fix)
                    $.each(result, function(index, value) {
                        var anal = createAnal(index, value)
                        $('#analysis').append(anal.html() + '<br>')
                    });
            }
        })
    })
}

function fixAnalysis() {
    fix ? unfixAndSetPosition() : fixAnal()
}

function fixAnal() {
    fix = true
    $('.fix-analysis-btn').html('Unfix')
    $('.a-best').each(function() {
        $(this).html(clickToMove($(this).html(), 'analMove(this)', '&emsp;'))
    })
    $('.a-cont').each(function() {
        $(this).html(clickToMove($(this).html(), 'analVariantMove(this, ' + $(this).attr('value') + ')', ' '))
    })
    showMoveVariantButtons()
}

function showMoveVariantButtons() {
    function createButton(action, sign, index) {
        var $btn = $('<a>')
        $btn.attr('id', 'mv-' + action)
        $btn.attr('href', 'javascript:mv' + action + '(' + index +')')
        $btn.html(sign)
        return $btn
    }
    $('.a-score').each(function() {
        var $btnPanel = $('<span>')
        $btnPanel.css('float', 'right')
        $btnPanel.css('font-size', '146%')

        $btnPanel.append(createButton('left', '&#10232;', $(this).attr("value")))
        $btnPanel.append(createButton('right', '&#10233;', $(this).attr("value")))

        var lines = parseInt($('input#analysis-lines').val())
        $(this).after($btnPanel)
    })
}

function mvleft(index) {
    var $prevMove = $('#a-cont' + index).children('.clicked-move').not('.gray-move').not('.last-move').last()
    if ($prevMove && $prevMove.length > 0) $prevMove[0].onclick(this)
    else {
        $('#a-cont' + index + ' .last-move').addClass('gray-move')
        setPosition()
    }
}

function mvright(index) {
    var $moveElement = $('#a-cont' + index)
    var $nextMove = $moveElement.children('.clicked-move.gray-move').first()
    if ($nextMove && $nextMove.length > 0) $nextMove[0].onclick(this)
    else if ($moveElement.children('.last-move').size() == 0)
        $moveElement.children('.clicked-move')[0].onclick()
}

function unfixAndSetPosition() {
    unfix()
    setPosition()
}

function setPosition(f, arg) {
    loadFenPgn(game, startFen, convertPgn(getPgnFromNotation()))
    f && f(arg)
    board.position(game.fen())
}

function unfix() {
    fix = false
    $('.fix-analysis-btn').html('Fix')
}

function analMove(element) {
    setPosition(game.move, getNotationText($(element).html().trim()))
}

function analVariantMove(element, contIndex) {
    var $cont = $('#a-cont' + contIndex)
    var $contChld = $('.a-cont').children()
    $contChld.removeClass('gray-move')
    $contChld.removeClass('last-move')
    clickMove(element, $cont, getFenFromNotation())
}

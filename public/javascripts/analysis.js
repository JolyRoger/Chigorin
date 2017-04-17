var analysis = false
var intervalId

function continueAnalysis(f) {
    $.get("/stopAnalysis/" + true, f)
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
    var continuation = pv.substring(pv.indexOf(',') + 1, pv.length)

    amoven.html('Move №' + (index+1) + ':&nbsp;')
    abest.html(pv.substring(0, pv.indexOf(',')) + '&emsp;')
    ascore.html('Score: ' + (whiteToMove ? sc : -sc) + '<br>')
    acont.html('Variant: ' + continuation + '<br>')

    avariant.append(amoven)
    avariant.append(abest)
    avariant.append(ascore)
    avariant.append(acont)

    return avariant
}


function startAnalysis() {
    if (analysis) {
        $.get("/stopAnalysis/" + false, function() {
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

function analysePosition() {        // FIXME
    $.get("/startAnalysis/" + encodeURIComponent(getFenFromPosition()), function() {
        intervalId = setInterval($.getJSON, 1000, "/analysis", function(result) {
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

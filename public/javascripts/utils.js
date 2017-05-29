function normalize(text) {
    return text.replace(/[хХ]/, 'x').replace(/[оО0]/g, 'O').replace(/[!\?]/g, '')
}

function getNotationText(text, from, to) {
    var out = normalize(text)
    if (from == to) return out

    var notFrom = [['K', 'Q', 'R', 'B', 'N'],
                   ['Кр', 'Ф', 'Л', 'С|C', 'К|K'],
                    ['♔', '♕', '♖', '♗', '♘']]

    var notTo = [['K', 'Q', 'R', 'B', 'N'],
                ['Кр', 'Ф', 'Л', 'С', 'К'],
                ['♔', '♕', '♖', '♗', '♘']]

    for (var i=0; i<notFrom[from].length; i++) {
        var newOut = out.replace(new RegExp(notFrom[from][i]), notTo[to][i])
        if (out != newOut) return newOut
    }
    return out
}

function convertPgn(pgn, from, to) {
    var elements = pgn.split(/\s/)
    for (var i=0; i<elements.length; i++) {
        elements[i] = getNotationText(elements[i], from, to)
    }
    return elements.join(' ')
}

function getCheckedValue(element) {
    return parseInt(element.children('input').filter(function() {
        return $(this).prop('checked')
    }).attr('value'))
}

function getPgnFromNotation(element) {
    var out = ''
    var movesElements = element.children()
    var lastMove = parseInt(element.children('.last-move').attr('value'))
    movesElements.each(function() {
        if (parseInt($(this).attr('value')) <= lastMove)
            out += $(this).html().replace('&nbsp;', ' ')
    })
    return out
}

function doSmth() {
    console.log('do smth')
    //console.info(getNotationText('1. e4 e5 2. Кf3 Кf6 3. Кxe5 d6 4. Кf3 Кxe4 5. d4 d5 6. Сd3 Сe7', 1, 0))        // en
    console.info(convertPgn('1. e4 e5 2. Кf3 Кf6 3. Кxe5 d6 4. Кf3 Кxe4 5. d4 d5 6. Сd3 Сe7', 1, 0))        // ru
    //clickMove(this, $('#a-cont' 2))
}

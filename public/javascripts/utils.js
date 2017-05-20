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

function doSmth() {
    console.log('do smth')
    console.info(getNotationText('Cg5', 1, 0))        // en
    console.info(getNotationText('Сg5', 1, 0))        // ru
}

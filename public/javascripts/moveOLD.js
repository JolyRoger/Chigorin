
function doFullMove(move) {
    movesHistory = movesHistory + ' ' + move
// It means a promotion has been done by player. Move will be finished later in finishMove function.
    if (doMove(move)) return
    finishMove(move)
}

function doMove(move) {
	var squareFrom = Squares.get(move[0], move[1])
	var squareTo = Squares.get(move[2], move[3])

	if (squareFrom !== undefined) {
		var pieceFrom = squareFrom.getPiece()
		if (pieceFrom !== undefined && squareTo !== undefined) {

            halfmoveCounter += 1
            enPassantSquare = '-'
            whiteToMove = !whiteToMove
            if (whiteToMove) fullmoveCounter += 1

            pawnMove(squareFrom, squareTo)
            updateCastlingState(squareFrom)
            enPassantMove(squareFrom, squareTo)
            captureMove(squareTo)
            castlingMove(squareFrom, squareTo)
            if (!promotionMove(squareFrom, squareTo, move)) {
                doSimpleMove(squareFrom, squareTo)
            } else return true
		}
	}
	return false
}

function moveNow() {
	updatePosition('')
}

function doSimpleMove(squareFrom, squareTo) {
    squareTo.setPiece(squareFrom.getPiece())
    squareFrom.removePiece()

    $('#fencontent').html(getFenFromPosition())
    $('#fencopybtn').children().attr('src', '/assets/images/copy.png')
}

function finishMove(move) {
    addMoveToPage(move, !whiteToMove)
    var fen = getFenFromPosition()
    $.get('/getLegalMoves/' + encodeURIComponent(fen), function(legal) {
        legalMoves = legal.split(' ')
        addMoveToSet(move, legalMoves, getFenFromPosition())
        if (legal == "") {
            gameOver(whiteToMove ? 'BLACK_MATE' : 'WHITE_MATE')
            return
        } else if (parseInt(fen.split(' ')[4]) > 50) {
            gameOver("DRAW")
            return
        }
        if (settings.whoPlay != 'human_human') {
            setEnableButton(false)
            updatePosition(move)
            return
        }
        setEnableButton(true)
    })

}

function getRookForCastling(from, to) {
    function getHalf(firstSquare, from, to) {
        var isw = from.piece.isWhite
        var issh = to.ver.charCodeAt(0) - from.ver.charCodeAt(0) > 0
        var square = firstSquare ? isw ? issh ? Squares.get('h', 1) : Squares.get('a', 1)
								 : issh ? Squares.get('h', 8) : Squares.get('a', 8)
					   : isw ? issh ? Squares.get('f', 1) : Squares.get('d', 1)
								 : issh ? Squares.get('f', 8) : Squares.get('d', 8)
        return square.ver + square.hor
    }
    return getHalf(true, from, to) + getHalf(false, from, to)
}

function pawnMove(squareFrom, squareTo) {
    var pieceFrom = squareFrom.getPiece()

    if (pieceFrom.type == 'p') {                                                                                        // pawn moves
        halfmoveCounter = 0
        if (Math.abs(squareFrom.hor - squareTo.hor) == 2) {
            var squareCandidate1 = Squares.get(String.fromCharCode(squareTo.verAsNum + charCodeOffset + 1), squareTo.hor)
            var squareCandidate2 = Squares.get(String.fromCharCode(squareTo.verAsNum + charCodeOffset - 1), squareTo.hor)
            var pieceFrom = squareFrom.getPiece()
            if (checkEnPassant(squareCandidate1, pieceFrom) || checkEnPassant(squareCandidate2, pieceFrom)) {
                enPassantSquare = squareFrom.ver + (squareFrom.hor + squareTo.hor) / 2
            }
        }
    }

}

function updateCastlingState(squareFrom) {
    var pieceFrom = squareFrom.getPiece()
    if (pieceFrom.type == 'r') {
        if (squareFrom.hor == 1 && whiteCastling != '-') {
            if (squareFrom.ver == 'h' && whiteCastling[0] == 'K') {
                if (whiteCastling == 'KQ') whiteCastling = 'Q'; else whiteCastling = '-'
            }
            if (squareFrom.ver == 'a' && (whiteCastling[0] == 'Q' || whiteCastling[1] == 'Q')) {
                if (whiteCastling == 'KQ') whiteCastling = 'K'; else whiteCastling = '-'
            }
        }
        if (squareFrom.hor == 8 && blackCastling != '-') {
            if (squareFrom.ver == 'h' && blackCastling[0] == 'k') {
                if (blackCastling == 'kq') blackCastling = 'q'; else blackCastling = '-'
            }
            if (squareFrom.ver == 'a' && (blackCastling[0] == 'q' || blackCastling[1] == 'q')) {
                if (blackCastling == 'kq') blackCastling = 'k'; else blackCastling = '-'
            }
        }
    } if (pieceFrom.type == 'k') {
        if (squareFrom.hor == 1) whiteCastling = '-'
        else if (squareFrom.hor == 8) blackCastling = '-'
    }
}

function captureMove(squareTo) {
    var pieceTo = squareTo.getPiece()
    if (pieceTo != undefined) {                                                                                 // capture
        squareTo.removePiece()
        halfmoveCounter = 0
    }
}

function castlingMove(squareFrom, squareTo) {
    var pieceFrom = squareFrom.getPiece()
    if (Math.abs(squareFrom.ver.charCodeAt(0) - squareTo.ver.charCodeAt(0)) > 1 && pieceFrom.type == 'k') {     // castling
        var r = getRookForCastling(squareFrom, squareTo)
        doSimpleMove(Squares.get(r[0], r[1]), Squares.get(r[2], r[3]))
        if (pieceFrom.isWhite) whiteCastling = '-'; else blackCastling = '-'
    }
}

function enPassantMove(squareFrom, squareTo) {
    var pieceFrom = squareFrom.getPiece()
    var pieceTo = squareTo.getPiece()
    if (squareFrom.ver != squareTo.ver && pieceFrom.type == 'p' && pieceTo == undefined) {                      // en passant
        Squares.get(squareTo.ver, squareFrom.hor).removePiece()
    }
}

function promotionMove(squareFrom, squareTo, move) {
    var pieceFrom = squareFrom.getPiece()

    if ((squareTo.hor == 1 || squareTo.hor == 8) && pieceFrom.type == 'p') {									// promotion
        if (move[4] !== undefined) {
            pieceFrom = new Piece(move[4], squareTo.hor == 8)
            squareFrom.removePiece()
            squareFrom.setPiece(pieceFrom)
        } else {
            var promotedPieces = { q: 'queen', r: 'rook', b: 'bishop', n: 'knight' }
            function finishPromotion() {
                sweepPanels()
                isFirstClick = true
                doSimpleMove(squareFrom, squareTo)
                finishMove(move)
                for (var i in promotedPieces) {
                    $('#' + promotedPieces[i]).removeClass((pieceFrom.isWhite ? 'w' : 'b') + promotedPieces[i])
                    $('#' + promotedPieces[i]).off('click')
                }
            }

            $('.promotion').css('display', 'block')
            for (var i in promotedPieces) {
                $('#' + promotedPieces[i]).addClass((pieceFrom.isWhite ? 'w' : 'b') + promotedPieces[i])
//						I don't know why it doesn't want to be called by anonimously (lambda-way), without the variable 'f'
                var f = function(i) {
                    $('#' + promotedPieces[i]).click(function() {
                        squareFrom.removePiece()
                        squareFrom.setPiece(new Piece(i, squareTo.hor == 8))
                        move += i
                        movesHistory += i
                        finishPromotion()
                    })
                }
                f(i)
            }
            return true
        }
    }
    return false
}

function moveDownShow() {
	var $spanArray = $('div#notation').find('span').not('.toDelete')
	spanIndex = $spanArray.index($spanArray.last()) - 1
	if (spanIndex >= 0) $spanArray[spanIndex].click()
    setFenToFencontent()
    continueAnalysis(clearAndAnalyse)
}

function moveUpShow() {
	var $spanArray = $('div#notation').find('span.toDelete')
	if ($spanArray.length > 0) $spanArray[0].click()
    setFenToFencontent()
    continueAnalysis(clearAndAnalyse)
}
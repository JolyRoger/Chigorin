var Moves = []
var Pieces = []
var Squares = []
var isFirstClick = true
var gamerPlaysWhite = true
var whiteIsUp = false
var legalMoves, currentLegalMoves
var startingFen
var fen, currentFen
var moveDisablable = true
var movesHistory = ''
var tmpMovesHistory
var isFen = false
var thinking = false		// true if server is thinking
var playWithFen = false
var START_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
var whiteToMove = true
var whiteCastling = 'KQ'
var blackCastling = 'kq'
var enPassantSquare = '-'
var halfmoveCounter = 0
var fullmoveCounter = 1
var charCodeOffset = 96;
var settings = { showToMove: true, showTimer: false, howLongThink: 1 }

$(function() {
    setPlayers()
})

function Square(_ver, _hor) {
    var piece
    var ver = this.ver = _ver; var hor = this.hor = _hor
    this.verAsNum = ver.charCodeAt(0) - charCodeOffset     // offset for letters a - 97, b - 98, etc.

    var element = $('<div>', {
        id: ver+hor,
        class: 'square'
    })

    $('.board').append(element)
    $(element).click(function() {
        if (thinking) return
        if (isFirstClick) {
            if (settings.whoPlay == 'ai_ai') return
            if(isMovable(ver, hor)) {
                $(this).addClass('clickedFrom')
            } else return

            var legal = getLegalMoves(ver, hor)
            for(i=0; i<legal.length; i++) {
                Squares.get(legal[i][2], legal[i][3]).setClicked(true, false)
            }
        } else {
            if ($(this).hasClass('clickedTo')) {
                doFullMove($('.clickedFrom').attr('id')+ver+hor)    // FIXME
                if (analysis) continueAnalysis(clearAndAnalyse)
            }
            sweepPanels()
        }
        isFirstClick = !isFirstClick
    })
    this.setPiece = function(_piece) {
        piece = this.piece = _piece
        $(element).addClass(piece.getClass())
    }
    this.getPiece = function() { return this.piece }
    this.setClicked = function(isClicked, isFrom) {
        if (isClicked)
            if (isFrom) $(element).addClass('clickedFrom')
            else {
                $(element).addClass('clickedTo')
                if (!settings.showToMove) $('.clickedTo').css('border-color', 'transparent')
            }
        else
        if (isFrom) $(element).removeClass('clickedFrom')
        else {
            $('.clickedTo').css('border-color', '')
            $(element).removeClass('clickedTo')
        }
    }
    this.removePiece = function() {
        if (piece !== undefined) {
            $(element).removeClass(piece.getClass())
            delete piece
            delete this.piece
        }
    }

    this.setLocation = function() {
        $(element).css( {top: ((whiteIsUp ? hor-1 : 8-hor)*56/*+$('.board').position().top*/) + 'px',
            left: ((whiteIsUp ? 104 - ver.charCodeAt(0) : ver.charCodeAt(0) - 97)*56/*+$('.board').position().left*/) + 'px'} )
    }
    this.setLocation()
}

function Piece(type, isWhite) {
    var type
    var isClicked
    this.type = type
    this.isClicked = isClicked = false
    this.isWhite = isWhite
    this.setPosition = function(_ver, _hor) {
        Squares.get(_ver, _hor).setPiece(this)
    }
    this.getClass = function() {
        switch(type) {
            case 'k' :
                return (isWhite ? 'w' : 'b') + 'king'
                break
            case 'q' :
                return (isWhite ? 'w' : 'b') + 'queen'
                break
            case 'r' :
                return (isWhite ? 'w' : 'b') + 'rook'
                break
            case 'b' :
                return (isWhite ? 'w' : 'b') + 'bishop'
                break
            case 'n' :
                return (isWhite ? 'w' : 'b') + 'knight'
                break
            case 'p' :
                return (isWhite ? 'w' : 'b') + 'pawn'
                break
        }
    }
}

function pause(millisecondi) {
    var now = new Date();
    var exitTime = now.getTime() + millisecondi;

    while(true) {
        now = new Date();
        if(now.getTime() > exitTime) return;
    }
}

function unload() {
    $.ajax( {
        url : '/deleteID/',
        type: 'GET',
        async: false
    })
 }

function init() {
    $(document).ready(function() {
		$('#movebtn').attr('disabled', 'true')
		$('button[name="moveback"]').attr('disabled', 'true')
        $('#fencontainer').hide()
        $('.fen-paste-element').hide()

		$(window).resize(function() {
			for(x in Squares) {
				if (typeof Squares[x] == 'object')
					Squares[x].setLocation()
			}
		}) 
		Array.prototype.string = function() {
			var str = ''
			for (var i = 0; i < this.length; i++) 
				str += this[i] + ' '
			return str.trim()
		}
		Array.prototype.get = function(ver, hor) {
			for(var x in this) {
				if(this[x].ver == ver && this[x].hor == hor)
					return this[x]
			}
		}

        $.get('/initEngine/' + settings.howLongThink, function() {
            //$('#welcome').html('<span>Lucky game, player!</span>')
			newPosition()
		})
    })
	var k = 0
	var abcdefgh = 'abcdefgh'
		for(j = 1; j<=8; j++)
			for(i = 0; i<abcdefgh.length; i++) {
				Squares[k++] = new Square(abcdefgh[i],j)
			}
}


function Move(_note) {
	var note = this.note = _note
	var fen, legal
	this.setFen = function(_fen) { fen = this.fen = _fen }
	this.setLegal = function(_legal) { legal = this.legal = _legal }
}

function newPosition(fen) {
	if (thinking) return

    var oldPlayWithFen = playWithFen
    playWithFen = fen != undefined
    if (fen == undefined) fen = START_FEN

    $.ajax({
        url : '/new/' + encodeURIComponent(fen),
        dataType: "json",
        contentType: 'application/json',
        success: function(json) {
            legalMoves = json.legalMoves.split(' ')
            sweepAll()
            var fenArr = fen.split(' ')
            whiteToMove = fenArr[1] == 'w'
            whiteCastling = getCastlingFromFen(fenArr[2], true)
            blackCastling = getCastlingFromFen(fenArr[2], false)
            enPassantSquare = fenArr[3]
            halfmoveCounter = parseInt(fenArr[4])
            fullmoveCounter = parseInt(fenArr[5])

            $('#fencontent').html(fen)
            showFenBlock(false)
            $('#fencopybtn').children().attr('src', '/assets/images/copy.png')

            setPositionFromFen(fen)

            $('#movebtn').removeAttr('disabled')

            for(x in Squares) {
                if (typeof Squares[x] == 'object')
                    Squares[x].setLocation()
            }
            $('#notation').html(' ')
            if ($('#winpanel').css('display') == 'block') {
                $('#winpanel').css('display', 'none')
                $('#wintitlepanel').css('display', 'none')
            }
	    },
        error: function(err) {
            var fenStringPaste = $('#fenstringpaste')
            fenStringPaste.val(fenStringPaste.val() + " - invalid fen")
            fenStringPaste.css('color', 'red')
            playWithFen = oldPlayWithFen
            fenStringPaste.click(function() {
                fenStringPaste.css('color', 'black')
                fenStringPaste.val(err.responseText)
            })
        }
    })
}

function newMoveReceived(json) {
    $('#movebtn').click(updatePosition)

    var move = json.bestmove
    if (json.status.indexOf('bestmove') !== 0) {
        gameOver(json.status); return
    }
    movesHistory += ' ' + move
    doMove(move)
    addClickToMove($('#thinking').parent(), move)
    $('#thinking').replaceWith(move)
    thinking = false
    if (json.gamover !== 'PROCESS') {
        gameOver(json.gamover)
        setEnableButton(true)
        return
    } else if (settings.whoPlay == 'ai_ai') {
        setTimeout(updatePosition, 500)
    } else {
        setEnableButton(true)
    }
}

function moveBtnClick() {       // FIXME
	if (thinking) {
        $.get('/stopPonder', function() {
            thinking = false
        })
    } else if (analysis) {
        continueAnalysis(function(move) {
                doFullMove(move)
                clearAndAnalyse()
            }
        )
    } else {
        updatePosition()
    }

}

function updatePosition() {
	if (thinking || analysis) return
    thinking = true
	sweepPanels()
	setEnableButton(false)

	addMoveToPage($('<img>', {'id': 'thinking', 'src': '/assets/images/thinking.gif'}), whiteToMove)
    $.ajax({
        url : '/next/' + playWithFen,
		data : JSON.stringify({ history: movesHistory.trim(), fen: getFenFromPosition() }),
		type : 'POST',
		contentType: 'application/json',
		success: function(json) {
            newMoveReceived(json)
            var fen = getFenFromPosition()
            $.get('/getLegalMoves/' + encodeURIComponent(fen), function(legal) {
                legalMoves = legal.split(' ')
                addMoveToSet(json.bestmove, legalMoves, getFenFromPosition())
                if (legal == "") {
                    gameOver(whiteToMove ? 'BLACK_MATE' : 'WHITE_MATE')
                    return
                } else if (parseInt(fen.split(' ')[4]) > 50) {
                    gameOver("DRAW")
                    return
                }
                setEnableButton(true)
            })
        } })
}


function getLegalMoves(ver, hor) {
    var legal = [], i=0

    for(c in legalMoves) {
        if (legalMoves[c][0] == ver && legalMoves[c][1] == hor)
            legal[i++] = legalMoves[c]
    }
    return legal
}

function notMove(move) {
    return Squares.get(move[0], move[1]) == undefined || Squares.get(move[2], move[3]) == undefined
}

function setPosition(from, to) {
	var piece = Pieces.get(from[0], from[1]); 
	if (piece !== undefined) piece.setPosition(to[0], to[1])
}

function isMovable(ver, hor) {
    return Squares.get(ver, hor).piece.isWhite == whiteToMove
}

function addMoveToSet(move, oldLegal, oldFen, newLegal, newFen) {
	var moveObj = new Move(move)
	moveObj.setFen(oldFen)
	moveObj.setLegal(oldLegal.join(' '))
    Moves[Moves.length] = moveObj
}

function addMoveToPage(move, isWhite, dontCreateClick) {
    var getEmptyMoveElement = function() { return $('<div>', {'text': fullmoveCounter + '.'}); }
    var createSemiMoveElement = function(move) {
        var semiMoveElement = $('<span>')
        semiMoveElement.addClass('semiMoveElement')
        semiMoveElement.append(move)
        return semiMoveElement
    }
    var semiMoveElement = createSemiMoveElement(move)
    var moveElement
    removeGrayMoves()

    if (typeof move !== 'object' && !dontCreateClick) {
        addClickToMove(semiMoveElement, move)
    }

    if ($('#notation').children().size() == 0 && !isWhite) {
        moveElement = getEmptyMoveElement()
        $('#notation').append(moveElement)
        moveElement.append(createSemiMoveElement("&nbsp;&nbsp;&nbsp;...&nbsp;&nbsp;"))
    }
    if (isWhite) {
        moveElement = getEmptyMoveElement()
        $('#notation').append(moveElement)
    } else {
        moveElement = $('#notation').children().last()
    }
    moveElement.append(semiMoveElement)
    var theight = 0
    $('#notation').children().each(function() {
        theight += $(this).height()
    })
    $('#notation').scrollTop(theight - $('#notation').height())
}

function addClickToMove(semiMoveElement, move) {
    var moveNum = Moves.length
    tmpMovesHistory = movesHistory
	semiMoveElement.click(function() {
		if (thinking) return
		sweepBoard()
		var parentIndex = $(this).parent().index()
		var index = $(this).index()
        $('div#notation').find('span').css('color', 'inherit')
        $('div#notation .toDelete').removeClass('toDelete')
        $('div#notation > div:lt(' + (parentIndex+1) + ')').css('color', 'inherit')
        $('div#notation > div:gt(' + (parentIndex) + ')').css('color', 'gray')
        $('div#notation > div:gt(' + (parentIndex) + ')').addClass('toDelete')
        $('div#notation > div:gt(' + (parentIndex) + ')').children('span').addClass('toDelete')
		$(this).next('span').css('color', 'gray'); $(this).next('span').addClass('toDelete')

		var movesHstArray = tmpMovesHistory.trim().split(' ')     // array
        movesHstArray.length = parentIndex * 2 + index + 1
        movesHistory = movesHstArray.join(' ')
        legalMoves = Moves[moveNum].legal.split(' ')
        setPositionFromFen(Moves[moveNum].fen)
	})
}

function setFenToFencontent() {
	$('#fencontent').html(getFenFromPosition())
    $('#fencopybtn').children().attr('src', '/assets/images/copy.png')
}

function removeGrayMoves() {
    $('.toDelete').remove()
}

function sweepAll() {
    whiteCastling = 'KQ'
    blackCastling = 'kq'
    enPassantSquare = '-'
    fullmoveCounter = 1
    halfmoveCounter = 0
    whiteToMove = true
    movesHistory = ''
	Moves = []
	sweepBoard()
}

function sweepBoard() {
	var pieces = ['king', 'queen', 'rook', 'bishop', 'knight', 'pawn']
	var colors = ['w', 'b']
	var k = 0
	var abcdefgh = 'abcdefgh'
		for(j = 1; j<=8; j++)
			for(i = 0; i<abcdefgh.length; i++)
				Squares.get(abcdefgh[i],j).removePiece()
				for(i in pieces)
					for(j in colors) {
						$('.square').removeClass(colors[j] + pieces[i])
					}
	sweepPanels()
}

function sweepPanels() {
	$('.promotion').css('display', 'none')
	$('.clickedFrom').removeClass('clickedFrom')
	$('.clickedTo').css('border-color', '')
	$('.clickedTo').removeClass('clickedTo')
	$('#winpanel').css('display', 'none')
	$('#wintitlepanel').css('display', 'none')
}

function setEnableButton(val) {
	if (val) {
		$('button[name="newgame"]').removeAttr('disabled')
		$('button[name="savegame"]').removeAttr('disabled')
		$('#movebtn').removeAttr('disabled')
		$('button[name="moveback"]').removeAttr('disabled')
	} else {
		$('button[name="newgame"]').attr('disabled', 'true')
		$('button[name="moveback"]').attr('disabled', 'true')
	}
}

function gameOver(result) {
	console.log('Game over: ' + result+ ' top='+$('.board').position().top+' left='+$('.board').position().left)
    thinking = false
    removeGrayMoves()
    $('#thinking').remove()
    if ($('#thinking').parents('div').first().children('span').length <= 1)
        $('#thinking').parents('div').first().remove()
    setEnableButton(true)
    $('#movebtn').attr('disabled', 'true')
    $('#winpanel').css('display', 'block')
	$('#winpanel').css({ top: $('.board').position().top + 'px', left: $('.board').position().left + 'px' })
	$('#wintitlepanel').css('display', 'block')
	$('#wintitlepanel').css({ top: $('.board').position().top + 'px', left: $('.board').position().left + 'px' })
	var res = (result.trim() == 'BLACK_MATE') ? 'Black win!' :
			  (result.trim() == 'WHITE_MATE') ? 'White win!' : 'Draw!'
	$('#wintitle').html(res)
}

function turnSide() {
	whiteIsUp = !whiteIsUp
	for ( var i in Squares) {
		if (typeof Squares[i] == 'object') Squares[i].setLocation()
	}
}

function start() {
	$.get('/start', function(data) {
		console.info('returned: ' + data)
	})
}

function isFen(cb) {
	isFen = cb.checked
}

function showAvailableMoves(input) {
	if (input.checked) $('.clickedTo').css('border-color', '')
	else $('.clickedTo').css('border-color', 'transparent')
	settings.showToMove = input.checked
}

function setPlayers(input) {
	if ($(':radio#ai_ai').attr('checked')) { settings.whoPlay = 'ai_ai' } else
	if ($(':radio#human_human').attr('checked')) { settings.whoPlay = 'human_human' } else settings.whoPlay = 'ai_human'
}

function changeEngine() {
    var newEngine = $('#select-engine').val()
    $.get('/changeEngine/' + newEngine, function(data) {
        $('#welcome').html('<span>' + data + '</span>')
    })
}

function setPonderTime() {
	moveDisablable = true
	if ($(':radio#time1').attr('checked')) { settings.howLongThink = 1 } else  
	if ($(':radio#time5').attr('checked')) { settings.howLongThink = 5 } else 
	if ($(':radio#time10').attr('checked')) { settings.howLongThink = 10 } else {
		settings.howLongThink = -1
        //$('#movebtn').html('STOP')
        //console.log('moveDisablable = false')
		moveDisablable = false 
	}
	$.get('/setPonderTime/' + settings.howLongThink)
}

function checkEnPassant(squareCandidate, pieceFrom) {
    if (squareCandidate == undefined) return false
    var pieceCandidate = squareCandidate.getPiece()
    return pieceCandidate != undefined && pieceCandidate.type == 'p' &&
        pieceCandidate.isWhite != pieceFrom.isWhite
}

function doSmth() {
//	Я «Пять недель на воздушном шаре» вообще всю жизнь с одиннадцатой главы читал.
    for (var i=0; i<Moves.length; i++) {
        console.info(Moves[i].note)
        console.info('oldfen: ' + Moves[i].oldfen)
        console.info('newfen: ' + Moves[i].newfen)
        console.info('oldlegal: ' + Moves[i].oldlegal)
        console.info('newlegal: ' + Moves[i].newlegal)
        console.log('\n')
    }
}

function print(move) {
	console.log('####################################')
    if (move) {
        console.log('move: ' + move.note)
        console.log('move.fen: ' + move.fen)
        console.log('move.legal: ' + move.legal)
	} 
	
	console.log('----------------------------------------')
	console.log('getFenFromPosition: ' + getFen())
	console.log('               FEN: ' + fen)
	console.log('History: ' + movesHistory)
	console.log('Legal: ' + legalMoves)
    console.log(JSON.stringify(Moves))
	console.log('####################################')
}




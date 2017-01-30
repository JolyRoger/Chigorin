
var allSquaresForPawn = function(square) {
    var out = []
    if (square.getPiece().isWhite) {
        out.push(square.ver + (square.hor+1))
        if (square.hor == 2) out.push(square.ver + (square.hor+2))
    } else {
        out.push(square.ver + (square.hor-1))
        if (square.hor == 7) out.push(square.ver + (square.hor-2))
    }
    return out
}

var allSquaresForKnight = function(square) {
    var out = new SqArr()
    out.push(incVer(square.ver, 1) + (square.hor + 2))
    out.push(incVer(square.ver, 2) + (square.hor + 1))
    out.push(incVer(square.ver, -1) +(square.hor + 2))
    out.push(incVer(square.ver, -2) +(square.hor + 1))
    out.push(incVer(square.ver, 1) + (square.hor + -2))
    out.push(incVer(square.ver, 2) + (square.hor + -1))
    out.push(incVer(square.ver, -1) +(square.hor + -2))
    out.push(incVer(square.ver, -2) +(square.hor + -1))
    return out.array()
}

var allSquaresForBishop = function(square) {
    var out = new SqArr()
    for(i = 1; i <= 8; i++) {
        out.push(incVer(square.ver, i) + (square.hor + i))
        out.push(incVer(square.ver, -i) + (square.hor + i))
        out.push(incVer(square.ver, i) + (square.hor - i))
        out.push(incVer(square.ver, -i) + (square.hor - i))
    }
    return out.array()
}

var allSquaresForRook = function(square) {
    var out = new SqArr()
    var vers = 'abcdefgh'
    var hors = '12345678'
    var squareStr = square.ver + square.hor
    for(ver in vers) out.push(vers[ver] + square.hor, squareStr)
    for(hor in hors) out.push(square.ver + hors[hor], squareStr)
    return out.array()
}

var allSquaresForQueen = function(square) {
    return allSquaresForBishop(square).concat(allSquaresForRook(square))
}

var allSquaresForKing = function(square) {
    var out = new SqArr()
    var squareStr = square.ver + square.hor
    for (var i = -1; i<=1; i++) {
        for (var j = -1; j <= 1; j++) {
            out.push(incVer(square.ver, i) + (square.hor + j), squareStr)
        }
    }
    return out.array()
}

var rules = { p: allSquaresForPawn,
              r: allSquaresForRook,
              b: allSquaresForBishop,
              q: allSquaresForQueen,
              n: allSquaresForKnight,
              k: allSquaresForKing  }

var SqArr = function() {
    var out = []
    this.push = function(to, from) {
        if (valid(to, from)) out.push(to)
    }
    var valid = function(to, from) {
        if (typeof to != 'string' || to == from || out.indexOf(to) >= 0) return false
        var a = to.split('')
        var verIndex = 'abcdefgh'.split('').indexOf(a[0])
        return a.length == 2 && verIndex >= 0 && verIndex < 8 &&
               !isNaN(a[1]) && a[1] > 0 && a[1] <= 8
    }
    this.array = function() {
        return out
    }
}

function getLegalMoves(ver, hor) {
    var legal = [], i=0

    for(c in legalMoves) {
        if (legalMoves[c][0] == ver && legalMoves[c][1] == hor)
            legal[i++] = legalMoves[c]
    }
    return legal
}

function getLegalMovesMy(ver, hor) {
    //var piece = Pieces.get(ver, hor)
    var piece = Squares.get(ver, hor)/*.getPiece()*/
    var allSquaresOnBoard = getAllSquaresOnBoardMoves(piece)
    var allPossibleSquaresOnBoard = getAllPossibleSquaresOnBoard(piece)
    var taking = getTaking(piece)
    var legal = transformSquaresToMoves(piece, allSquaresOnBoard)
    return legal
}

function getAllSquaresOnBoardMoves(square) {
    return rules[square.getPiece().type](square)
}

function getAllPossibleSquaresOnBoard(square) {

}

function getTaking(piece) {

}

function transformSquaresToMoves(square, possibleSquares) {
    var out = []
    for (var i = 0; i < possibleSquares.length; i++)
        out.push(square.ver + square.hor + possibleSquares[i] + " ")
    return out
}

function incVer(ver, num) {
    var abcdefgh = 'abcdefgh'.split('')
    var newIndex = abcdefgh.indexOf(ver) + num
    if (newIndex >= abcdefgh.length || newIndex < 0) return undefined
    else return abcdefgh[newIndex]
}

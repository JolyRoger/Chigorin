
$(function() {
    fbPopup('-settings')
    fbPopup('-about')

    $('#fenstringcopy').hover(function() {
        $('#fencopybtn').show()
    }, function() {
        setTimeout(function() {
            $('#fencopybtn').hide()
        }, 3000)
    })
    $('#fenstringpaste').hover(function() {
        $('#fenpastebtn').show()
    }, function() {
        setTimeout(function() {
            $('#fenpastebtn').hide()
        }, 3000)
    })
    //console.log('content=' + $('#content').width() + ' :: board=' + $('.board').width())
    //console.log('moves-group=' + $('#moves-group').width())
    //$(window).resize(analSize)
    //$('#content').ready(function() {
    //    $('.board').ready(analSize)
    //})
})

function analSize() {
    //var mgSize = $('#content').width() - $('.board').width() -
    //    parseInt($('.board').css('padding-left')) -
    //    parseInt($('.board').css('padding-right')) -
    //    $('#moves-group').width(mgSize)


    var s = parseInt($('#content').width()) / 2
    $('#analysis').css('padding-right', s)
    console.log('aaa: ' + $('#content').width() + ' :: ' + $('#analysis').css('padding-right'))
    //pgnEl.width(mgSize / 3)
    //$('#analysis').width(mgSize - (mgSize/3) -
    //    parseInt($('#analysis').css('padding-left')) -
    //    parseInt($('#analysis').css('padding-right')) -
    //    parseInt(pgnEl.css('padding-right')) -
    //    parseInt(pgnEl.css('padding-left'))
    //)
}


function Transform(fin, step, obj) {
    var t,t2;

    this.appear = function() {
        var op = (obj.style.opacity) ? parseFloat(obj.style.opacity) : parseInt(obj.style.filter) / 100;
        if(op < fin) {
            op = fin;
            clearTimeout(t2);
            obj.style.opacity = op;
            obj.style.filter='alpha(opacity='+op*100+')';
            t = setTimeout(arguments.callee, 20);
        }
    }
    this.disappear = function() {
        if (!thinking) {
            var op = (obj.style.opacity)?parseFloat(obj.style.opacity):parseInt(obj.style.filter)/100;
            if(op > step) {
                clearTimeout(t);
                op -= 0.05;
                obj.style.opacity = op;
                obj.style.filter='alpha(opacity='+op*100+')';
                t2 = setTimeout(arguments.callee, 20);
            }
        }
    }
    this.down = function() {
        if (!thinking) {
            obj.parentNode.style.MozTransform = 'scale(0.97)';
            obj.parentNode.style.WebkitTransform = 'scale(0.97)';
//            setTimeout(this.up, 100)
        }
    }

    this.down2 = function() {
        obj.style.opacity = 0
        obj.style.filter='alpha(opacity=0)'
        obj.style.cursor='default'
        this.pressed = true
    }
    this.up2 = function() {
        obj.style.opacity = 1
        obj.style.filter='alpha(opacity=100)';
        obj.style.cursor='pointer'
        this.pressed = false
    }
    this.up = function() {
        obj.parentNode.style.MozTransform = 'scale(1.0)';
        obj.parentNode.style.WebkitTransform = 'scale(1.0)';
    }
}

function contains($elem, x, y) {
    function getOffset(elem) {
        if (elem.getBoundingClientRect) {
            // "правильный" вариант
            return getOffsetRect(elem)
        } else {
            // пусть работает хоть как-то
            return getOffsetSum(elem)
        }
    }

    function getOffsetSum(elem) {
        var top=0, left=0
        while(elem) {
            top = top + parseInt(elem.offsetTop)
            left = left + parseInt(elem.offsetLeft)
            elem = elem.offsetParent
        }

        return {top: top, left: left}
    }

    function getOffsetRect(elem) {
        // (1)
        var box = elem.getBoundingClientRect()

        // (2)
        var body = document.body
        var docElem = document.documentElement

        // (3)
        var scrollTop = window.pageYOffset || docElem.scrollTop || body.scrollTop
        var scrollLeft = window.pageXOffset || docElem.scrollLeft || body.scrollLeft

        // (4)
        var clientTop = docElem.clientTop || body.clientTop || 0
        var clientLeft = docElem.clientLeft || body.clientLeft || 0

        // (5)
        var top  = box.top +  scrollTop - clientTop
        var left = box.left + scrollLeft - clientLeft

        return { top: Math.round(top), left: Math.round(left) }
    }
   var btnCoord = getOffset($elem.get(0))
   return x > btnCoord.left && x < btnCoord.left + $elem.width() &&
          y > btnCoord.top && y < btnCoord.top + $elem.height()

}

function fbPopup(indx) {
    var str = '#fb-modal' + indx
      $(str).css({
        opacity:0,
        display:'block'
    })
    $('#fb-close' + indx).click(function(e) { $(str).fadeOut('slow') })
    $(document).keypress(function(e) { if (e.key == 'esc') $(str).fadeOut('slow') })
    $('#fb-trigger' + indx).click(function() { $(str).fadeTo('slow', 1.0) })
    $(str).fadeOut('slow')
}

function print(value) {
	if (typeof value == 'object') {
		for (var x in value) {
			console.log(x + ' :: ' + value)
		}
	} else if (typeof value == 'string') {
		console.log(value)
	}		
}

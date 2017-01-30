
//var container = document.querySelector('#container');
//var typer = container.querySelector('[contenteditable]');

const MIME_TYPE = 'text/plain';
var mouseX, mouseY
$(function() {
// Rockstars use event delegation!
//	document.body.addEventListener('dragstart', function(e) {
//	  var a = e.target;
//	  if (a.classList.contains('dragout')) {
//	    e.dataTransfer.setData('DownloadURL', a.dataset.downloadurl);
//	  }
//	}, false);
//
//	document.body.addEventListener('dragend', function(e) {
//	  var a = e.target;
//	  if (a.classList.contains('dragout')) {
//	    cleanUp(a);
//	  }
//	}, false);
//
//	document.addEventListener('keydown', function(e) {
//	  if (e.keyCode == 27) {  // Esc
//	    document.querySelector('details').open = false;
//	  } else if (e.shiftKey && e.keyCode == 191) { // shift + ?
//	    document.querySelector('details').open = true;
//	  }
//	}, false);

    $(document).mousemove(function(e) {
        mouseX = e.pageX; mouseY = e.pageY
    })
    fbPopup('-settings')
    fbPopup('-about')
	$('#fb-modal-settings').fadeOut('slow')
    $('#fb-modal-about').fadeOut('slow')
})

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

function enableMoveBtn(en) {
    if (en) {
        $('img#movebtn').attr('src', '/assets/images/movebtn.png')
       $('img#movebtn').css('opacity',  contains($('img#movebtn'), mouseX, mouseY) ? '0' : '1')
    } else {
        $('img#movebtn').attr('src', '/assets/images/movebtn_back_gray.png')
        $('img#movebtn').css('opacity', '1')
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

var cleanUp = function(a) {
  a.textContent = 'Downloaded';
  a.dataset.disabled = true;

  // Need a small delay for the revokeObjectURL to work properly.
  setTimeout(function() {
    window.URL.revokeObjectURL(a.href);
  }, 1500);
}

var downloadFile = function(data) {
    var jsData0 = JSON.stringify(data)
    var jsData1 = {'a': 42, 'b': {'name': 'John', 'sname': 'Smith', 'arr': [1,2,3,6,4,5,23,565]}}
    var jsData2 = {"moves": [{"a": 42, "b": 45}, {"a": 91, "b": 15}, {"a": 39, "b": 80}]}

    $.ajax( {
		url : '/saveGame', 
		data : JSON.stringify(data),
		type : 'POST',
		contentType: 'application/json', 
		success: function(filename) {
			console.info('response: ' + filename)
//			  $('iframe_dl').src="/app?download=1&filename=" + filename
			  var url = '/assets/saves/'+filename
//			  var url = '/assets/saves/Akka.pdf'
//			  $.fileDownload(url, {
//				    successCallback: function (url) {
//				        alert('You just got a file download dialog or ribbon for this URL :' + url)
//				    },
//				    failCallback: function (html, url) {
//				        alert('Your file download just failed for this URL:' + url + '\r\n' +
//				                'Here was the resulting error HTML: \r\n' + html
//				                )
//				    }
//				})
//			  var $iframe = $('<iframe>', { id: 'iframe_dl', 
//				  							style: 'display: none;'} )
//			  tryToDownload(url)
//              $iframe.src = url
              window.location = url
//			  $('#iframe_dl').src="/app?download=1&filename=" + filename
//			  var $a = $('<a>', {
//				  href: '/assets/saves/' + filename,
//				  download: filename
//			  })
//			  $a.append('save')
//			  $('#btn-group').append($a)
//			  $('#btn-group').append($iframe)
//			  $a.get(0).click()
		},
        error: function(error) {
            console.log('error: ' + error.responseText)
        }
	} )
}

function tryToDownload(url)
{
    oIFrm = document.getElementById('myIFrm');
    oIFrm.src = url;
    window.location = url
//    alert(url);
}

var downloadFile_ = function() {
  var output = document.querySelector('output')
  var content = 'some text content'
  var content2 = 'another text content'
  window.URL = window.webkitURL || window.URL
  window.BlobBuilder = window.BlobBuilder || window.WebKitBlobBuilder ||
                       window.MozBlobBuilder

  var prevLink = output.querySelector('a')
  if (prevLink) {
    window.URL.revokeObjectURL(prevLink.href)
  }

  var blob0 = new Blob()
  var a = document.createElement('a')
  a.download = 'somefile.xml'
//  var dataView = new DataView(new ArrayBuffer('some text content'))
  var blob = new Blob([content, content2], { type: MIME_TYPE })
  
  a.href = window.URL.createObjectURL(blob)
  a.textContent = 'Download ready'
  a.dataset.downloadurl = [MIME_TYPE, a.download, a.href].join(':')
  a.draggable = true; // Don't really need, but good practice.
  a.classList.add('dragout')

  output.appendChild(a)
  a.click()
//  a.onclick = function(e) {
//    if ('disabled' in this.dataset) {
//      return false;
//    }
//
//    cleanUp(this);
//  };
}

function fbPopup(indx) {
    var str = '#fb-modal' + indx
		  $('#fb-modal' + indx).css({
			  opacity:0,
			  display:'block'
		  })
		  $('#fb-close' + indx).click(function(e) { $('#fb-modal' + indx).fadeOut('slow') })
		$(document).keypress(function(e) {
			if(e.key == 'esc') { 
				$('#fb-modal' + indx).fadeOut('slow')
			}
		})
		  
//		  $('body').click(function(e) {
//				if($('#fb-modal').css('opacity') == 1  && !$(e.target).parents().hasClass('generic_dialog') ) { 
//					$('#fb-modal').fadeOut('slow')
//				}
//		  })

		  
		  /* click to show */
		  $('#fb-trigger' + indx).click(function() {
 			$('#fb-modal' + indx).fadeTo('slow', 1.0)
		  })
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

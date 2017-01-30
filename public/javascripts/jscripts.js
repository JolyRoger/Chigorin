
var prev = -1;
var obj, op;
var handleHtml = {};

function saveSID(serial) {
	$('.scenariorun' + serial).each(function() {
		handleHtml[$(this).attr('id')] = $(this).attr('sid')
	})
	
	$('.scenariorun'+serial).attr('title', 'The target is busy. Device is running a scenario or is performing another operation. ' +
			'This item will be available as soon as the action finished.')
	$('.scenariorun'+serial).html('Execute')
	$('.scenariorun'+serial).css('color', '#666')
}

function purgeSID(cid, tid, serial) {
	for(v in handleHtml) {
		if (v.search(serial) != -1) {
			$('#'+v).html('<a href="##" onclick="startScenario(' + cid + ', ' + tid + ', ' + handleHtml[v] +
					', \'scenario_state\', ' + '\'' + serial + '\');"> Execute </a>')
			$('#'+v).attr('title', 'Run the scenario to execute.')
			delete v
		}
	}
}

function cleanHandleHtml() {
	handleHtml = 0;	
}

function updateScenarioState(cid, tid, serial) {
	$(document).ready(function() {
		$.get('/getState/' + cid+'/'+tid, function(data) {
			console.info(data)
			if (data == 'ready') {
				purgeSID(cid, tid, serial)
			} else {
				saveSID(serial)
			}
		})
	})
}

function updateScenarioRetVal(statusElem, cid, tid, sid, targetModel, serial) {
	$.get('/updateScenarioRetval/' + cid +'/' + tid +'/' + sid, function(data) {
		statusElem.html(data)
		purgeSID(cid, tid, serial)
		targetModel.css('color', '#666')
	})
}

function updateTargetState(cid, tid, sid, statusId, serial) {
	var $statusElem = $('#' + statusId + serial + sid)
	var $targetModel = $('#target_model' + serial)
	
	function recursion() { updateTargetState(cid, tid, sid, statusId, serial) }
	
	console.info('start ajax getState URL: ' +'/getState/' + cid+'/'+tid + ' serial: ' + serial)
	
	$.get('/getState/' + cid+'/'+tid, function(data) {
		console.info('response retrieved. SID='+sid)
		if (sid == -1) {
			if ($statusElem != null) {
//				setTimeout(recursion, 5000);
				$targetModel.css('color', '#666');
				$statusElem.html(data)
			} 
		} else {
			console.info('set data='+data)
			if (data == 'ready') {
				updateScenarioRetVal($statusElem, cid, tid, sid, $targetModel, serial)
			} else {
				if (data == 'notfound') {
//					console.info('sid='+sid)
					$.get('/getId/' + cid + '/' +serial, function(sn) {
						var $targetId = $('#target_id' + serial)
						console.info('targetId' + $targetId)
						if ($.isNumeric(sn)) {
							tid = sn
							if ($targetId != null) 
								$targetId.html(tid);
						} else {
							if ($targetId != null) 
								$targetId.html('?');
						}
						
						setTimeout(recursion, 1000);
					})
				} else {
					setTimeout(recursion, 1000);
				}
				$statusElem.html('<img src="/assets/images/ajax-loader.gif"><br />' + data)
			}
		}
	})
}

function startScenario(cid, tid, sid, statusId, serial) {
//	console.info('cid='+cid+' tid='+tid+' sid='+sid+' serial='+serial)
	
	var $statusElem = $('#' + statusId + serial + sid)
	var $targetModel = $('#target_model' + serial)
	
	$statusElem.html('<img src="/assets/images/ajax-loader.gif"><br />started')
	saveSID(serial)
	if ($targetModel != null) {
		$targetModel.css('color', '#FF0000');
	}

	$.getJSON('/connect/'+cid+'/'+tid+'/'+sid, function(data) {
		if ($.trim(data.response) == 'success') {
			$statusElem.html('<img src="/assets/images/ajax-loader.gif"><br />success')
			console.info('SUCCESS cid='+cid+' tid='+tid+' sid='+sid+' statusId: '+statusId+' data.serial: '+data.serial)
			updateTargetState(cid, tid, sid, statusId, data.serial);
		} else {
			$statusElem.html(data.response);
			purgeSID(cid, tid, serial)
			$targetModel.css('color', '#666')
		}
	})
}

function setColor(status, cid, tid) {
	var statusElem = document.getElementById(status + cid + tid)
}

function transparent(fin, step, obj)
{
    var t,t2;
    this.appear = function() {
    	var op = (obj.style.opacity)?parseFloat(obj.style.opacity):parseInt(obj.style.filter)/100;

        if(op < fin) {
        	op = fin;
            clearTimeout(t2);
            obj.style.opacity = op;
            obj.style.filter='alpha(opacity='+op*100+')';
            t = setTimeout(arguments.callee, 20);
        } 
    }
    this.disappear = function() {
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






function filter(id, flag) {
	if (flag){
		document.getElementById('link'+id).innerHTML="Show available";
	} else { 
		document.getElementById('link'+id).innerHTML="Hide available";
	}
}

function insertImage(id) {
	var element = document.getElementById(id);
	console.log(id, " ::: ", element.className);
	
	
//	element.src = '@{\'/public/images/indicator-big-2.gif\'';
	element.src = '@routes.Assets.at("images/indicator-big-2.gif")';
//	element.
}

function shoh(id) {
//	console.log("shoh: "+id);
//	if (prev != id && prev > -1) {
//		shoh(prev);
//	}
	
//	var tmp = #{jsAction @exploits(':id') /};
//	console.log("shoh2: "+id);
	
	if (document.getElementById) {
		if (document.getElementById(id).style.display == "none"){
			document.getElementById(id).style.display = 'block';
			filter(id, false);
			prev = id;
		} else {
			document.getElementById(id).style.display = 'none';
			filter(id, true);
			prev = -1;
		}	
	} 
}


function fbstyle(indx) {
	$(document).ready(function() {
		var fbmodalindx = "fb-modal"+indx;
		function callback() {
			$('#' + fbmodalindx).css( { opacity: 0, display: 'none' } );
		}
		callback();

		/* hiders */
		var fbcloseindx = "fb-close"+indx;
	
		$('#' + fbcloseindx).click(function(e) {
			$('#' + fbmodalindx).fadeOut('slow', callback) 
			location.href = '##';
		});

		$(document).keypress(function(e) {
			if(e.key == 'esc') { 
				$(fbmodalindx).fadeOut('slow', callback) 
				location.href = '##';
			}
		})
	
		$('body').click(function(e) { 
//			console.log(fbmodalindx + ' opacity: ' + $('#' + fbmodalindx).css('opacity') + ' ' +
//					$(e.target).parent('.generic_dialog') + ' opacity==1: ' + ($('#' + fbmodalindx).css('opacity') == 1) +
//					' sec: ' + e.target.nodeName);
			
			if($('#' + fbmodalindx).css('opacity') == 1  && !$(e.target).parents().hasClass('generic_dialog') ) { 
				$('#' + fbmodalindx).fadeOut('slow', callback)
				location.href = '##';
			} 
		});
	
		/* click to show */
		var fbtriggerindx = 'fb-trigger'+indx;
		$('#' + fbtriggerindx).click(function() {
			$('#' + fbmodalindx).fadeTo('slow', 1.0);
		}); 	
	})
}
/*
function showReportLink(id, action) {
	if (action) {
		$('#'+id).css( {display: 'inline', position: 'absolute' } )
	} else {
		setTimeout(function() { 
			if ($('#'+id+':hover').length == 0)
			$('#'+id).css( {display: 'none', position: 'absolute' } ) 
		}, 100)
	}
}
*/
function login() {
	$(document).ready(function() {
		$('#show_modal').click(function() {
			console.log('show_modal');
			$.blockUI({
				message : $('#modal_dialog'),
				css : {
					width : '375px'
				}
			});
		});
		$('.rotes_kreuz, .close_dialog').click(function() {
			console.log('rotes_kreuz click!');
			$.unblockUI();
			return false;
		});
	});
}

function tabSwitch(newTab, newContent, reportId) {
	$('#info'+reportId).css('display', 'none')
	$('#content'+reportId).css('display', 'none')
	$('#'+newContent).css('display', 'block')
	
	$('#tabInfo'+reportId).removeClass()
	$('#tabContent'+reportId).removeClass()
	$('#'+newTab).addClass('active')
}

function expandAll() {
	console.info('expandAll')
}

function collapseAll() {
	console.info('collapseAll')
}
















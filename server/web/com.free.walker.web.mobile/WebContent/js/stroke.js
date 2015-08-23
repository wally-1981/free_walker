function createArrows(div_id, num_arrow){
	var share = 100/num_arrow;
	document.getElementById(div_id).innerHTML = "";
	for (var i=0; i< num_arrow; i++){
		if (i == num_arrow - 1) {
			document.getElementById(div_id).innerHTML += "<div class=\"stroke_container\" style=\"width: "+ share +"%\"><div class=\"stroke_tail\"></div></div>";
		}else{			
			document.getElementById(div_id).innerHTML += "<div class=\"stroke_container\" style=\"width: "+ share +"%\"><div class=\"stroke\"></div></div>";
		}
	}
}
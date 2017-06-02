
// Michael Dik's Code 

function showPopup(photoNum) {
	
	 
	    
	
    document.getElementById('blackOverlay').style.display = 'block';

    document.getElementById(photoNum).style.display = 'inline';
    
   // document.getElementById(CaptionID).style.display = 'inline';
    
}

function next (photoNum) { 
	document.getElementById(photoNum).style.display = 'none';
	var next = photoNum+1; 
	
	document.getElementById(next).style.display = 'inline';
}

function previous (photoNum) { 
	document.getElementById(photoNum).style.display = 'none';
	var prev = photoNum-1; 
	
	document.getElementById(prev).style.display = 'inline';
}




function closePopup(photoNum) {

    document.getElementById('blackOverlay').style.display = 'none';
    document.getElementById(photoNum).style.display = 'none';
    
}

// Original Code  



//Toggle between two views
$("#showGallery").click(function(){
	$("#gallery").show();
	$("#original").hide();
});
	      
$("#showOriginal").click(function(){
	$("#original").show();
	$("#gallery").hide();
});

//Toggle Translations
$(".showTrans").click(function(){
	var button = $(this);
	if (button.val() == "Show Translation"){
		button.val("Hide Translation");
	}else{
		button.val("Show Translation");
	}
	
	button.prev().toggle();
});


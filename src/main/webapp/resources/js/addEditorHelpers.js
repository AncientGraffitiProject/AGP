/**
 * 
 *Helper functions for add editor feature
 * author: Azmain Amin
 */



function checkPassword(){
	console.log("Starting")
	var pw1 = document.getElementById("password1");
	var pw2 = document.getElementById("password2");
	var button = document.getElementById("add_button");
	var message = document.getElementById("msg");
    var goodColor = "#66cc66";
    var badColor = "#ff6666";
    
    if(pw1.value == pw2.value){
    	pw2.style.backgroundColor = goodColor;
    	message.style.color = goodColor;
    	message.innerHTML = "Passwords Match."
    	button.disabled = false;
    	//console.log("Matched");
    }else{
    	pw2.style.backgroundColor = badColor;
    	message.style.color = badColor;
    	message.innerHTML = "Passwords Don't Match."
    	//console.log("Not matched");
    	button.disabled = true;
    }
	
}
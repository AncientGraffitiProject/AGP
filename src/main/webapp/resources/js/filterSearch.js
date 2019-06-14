/**
 * Provides functionality to filter the search results on the results page
 * 
 * @author whitej
 * @author shnoudah -- added report generation functionality
 * @author sprenkle -- version 2.0 filtering
 * @author ahmadh -- version 2.1 filtering, includes bug fixes
 * @author KellyM --Modified refineResults method so that it reloads the pompeiiMap with new location keys based on the filtering.
 * @author cooperbaird -- Modified refineResults to allow for sorting
 */

var currentParams = new Array(); // the ending array of parameters
var requestUrl = "filter"; // request url for filtering
var reportUrl = "admin/report"; // report url for reports
var filters = new Array(); // an array of the filters applied so far on the search

/**
 * flips the arrow when a filter type (City, Property Type, Drawing Type, etc.)
 * is clicked
 * 
 * @param x
 *            the value to append to "expand" to find the appropriate arrow to
 *            flip
 */
function switchArrow(x) {
	var menu = document.getElementById("menu" + x);
	var code = "&#" + menu.innerHTML.charCodeAt(0) + ";";
	if (code == "&#9650;") {
		menu.innerHTML = "&#9660;"; // change to down arrow
	} else {
		menu.innerHTML = "&#9650;"; // change to up arrow
	}
}

/**
 * switches between + and - when an expandable item (city or insula) is clicked
 * 
 * @param x
 *            the value to append to "expand" to find the appropriate sign to
 *            change
 */
function switchSign(x) {
	var expand = document.getElementById("expand" + x);
	var code = "&#" + expand.innerHTML.charCodeAt(0) + ";";
	if (code == "&#43;") {
		expand.innerHTML = "&#8722;"; // change to minus
	} else {
		expand.innerHTML = "&#43;"; // change to plus
	}
}

/**
 * search method for content keyword search
 */
function contentSearch() {
	var input = document.getElementById("keyword").value;
	if (input.trim() != "") {
		addSearchTerm("Content", input, input, 0);
	}
	document.getElementById("keyword").value = "";
}

/**
 * search method for global keyword search
 */
function globalSearch() {
	var input = document.getElementById("keyword").value;
	if (input.trim() != "") {
		//input = "Global: " + input;
		addSearchTerm("Global", input, input, 0);
	}
	document.getElementById("keyword").value = "";
}

/**
 * search method specifically for CIL number search
 */
function cilSearch(){
	var input = document.getElementById("keyword").value;
	if (input.trim() != "") {
		addSearchTerm("CIL", input, input, 0);
	}
	document.getElementById("keyword").value = "";
}

/**
 * search method for checkboxes (location, property type, etc.) adds search term
 * if it doesn't exist otherwise, removes the term and the respective blue
 * search term label
 * 
 * @param type
 *            the search term type (City, Property Type, etc.)
 * @param label
 *            the human-readable search term, used for the label
 * @param choice
 *            the choice selected by the user, used in the actual search
 * @param id
 *            the value used to identify the input so it can be unchecked later
 */
function filterBy(type, label, choice, id) {
	
	var desc = type + ": " + label;
	filters.push(desc);
	
	// if the user has clicked on "All" under Drawing Category, update the filters accordingly
	if(type == "Drawing Category" && label == "All") {
		uncheckOtherCheckboxes("Drawing_Category");
	} else if(type == "Drawing Category" && label != "All" ) {
		uncheckAllCheckbox("Drawing_Category");
	}
	
	if (termExists(desc)) {
		removeSearchTermByName(desc);
	} else {
		addSearchTerm(type, label, choice, id);
	}
}

/**
 * helper function to uncheck other checkboxes once "All" is checked and update the filters
 * 
 * @param divId
 *            id of the div class
 */
function uncheckOtherCheckboxes(divId) {
	// uncheck the other checkboxes
	var checkboxes = document.querySelectorAll('#' + divId + ' input[type = "checkbox"]');
	for(var i = 0; i < checkboxes.length; i++) {
		var checkbox = checkboxes[i];
		if(checkbox.id!="dc0") {
			checkbox.checked = false;
		}
	}
	// update the filters
	for(var i = 0; i< filters.length; i++) {
		var filter = filters[i];
		if(filter != "Drawing Category: All" && filter.startsWith("Drawing Category:")) {
			if (termExists(filter)) {
				removeSearchTermByName(filter);
			}
		}
	}
}

/**
 * helper function to uncheck "All" and update the filters
 * 
 * @param divId
 *            id of the div class
 */
function uncheckAllCheckbox(divId) {
	// uncheck "All" if something else is checked
	var checkboxes = document.querySelectorAll('#' + divId + ' input[type = "checkbox"]');
	for(var i = 0; i < checkboxes.length; i++) {
		var checkbox = checkboxes[i];
		if(checkbox.id=="dc0") {
			checkbox.checked = false;
		}
	}
	// update the filters
	for(var i = 0; i< filters.length; i++) {
		var filter = filters[i];
		if(filter == "Drawing Category: All") {
			if (termExists(filter)) {
				removeSearchTermByName(filter);
			}
		}
	}
}

/**
 * adds blue label with search term information to the top of the results page
 * 
 * @param type
 *            the type of search to do
 * 
 * @param label
 *            the human-readable description to show on the label
 * 
 * @param choice
 *            the value the filter will search for
 * 
 * @param id
 *            the hidden id used to uncheck an input when the label is clicked
 *            and removed
 */
function addSearchTerm(type, label, choice, id) {
	var desc = type + ": " + label;
	if (!termExists(desc)) {
		if( document.getElementById(id) != null )
			document.getElementById(id).checked = true;

		var list = document.createElement('li');
		var label = document.createElement("label");
		label.className = "label label-primary search-term-label";
		label.onclick = removeSearchTerm;

		var value = " " + String.fromCharCode(215);
		var x = document.createTextNode(value);

		var span = document.createElement("span");
		span.className = "large-font";
		span.onclick = removeSearchTermBySpan;
		span.appendChild(x);

		var hiddenId = document.createElement("span");
		hiddenId.style.display = "none";
		hiddenId.appendChild(document.createTextNode(id));

		var hiddenSearch = document.createElement("span");
		hiddenSearch.style.display = "none";
		hiddenSearch.appendChild(document.createTextNode(type + ": " + choice));

		var text = document.createTextNode(desc);

		label.appendChild(text);
		label.appendChild(span);
		label.appendChild(hiddenId);
		label.appendChild(hiddenSearch);

		list.appendChild(label);
		
		// currentParams.push(type + ": " + choice);
		var searchTerms = document.getElementById("searchTerms");
		//searchTerms.appendChild(label);
		searchTerms.appendChild(list);
		refineResults("filter");
	}
}

/**
 * removes a clicked search term label from the top bar if not a keyword search,
 * unchecks the respective input
 * 
 * @param e
 *            the event in which the user clicked the label
 */
function removeSearchTerm(e) {
	var desc = e.target.childNodes[0].textContent;
	// alert(currentParams.indexOf(desc));
	// currentParams.splice(currentParams.indexOf(desc), 1);
	var type = desc.split(": ")[0];
	if (type != "Content" && type != "Global" && type!="CIL") {
		if (e.target.childNodes[2]) {
			var id = e.target.childNodes[2].textContent;
			document.getElementById(id).checked = false;
		}
	}
	//e.target.parentElement.removeChild(e.target);
	e.target.parentElement.parentElement.removeChild(e.target.parentElement);
	refineResults("filter");
}

/**
 * fixes bug where X was removed when span was clicked, but button remained
 * until clicked removes a clicked search term label from the top bar when the X
 * is clicked if not a keyword search, unchecks the respective input
 * 
 * @param e
 *            the event in which the user clicked the span
 */
function removeSearchTermBySpan(e) {
	var desc = e.target.parentElement.childNodes[0].textContent;
	var type = desc.split(": ")[0];
	if (type != "Content" && type != "Global" && type!="CIL") {
		var id = e.target.parentElement.childNodes[2].textContent;
		document.getElementById(id).checked = false;
	}
	//e.target.parentElement.parentElement.removeChild(e.target.parentElement);
	e.target.parentElement.parentElement.parentElement.removeChild(e.target.parentElement.parentElement);
	refineResults("filter");
}

/**
 * removes the blue label associated with an input which has been unchecked by
 * the user
 * 
 * @param name
 *            the description on the label to be removed
 */
function removeSearchTermByName(name) {
	var labels = document.getElementsByClassName("search-term-label");
	for (var i = 0; i < labels.length; i++) {
		if (labels[i].childNodes[0].textContent == name) {
			labels[i].parentElement.removeChild(labels[i]);
		}
	}
	refineResults("filter");
}

/**
 * prevents the user from searching the same term twice by checking if the label
 * already exists
 * 
 * @param desc
 *            the description on the label
 * @returns true if the term exists and false otherwise
 */
function termExists(desc) {
	var labels = document.getElementsByClassName("search-term-label");
	for (var i = 0; i < labels.length; i++) {
		if (labels[i].childNodes[0].textContent == desc) {
			return true;
		}
	}
	return false;
}

function createURL(baseURL) {
	var myUrl = baseURL + "?";
	
	var labels = document.getElementsByClassName("search-term-label");

	currentParams = new Array();
	for (var i = 0; i < labels.length; i++) {
		currentParams.push(labels[i].childNodes[3].textContent);
	}
		
	if (currentParams.length != 0) {
		for (var i = 0; i < currentParams.length; i++) {
			desc = currentParams[i].split(": ");
			myUrl += "&" + desc[0].toLowerCase() + "=" + desc[1];
			myUrl = myUrl.replace(/\s/g, '_');
		}
	}

	var selectBox = document.getElementById("sortParam");
	var selection = selectBox.options[selectBox.selectedIndex].value;
	myUrl += ("&sort_by=" + selection);
	
	return myUrl;
}

/**
 * Adds all currently selected search parameters to the currentParams array.
 * Sends an Ajax request to FilterController, which fills the page with the
 * results
 * Also re-initializes the new map with new location keys for selection. 
 */
function refineResults(filterOrSort) {
	var labels = document.getElementsByClassName("search-term-label");
	xmlHttp = new XMLHttpRequest();
	newUrl = createURL(requestUrl);
	//alert(newUrl);
	xmlHttp.open("GET", newUrl, false);
	xmlHttp.send(null);
	document.getElementById("search-results").innerHTML = xmlHttp.responseText;
	//Without eval, innerHTML is a string instead of a list. Eval fixes this.
	//making locationKeys of the list type.
	locationKeys=eval(document.getElementById("mapkeys").innerHTML);
	pompeiiMap.remove();
	hercMap.remove();
	window.initPompeiiMap("regio",false,false,false,0,locationKeys);
	window.initHerculaneumMap(true,false,false,false,0,locationKeys);
}

/*
 * Generates a report on all currently selected search parameters.
 */
function generateReport() {
	var labels = document.getElementsByClassName("search-term-label");
	xmlHttp = new XMLHttpRequest();
	newUrl = createURL(reportUrl);
	window.location = newUrl;
}

/**
 * Adds the parameter to the search terms; used to update the page to match the
 * previous results page when the user returns to the by clicking the "Back to
 * Results" button on the details page
 * 
 * @param param
 *            the search parameter (in the form parameter=value)
 */
function parseParam(param) {
	var term = param.split("=");
	var type = term[0];
	var value = term[1];
	if (type != "query_all") {
		if (type == "Content") {
			addSearchTerm("Content: " + value, 0);
		} else if (type == "Global") {
			addSearchTerm("Global: " + value, 0);
		} else if(type == "CIL"){
			addSearchTerm("CIL: " + value, 0)
		} else {
			value = value.replace("_", " ");
			var id = findId(type, value);
			type = type.replace("_", " ");
			addSearchTerm(type + ": " + value, id);
			document.getElementById(id).checked = true;
		}
	}
}

/**
 * Looks up the id of the checkbox associated with a given search term; used in
 * parseParam()
 * 
 * @param type
 *            the type of search parameter (City, Drawing Category, etc.)
 * @param value
 *            the value of the search parameter (Pompeii, Plants, etc.)
 * @returns {String} the checkbox id associated with the given value
 */
function findId(type, value) {
	var parent = document.getElementById(type);
	var labels = parent.getElementsByTagName("label");
	if (type == "Property") {
		for (var i = 0; i < labels.length; i++) {
			if (labels[i].textContent.split(" ")[0] == value) {
				return labels[i].firstChild.id;
			}
		}
	} else {
		if (type == "Insula") {
			value = "Insula " + value;
		}
		for (var i = 0; i < labels.length; i++) {
			if (labels[i].textContent == value) {
				return labels[i].firstChild.id;
			}
		}
	}
}
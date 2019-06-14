var hercMap;

var SELECTED_COLOR = '#800000';
// an orangey-yellow
var DEFAULT_COLOR = '#FEB24C';

function initHerculaneumMap(moreZoom=false,showHover=true,colorDensity=false,interactive=true,propertyIdToHighlight=0,propertyIdListToHighlight=[],zoomOnOneProperty) {
	// sets the MapBox access token
	var mapboxAccessToken = 'TOKEN';
	var borderColor;
	var fillColor;
	var southWest = L.latLng(40.8040619, 14.343131),
	northEast = L.latLng(40.8082619, 14.351131),
	bounds = L.latLngBounds(southWest, northEast);
	
	var currentZoomLevel;
	
	// The maximum zoom level to show insula view instead of property
	// view(smaller zoom level means more zoomed out)
	var insulaViewZoomLevel=17;
	
	if(moreZoom){
		currentZoomLevel=17;
	}
	else{
		currentZoomLevel=18;
	}
	
	var showInsulaMarkers;
	var zoomLevelForIndividualProperty=18;
	var initialZoomNotCalled=true;
	var totalInsulaGraffitisDict=new Array();
	var graffitiInLayer;
	var numberOfGraffitiInGroup;
	
	var insulaMarkersList=[];
	var clickedInsula=[];
	var clickedAreas = [];
	
	var insulaGroupIdsList=[];
	var insulaShortNamesDict=[];
	
	// Fires when the map is initialized
	hercMap = new L.map('herculaneummap', {
		center: [40.8059119, 14.3473933], 
		zoom: currentZoomLevel,
		minZoom: currentZoomLevel-1,
		maxZoom:20,
		maxBounds: bounds,
	});
	
	var herculaneumProperties = L.geoJson(herculaneumPropertyData, {
		style: propertyStyle,
	    onEachFeature: onEachProperty
	}).addTo(hercMap);
	
	var herculaneumInsulae = L.geoJson(herculaneumInsulaData, {
		style: insulaStyle
	}).addTo(hercMap);
	
	// Syncs with map box
	var mapboxUrl = 'https://api.mapbox.com/styles/v1/TOBECOMPLETED?access_token=' + mapboxAccessToken;
	
	if(interactive){
		makeTotalInsulaGraffitiDict();
		makeInsulaIdsListShortNamesList();
		displayInsulaLabels();
		//insulaLevelLegend.addTo(hercMap);
		//legend.remove(hercMap);
		
		hercMap.addControl(new L.Control.Compass({autoActive: true, position: "bottomleft"}));
	}
	
	// A listener for zoom events.
	hercMap.on('zoomend', function(e) {
		dealWithInsulaLevelView();
		dealWithInsulaLabelsAndSelectionOnZoom();
	});
	
	// Centers the map around a single property
	function showCloseUpView(){
		if(propertyIdToHighlight){
			var newCenterCoordinates=[];
			hercMap.eachLayer(function(layer){
				if(layer.feature!=undefined){
					if(layer.feature.properties.Property_Id==propertyIdToHighlight){
						newCenterCoordinates=layer.getBounds().getCenter();
						hercMap.setView(newCenterCoordinates,zoomLevelForIndividualProperty);
					}
				}
			});
		}
		else if(propertyIdListToHighlight.length==1){
			newCenterCoordinates=[];
			hercMap.eachLayer(function(layer){
				if(layer.feature!=undefined){
					if(layer.feature.properties.Property_Id==propertyIdListToHighlight[0]){
						newCenterCoordinates=layer.getBounds().getCenter();
						hercMap.setView(newCenterCoordinates,zoomLevelForIndividualProperty);
					}
				}
			});
		}
	}
	
	if(propertyIdToHighlight!=0 || propertyIdListToHighlight.length==1)	{
		showCloseUpView();
	}
	
	// Responsible for showing the map view on the insula level.
	function dealWithInsulaLevelView(){
		// This must be reset
		totalInsulaGraffitisDict=new Array();
		hercMap.eachLayer(function(layer){
			if(zoomedOutThresholdReached() && layer.feature!=undefined){
				graffitiInLayer=layer.feature.properties.Number_Of_Graffiti;
				layer.setStyle({color: getFillColor(graffitiInLayer)});
				currentInsulaNumber=layer.feature.properties.insula_id;
				if(totalInsulaGraffitisDict[currentInsulaNumber]!=undefined){
					totalInsulaGraffitisDict[currentInsulaNumber]+=graffitiInLayer;
				}
				else{
					totalInsulaGraffitisDict[currentInsulaNumber]=graffitiInLayer;
				}
				if(propertyLevelLegend._map) {
					propertyLevelLegend.remove(hercMap);
				}
				//insulaLevelLegend.addTo(hercMap);
			}
			// Resets properties when user zooms back in
			else if (colorDensity && layer.feature!=undefined){
				layer.setStyle({color: getBorderColorForCloseZoom(layer.feature)});
				graffitiInLayer=layer.feature.properties.Number_Of_Graffiti;
				layer.setStyle({fillColor: getFillColor(graffitiInLayer)});
				if(insulaLevelLegend._map) {
					insulaLevelLegend.remove(hercMap);
				}
				propertyLevelLegend.addTo(hercMap);
			}
			
		});
		// The second loop fills in the colors of the properties to match the
		// total group color.
		// Again, only runs if the above conditions are true.
		// Empty slots are caused by there not yet being a group at those
		// indexes yet them being surrounded by values.
		hercMap.eachLayer(function(layer){
			if(zoomedOutThresholdReached() && layer.feature!=undefined){
				currentInsulaNumber=layer.feature.properties.insula_id;
				numberOfGraffitiInGroup=totalInsulaGraffitisDict[currentInsulaNumber];
				// For an unknown reason, forEachLayer loops through two times
				// instead of one.
				// We compensate by dividing number of graffiti by two(?).
				newFillColor=getFillColor(numberOfGraffitiInGroup/2);
				layer.setStyle({fillColor:newFillColor});
				layer.setStyle({color: getFillColor(numberOfGraffitiInGroup)});
				
				borderColor=newFillColor;
			}
			
		});
		
	}
	
	// Returns a new array with the contents of the previous index absent
	// We must search for a string in the array because, again, indexOf does not
	// work for nested lists.
	function removeStringedListFromArray(someArray,stringPortion){
		var newArray=[];
		var i;
		for(i=0;i<someArray.length;i++){
			if(""+someArray[i]!=stringPortion){
				newArray.push(someArray[i]);
			}
		}
		return newArray;
	}
	
	function updateBorderColors(){
		hercMap.eachLayer(function(layer){
			if(layer.feature!=undefined && layer.feature.properties.clicked ){
				borderColor=getBorderColorForCloseZoom(layer.feature);
				// layer.feature.setStyle(color,borderColor);
			}
		});
	}
	
	// Shows or hides insula labels depending on zoom levels and if the map is
	// interactive
	function dealWithInsulaLabelsAndSelectionOnZoom(){
		// console.log("5");
		if(interactive){
			if(!zoomedOutThresholdReached()){
				if(showInsulaMarkers){
					// removeInsulaLabels();
					showInsulaMarkers=false;
					// This shows selected properties from the insula when the
					// map zooms in.
					updateBorderColors();
				}
			}
			else if(!showInsulaMarkers){
				// displayInsulaLabels();
				showInsulaMarkers=true;
			}
		}
	}
	
	// Builds the global list of insula ids.
	function makeInsulaIdsListShortNamesList(){
		var currentInsulaId=183;
		hercMap.eachLayer(function(layer){
			if(layer.feature!=undefined){
				if(layer.feature.properties.insula_id!=currentInsulaId){
					if(insulaGroupIdsList.indexOf(currentInsulaId)==-1){
						insulaGroupIdsList.push(currentInsulaId);
					}
				}
				currentInsulaId=layer.feature.properties.insula_id;
				insulaShortNamesDict[currentInsulaId]=layer.feature.properties.short_insula_name;	
			}
		});
	}
	
	// Builds the dictionary of the graffiti in each insula
	// This works well as graffiti numbers should not change over the session.
	// Modifies the clojure wide variable once and only once at the beginning of
	// the program
	function makeTotalInsulaGraffitiDict(){
		totalInsulaGraffitisDict=new Array();
		hercMap.eachLayer(function(layer){
			if(zoomedOutThresholdReached() && layer.feature!=undefined){
				graffitiInLayer=layer.feature.properties.Number_Of_Graffiti;
				currentInsulaNumber=layer.feature.properties.insula_id;
				if(totalInsulaGraffitisDict[currentInsulaNumber]!=undefined){
					totalInsulaGraffitisDict[currentInsulaNumber]+=graffitiInLayer;
				}
				else {
					totalInsulaGraffitisDict[currentInsulaNumber]=graffitiInLayer;
				}
			}
		});
	}
	
	// Marks all properties inside of selected insula as selected by
	// adding them to the clickedInsula list.
	function selectPropertiesInAllSelectedInsula(uniqueClicked){
		if(interactive){
			var i=0;
			var currentInsulaId;
			var currentInsula;
			var listOfSelectedInsulaIds=[];
			for(i;i<clickedInsula.length;i++){
				currentInsula=clickedInsula[i];
				currentInsulaId=currentInsula[1];
				listOfSelectedInsulaIds.push(currentInsulaId);	
			}
			hercMap.eachLayer(function(layer){
				if(layer.feature!=undefined){
					if(listOfSelectedInsulaIds.indexOf(layer.feature.properties.insula_id)!=-1 && !uniqueClicked.includes(layer)){
						uniqueClicked.push(layer);
						layer.feature.properties.clicked=true;

					}
				}
			});
		}
		return uniqueClicked;

	}
	
	var createLabelIcon = function(labelClass,labelText){
		return L.divIcon({ 
			className: labelClass,
			html: labelText
		});
	}
	
	// Shows the names of each insula at the center of the feature.
	function displayInsulaLabels(){
		herculaneumInsulae.eachLayer(function(layer){
			//layer.bindTooltip(layer.feature.properties.name, {permanent:true, direction:'centered'}).addTo(hercMap);
			if(layer.feature!=undefined){
				var myIcon = L.divIcon({ 
					className: "labelClassHerculaneum",
					html: formatInsulaLabels(layer.feature.properties.name),
					// shifting the iconAnchor to get the labels for the insula a little closer to center of the insula
					iconAnchor: shiftIconAnchor(layer.feature.properties.name)
				});
				var myMarker=new L.marker(layer.getBounds().getCenter(), {icon: myIcon}).addTo(hercMap);
				insulaMarkersList.push(myMarker);
			}
		});
	}
	
	//shift labels of the insulae to make them better centered
	function shiftIconAnchor(insulaName){
		if (insulaName.valueOf() == new String("Insula Orientalis I").valueOf()){
			return [27,30];
		}
		else if (insulaName.valueOf() == new String("Insula Orientalis II").valueOf()){
			return [55,5];	
		}
		else if (insulaName.valueOf() == new String("Insula II").valueOf()){
			return[26,27];
		}
		else if (insulaName.valueOf() == new String("Insula VII").valueOf()){
			return [40, 30];
		}
		else if (insulaName.valueOf() == new String("Insula III").valueOf()){
			return [25, 10];
		}
		else{
			return [25,15];
		}
	}
	
	//make Insula Orientalis I be on two lines
	function formatInsulaLabels(insulaName){
		if (insulaName.valueOf() == new String("Insula Orientalis I").valueOf()){
			return "Insula<br/>Orientalis I";
		}
		else{
			return insulaName;
		}
	}
	
	function zoomedOutThresholdReached(){
		currentZoomLevel=hercMap.getZoom();
		return (currentZoomLevel<=insulaViewZoomLevel && colorDensity);
	}
	
	function zoomedInThresholdReached(){
		currentZoomLevel=hercMap.getZoom();
		return (currentZoomLevel>=zoomLevelForIndividualProperty && colorDensity);
	}
	
	function getBorderColorForCloseZoom(feature){
		borderColor='white';
		if (isPropertySelected(feature)) {
			return 'black';
		}
		return 'white';
	}
	
	function isPropertySelected(feature) {
		return feature.properties.clicked == true || feature.properties.Property_Id==propertyIdToHighlight || propertyIdListToHighlight.indexOf(feature.properties.Property_Id)>=0;
	}
	
	// Sets the style of the portions of the map. Color is the outside borders.
	// There are different colors for
	// clicked or normal fragments. When clicked, items are stored in a
	// collection. These collections will have the color
	// contained inside of else.
	function propertyStyle(feature) {
		// Displays the insula level view at the start of the run if necessary
		borderColor=getBorderColorForCloseZoom(feature);
		if( isPropertySelected(feature)) {
			fillColor = SELECTED_COLOR;
		}
		else if( colorDensity ) {
			fillColor = getFillColor( feature.properties.Number_Of_Graffiti);
		} else {
			fillColor = getFillColorByFeature(feature);
		}
		return { 
			fillColor:fillColor,
			weight: 1,
			opacity: 1,
			color: borderColor,
			fillOpacity: 0.7,
			zIndex: 1,
		};
	}
	
	function insulaStyle(feature) {
		borderColor="black";
		fillColor = DEFAULT_COLOR;
		
		return { 
			fillColor:fillColor,
			width:200,
			height:200, 
			weight: 1,
			opacity: 1,
			color: borderColor,
			fillOpacity: 0.7,
			zIndex: 2,
		};
	}
	
	function getFillColor(numberOfGraffiti){
		if(colorDensity){
			if( zoomedOutThresholdReached() ) { // for insula level
				return numberOfGraffiti == 0   ? '#FFEDC0' :
					   numberOfGraffiti <= 5   ? '#FFEDA0' :
					   numberOfGraffiti <= 10  ? '#fed39a' :
					   numberOfGraffiti <= 20  ? '#fec880' :
					   numberOfGraffiti <= 90 ? '#FEB24C':
												 '#000000';
			} else { // for property level
				return numberOfGraffiti == 0   ? '#FFEDC0' :
					   numberOfGraffiti <= 5   ? '#FFEDA0' :
					   numberOfGraffiti <= 10  ? '#fed39a' :
					   numberOfGraffiti <= 90  ? '#fec880' :
												 '#000000';
			}
		}
		
		return DEFAULT_COLOR;
	}
	
	function getFillColorByFeature(feature){
		if( isPropertySelected(feature)) {
			return SELECTED_COLOR;
		}
		return DEFAULT_COLOR;
	}
	
	// On click, sees if a new insula id # has been selected. If so, adds it to
	// the list of
	// selected insula.
	function checkForInsulaClick(clickedProperty){
		// Clicked property is a layer
		// layer.feature.properties.insula_id
		
		// indexOf does not work for nested lists. Thus, we have no choice but
		// to use it with strings.
		var clickedInsulaAsString=""+clickedInsula;
		var clickedInsulaFullName=clickedProperty.feature.properties.full_insula_name;
		var clickedInsulaId=clickedProperty.feature.properties.insula_id;
		var clickedInsulaShortName=clickedProperty.feature.properties.short_insula_name;
		var targetInsulaString=""+[clickedInsulaFullName,clickedInsulaId,clickedInsulaShortName];
		var indexOfInsulaName=clickedInsulaAsString.indexOf(targetInsulaString);

		// Only adds the new id if it is not already in the list
		if(indexOfInsulaName==-1){
			clickedInsula.push([clickedInsulaFullName,clickedInsulaId,clickedInsulaShortName]);
		}
		// Otherwise, removed the insula id from the list to deselect it
		else{
			clickedInsula=removeStringedListFromArray(clickedInsula,targetInsulaString);
		}
	}
	
	// Used on click for insula level view in place of display selected regions
	// In charge of the right information only, does not impact the actual map
	function displayHighlightedInsula(){
		// clickedInsula.push([clickedInsulaFullName,clickedInsulaId,clickedInsulaShortName]);
		var html = "<strong>Selected Insula:</strong><ul>";
		var numberOfInsulaSelected=clickedInsula.length;
		for (var i=0; i<numberOfInsulaSelected; i++) {
			html += "<li>"+clickedInsula[i][0] + ", " +
					"<p>"+totalInsulaGraffitisDict[clickedInsula[i][1]]+" graffiti</p>"+ "</li>"
		}
		html += "</ul>";
		// Checks to avoid error for element is null.
		var elem = document.getElementById("toSearch");
		if(typeof elem !== 'undefined' && elem !== null) {
			document.getElementById("toSearch").innerHTML = html;
		}
	}
	
	// Sets color for properties which the cursor is moving over.
	function highlightFeature(e) {
		if(interactive && !zoomedOutThresholdReached()){
			var layer = e.target;
			layer.setStyle({
				color:'maroon',
				strokeWidth:"6"
			});
		
			if (!L.Browser.ie && !L.Browser.opera) {
				layer.bringToFront();
				//keep black outline of insulae at the front when hovering over properties
				herculaneumInsulae.bringToFront();
			}
			info.update(layer.feature.properties);
		}
	}
	
	
	// If they have been clicked and are clicked again, sets to false and vice versa. I am
	// confused what pushToFront is
	// or how it interacts with the wider collection of items if there is one.
	function showDetails(e) {
		if(interactive){
			if(!zoomedOutThresholdReached()){
				var layer = e.target;
				if (layer.feature.properties.clicked != null) {
					layer.feature.properties.clicked = !layer.feature.properties.clicked;
					if(layer.feature.properties.clicked == false) {
						resetHighlight(e);
						var index = clickedAreas.indexOf(layer);
						if(index > -1) {
							clickedAreas.splice(index, 1);
						}
					} else {
						e.target.setStyle({fillColor:SELECTED_COLOR});
						clickedAreas.push(layer);
					}
				} else {
					layer.feature.properties.clicked = true;
					e.target.setStyle({fillColor:SELECTED_COLOR});
					clickedAreas.push(layer);
				}
				if (!L.Browser.ie && !L.Browser.opera) {
			        layer.bringToFront();
			    }
				info.update(layer.feature.properties);
			}
			else {
				checkForInsulaClick(e.target);
			}
		}
	}
	
	
	// Used to reset the color, size, etc of items to their default state(ie.
	// after being clicked twice)
	function resetHighlight(e) {
		if(interactive && !zoomedOutThresholdReached()){
			herculaneumProperties.resetStyle(e.target);
			info.update();
		}
	}

	// Calls the functions on their corresponding events for EVERY feature(from
	// tutorial)
	function onEachProperty(feature, layer) {
	    layer.on({
	        mouseover: highlightFeature,
	        mouseout: resetHighlight,
	        click: showDetails,
	    });
	}
	
	// Putting this after the above appears to make it this start correctly.
	if(initialZoomNotCalled==true){
		dealWithInsulaLevelView();
		initialZoomNotCalled=false;
	}
	
	var info = L.control();
	info.onAdd = function(map) {
		// create a div with a class "info"
		this._div = L.DomUtil.create('div', 'info'); 
	    this.update();
	    return this._div;
	};
	
	// method that we will use to update the control based on feature properties
	// passed
	function updateHoverText(){
		info.update = function (props) {
			if(showHover){
				this._div.innerHTML = (props ? props.Property_Address + " " + props.Property_Name
						: 'Hover over property to see name');
			}
		};
	
		info.addTo(hercMap);
	}
	
	updateHoverText();
	
	// Used to acquire all of the items clicked for search(red button "Click
	// here to search).
	// Does this by iterating through the list of clicked items and adding them
	// to uniqueClicked,
	// then returning uniqueClicked.
	function getUniqueClicked() {
		var uniqueClicked = [];
		var length = clickedAreas.length;
		for (var i = 0; i < length; i++) {
			var property = clickedAreas[i];
			if (!uniqueClicked.includes(property)) {
				uniqueClicked.push(property)
			}
		}
		uniqueClicked=selectPropertiesInAllSelectedInsula(uniqueClicked);
		return uniqueClicked;
	}

	// Collects the ids of the clicked item objects(the id property).
	function collectClicked() {
		var propIdsOfClicked = [];
		var selectedProps = getUniqueClicked();
		var length = selectedProps.length;
		for (var i=0; i<length; i++) {
			var property = selectedProps[i];
			var propertyID = property.feature.properties.Property_Id;
			propIdsOfClicked.push(propertyID);
		}
		
		return propIdsOfClicked;
	}
	
	// function called when user clicks the search button.
	function searchProperties() {
		var highlighted = collectClicked();
		var argString = "";
		if (highlighted.length > 0){
			for (var i = 0; i < highlighted.length; i++) {
				argString = argString + "property=" + highlighted[i];
				argString = argString + "&";
			}
			window.location = "results?" + argString;
			return true;
		}
		else {
			document.getElementById("hidden_p").style.visibility = "visible";
		}
	}
	
	// Displays the Selected Properties and their corresponding information in
	// an HTML table formatted.
	// Achieved by mixing html and javascript, accessing text properties of the
	// regions(items).
	function displayHighlightedRegions() {
		if(!zoomedOutThresholdReached()){
			var clickedAreasTable = getUniqueClicked();
			
			var html = "<strong>Selected Properties:</strong><ul>";
			var length = clickedAreasTable.length;
			for (var i=0; i<length; i++) {
				var property = clickedAreasTable[i];
				/* alert(property.feature.geometry.coordinates); */
				if (property.feature.properties.clicked === true) {
					
					html += "<li>" +property.feature.properties.Property_Address + ", " +property.feature.properties.Property_Name +
							"<p>"+property.feature.properties.Number_Of_Graffiti+" graffiti</p>"+ "</li>";
				}
			}
			html += "</ul>";
			// Checks to avoid error for element is null.
			var elem = document.getElementById("toSearch");
			if(typeof elem !== 'undefined' && elem !== null) {
				document.getElementById("toSearch").innerHTML = html;
			}
				
		}
		else{
			displayHighlightedInsula();
			var clickedAreasTable = getUniqueClicked();
		}	
	}
	
	// handles additional events.
	var el = document.getElementById("search");
	if(el!=null){
		el.addEventListener("click", searchProperties, false);
	}
	
	var el2 = document.getElementById("herculaneummap");
	if(el2!=null){
		el2.addEventListener("click", displayHighlightedRegions, false);
	}
	
}

var pompeiiMap;

var SELECTED_COLOR = '#800000';
var DEFAULT_COLOR = '#FEB24C';
var wallBorderColor='black';
var wallFillColor='black';
var streetBorderColor='#888';
var streetFillColor='#888';
var unexBorderColor='#C9C9C9';
var unexFillColor='#CDCDCD';

var REGIO_VIEW_ZOOM=15;
// The minimum zoom level to show insula view instead of property
// view (smaller zoom level means more zoomed out)
var LARGE_REGIO_ZOOM=16;
var INSULA_VIEW_ZOOM = 17;
var INDIVIDUAL_PROPERTY_ZOOM=18;

var zoomLevels = {};
zoomLevels["regio"] = REGIO_VIEW_ZOOM;
zoomLevels["large"] = LARGE_REGIO_ZOOM;
zoomLevels["insula"] = INSULA_VIEW_ZOOM;
zoomLevels["property"] = INDIVIDUAL_PROPERTY_ZOOM;

function initPompeiiMap(zoomLevel="large",showHover=true,colorDensity=false,interactive=true,propertyIdToHighlight=0,propertyIdListToHighlight=[],zoomOnOneProperty, toggle=false) {
	// sets MapBox access token
	var mapboxAccessToken = 'TOKEN';
	var borderColor;
	var southWest = L.latLng(40.746, 14.48),
	northEast = L.latLng(43.754, 15.494),
	bounds = L.latLngBounds(southWest, northEast);

	var propertyLayer, pompeiiWallsLayer, backgroundInsulaLayer, pompeiiStreetsLayer, pompeiiUnexcavatedLayer, pompeiiInsulaLayer;

	var currentZoomLevel = zoomLevels[zoomLevel];
	var showInsulaMarkers;

	var totalInsulaGraffitisDict=new Array();
	// The list of active insula markers.
	// Can be iterated through to remove all markers from the map(?)
	var insulaMarkersList=[];
	var regioMarkersList=[];
	var unexcavatedMarkersList=[];

	var clickedAreas = [];
	// A list filled with nested list of the full name, id, and short name of
	// each insula selected.
	var clickedInsula=[];

	// Holds the center latitudes and longitudes of all insula on the map.
	var insulaCentersDict=[];
	var insulaGroupIdsList=[];
	var insulaShortNamesDict=[];

	//Holds the center latitudes and longitudes of all unexcavated areas on the map
	var unexcavatedCentersList=[];

	// Variables for all things regio:
	var regioCentersDict={};
	var regioNamesList=[];
	var graffitiInEachRegioDict={};

	// Shows or hides insula labels depending on zoom levels and if the map is
	// interactive
	function dealWithLabelsAndSelection(){
		if(interactive){
			currentZoomLevel=pompeiiMap.getZoom();
			if(currentZoomLevel< INSULA_VIEW_ZOOM){
				removeInsulaLabels();
				displayInsulaLabels();
			}
			else if(currentZoomLevel>=INSULA_VIEW_ZOOM) {
				removeInsulaLabels();
				displayInsulaLabels();
			}
			else {
				displayInsulaLabels();
			}
		}
	}

	// Centers the map around a single property
	function showCloseUpView(){
		if(propertyIdToHighlight){
			var newCenterCoordinates=[];
			propertyLayer.eachLayer(function(layer){
				if(layer.feature!=undefined){
					if(layer.feature.properties.Property_Id==propertyIdToHighlight){
						newCenterCoordinates=layer.getBounds().getCenter();
						pompeiiMap.setView(newCenterCoordinates,INDIVIDUAL_PROPERTY_ZOOM);
					}
				}
			});
		} // below is for insula?
		else if(propertyIdListToHighlight.length==1){
			var newCenterCoordinates=[];
			var idOfListHighlight=propertyIdListToHighlight[0];
			insulaLayer.eachLayer(function(layer){
				if(layer.feature!=undefined){
					if(layer.feature.properties.insula_id==idOfListHighlight){
						newCenterCoordinates=layer.getBounds().getCenter();
						pompeiiMap.setView(newCenterCoordinates,INDIVIDUAL_PROPERTY_ZOOM);
					}
				}
			});
		}
	}

	// Builds the global list of insula ids.
	function makeInsulaIdsListShortNamesList(){
		var currentInsulaId;
		pompeiiInsulaLayer.eachLayer(function(layer){
			if(layer.feature!=undefined){
				currentInsulaId=layer.feature.properties.insula_id;
				//if(layer.feature.properties.insula_id!=currentInsulaId){
				if(insulaGroupIdsList.indexOf(currentInsulaId)==-1){
					insulaGroupIdsList.push(currentInsulaId);
				}
				//}
				insulaShortNamesDict[currentInsulaId]=layer.feature.properties.insula_short_name;
			}
		});
	}

	function makeListOfRegioNames(){
		var someName;
		pompeiiInsulaLayer.eachLayer(function(layer){
			if(layer.feature!=undefined){
				someName=layer.feature.properties.insula_short_name.split(".")[0];
				if(regioNamesList.indexOf(someName)==-1){
					regioNamesList.push(someName);
				}
			}
		});
	}

	function makeTotalRegioGraffitiDict(){
		var currentNumberOfGraffiti;
		var currentRegioName;
		var regioNamesSoFar=[];
		pompeiiInsulaLayer.eachLayer(function(layer){
			if(layer.feature!=undefined){
				currentRegioName=layer.feature.properties.insula_short_name.split(".")[0];
				currentNumberOfGraffiti=layer.feature.properties.number_of_graffiti;
				if(regioNamesSoFar.indexOf(currentRegioName)==-1){
					regioNamesSoFar.push(currentRegioName);
					graffitiInEachRegioDict[currentRegioName]=currentNumberOfGraffiti;
				}
				else{
					graffitiInEachRegioDict[currentRegioName]+=currentNumberOfGraffiti;
				}
			}
		});
	}


	// Builds the dictionary of the graffiti in each insula
	// This works well as graffiti numbers should not change over the session.
	// Modifies the clojure wide variable once and only once at the beginning of
	// the program
	function makeTotalInsulaGraffitiDict(){
		totalInsulaGraffitisDict=new Array();
		pompeiiInsulaLayer.eachLayer(function(layer){
			if(insulaViewZoomThresholdReached() && layer.feature!=undefined){
				graffitiInLayer=layer.feature.properties.number_of_graffiti;
				currentInsulaNumber=layer.feature.properties.insula_id;
				if(totalInsulaGraffitisDict[currentInsulaNumber]!=undefined){
					totalInsulaGraffitisDict[currentInsulaNumber]+=graffitiInLayer;
				}
				else{
					totalInsulaGraffitisDict[currentInsulaNumber]=graffitiInLayer;
				}
			}
		});
	}

	// Meant to show the insula short name labels at the given x/y coordinates
	// (given as a normal list in Java array form)
	function showALabelOnMap(xYCoordinates,textToDisplay,textSize="small", markerType){
		currentZoomLevel=pompeiiMap.getZoom();
		if (markerType=="insula" && currentZoomLevel < INSULA_VIEW_ZOOM){
			var splitLabel = textToDisplay.split(".");
			//textToDisplay = "<sup>" + splitLabel[0] + "</sup>&#47;<sub>" + splitLabel[1] +"</sub>";
			var formattedText = "<u><sub>" + splitLabel[0] + "</sub></u><br><sup>" + splitLabel[1] +"</sup>";
			var myIcon= L.divIcon({
				// iconSize: new L.Point(0, 0),
				// iconSize:0,
				className: "labelClass",
				html: formattedText
			});
		}
		else if (markerType=="insula" && currentZoomLevel >= INSULA_VIEW_ZOOM){
			var splitLabel = textToDisplay.split(".");
			//textToDisplay = "<sup>" + splitLabel[0] + "</sup>&#47;<sub>" + splitLabel[1] +"</sub>";
			var formattedText = "<u><sub>" + splitLabel[0] + "</sub></u><br><sup>" + splitLabel[1] +"</sup>";
			var myIcon= L.divIcon({
				// iconSize: new L.Point(0, 0),
				// iconSize:0,
				className: "labelClassInsula",
				html: formattedText
			});
		}
		else{
			var myIcon= L.divIcon({
				// iconSize: new L.Point(0, 0),
				// iconSize:0,
				className: "labelClass",
				html: textToDisplay
			});
		}

		var myMarker;
		myMarker=new L.marker([xYCoordinates[0], xYCoordinates[1]], {icon: myIcon}).addTo(pompeiiMap);

		if(markerType=="insula") {
			myMarker.on({
		        mouseover: highlightInsulaFromMarker,
		        mouseout: resetHighlightFromMarker,
		        click: showInsulaDetailsFromMarker,
		    });
			insulaMarkersList.push(myMarker);
		} else if (markerType=="regio") {
			regioMarkersList.push(myMarker)
		} else if (markerType=="unexcavated") {
			unexcavatedMarkersList.push(myMarker);
		}
	}

	//returns insula id of the insula that the marker represents
	function getInsulaFromMarker(insulaMarker){
		var markerCoordinates = [insulaMarker._latlng["lat"], insulaMarker._latlng["lng"]];
		var insulaToHighlight;
		for (var i=0;i<insulaGroupIdsList.length;i++){
			insulaID=insulaGroupIdsList[i];
			insulaCenterCoordinates=insulaCentersDict[insulaID];
			if (insulaCenterCoordinates[0] == markerCoordinates[0] && insulaCenterCoordinates[1] == markerCoordinates[1]){
				return insulaID;
			}
		}
		return -1;
	}

	//highlights insula if mouseover the label of insula
	function highlightInsulaFromMarker(e){
		var insulaMarker = e.target;
		var insulaToHighlight = getInsulaFromMarker(insulaMarker);
		pompeiiInsulaLayer.eachLayer(function(layer){
			if(layer.feature!=undefined && interactive){
				if (layer.feature.properties.insula_id == insulaToHighlight){
					highlightInsula(layer);
				}
			}
		});
	}

	//resets highlight of insula if mouseout of the label of insula
	function resetHighlightFromMarker(e){
		var insulaMarker = e.target;
		var insulaToHighlight = getInsulaFromMarker(insulaMarker);
		pompeiiInsulaLayer.eachLayer(function(layer){
			if(layer.feature!=undefined && interactive){
				if (layer.feature.properties.insula_id == insulaToHighlight){
					resetHighlight(layer);
				}
			}
		});
	}

	//selects and shows details of insula if click on the label of insula
	function showInsulaDetailsFromMarker(e){
		var insulaMarker = e.target;
		var insulaToHighlight = getInsulaFromMarker(insulaMarker);
		pompeiiInsulaLayer.eachLayer(function(layer){
			if(layer.feature!=undefined && interactive){
				if (layer.feature.properties.insula_id == insulaToHighlight){
					showInsulaDetails(layer);
				}
			}
		});
	}



	// Removes each of the insula labels from the map.
	// Meant to be used for when the user zooms past the zoom threshold.
	// Stopped being used due to recommendations at the demo.
	function removeInsulaLabels(){
		var i=0;
		for(i;i<insulaMarkersList.length;i++){
			pompeiiMap.removeLayer(insulaMarkersList[i]);
		}
	}

	function removeRegioLabels(){
		var i=0;
		for(i;i<regioMarkersList.length;i++){
			pompeiiMap.removeLayer(regioMarkersList[i]);
		}
	}

	// Shows the short names of each insula in black
	// at the center ccoordinates.
	function displayInsulaLabels(){
		var i;
		var insulaId;
		var insulaCenterCoordinates;
		var shortInsulaName;
		for(i=0;i<insulaGroupIdsList.length;i++){
			insulaId=insulaGroupIdsList[i];
			insulaCenterCoordinates=insulaCentersDict[insulaId];
			shortInsulaName=insulaShortNamesDict[insulaId];
			//insulaCenterCoordinates= adjustInsulaCenters(shortInsulaName, insulaCenterCoordinates);
			if(insulaCenterCoordinates!=null){
				showALabelOnMap(insulaCenterCoordinates,shortInsulaName, "small", "insula");
			}
		}
	}

	function adjustInsulaCenters(insulaID, center){
		if (insulaID=="I.8"){
			center[0]+=0.0001;
			center[1]+=0.0001;
		} else if (insulaID=="VII.12"){
			center[0]+=0.00021;
			center[1]+=0;
		} else if (insulaID=="V.1"){
			center[0]+=0.0002;
			center[1]+=0.00015;
		} else if (insulaID=="II.7"){
			center[0]+=0.00005;
			center[1]+=0.00005;
		}
		return center;
	}

	//labels 3 unexcavated areas with "unexcavated at adjusted
	//center coordinates
	function displayUnexcavatedLabels(){
		var i;
		var currentCenter;
		var currentCoordinatesList;
		var displayTheseUnexAreas = [];
		displayTheseUnexAreas.push(unexcavatedCentersList[0]);
		displayTheseUnexAreas.push(unexcavatedCentersList[1]);
		displayTheseUnexAreas.push(unexcavatedCentersList[6]);
		displayTheseUnexAreas=adjustUnexcavatedCenters(displayTheseUnexAreas);
		for(i=0; i<displayTheseUnexAreas.length; i++){
			currentCenter=displayTheseUnexAreas[i];
			if(currentCenter!=null){
				showALabelOnMap(currentCenter, "unexcavated", "small", "unexcavated");
			}
		}

	}

	//adjusts the unexcavated areas with
	function adjustUnexcavatedCenters(listOfCenters){
		//adjust bottom right unex area
		listOfCenters[0][0]-=0.0003;
		listOfCenters[0][1]-=0.0007;
		//adjust top right unex area
		listOfCenters[1][0]+=0.0003;
		listOfCenters[1][1]-=0.0005;
		//adjust top left unex area
		listOfCenters[2][0]+=0.0005;
		listOfCenters[2][1]-=0.0003;

		return listOfCenters;
	}

	function displayRegioLabels(){
		var i;
		var regioCenterCoordinates;
		var regioName;
		for(i=0;i<regioNamesList.length;i++){
			regioName=regioNamesList[i];
			regioCenterCoordinates=regioCentersDict[regioName];
			if(regioCenterCoordinates!=null){
				showALabelOnMap(regioCenterCoordinates,regioName,"large", "regio");
			}
		}
	}

	function addMoreLatLng(oldList,newList){
		oldList=[oldList[0]+newList[0],oldList[1]+newList[1]];
		return oldList;
	}

	// This way will take more compiler time.
	// Trying to do it inside make center dict was too confusing/complex for me.
	function makeTotalPropsPerRegioDict(totalPropsSoFarDict){
		var regioList=[];
		var currentRegio;
		var currentCount;
		pompeiiInsulaLayer.eachLayer(function(layer){
			if(layer.feature!=undefined){
				currentRegio=layer.feature.properties.insula_short_name.split(".")[0];

				if(regioList.indexOf(currentRegio)==-1){
					currentCount=1;
					totalPropsSoFarDict[currentRegio]=currentCount;
					regioList.push(currentRegio);
				}
				else{
					currentCount=totalPropsSoFarDict[currentRegio];
					totalPropsSoFarDict[currentRegio]=currentCount+1;
				}
			}
		});
		return totalPropsSoFarDict;
	}

	// Works like the maker for insula centers dict but for Regio instead.
	// Needed to account for the fact that Regio were not ordered one to the
	// other in database.
	function makeRegioCentersDict(){
		var currentRegioName;
		var latLngList;
		var totalPropsSoFarDict={};
		var regioNamesSoFar=[];
		var currentRegioName;
		totalPropsSoFarDict=makeTotalPropsPerRegioDict(totalPropsSoFarDict);
		pompeiiInsulaLayer.eachLayer(function(layer){
			if(layer.feature!=undefined){
				currentRegioName=layer.feature.properties.insula_short_name.split(".")[0];
				if(regioNamesSoFar.indexOf(currentRegioName)==-1){
					regioNamesSoFar.push(currentRegioName);
					if(layer.feature.geometry.coordinates!=undefined){
						regioCentersDict[currentRegioName]=findCenter(layer.feature.geometry.coordinates[0]);
					}
					else{
						regioCentersDict[currentRegioName]=0;
					}
				}
				else{
					if(layer.feature.geometry.coordinates!=undefined){
						regioCentersDict[currentRegioName]=addMoreLatLng(regioCentersDict[currentRegioName],[findCenter(layer.feature.geometry.coordinates[0])[0],findCenter(layer.feature.geometry.coordinates[0])[1]]);
					}
				}
			}
		});
		for(var key in regioCentersDict){
			var div=[regioCentersDict[key][0]/totalPropsSoFarDict[key],regioCentersDict[key][1]/totalPropsSoFarDict[key]];
			regioCentersDict[key]=div;
		}
	}

	// This function gets and returns a "dictionary" of the latitude and
	// longitude of each insula given its id(as index).
	// Used to find where to place the labels of each insula on the map, upon
	// iteration through this list.
	function makeInsulaCentersDict(){
		var currentInsulaNumber;
		// Manually set as the first insula id for pompeii
		var oldInsulaNumber=-1;
		var xSoFar=0;
		var ySoFar=0;
		var latLngList;
		var currentCoordinatesList;
		var propertiesSoFar=0;

		insulaCentersDict[oldInsulaNumber]=[xSoFar,ySoFar];

		pompeiiInsulaLayer.eachLayer(function(layer){
			propertiesSoFar+=1;
			if(layer.feature!=undefined && interactive){
				currentInsulaNumber=layer.feature.properties.insula_id;
				currentCoordinatesList=layer.feature.geometry.coordinates;

				xAndYAddition=findCenter(currentCoordinatesList[0]);
				ySoFar+=xAndYAddition[0];
				xSoFar+=xAndYAddition[1];
				// Add to dictionary:
				// Both divisions are required
				latLngList=[xSoFar/propertiesSoFar,ySoFar/propertiesSoFar];
				// This treats the currentInsulaNumber as a key to the
				// dictionary

				latLngList=adjustInsulaCenters(layer.feature.properties.insula_short_name, latLngList);

				insulaCentersDict[currentInsulaNumber]=latLngList;

				// Reset old variables:
				xSoFar=0;
				ySoFar=0;
				propertiesSoFar=0;
				oldInsulaNumber=currentInsulaNumber;
			}
		});
	}

	function makeUnexcavatedCentersList(){
		var xSoFar=0;
		var ySoFar=0;
		var latLngList;
		var currentCoordinatesList;
		var propertiesSoFar=0;

		pompeiiUnexcavatedLayer.eachLayer(function(layer){
			propertiesSoFar+=1;
			if(layer.feature!=undefined && interactive){
				currentCoordinatesList=layer.feature.geometry.coordinates;
				xAndYAddition=findCenter(currentCoordinatesList[0]);
				ySoFar+=xAndYAddition[0];
				xSoFar+=xAndYAddition[1];
				// Add to dictionary:
				// Both divisions are required
				latLngList=[xSoFar/propertiesSoFar,ySoFar/propertiesSoFar];
				// This treats the currentInsulaNumber as a key to the
				// dictionary

				unexcavatedCentersList.push(latLngList);

				// Reset old variables:
				xSoFar=0;
				ySoFar=0;
				propertiesSoFar=0;
				//}
			}
		});
	}

	// Uses math to directly find and return the latitude and longitude of the
	// center of a list of coordinates.
	// Returns a list of the latitude, x and the longitude, y
	function findCenter(coordinatesList){
		var i=0;
		var x=0;
		var y=0
		var pointsSoFar=0;
		for(i;i<coordinatesList.length;i++){
			x+=coordinatesList[i][0];
			y+=coordinatesList[i][1];
			pointsSoFar+=1;
		}
		return [x/pointsSoFar,y/pointsSoFar];
	}

	// Responsible for showing the map view on the insula level.
	function dealWithInsulaLevelPropertyView(){
		if(insulaViewZoomThresholdReached() && interactive) {
			//if(propertyLayer._map) {
				//propertyLayer.remove(pompeiiMap);
			//}
			pompeiiInsulaLayer.addTo(pompeiiMap);
			pompeiiInsulaLayer.eachLayer(function(layer){
				if(regioViewZoomThresholdReached() && layer.feature!=undefined){
					regioName=layer.feature.properties.insula_short_name.split(".")[0];
					numberOfGraffitiInGroup=graffitiInEachRegioDict[regioName];
					// numberOfGraffitiInGroup=layer.feature.properties.number_of_graffiti;
					newFillColor=getFillColor(numberOfGraffitiInGroup);
					if(!clickedAreas.includes(layer.feature)){
						layer.setStyle({fillColor:newFillColor});
						layer.setStyle({color: getFillColor(numberOfGraffitiInGroup)});
						layer.setStyle({fillOpacity: 0.7});
					}
				} else if(insulaViewZoomThresholdReached() && layer.feature!=undefined && !regioViewZoomThresholdReached()) {
					currentInsulaNumber=layer.feature.properties.insula_short_name;
					// numberOfGraffitiInGroup=totalInsulaGraffitisDict[currentInsulaNumber];
					numberOfGraffitiInGroup=layer.feature.properties.number_of_graffiti;
					newFillColor=getFillColor(numberOfGraffitiInGroup);
					if(!clickedAreas.includes(layer.feature)){
						layer.setStyle({fillColor:newFillColor});
						layer.setStyle({color: getFillColor(numberOfGraffitiInGroup)});
						layer.setStyle({fillOpacity: 0.7});
					}

				}
			});
		}

		//deals with zoomed out pompeii results map (insula level)
		if(resultsViewThresholdReached() && !colorDensity && !interactive){
			//if(propertyLayer._map) {
				//propertyLayer.remove(pompeiiMap);
			//}
			//pompeiiInsulaLayer.addTo(pompeiiMap);
			pompeiiInsulaLayer.eachLayer(function(layer){
				if (layer.feature!=undefined) {
					currentInsulaNumber=layer.feature.properties.insula_short_name;
					//numberOfGraffitiInGroup=totalInsulaGraffitisDict[currentInsulaNumber];
					numberOfGraffitiInGroup=layer.feature.properties.number_of_graffiti;
					newFillColor=getFillColor(numberOfGraffitiInGroup);

					if(isInsulaSelected(layer.feature)) {
						layer.setStyle({fillColor:SELECTED_COLOR});
						layer.setStyle({color:SELECTED_COLOR});
					}
					else{
						layer.setStyle({fillColor:newFillColor});
						layer.setStyle({color: getFillColor(numberOfGraffitiInGroup)});
					}
					layer.setStyle({fillOpacity:0.95});
					layer.bringToFront();
				}
			});
		}

		//deals with zoomed in pompeii results map (property level)
		if(!resultsViewThresholdReached() && !colorDensity && !interactive){
			//propertyLayer.addTo(pompeiiMap);
			pompeiiInsulaLayer.eachLayer(function(layer){
				if (layer.feature!=undefined) {
					layer.setStyle({fillColor:DEFAULT_COLOR});
					layer.setStyle({color:DEFAULT_COLOR});
					layer.setStyle({fillOpacity:1});
				}
			});
			propertyLayer.eachLayer(function(layer){
				layer.bringToFront();
			});
		}

		//zoomed out (no colorDensity on the main search map, change that to interactive as condition)
		// Resets properties when user zooms back in
		if (!insulaViewZoomThresholdReached() && interactive){
			if(pompeiiInsulaLayer._map) {
				pompeiiInsulaLayer.remove(pompeiiMap);
			}
			//propertyLayer.addTo(pompeiiMap);
			/*pompeiiInsulaLayer.eachLayer(function(layer){
				layer.setStyle({fillColor:DEFAULT_COLOR});
				layer.setStyle({color:DEFAULT_COLOR});
				layer.setStyle({fillOpacity:1});
			});*/
			propertyLayer.eachLayer(function(layer){
				if (layer.feature!=undefined) {
					//layer.setStyle({color: getBorderColorForCloseZoom(layer.feature)});
					//graffitiInLayer=layer.feature.properties.Number_Of_Graffiti;
					//layer.setStyle({fillColor: getFillColor(graffitiInLayer)});
					//layer.setStyle({color: getFillColor(graffitiInLayer)});
					if(!isPropertySelected(layer.feature)){
						layer.setStyle({fillColor:DEFAULT_COLOR});
						layer.setStyle({color:'white'});
						layer.setStyle({fillOpacity:1});
					}
				}
			});
		}

		/*
		 * propertyLayer.eachLayer(function(layer){
		 * if(insulaViewZoomThresholdReached() && layer.feature!=undefined &&
		 * !regioViewZoomThresholdReached()){
		 * currentInsulaNumber=layer.feature.properties.insula_id;
		 * numberOfGraffitiInGroup=totalInsulaGraffitisDict[currentInsulaNumber];
		 * newFillColor=getFillColor(numberOfGraffitiInGroup);
		 * layer.setStyle({fillColor:newFillColor}); layer.setStyle({color:
		 * getFillColor(numberOfGraffitiInGroup)}); } else
		 * if(regioViewZoomThresholdReached() && colorDensity &&
		 * layer.feature!=undefined && layer.feature.properties.PRIMARY_DO){
		 * regioName=layer.feature.properties.PRIMARY_DO.split(".")[0];
		 * numberOfGraffitiInGroup=graffitiInEachRegioDict[regioName];
		 * newFillColor=getFillColor(numberOfGraffitiInGroup);
		 * layer.setStyle({fillColor:newFillColor}); layer.setStyle({color:
		 * getFillColor(numberOfGraffitiInGroup)}); } else if(layer.feature &&
		 * !layer.feature.properties.PRIMARY_DO){
		 * layer.setStyle({fillColor:'pink'}); } // Resets properties when user
		 * zooms back in if (!insulaViewZoomThresholdReached() && colorDensity &&
		 * layer.feature!=undefined){ layer.setStyle({color:
		 * getBorderColorForCloseZoom(layer.feature)});
		 * graffitiInLayer=layer.feature.properties.Number_Of_Graffiti;
		 * layer.setStyle({fillColor: getFillColor(graffitiInLayer)});
		 * layer.setStyle({color: getFillColor(graffitiInLayer)}); } else if(
		 * layer.feature && !layer.feature.properties.PRIMARY_DO){
		 * layer.setStyle({fillColor:'pink'}); } });
		 */
	}

	function regioViewZoomThresholdReached(){
		currentZoomLevel=pompeiiMap.getZoom();
		return (currentZoomLevel<=REGIO_VIEW_ZOOM);
	}

	function insulaViewZoomThresholdReached(){
		currentZoomLevel=pompeiiMap.getZoom();
		return (currentZoomLevel<=INSULA_VIEW_ZOOM);
	}

	function resultsViewThresholdReached(){
		currentZoomLevel=pompeiiMap.getZoom();
		//one zoom click from original view to see properties: 15
		//two zoom clicks from original view to see properties: 16
		return (currentZoomLevel<=16);
	}

	function isPropertySelected(feature) {
		return feature.properties.clicked == true || feature.properties.Property_Id==propertyIdToHighlight || propertyIdListToHighlight.indexOf(feature.properties.Property_Id)>=0;
	}

	function isInsulaSelected(feature) {
		return feature.properties.clicked == true || feature.properties.insula_id==propertyIdToHighlight || propertyIdListToHighlight.indexOf(feature.properties.insula_id)>=0;
	}

	//for facades
	/*function isFacadeSelected(feature) {
		return feature.properties.clicked == true || feature.properties.AUX_ORIG_OBJID==propertyIdToHighlight || propertyIdListToHighlight.indexOf(feature.properties.AUX_ORIG_OBJID)>=0;
	}*/

	function getBorderColorForCloseZoom(feature){
		if (isPropertySelected(feature)) {
			return 'black';
		}
		return 'white';
	}

	function updateBorderColors(){
		pompeiiInsulaLayer.eachLayer(function(layer){
			if(layer.feature!=undefined && layer.feature.properties.clicked ){
				borderColor=getBorderColorForCloseZoom(layer.feature);
			}
		});
	}

	// Sets the style of the portions of the map. Color is the outside borders.
	// There are different colors for
	// clicked or normal fragments. When clicked, items are stored in a
	// collection. These collections will have the color
	// contained inside of else.
	function propertyStyle(feature) {
		var layer = feature.target;
		borderColor=getBorderColorForCloseZoom(feature);
		if( isPropertySelected(feature)) {
			fillColor = SELECTED_COLOR;
		}
		else if( colorDensity ) {
			fillColor = getFillColor(feature.properties.Number_Of_Graffiti);
		} else {
			fillColor=getFillColorForFeature(feature);
		}
		return {
	    	fillColor:fillColor,
	        weight: 1,
	        opacity: 1,
	        color: borderColor,
	        fillOpacity: 0.7,
	    };
	}

	// what's happening is that the propertyStyle is being set correctly,
	// but the insula style is being overwritten on top of what is already the correct property color.
	// therefore, if you change fill color for the insula level as "none," it fixes things.

	function insulaStyle(feature) {
		borderColor=getBorderColorForCloseZoom(feature);

		currentZoomLevel=pompeiiMap.getZoom();
		//currentZoomLevel<=REGIO_VIEW_ZOOM && colorDensity;

		var fillOp = 0.7;
		//if( isInsulaSelected(feature) && (feature.properties.insula_short_name == "I.8" || feature.properties.insula_short_name == "VII.12")) {
			//fillColor = SELECTED_COLOR;
		//}
		if (colorDensity && regioViewZoomThresholdReached())  {
			regioName=feature.properties.insula_short_name.split(".")[0];
			numberOfGraffitiInGroup=graffitiInEachRegioDict[regioName];
			fillColor = getFillColor(numberOfGraffitiInGroup);
		}
		else if( colorDensity && insulaViewZoomThresholdReached()) {
			fillColor = getFillColor(feature.properties.number_of_graffiti);
		}
		else if (interactive && propertyIdToHighlight == 0){
			//this highlights insula on click on big map
			fillColor= getFillColorForFeature(feature);
		}
		//results pompeii map: first zoom level: 15
		//results pompeii map: second zoom level:
		//results pompeii map: third zoom level:
		else if (!interactive){
			//this fills insula color on small map on results screen
			fillColor = DEFAULT_COLOR;
			fillOp = 1;
		}
		else {
			fillColor = "none";
		}
		return {
	    	fillColor:fillColor,
	        weight: 1,
	        opacity: 1,
	        color: fillColor,
	        fillOpacity: fillOp,
	    };
	}

	function wallStyle(feature) {
		return {
	    		fillColor: wallFillColor,
	        weight: 1,
	        opacity: 1,
	        color: wallBorderColor,
	        fillOpacity: 0.7,
	    };
	}

	function streetStyle(feature) {
		return {
	    		fillColor: streetFillColor,
	        weight: 1,
	        opacity: 1,
	        color: streetBorderColor,
	        fillOpacity: 1,
	    };
	}

	function backgroundInsulaStyle(feature){
		return {
				fillColor: DEFAULT_COLOR,
			weight: 1,
			opacity: 1,
			color: DEFAULT_COLOR,
			fillOpacity: 1,
		}
	}

	//for facades
	/*function facadeStyle(feature) {
		return {
	    		fillColor: DEFAULT_COLOR,
	        weight: 1,
	        opacity: 1,
	        color: DEFAULT_COLOR,
	        fillOpacity: 0.7,
	    };
	}*/

	//for facades
	/*function insulaNotInteractiveStyle(feature) {
		return {
				fillColor: streetFillColor,
			weight: 1,
			opacity: 1,
			color: streetBorderColor,
			fillOpacity: 0.7,
		};
	}*/

	function unexcavatedStyle(feature){
		return{
				fillColor: unexFillColor,
			weight: 1,
			opacity: 1,
			color: unexBorderColor,
			fillOpacity: 1,
		};
	}

	function getFillColorForFeature(feature){
		// If the property is selected and there is no colorDensity, make the
		// fill color be maroon(dark red).
		if(isInsulaSelected(feature)){
			return SELECTED_COLOR;
		}
		// an orangey-yellow
		return DEFAULT_COLOR;
	}

	function getFillColor(numberOfGraffiti){
		if(colorDensity){
			return numberOfGraffiti <= 2   ? '#FFEDC0' :
			   numberOfGraffiti <= 5   ? '#FFEDA0' :
			   numberOfGraffiti <= 10  ? '#fed39a' :
			   numberOfGraffiti <= 20  ? '#fec880' :
			   numberOfGraffiti <= 30  ? '#FEB24C' :
			   numberOfGraffiti <= 40  ? '#fe9b1b' :
			   numberOfGraffiti <= 60  ? '#fda668' :
		       numberOfGraffiti <= 80  ? '#FD8D3C' :
			   numberOfGraffiti <= 100 ? '#fd7a1c' :
		       numberOfGraffiti <= 130 ? '#fc6c4f' :
			   numberOfGraffiti <= 160 ? '#FC4E2A' :
			   numberOfGraffiti <= 190 ? '#fb2d04' :
			   numberOfGraffiti <= 210 ? '#ea484b' :
			   numberOfGraffiti <= 240 ? '#E31A1C' :
			   numberOfGraffiti <= 270 ? '#b71518' :
			   numberOfGraffiti <= 300 ? '#cc0029' :
			   numberOfGraffiti <= 330 ? '#b30024' :
			   numberOfGraffiti <= 360 ? '#99001f' :
			   numberOfGraffiti <= 390 ? '#80001a' :
			   numberOfGraffiti <= 420 ? '#660014' :
			   numberOfGraffiti <= 460 ? '#4d000f' :
			   numberOfGraffiti <= 500 ? '#33000a' :
										 '#000000';
		}
		// an orangey-yellow
		return DEFAULT_COLOR;
	}

	// Sets color for properties which the cursor is moving over.
	function highlightFeature(e) {
		if(interactive){
			var layer = e.target;
			layer.setStyle({
			color:'maroon', fillColor: 'maroon',
				strokeWidth:"150", fillOpacity:"1"
			});

			if (!L.Browser.ie && !L.Browser.opera) {
				layer.bringToFront();
				pompeiiInsulaLayer.bringToFront();
			}
			info.update(layer.feature.properties);
		}
	}

	// Sets color for properties which the cursor is moving over.
	function highlightInsula(e) {
		if(interactive ){
			var layer;
			// if true mouseover on actual insula; if false mouseover on insula label
			if (e.feature == undefined){
				layer = e.target;
			} else{
				layer = e;
			}
			layer.setStyle({
			color:'maroon', fillColor: 'maroon',
				strokeWidth:"150"
			});

			if (!clickedAreas.includes(layer)){
				layer.feature.properties.clicked = false;
			}

			if (!L.Browser.ie && !L.Browser.opera) {
				layer.bringToFront();
			}
			info.update(layer.feature.properties);
		}
	}

	// Sorts items based on whether they have been clicked
	// or not. If they have been and are clicked again, sets to false and vice
	// versa.
	function showDetails(e) {
		if(interactive){
			if(!insulaViewZoomThresholdReached()){
				var layer = e.target;
				if (layer.feature.properties.clicked != null) {
					layer.feature.properties.clicked = !layer.feature.properties.clicked;
				} else {
					layer.feature.properties.clicked = true;
				}
				if (!L.Browser.ie && !L.Browser.opera) {
			        layer.bringToFront();
			    }
				clickedAreas.push(layer);
				info.update(layer.feature.properties);
			}
			else if(! regioViewZoomThresholdReached()){
				checkForInsulaClick(e.target);
			}
		}
	}

	// Sorts items based on whether they have been clicked
	// or not. If they have been and are clicked again, sets to false and vice
	// versa.
	// This version is for insula click compatibility only. When we introduce
	// property clicks,
	// it will need redesign.
	function showInsulaDetails(e) {
		if(interactive){
			var layer;
			// if true clicked on actual insula; if false clicked on insula label
			if (e.feature == undefined){
				layer = e.target;
			} else{
				layer = e;
			}
			if (layer.feature.properties.clicked != null) {
				layer.feature.properties.clicked = !layer.feature.properties.clicked;
				if(layer.feature.properties.clicked == false) {
					resetHighlight(e);
					var removedInsula=[];
					var length = clickedAreas.length;
					for(var i=0;i<length;i++){
						var singleInsula = clickedAreas[i]
						if(singleInsula.feature.properties.insula_id!=layer.feature.properties.insula_id){
							removedInsula.push(clickedAreas[i]);
						}
					}
					clickedAreas = removedInsula;

				} else {
					layer.setStyle({fillColor:SELECTED_COLOR});
					clickedAreas.push(layer);
					document.getElementById("propertyPopup").style.display = 'block';
				}
			} else {
				layer.feature.properties.clicked = true;
				layer.setStyle({fillColor:SELECTED_COLOR});
				clickedAreas.push(layer);
				document.getElementById("propertyPopup").style.display = 'block';
			}
			if (!L.Browser.ie && !L.Browser.opera) {
		        layer.bringToFront();
		    }
			info.update(layer.feature.properties);
		}
	}

	// Returns a new array with the contents of the previous index absent
	// We must search for a string in the array because, again, indexOf does not
	// work for nested lists.
	function removeStringedListFromArray(someArray,targetItem){
		var newArray=[];
		var i;
		for(i=0;i<someArray.length;i++){
			if(someArray[i]!=targetItem){
				newArray.push(someArray[i]);
			}
		}
		return newArray;
	}

	// On click, sees if a new insula id # has been selected. If so, adds it to
	// the list of selected insula.
	function checkForInsulaClick(clickedProperty){
		// Clicked property is a layer
		// layer.feature.properties.insula_id

		// indexOf does not work for nested lists. Thus, we have no choice but
		// to use it with strings.
		var clickedInsulaAsString=""+clickedAreas;
		var clickedInsulaFullName=clickedProperty.feature.properties.insula_full_name;
		var clickedInsulaId=clickedProperty.feature.properties.insula_id;
		var clickedInsulaShortName=clickedProperty.feature.properties.insula_short_name;
		var targetInsulaItem=[clickedInsulaFullName,clickedInsulaId,clickedInsulaShortName];
		var indexOfInsulaName=clickedInsulaAsString.indexOf(targetInsulaString);
		// Only adds the new id if it is already in the list

		if(!clickedAreas.includes(targetInsulaItem)){
			clickedAreas.push(targetInsulaItem);
		}
		// Otherwise, removed the insula id from the list to deselect it
		else{
			clickedAreas=removeStringedListFromArray(clickedAreas,targetInsulaItem);
		}
	}

	// Used on click for insula level view in place of display selected regions
	// In charge of the right information only, does not impact the actual map
	function displayHighlightedInsula(){
		var html = "<strong>Selected Insula:</strong><ul>";
		var numberOfInsulaSelected=clickedInsula.length;
		for (var i=0; i<numberOfInsulaSelected; i++) {
			html += "<li>"+clickedInsula[i][0] + ", " +
					"<p>"+totalInsulaGraffitisDict[clickedInsula[i][1]]+" graffiti</p>"+ "</li>"
		}
		html += "</ul>";
		// Checks to avoid error for element is null.
		var elem = document.getElementById("selectionDiv");
		if(typeof elem !== 'undefined' && elem !== null) {
			document.getElementById("selectionDiv").innerHTML = html;
		}
	}

	function displayHighlightedRegio(){
		/*
		 * var html = "<table><tr><th>Selected Regio:</th></tr>"; var
		 * numberOfInsulaSelected=clickedInsula.length; for (var i=0; i<numberOfInsulaSelected;
		 * i++) { html += "<tr><td><li>"+clickedInsula[i][0] + ", " + "<p>"+totalInsulaGraffitisDict[clickedInsula[i][1]]+"
		 * graffiti</p>"+ "</li></td></tr>" } html += "</table"; //Checks
		 * to avoid error for element is null. var elem =
		 * document.getElementById("selectionDiv"); if(typeof elem !==
		 * 'undefined' && elem !== null) {
		 * document.getElementById("selectionDiv").innerHTML = html; }
		 */
	}


	// Used to reset the color, size, etc of items to their default state (ie.
	// after being clicked twice)
	function resetHighlight(e) {
		var layer;
		// if true mouseout on actual insula; if false mouseout on insula label
		if (e.feature == undefined){
			layer = e.target;
		} else{
			layer = e;
		}
		if (interactive) {
			pompeiiInsulaLayer.resetStyle(layer);
			if(clickedAreas.includes(layer)){
				layer.setStyle({fillColor:SELECTED_COLOR});
				layer.setStyle({color:SELECTED_COLOR});
			}
			else{
				layer.setStyle({fillColor:DEFAULT_COLOR});
				layer.setStyle({color:DEFAULT_COLOR});
				layer.feature.properties.clicked = false;
			}
			info.update();
		}
	}

	function resetPropertyHighlight(e){
		if (interactive) {
			propertyLayer.resetStyle(e.target);
			e.target.setStyle({fillOpacity:"1"});
			info.update();
		}
	}

	// Calls the functions on their corresponding events for EVERY feature
	function onEachPropertyFeature(feature, layer) {
	    layer.on({
	        mouseover: highlightFeature,
	        mouseout: resetPropertyHighlight,
	        click: showDetails,
	    });
	}

	function onEachInsulaFeature(feature, layer) {
	    layer.on({
	        mouseover: highlightInsula,
	        mouseout: resetHighlight,
	        click: showInsulaDetails,
	    });
	}

	function onEachWallFeature(feature, layer) {
	    layer.on({
	    });
	}

	function onEachStreetFeature(feature, layer) {
	    layer.on({
	    });
	}

	function onEachBackgroundInsulaFeature(feature, layer) {
		layer.on({
		});
	}

	//for facades
	/*function onEachFacadeFeature(feature, layer) {
			layer.on({
				mouseover: highlightFeature,
				mouseout: resetHighlight,
				click: showDetails,
			});
	}*/

	//for facades
	/*function onEachInsulaNotInteractiveFeature(feature, layer) {
		layer.on({
		});
	}*/

	function onEachUnexcavatedFeature(feature, layer) {
	    layer.on({
	    });
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
		// TODO: Only do for the properties?
		currentZoomLevel=pompeiiMap.getZoom();
		info.update = function (props) {
			if(showHover){
				if(!props && currentZoomLevel<19)
					this._div.innerHTML = 'Hover over insula to see name';
				else if(!props && currentZoomLevel>=19)
					this._div.innerHTML = 'Hover over property to see name';
				else if(props.insula_full_name && currentZoomLevel<19)
					this._div.innerHTML = props.insula_full_name;
				else if (props.PRIMARY_DOOR && currentZoomLevel>=19)
					this._div.innerHTML = 'Property ' + props.PRIMARY_DOOR;
			}
		};
		info.addTo(pompeiiMap);
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
			pompeiiInsulaLayer.eachLayer(function(layer){
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

	// Used to acquire all of the items clicked for search(red button "Click
	// here to search).
	// Does this by iterating through the list of clicked items and adding them
	// to uniqueClicked, then returning uniqueClicked.
	function getUniqueClicked() {
		var uniqueClicked = [];
		var listInSelectedInsula;
		var length = clickedAreas.length;
		for (var i = 0; i < length; i++) {
			var insula = clickedAreas[i];
			if (!uniqueClicked.includes(insula)) {
				uniqueClicked.push(insula)
			}
		}
		return uniqueClicked;
	}

	// Collects the ids of the clicked item objects (the property id).
	function collectClicked() {
		var insulaIdsOfClicked = [];
		var propertyIdsOfClicked = [];
		var selectedInsulae = getUniqueClicked();
		var length = selectedInsulae.length;
		for (var i=0; i<length; i++) {
			var property = selectedInsulae[i];
			if (property.feature.properties.Property_Id != undefined){
				var propertyID = property.feature.properties.Property_Id;
				propertyIdsOfClicked.push(propertyID);
			}
			else{
				var insulaID = property.feature.properties.insula_id;
				insulaIdsOfClicked.push(insulaID);
			}
		}
		return [insulaIdsOfClicked, propertyIdsOfClicked];
	}

	// creates url to call for searching when the user clicks the search button.
	function searchForProperties() {
		var highlighted = collectClicked();
		var argString = "";
		if (highlighted[0].length > 0 && highlighted[1].length > 0){
			for (var k = 0; k<highlighted[0].length; k++){
				var currentInsulaId = highlighted[0][k];
				propertyLayer.eachLayer(function(layer){
					if(layer.feature!=undefined){
						if(layer.feature.properties.insula_id == currentInsulaId
								&& !highlighted[1].includes(layer.feature.properties.insula_id)){
							highlighted[1].push(layer.feature.properties.Property_Id);
						}
					}
				});
			}
			for (var j = 0; j < highlighted[1].length; j++){
				argString = argString + "property=" + highlighted[1][j] + "&";
			}
			window.location = "results?" + argString;
			return true;
		}
		else if (highlighted[0].length > 0 || highlighted[1].length > 0){
			for (var i = 0; i < highlighted[0].length; i++) {
				argString = argString + "insula=" + highlighted[0][i] + "&";
			}
			for (var j = 0; j < highlighted[1].length; j++){
				argString = argString + "property=" + highlighted[1][j] + "&";
			}
			window.location = "results?" + argString;
			return true;
		}
		else {
			document.getElementById("hidden_p").style.visibility = "visible";
		}
	}

	function displayHighlightedRegions() {
		// when you click on the map, it updates the selection info
		var clickedAreasTable = getUniqueClicked();
		var html = "<strong>Selected Areas:</strong><ul>";
		var length = clickedAreasTable.length;
		for (var i=0; i<length; i++) {
			var property = clickedAreasTable[i];
			if (property.feature.properties.clicked) {
				if(property.feature.properties.PRIMARY_DOOR != undefined){
					html += "<li>" +property.feature.properties.PRIMARY_DOOR + ",<p>"+property.feature.properties.Number_Of_Graffiti+" graffiti</p>"+ "</li>";
				}
				else if(property.feature.properties.insula_full_name !=undefined){
					html += "<li>" +property.feature.properties.insula_full_name + ",<p>"+property.feature.properties.number_of_graffiti+" graffiti</p>"+ "</li>";
				}
				else if(property.feature.properties.NAME_1 !=undefined){
					html += "<li>" +property.feature.properties.NAME_1 + ",<p>"+property.feature.properties.number_of_graffiti+" graffiti</p>"+ "</li>";
				}
			}
		}
		html += "</ul>";
		// Checks to avoid error for element is null.
		var elem = document.getElementById("selectionDiv");
		if(typeof elem !== 'undefined' && elem !== null) {
			document.getElementById("selectionDiv").innerHTML = html;
		}
	}

	//for facades
	/*function toggleInsulavsFacade(){
		if(!toggle){
			pompeiiMap.removeLayer(pompeiiInsulaLayer);
			pompeiiMap.addLayer(pompeiiInsulaNotIALayer);
			pompeiiMap.addLayer(pompeiiFacadesLayer);
			pompeiiUnexcavatedLayer.bringToFront();

		}
		else{
			pompeiiMap.removeLayer(pompeiiFacadesLayer);
			pompeiiMap.removeLayer(pompeiiInsulaNotIALayer);
			pompeiiMap.addLayer(pompeiiInsulaLayer);
		}
		toggle=!toggle;
	}*/

	function goToPropertySelect(){
		if (clickedAreas.length>0){
			var lastClickedPos = clickedAreas.length-1;
			var latestInsula = clickedAreas[lastClickedPos];
			var latestInsulaID=latestInsula.feature.properties.insula_id;
			var coordinates = insulaCentersDict[latestInsulaID];
			//if(latestInsula.feature.properties.insula_short_name=="II.7"){
				//pompeiiMap.setView(coordinates, INDIVIDUAL_PROPERTY_ZOOM-1);
			//}
			//else{
			pompeiiMap.setView(coordinates, INDIVIDUAL_PROPERTY_ZOOM);
			//}
			clickedAreas.splice(lastClickedPos, 1);
			displayHighlightedRegions();
			pompeiiInsulaLayer.resetStyle(latestInsula);
			propertyLayer.bringToFront();
		}
	}


	pompeiiMap = new L.map('pompeiimap', {
		center: [40.750950, 14.488600],
		zoom: currentZoomLevel,
		minZoom: REGIO_VIEW_ZOOM,
		maxZoom: 19, /*
									 * can change this when we want the property
									 * view as well
									 */
		maxBounds: bounds
	});

	backgroundInsulaLayer = L.geoJson(pompeiiInsulaData, {style: backgroundInsulaStyle, onEachFeature: onEachBackgroundInsulaFeature});
	if(interactive){
		backgroundInsulaLayer.addTo(pompeiiMap);
	}

	propertyLayer = L.geoJson(pompeiiPropertyData, { style: propertyStyle, onEachFeature: onEachPropertyFeature });
	propertyLayer.addTo(pompeiiMap);

	pompeiiStreetsLayer = L.geoJson(pompeiiStreetsData, {style: streetStyle, onEachFeature: onEachStreetFeature});
	pompeiiStreetsLayer.addTo(pompeiiMap);

	pompeiiUnexcavatedLayer = L.geoJson(pompeiiUnexcavatedData, {style: unexcavatedStyle, onEachFeature: onEachUnexcavatedFeature});
	pompeiiUnexcavatedLayer.addTo(pompeiiMap);

	pompeiiWallsLayer = L.geoJson(pompeiiWallsData, {style: wallStyle, onEachFeature: onEachWallFeature});
	pompeiiWallsLayer.addTo(pompeiiMap);

	pompeiiInsulaLayer = L.geoJson(pompeiiInsulaData, { style: insulaStyle, onEachFeature: onEachInsulaFeature });
	pompeiiInsulaLayer.addTo(pompeiiMap);

	if( interactive && colorDensity){
		// Insula Functions:
		makeInsulaCentersDict();
		makeTotalInsulaGraffitiDict();
		makeInsulaIdsListShortNamesList();

		// Regio Functions:
		makeRegioCentersDict();
		makeTotalRegioGraffitiDict();
		makeListOfRegioNames();

		dealWithLabelsAndSelection();
		insulaLevelLegend.addTo(pompeiiMap);
		pompeiiMap.addControl(new L.Control.Compass({autoActive: true, position: "bottomleft"}));
	}

	if(interactive){
		makeInsulaCentersDict();
		makeUnexcavatedCentersList();
		makeInsulaIdsListShortNamesList();
		dealWithLabelsAndSelection();
		//displayInsulaLabels();
		displayUnexcavatedLabels();
	}

	if(!interactive){
		propertyLayer.bringToFront();
	}

	// A listener for zoom events.
	pompeiiMap.on('zoomend', function(e) {
		dealWithInsulaLevelPropertyView();
		dealWithLabelsAndSelection();
	});
	dealWithInsulaLevelPropertyView();

	updateHoverText();

	showCloseUpView();

	// Handles the events
	var el = document.getElementById("search");
	if(el!=null){
		el.addEventListener("click", searchForProperties, false);
	}

	var el2 = document.getElementById("pompeiimap");
	if(el2!=null){
		el2.addEventListener("click", displayHighlightedRegions, false);
	}

	var el4 = document.getElementById("yesButton");
	if(el4!=null){
		el4.addEventListener("click", goToPropertySelect, false);
	}

}

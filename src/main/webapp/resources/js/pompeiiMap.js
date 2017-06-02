
function initmap() {
	var mapboxAccessToken = 'pk.eyJ1IjoibWFydGluZXphMTgiLCJhIjoiY2lxczduaG5wMDJyc2doamY0NW53M3NnaCJ9.TeA0JhIaoNKHUUJr2HyLHQ';
	
	var southWest = L.latLng(40.746, 14.48),
	northEast = L.latLng(40.754, 14.498),
	bounds = L.latLngBounds(southWest, northEast);
	
	var map = new L.map('pompeiimap', {
		center: [40.750, 14.4884],
		zoom: 16,
		minZoom: 16,
		maxBounds: bounds
	}); //this just sets my access token
	
	var mapboxUrl = 'https://api.mapbox.com/styles/v1/martineza18/ciqsdxkit0000cpmd73lxz8o5/tiles/256/{z}/{x}/{y}?access_token=' + mapboxAccessToken;
	
	var grayscale = new L.tileLayer(mapboxUrl, {id: 'mapbox.light', attribution: 'Mapbox Light'});
	
	var clickedAreas = [];
	
	map.addLayer(grayscale);
	L.geoJson(updatedEschebach).addTo(map);
	
	function style(feature) {
		if (feature.properties.clicked !== true) {
			fillColor = '#ecf0f1';
		} else {
			fillColor = '#3a7ae7';
		} return { 
	    	fillColor,
	        weight: 2,
	        opacity: 1,
	        color: 'black',
	        fillOpacity: 0.7,
	    };
		L.geoJson(updatedEschebach, {style: style}).addTo(map);
	}
	
	var geojson;
	
	function highlightFeature(e) {
		var layer = e.target;
		layer.setStyle({
			fillColor: '#3a7ae7'
		});
		
		if (!L.Browser.ie && !L.Browser.opera) {
			layer.bringToFront();
		}
		info.update(layer.feature.properties);
	}
	
	function showDetails(e) {
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
	
	function resetHighlight(e) {
		geojson.resetStyle(e.target);
	    info.update();
	}
	
	function onEachFeature(feature, layer) {
	    layer.on({
	        mouseover: highlightFeature,
	        mouseout: resetHighlight,
	        click: showDetails
	    });
	}
	
	geojson = L.geoJson(updatedEschebach, {
		style: style,
	    onEachFeature: onEachFeature
	}).addTo(map);
	
	var info = L.control();
	info.onAdd = function(map) {
		// create a div with a class "info"
		this._div = L.DomUtil.create('div', 'info'); 
	    this.update();
	    return this._div;
	};
	
	// method that we will use to update the control based on feature properties passed
	info.update = function (props) {
	    this._div.innerHTML = (props ? props.Property_Name + ", " + props.PRIMARY_DO
	        : 'Hover over property to see name');
	};

	info.addTo(map);
	
	function getUniqueClicked() {
		var uniqueClicked = [];
		var length = clickedAreas.length;
		for (var i = 0; i < length; i++) {
			var property = clickedAreas[i];
			if (!uniqueClicked.includes(property)) {
				uniqueClicked.push(property)
			}
		}
		return uniqueClicked;
	}
	
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
	
	function DoSubmit() {
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
	
	function displayHighlightedRegions() {
		var clickedAreasTable = getUniqueClicked();
		
		var html = "<table><tr><th>Selected Properties:</th></tr>";
		var length = clickedAreasTable.length;
		for (var i=0; i<length; i++) {
			var property = clickedAreasTable[i];
			if (property.feature.properties.clicked === true) {
				html += "<tr><td>" +property.feature.properties.Property_Name + ", " + 
						property.feature.properties.PRIMARY_DO + "</td></tr>";
			}
		}
		html += "</table";
		document.getElementById("newDiv").innerHTML = html;
		// when you click anywhere on the map, it updates the table
	}
	
	var el = document.getElementById("search");
	el.addEventListener("click", DoSubmit, false);
	
	var el2 = document.getElementById("pompeiimap");
	el2.addEventListener("click", displayHighlightedRegions, false);
	
//	var locationNeeded = false;
//	if (document.title == "Ancient Graffiti Project :: Property Info") {
//		locationNeeded = true;
//	}
//	
//	// can the propertyId even be reverse accessed? Humm...
//	function focusOnProperty(propId) {
//		
//	} **** this feature is currently in the works
	
}
	
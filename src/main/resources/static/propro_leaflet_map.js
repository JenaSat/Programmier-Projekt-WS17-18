var map = L.map( 'map', {
    center: [20.0, 5.0],
    minZoom: 2,
    zoom: 2
}).setView(new L.LatLng(48.77490788045187, 9.17959213256836), 8);

L.tileLayer('https://{s}.tile.openstreetmap.de/tiles/osmde/{z}/{x}/{y}.png', {
	maxZoom: 18,
	attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
}).addTo( map );

var srcLat;
var srcLng;
var srcMarker;
var srcID;

var destLat;
var destLng;
var destMarker;
var destID;

var globalZoom;
var last;



//read user input for lat long and create a new source marker
function flyTo() {
    srcLat = document.getElementById("lat").value;
    srcLng = document.getElementById("lng").value;
    if(!srcLat || !srcLng){
        alert("Please Enter a valid Source (Lat,Lng)")
    } else {
    globalZoom = document.getElementById("zoom").value;
    if(globalZoom){
    	
    	$.get("neighbour", {lat: srcLat, lng: srcLng}, function(data){
    		if(!data){
    			alert("Kein Knoten in der Nähe!")
    		} else {
    		map.flyTo(new L.LatLng(data[0], data[1]), globalZoom);
    		createSrcMarker(data[0], data[1]);
    		}
    	});
    }
    else {
    	$.get("neighbour", {lat: srcLat, lng: srcLng}, function(data){
    		if(!data){
    			alert("Kein Knoten in der Nähe!")
    		} else {
    		map.flyTo(new L.LatLng(data[0], data[1]), 10);
    		createSrcMarker(data[0], data[1]);
    		}
    	});
    }
    }
}

function flyTo2() {
    destLat = document.getElementById("lat2").value;
    destLng = document.getElementById("lng2").value;
    if(!srcLat || !srcLng){
        alert("Please Enter a valid Dest. (Lat,Lng)")
    } else {
    globalZoom = document.getElementById("zoom").value;
    if(globalZoom){
    	
    	$.get("neighbour", {lat: destLat, lng: destLng}, function(data){
    		if(!data){
    			alert("Kein Knoten in der Nähe!")
    		} else {
    		map.flyTo(new L.LatLng(data[0], data[1]), globalZoom);
    		createDestMarker(data[0], data[1]);
    		}
    	});
    }
    else {
    	$.get("neighbour", {lat: destLat, lng: destLng}, function(data){
    		if(!data){
    			alert("Kein Knoten in der Nähe!")
    		} else {
    		map.flyTo(new L.LatLng(data[0], data[1]), 10);
    		createDestMarker(data[0], data[1]);
    		}
    	});
    }
    }
}


//read user input for lat long and create a new dest and/or source marker, display route inbetween.
function showRoute() {
	clearRoute();
	if (srcLat && srcLng && destLat && destLng) {
			$.get("shortestPath", {srcID : srcID, destID : destID}, function(distance){
				if(distance == -1){
					alert("Es existiert keine Route!")
				}else {
					$.get("route", {srcID : srcID, destID : destID}, function(route){
						createRoute(distance, route);
					});
				}
			});

    } else {
    srcLat = document.getElementById("lat").value;
    srcLng = document.getElementById("lng").value;
    destLat = document.getElementById("lat2").value;
    destLng = document.getElementById("lng2").value;
    //globalZoom = document.getElementById("zoom").value;
    if(!srcLat || !srcLng){
        alert("Please Enter a valid Source (Lat,Lng)")
    } else if(!destLat || !destLng) {
        alert("Please Enter a valid Destination (Lat,Lng)")
    } else {
    	//get the closest Neighbour of the given src/dest node
    	$.get("neighbourID", {lat: srcLat, lng: srcLng}, function(srcCoordID){
    		srcLat = srcCoordID[0][0];
    		srcLng = srcCoordID[0][1];
    		srcData = srcCoordID[1][0];
    	});	
    	$.get("neighbourID", {lat: destLat, lng: destLng}, function(destCoordID){
    		destLat = destCoordID[0][0];
    		destLng = destCoordID[0][1];
    		destData = srcCoordID[1][0];
    	});	
    		alert("finished danking")
			createSrcMarkerWithID(srcLat, srcLng, srcData);
			createDestMarkerWithID(destLat, destLng, destData)
			$.get("shortestPath", {srcID : srcData, destID : destData}, function(distance){
				if(distance == -1){
					alert("Es existiert keine Route!")
				}else {
					$.get("route", {srcID : srcData, destID : destData}, function(route){
						createRoute(distance, route);
					});
				}
			});
        }
    }
}

function showRouteViaID(){
	clearRoute();
	srcID = document.getElementById("id").value; 
	destID = document.getElementById("id2").value;
	if(!srcID){
		alert("Bitte valide source ID eintragen!")
	}
	$.get("nodeCoord", {nodeID : srcID}, function(srcCoord){
		if(!srcCoord){
			alert("Bitte valide source ID's eintragen!")
		} else {
			createSrcMarkerWithID(srcCoord[0], srcCoord[1], srcID);
			if(!destID || destID == -1){
				destID = -1;
				$.get("shortestPath", {srcID : srcID, destID : destID}, function(distance){
					alert("One-to-All ab " + srcID + " berechnet. Bitte valide dest ID eingeben und Button erneut drücken")
				});
			}
			else {
				$.get("nodeCoord", {nodeID : destID}, function(destCoord){
					if(!destCoord){
						alert("Bitte valide dest ID's eintragen!")
					} else {
					createDestMarkerWithID(destCoord[0], destCoord[1], destID);
					$.get("shortestPath", {srcID : srcID, destID : destID}, function(distance){
						if(distance == -1){
							alert("Es existiert keine Route!")
						}else {
							$.get("route", {srcID : srcID, destID : destID}, function(route){
								createRoute(distance, route);
							});
						}
					});
					}
				});
			}
		}
	});
}


//-------------------------------------------Marker fuer src-dest----------------------------------------------------------------------

//creates a new source marker at the user given position. Also displays the lat/long in textbox (relevant for free markers)
function createSrcMarker(lat, lng){
        if(srcMarker){
            map.removeLayer(srcMarker);
        }
        srcLat = lat;
        srcLng = lng;
        $.get("nodeID", {lat: lat, lng: lng}, function(data){
        	srcID = data;
        	srcMarker = L.marker([lat, lng], {draggable:true}).on('click', srcMarkerOnClick).addTo(map).bindPopup("Source NodeID: " +  data).openPopup();
            last = false;
            document.querySelector('#id').value = data;
        })
        document.querySelector('#lat').value = lat;
        document.querySelector('#lng').value = lng;
}

//creates a new dest marker at the user given position. Also displays the lat/long in textbox (relevant for free markers)
function createDestMarker(lat, lng){
        if(destMarker){
            map.removeLayer(destMarker);
        }
        destLat = lat;
        destLng = lng;
        $.get("nodeID", {lat: lat, lng: lng}, function(data){
        	destID = data;
        	destMarker = L.marker([lat, lng], {draggable:true}).on('click', destMarkerOnClick).addTo(map).bindPopup("Dest. NodeID: " + data).openPopup();
            last = true;
            document.querySelector('#id2').value = data;
        });
        document.querySelector('#lat2').value = lat;
        document.querySelector('#lng2').value = lng;

    
}

//-------------------------------------------------------------------------------------------------------------------------------------
//---------------------------------------These Methods are used, if the nodeID is already known/precalculated--------------------------
//---------------------------------------Otherwise same functionality as above---------------------------------------------------------
function createSrcMarkerWithID(lat, lng, id){
    if(srcMarker){
        map.removeLayer(srcMarker);
    }
    srcLat = lat;
    srcLng = lng;
    srcMarker = L.marker([lat, lng], {draggable:true}).on('click', srcMarkerOnClick).addTo(map).bindPopup("Source NodeID: " + id).openPopup();
    last = false;
    document.querySelector('#lat').value = lat;
    document.querySelector('#lng').value = lng;
    document.querySelector('#id').value = id;
}


function createDestMarkerWithID(lat, lng, id){
    if(destMarker){
        map.removeLayer(destMarker);
    }
    destLat = lat;
    destLng = lng;
    destMarker = L.marker([lat, lng], {draggable:true}).on('click', destMarkerOnClick).addTo(map).bindPopup("Dest. NodeID: " + id).openPopup();
    last = true;
    document.querySelector('#lat2').value = lat;
    document.querySelector('#lng2').value = lng;
    document.querySelector('#id2').value = id;


}
//-------------------------------------------------------------------------------------------------------------------------------------
//---------------------------------------These Methods are used, if the nodeID is already known/precalculated--------------------------
//---------------------------------------Otherwise same functionality as above---------------------------------------------------------



//-------------------------------------------Marker fuer src-dest----------------------------------------------------------------------

//-------------------------------------------freie Marker mit Popup--------------------------------------------------------------------

/*
alternative creation of markers: if the user clicks on the map, 
a new source/dest. marker is created dynamically (if no src. exists or both src and dest. exist,
--> new src marker, else if src exists --> new dest. marker)
*/

map.on('click', function(e) {
    //alert("Lat, Lon : " + e.latlng.lat + ", " + e.latlng.lng)
    var lat = e.latlng.lat;
    var lng = e.latlng.lng;
    getClosestNode(lat, lng);
});

function getClosestNode(lat, lng){
	$.get("neighbour", {lat: lat, lng: lng}, function(data){
		if(!data){
			alert("Kein Knoten in der Nähe!")
		} else {
		if(!srcMarker || last == true) {
	        if(srcMarker){
	            map.removeLayer(srcMarker);
	        }
	        createSrcMarker(data[0], data[1]);
	        last = false;
	    } else if(last == false) {
	        if(destMarker){
	            map.removeLayer(destMarker);
	        }
	        createDestMarker(data[0], data[1]);
	        last = true;
	    }
		}
	})
}


function srcMarkerOnClick(){
	last = false;
}

function destMarkerOnClick(){
	last = true;
}

//--------------------------------------------geoJSON----------------------------------------------------------------------

var geoLayer = L.geoJSON().addTo(map);
var route;
var routeLayer;
function onEachFeature(feature, layer) {
    if (feature.properties && feature.properties.popupContent) {
        routeLayer.bindPopup(feature.properties.popupContent);
    }
}

function clearRoute(){
	if(routeLayer){
        map.removeLayer(routeLayer);
    }
}

//creates a route from the current source to current dest. and displays it
function createRoute(distance, route) {
    route = [{
        "type": "LineString",
        //"coordinates": [[srcLng, srcLat], [destLng, destLat]]
        "coordinates": route
    }];

    var routeStyle = {
        "color": "#ff7800",
        "weight": 5,
        "opacity": 0.65
    };

    //Dummy distance display
    var routeFeature = {
        "type" : "Feature",
        "properties": {
            "popupContent": "distance: " + distance
        }
    }
    routeLayer = L.geoJSON(route, {style : routeStyle});
    routeLayer.addTo(map);
    routeLayer.bindPopup(routeFeature.properties.popupContent).openPopup();
}


//--------------------------------------------geoJSON----------------------------------------------------------------------

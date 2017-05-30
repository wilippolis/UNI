
function getLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition);
    } else {
        x.innerHTML = "Geolocation is not supported by this browser.";
    }
}
function getLatPosition(){
	return navigator.geolocation.getCurrentPosition(showPosition).coords.latitude;
}

function showPosition(position) {
    x.innerHTML = "Latitude: " + position.coords.latitude + 
    "<br>Longitude: " + position.coords.longitude; 
}

function setLocation(){
	 getLocation();
	 var posx = -73.201157;
	 var posy = 44.478283;
	 if(posx == -73.201157 && posy == 44.478283){
		 var element = document.getElementById("location")
		 element.value = "Waterman"
	 }
}
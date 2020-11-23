let locationPreviewMap = Vue.component("map-location-selector", {
  props: {
    points: Array,
    number: Number,
    clicked: false,
  },
  data() {
    return {
    startPoint: [0, 0],
    endPoint: [0, 0],

    // Map objects
    map: new Object(),
    polyline: new Object(),
    marker: new Object(),
    dot: new Object(),

    // Form objects
    selectedIndex: 0,
    selectedValues: {}
    }
  },
  methods: {
    updateMapWithPoints: function(latlngs){
      if (latlngs) {
        // create a red polyline from an array of LatLng points
        this.polyline = L.polyline(latlngs, {
          color: "red"
        });
        this.polyline.addTo(this.map);
      }
    },
    addInitialFinalMarker: function(){
      this.marker = L.marker(this.startPoint);
      this.marker.addTo(this.map);
    },
    clearMap: function() {
      if(this.polyline != null) { this.map.removeLayer(this.polyline) }
      if(this.marker != null) { this.map.removeLayer(this.marker) }
    },
    clearCircle: function() {
      if(this.circle) { this.map.removeLayer(this.circle); }
    },
    onChangeProgress: function(e){
      this.clearMap();
      var pointsCoordinates = this.points.map(coord=>coord.values)
      var pointsCoordinatesLatLngs = pointsCoordinates.map(coord=> [coord[1], coord[0]]);
      this.startPoint = pointsCoordinatesLatLngs[0];
      this.endPoint = pointsCoordinatesLatLngs[pointsCoordinatesLatLngs.length-1];
      this.updateMapWithPoints(pointsCoordinatesLatLngs);
      this.addInitialFinalMarker();
      this.onCenterTrail();
      this.map._onResize();
    },
    onSelectPoint: function(e){
      this.clearCircle();
      this.selectedIndex = parseInt(e.target.value);
      let lat = this.points[this.selectedIndex].values[1];
      let long = this.points[this.selectedIndex].values[0]; 
      this.selectedValues = this.points[this.selectedIndex];
      this.circle = L.circle([lat, long], {radius: 15, color: 'red'}).addTo(this.map);
      this.map.setView([lat, long], 14);
    },
    onMountMap: function(e){ 
      let topoLayer = L.tileLayer(
        "https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png",
        { attribution: this.openStreetmapCopy }
      );
      this.map = L.map("preview-map-" + this.number, {layers: topoLayer}).setView([44.498955, 11.327591], 9);
    },
    onCenterTrail: function(){
      if(this.polyline){
        this.map.fitBounds(this.polyline.getBounds());
      }
    }
  },
  watch: {
    points: function(){
      if(this.points.length == 0){
        console.error("Points passed to map are of length 0, but that is not accepted");
      }
    },
    clicked: function(){
      setTimeout(this.onMountMap, 2000)
      setTimeout(this.onChangeProgress, 3000);
    }
  },
  mounted: function () {
    this.clicked = false;
  },
  created: function () {
    this.selectedValues = {	altitude:	0,
      distanceFromTrailStart:	0,
      latitude:	0,
      longitude:	0}
  },
  template: `
  <div class="row form-group space-up">
    <legend class="col-form-label col-sm-2 pt-0">Seleziona dalla mappa</legend>
    <div>
      <div class="col-sm-12 bootstrap-select-wrapper">
        <input type="range" min="0" v-bind:max="points.length-1" value="0" v-on:change="onSelectPoint" />
      </div>
      <div class="col-sm-8 bootstrap-select-wrapper">
        <button type="button" v-on:click="onCenterTrail" class="btn btn-info">Centra sentiero</button>
      </div>
      <div class="col-sm-8 bootstrap-select-wrapper">
        <div class="map-preview" style="width:500px; min-height:300px" v-bind:id="'preview-map-' + number" ></div>
      </div>
      <div class="col-sm-12 bootstrap-select-wrapper space-up">
        <span>Longitudine</span>
        <p v-bind:id="'long-' + number"> {{ selectedValues.longitude }}</p>
      </div>
      <div class="col-sm-12 bootstrap-select-wrapper">
        <span>Latitudine</span>
        <p v-bind:id="'lat-' + number"> {{ selectedValues.latitude }}</p>
      </div>
      <div class="col-sm-12 bootstrap-select-wrapper">
        <span>Altitudine</span>
        <p v-bind:id="'alt-' + number"> {{ selectedValues.altitude }}</p>
      </div>
      <div class="col-sm-12 bootstrap-select-wrapper">
        <span>Distanza dalla partenza</span>
        <p v-bind:id="'dist-' + number">{{ selectedValues.distanceFromTrailStart }}</p>
      </div>
    </div>
  </div>
  `,
});

module.exports = locationPreviewMap;

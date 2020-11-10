let previewMap = Vue.component("preview-map", {
  template: `
        <div class="map-preview" id="map-table"></div>
  `,
  props: {
    points: Array,
  },
  data: {
    startPoint: [0, 0],
    endPoint: [0, 0],
    map: null,
    polyLine: null,
    marker: null
  },
  methods: {
    updateMapWithPoints: function(latlngs){
      if (latlngs) {
        // create a red polyline from an array of LatLng points
        this.polyline = L.polyline(latlngs, {
          color: "red"
        })
        this.polyline.addTo(this.map);
        this.map.fitBounds(this.polyline.getBounds());
      }
    },
    addInitialFinalMarker: function(){
      this.marker = L.marker(this.startPoint);
      this.marker.addTo(this.map);
    },
    clearMap: function() {
      if(this.polyline != null) { this.map.removeLayer(this.polyline) };
      if(this.marker != null) { this.map.removeLayer(this.marker) };
    }
  },
  watch: {
    points: function () {
      this.clearMap();
      var pointsCoordinates = this.points.map(coord=>coord.values)
      var pointsCoordinatesLatLngs = pointsCoordinates.map(coord=> [coord[1], coord[0]]);
      this.startPoint = pointsCoordinatesLatLngs[0];
      this.endPoint = pointsCoordinatesLatLngs[pointsCoordinatesLatLngs.length-1];
      this.updateMapWithPoints(pointsCoordinatesLatLngs);
      this.addInitialFinalMarker();
    },
  },
  mounted: function () {
    this.map = L.map("map-table").setView([44.498955, 11.327591], 9);

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution:
        '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    }).addTo(this.map);
  },
});

module.exports = previewMap;

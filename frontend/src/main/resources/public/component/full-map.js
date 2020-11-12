var mapFull = Vue.component("map-full", {
  template: `<div class="col-12 col-md-9" id="map-full"></div>`,
  data : {
    openStreetmapCopy : '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    currentTileLayer : new Object(),
    currentTileLayerName : "topo",
    isShowingAllTrails: true,
    map: new Object(),
    trails: [], // All trails data in low coords resolution
    trailFocus: new Object(),    
  },
  props: {
    points: Array,
    tileLayerType: String,
    allTrailsObjects: Array,
    typeTrail: String,
    selectedTrail: Object,
  },
  watch: {
    allTrailsObjects: function() {
      this.updateAllOtherTrails(this.allTrailsObjects);
    },
    points: function () {
      console.log("Render points");
      this.updateTrail(this.points);
    },
    tileLayerType: function(){
      this.updateTileLayer(this.tileLayerType);
    },
  },
  mounted() {
    this.currentTileLayerName = "topo";
    this.currentTileLayer = L.tileLayer("https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png", { attribution: this.openStreetmapCopy});
    let topoLayer = L.tileLayer("https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png", { attribution: this.openStreetmapCopy});

    this.map = L.map("map-full", { layers: [topoLayer], maxZoom: 17 }).setView(
      [44.498955, 11.327591],
      12
    );
  },

  methods: {
    toggleLoad: function(isLoading) {
      if(isLoading){
        console.log("Showing load screen");
      }
      console.log("Hiding load screen")
    },
    getLineStyle: function() {
      switch(this.typeTrail){
        case "EEA":
          return  { color: "red", dashArray: "2, 10"};
        case "EE":
          return {color: "red", dashArray: "5, 10"};
        default:
          return { color: "red" };
      }
    },
    updateTrail: function (latlngs) {
      console.log("Updating trail...");
      if (latlngs) {
        var lineStyle = this.getLineStyle();
        var polyline = L.polyline(latlngs);
        polyline.setText(generateEmptySpace() + this.selectedTrail.code + generateEmptySpace(), { repeat: true, offset: -10, attributes: { fill: lineStyle.color} });
        polyline.setStyle(lineStyle);
        polyline.addTo(this.map);
        L.marker(latlngs[0]).addTo(this.map);
        L.control.scale().addTo(this.map);
        this.trailFocus = polyline;
        this.map.fitBounds(polyline.getBounds());
      }
      function generateEmptySpace(){ var empty = ""; for(var i=0; i<35; i++) { empty += " " }; return empty; }
    },
    updateAllOtherTrails: function (allTrailsObjects) {
      console.log("Loading all other trails");
    },
    updateTileLayer: function (tileLayerName) {
      this.map.removeLayer(getLayerByName(this.currentTileLayerName));
      let tileLayerObject = getLayerByName(tileLayerName, this.openStreetmapCopy).addTo(this.map);
      this.currentTileLayer = tileLayerObject;
      this.currentTileLayerName = tileLayerName;

      function getLayerByName(layerName, copyright) {
        switch (layerName) {
          case "topo":
            return L.tileLayer("https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png", { attribution: copyright });
          case "geopolitic":
            return L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", { attribution: copyright });
          case "geopolitic2":
            return L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}', { attribution: copyright });
          default:
            throw "TileLayer not in list";

        }
      }
    },
  },
});

module.exports = mapFull;

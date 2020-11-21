var mapFull = Vue.component("map-full", {
  template: `<div class="col-12 col-md-9" id="map-full"></div>`,
  data: {
    openStreetmapCopy:
      '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    lineWeight: 3,
    currentTileLayer: new Object(),
    currentTileLayerName: "topo",
    isShowingAllTrails: true,
    map: new Object(),
    trailFocus: new Object(),

    // Polyline + Marker of selected trail
    selectedPolyline: new Object(),
    selectedMarker: new Object(),

    // Polylines of all unselected trails
    trailsPolylines: [],
  },
  props: {
    points: Array,
    tileLayerType: String,
    typeTrail: String,
    selectedTrail: Object,
    trails: Array,
  },
  watch: {
    selectedTrail: function () {
      console.log("Render points");
      this.updateSelectedTrail(this.selectedTrail);
    },
    tileLayerType: function () {
      this.updateTileLayer(this.tileLayerType);
    },
    trails: function () {
      this.updateTrails(this.trails);
    },
  },
  mounted() {
    this.trailsPolylines = [];
    this.lineWeight = 3;
    this.currentTileLayerName = "topo";
    this.currentTileLayer = L.tileLayer(
      "https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png",
      { attribution: this.openStreetmapCopy }
    );
    let topoLayer = L.tileLayer(
      "https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png",
      { attribution: this.openStreetmapCopy }
    );

    this.map = L.map("map-full", { layers: [topoLayer], maxZoom: 17 }).setView(
      [44.498955, 11.327591],
      12
    );
    this.map._onResize();

    L.control.scale().addTo(this.map);
  },
  methods: {
    toggleLoad: function (isLoading) {
      if (isLoading) {
        console.log("Showing load screen");
      }
      console.log("Hiding load screen");
    },
    getLineStyle: function (isSelected, typeTrail) {
      var trailColor = isSelected ? "red" : "#ff1414";
      switch (typeTrail) {
        case "E":
          return {
            weight: this.lineWeight,
            color: trailColor,
            dashArray: "5, 10",
          };
        case "EEA":
          return {
            weight: this.lineWeight,
            color: trailColor,
            dashArray: "2, 10",
          };
        case "EE":
          return {
            weight: this.lineWeight,
            color: trailColor,
            dashArray: "3, 10",
          };
        default:
          return { weight: this.lineWeight, color: trailColor };
      }
    },
    updateSelectedTrail: function (selectedTrail) {
      console.log("Updating selected trail...");
      if (selectedTrail) {
        this.clearPreviouslySelectedTrails();
        this.clearTrail(selectedTrail.code); // Remove the selected trail
        let pointsCoordinatesLatLngs = this.getCoordinates(selectedTrail);
        var lineStyle = this.getLineStyle(true, selectedTrail.classification);
        this.drawSelectedTrail(pointsCoordinatesLatLngs, lineStyle); // Draw the selected trail
        this.updateTrails(this.trails); // Redraw the previously selected one
        this.map.fitBounds(this.selectedPolyline.getBounds());
      }
    },
    updateTrails: function (allTrailsObjects) {
      console.log("Loading all other trails...");
      let alreadyRenderedCodes = this.trailsPolylines.map((x) => x.code);
      let allToBeRendered = allTrailsObjects.filter(
        (x) =>
          x.code != this.selectedTrail.code &&
          alreadyRenderedCodes.indexOf(x) == -1
      );
      allToBeRendered.forEach((trail) => {
        let polyline = this.drawTrail(trail);
        polyline.on("click", this.selectTrail);
        this.trailsPolylines.push({ code: trail.code, graphic: polyline });
      });
    },
    selectTrail: function (event) {
      let codeFromText = event.sourceTarget._text;
      let code = codeFromText.trim();
      this.$router.push("/map/" + code);
    },
    clearPreviouslySelectedTrails: function () {
      if (this.selectedPolyline) this.map.removeLayer(this.selectedPolyline);
      if (this.selectedMarker) this.map.removeLayer(this.selectedMarker);
    },
    clearTrail: function (code) {
      var item = this.trailsPolylines.filter((x) => x.code == code);
      if (item.length > 0) {
        item.forEach((x) => this.map.removeLayer(x.graphic));
      }
    },
    drawSelectedTrail: function (latlngs, lineStyle) {
      if (latlngs) {
        let startingPoint = latlngs[0];
        var polyline = L.polyline(latlngs);
        polyline.setText(
          this.generateEmptySpace() +
            this.selectedTrail.code +
            this.generateEmptySpace(),
          {
            repeat: true,
            offset: -10,
            attributes: { fill: lineStyle.color, below: true },
            center: true,
          }
        );
        polyline.setStyle(lineStyle);
        polyline.addTo(this.map);
        let marker = L.marker(startingPoint);
        marker.addTo(this.map);

        this.selectedPolyline = polyline;
        this.selectedMarker = marker;
      }
    },
    drawTrail: function (trail) {
      let latlngs = this.getCoordinates(trail);
      var lineStyle = this.getLineStyle(false, trail.classification);
      var polyline = L.polyline(latlngs);
      polyline.setText(
        this.generateEmptySpace() + trail.code + this.generateEmptySpace(),
        { repeat: true, offset: -10, attributes: { fill: lineStyle.color } }
      );
      polyline.bindPopup(trail.code);
      polyline.setStyle(lineStyle);
      polyline.addTo(this.map);
      return polyline;
    },
    getCoordinates: function (trail) {
      var coordinates = trail.coordinates.map((x) => x.values);
      return coordinates.map((x) => [x[1], x[0]]);
    },
    generateEmptySpace: function () {
      var empty = "";
      for (var i = 0; i < 70; i++) {
        empty += " ";
      }
      return empty;
    },
    updateTileLayer: function (tileLayerName) {
      this.map.removeLayer(getLayerByName(this.currentTileLayerName));
      let tileLayerObject = getLayerByName(
        tileLayerName,
        this.openStreetmapCopy
      ).addTo(this.map);
      this.currentTileLayer = tileLayerObject;
      this.currentTileLayerName = tileLayerName;

      function getLayerByName(layerName, copyright) {
        switch (layerName) {
          case "topo":
            return L.tileLayer(
              "https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png",
              { attribution: copyright }
            );
          case "geopolitic":
            return L.tileLayer(
              "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
              { attribution: copyright }
            );
          case "geopolitic2":
            return L.tileLayer(
              "https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}",
              { attribution: copyright }
            );
          default:
            throw "TileLayer not in list";
        }
      }
    },
  },
});

module.exports = mapFull;

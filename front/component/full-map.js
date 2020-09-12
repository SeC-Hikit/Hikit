var mapFull = Vue.component("map-full", {
  template: `<div class="col-12 col-md-6" id="map-full"></div>`,
  data : {
    openStreetmapCopy : '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    currentTileLayer : new Object(),
    currentTileLayerName : "topo",
    isShowingAllTrails: true
  },
  props: {
    points: Array,
    tileLayerType: String,
    allTrailsObjects: Array,
  },
  watch: {
    allTrailsObjects: function() {
      this.updateAllOtherTrails(this.allTrailsObjects);
    },
    points: function () {
      this.updateTrail(this.points);
    },
    tileLayerType: function(){
      this.updateTileLayer(this.tileLayerType);
    }
  },
  mounted() {

    this.currentTileLayerName = "topo";
    this.currentTileLayer = L.tileLayer("https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png", { attribution: this.openStreetmapCopy});
    let topoLayer = L.tileLayer("https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png", { attribution: this.openStreetmapCopy});
    
    this.map = L.map("map-full", { layers: [topoLayer] }).setView(
      [44.498955, 11.327591],
      12
    );
    
    // L.marker([44.498955, 11.327591]).addTo(this.map);
    //   .bindPopup("A pretty CSS3 popup.<br> Easily customizable.")
    //   .openPopup();
    L.control.scale().addTo(this.map);

    // Prepare graphs

    let data = {
      labels: ["1", "2", "3", "4", "5", "6", "7", "8", "9"],
      datasets: [
        {
          label: "Altitudine",
          backgroundColor: "rgb(255, 99, 132)",
          borderColor: "rgb(255, 99, 132)",
          data: [1000, 1500, 1600, 1400, 2000, 1950, 1000],
          label: "Sentiero 100",
        },
      ],
    };

    var options = {
      maintainAspectRatio: true,
      spanGaps: false,
      elements: {
        line: {
          tension: 0.000001,
        },
      },
      scales: {
        xAxes: [
          {
            display: true,
            scaleLabel: {
              display: true,
              labelString: "Distanza",
            },
          },
        ],
        yAxes: [
          {
            display: true,
            scaleLabel: {
              display: true,
              labelString: "Altitudine (m)",
            },
          },
        ],
      },
      plugins: {
        filler: {
          propagate: false,
        },
        "samples-filler-analyser": {
          target: "chart-analyser",
        },
      },
    };

    var chart = new Chart("chart-hike", {
      type: "line",
      data: data,
      options: options,
    });

    function removeData(chart) {
      // chart.data.labels.pop();
      chart.data.datasets.forEach((dataset) => {
        dataset.data.pop();
      });
      chart.update();
    }

    /**
     *
     * @param Chart chart
     * @param array Array of number values
     */
    function updateChartWithPoints(chart, datapointY) {
      chart.data.datasets = [
        {
          label: "Altitudine",
          backgroundColor: "rgb(255, 99, 132)",
          borderColor: "rgb(255, 99, 132)",
          data: datapointY,
          label: "Sentiero 100",
        },
      ];
      chart.update();
    }
    removeData(chart);
    updateChartWithPoints(chart, [1, 2, 3, 4, 5, 4, 3, 2, 1]);
  },

  methods: {
    toggleLoad: function(isLoading) {
      if(isLoading){
        console.log("Showing load screen");
      }

      console.log("Hiding load screen")
    },
    updateTrail: function (latlngs) {
      console.log("Updating trail...");
      if (latlngs) {
        // create a red polyline from an array of LatLng points
        var polyline = L.polyline(latlngs, {
          color: "red",
          dashArray: "5, 10",
        }).addTo(this.map);
        this.map.fitBounds(polyline.getBounds());
      }
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

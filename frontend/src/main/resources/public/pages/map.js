import mapFull from "../component/full-map";

var MapPage = Vue.component("map-page", {
  props: { id: Number },

  data() {
    return {
      valuerouter: 0,
      tileLayerType: "topo",
      typeTrail: "E",
      trailSelectedObj: {},

      // Selected trail
      points: [],
      downloadLink: "",

      // List of all trails
      error: false,
      trailPreviewResponse: [],

      // Chart data
      chart: {},
      chartOptions: {},

      // Show-hide
      renderAllTrails: false,
      showListTrails: false,
    };
  },
  components: {
    "preview-map": mapFull,
  },
  created: function () {
    this.valuerouter = this.id ? this.id : 0;
    this.trailSelectedObj = {
      startPos: { name: "" }, finalPos: { name: "" },
      statsMetadata: { length: 0, eta: 0, totalRise: 0, totalFall: 0 }
    };
  },
  mounted: function () {
    if (this.valuerouter != 0) {
      this.updateTrail(this.valuerouter);
    }

    // Load all trails in list
    axios.get("http://localhost:8991/app/preview/all").then(
      response => {
        this.trailPreviewResponse = JSON.parse(response.data).trailPreviews
      }).catch(error => {
        console.log(error)
        this.errored = true
      })
      .finally(() => this.loading = false)

    this.chartOptions = {
      tooltips: {
        enabled: true,
      },
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
            display: false,
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
    this.chart = new Chart("chart-hike", {
      type: "line",
      options: this.chartOptions,
    });
  },
  methods: {
    renderAllTrails() {
      axios.get("http://localhost:8991/app/trail/" + id)
    },
    updateTrail(code) {
      if (code) {
        console.log("Getting trail data for " + code);
        // TODO: get trail points
        axios.get("http://localhost:8991/app/trail/" + code).then(
          response => {
            if (response.data) {
              var pointsCoordinates = response.data.trails.map(trail => trail.coordinates)[0];
              var coordinates = pointsCoordinates.map(x => x.values)
              var pointsCoordinatesLatLngs = coordinates.map(coord => [coord[1], coord[0]]);
              this.trailSelectedObj = response.data.trails[0];
              this.typeTrail = response.data.classification;
              this.points = pointsCoordinatesLatLngs; // Triggers Rendering
              this.updateChart(
                this.trailSelectedObj.code,
                pointsCoordinates.map(coord => coord.altitude)
              );
            }
          }).catch(error => {
            console.log(error)
            this.errored = true
          })
          .finally(() => this.loading = false)
      }
    },
    /**
     * Update Chart on page
     * @param {*} code 
     * @param {*} altitudeValues 
     * @param {*} trailLength 
     */
    updateChart(code, altitudeValues) {
      function removeData(chart) {
        chart.data.datasets.forEach((dataset) => {
          dataset.data.pop();
        });
        chart.update();
      }
      function updateChartWithPoints(codeLabel, chart, datapointY) {
        chart.data.labels = datapointY.map(dp => "");
        chart.data.datasets = [
          {
            label: "Altitudine",
            backgroundColor: "rgb(255, 99, 132)",
            borderColor: "rgb(255, 99, 132)",
            pointRadius: 0,
            data: datapointY,
            label: "Sentiero " + codeLabel,
          },
        ];
        chart.update();
      }
      removeData(this.chart);
      updateChartWithPoints(code, this.chart, altitudeValues);
    },
    onTrailListClick(event) {
      var code = $(event.currentTarget).text()
      console.log("Previewing trail code:" + code);
      this.updateTrail(code);
    },
    loadTrailSideBarInfo(infoObj) {
      console.log("Updating trail sidebar...");
    },
    changeTileLayer(layerType) {
      console.log("Updating layer to " + layerType)
      this.tileLayerType = layerType;
    },
    downloadGpx() {
      if (this.trailSelectedObj) {
        var trailCode = this.trailSelectedObj.code;
        axios.get("http://localhost:8991/app/download/" + this.trailSelectedObj.code).then(
          response => {
            if (response.data) {
              var saveData = (function () {
                var a = document.createElement("a");
                document.body.appendChild(a);
                a.style = "display: none";
                return function (fileName) {
                  var blob = new Blob([response.data], { type: "octet/stream" }),
                    url = window.URL.createObjectURL(blob);
                  a.href = url;
                  a.download = fileName;
                  a.click();
                  window.URL.revokeObjectURL(url);
                };
              }());
              var fileName = trailCode + ".gpx";
              saveData(fileName);
            }
          }).catch(error => {
            console.log(error)
            this.errored = true
          })
          .finally(() => this.loading = false)
      }
    },
    toggleList() {
      if (this.showListTrails) {
        $(".column-hikes").addClass("hide");
        $(".column-hike-title").addClass("hide");
        this.showListTrails = false;
      } else {
        $(".column-hikes").removeClass("hide");
        $(".column-hike-title").removeClass("hide");
        this.showListTrails = true;
      }
    },
    toggleAllTrails() {

    }
  },
  template: `
  <div>
  <div class="row relative-map">
  <map-full :points='points' :selectedTrail='trailSelectedObj' :typeTrail='typeTrail' :tileLayerType='tileLayerType'></map-full>
  <div class="column-map col-12 col-md-3 white">
    <div class="row">
    <div class="col-md-10">
      <h1>{{ trailSelectedObj.code }}</h1>
    </div>
    <div class="col-md-2 space-up">
      <svg class="bi" width="24" height="24" fill="red">
        <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#arrow-up-right-square"/>
      </svg>
      </div>
    </div>
  </div>
  <div class="column-map col-12 col-md-4">
      <div class="btn-group btn-group-toggle" data-toggle="buttons">
          <label v-on:click="changeTileLayer('topo')" class="btn btn-light active"><input autocomplete="off" checked id="option1" name="options" type="radio">Topografico</label> 
          <label v-on:click="changeTileLayer('geopolitic')" class="btn btn-light"><input autocomplete="off" id="option2" name="options" type="radio">Geopolitica</label> 
          <label v-on:click="changeTileLayer('geopolitic2')" class="btn btn-light"><input autocomplete="off" id="option3" name="options" type="radio">Geopolitica 2</label>
      </div>
  </div>
  <div class="column-map col-12 col-md-2">
    <div class="btn-group-toggle" data-toggle="buttons">
      <label v-on:click="toggleAllTrails()" class="btn btn-light">
      <svg class="bi" width="24" height="24" fill="currentColor">
        <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#eye"/>
      </svg>
      </label>
      <label v-on:click="toggleList()" class="btn btn-light">
      <svg class="bi" width="24" height="24" fill="currentColor">
      <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#list-ol"/>
      </svg>
      </label>
    </div>
  </div>
  <div class="column-map col-12 col-md-3 column-hike-title white hide">
    <div class="row">
      <div class="col-md-10">
        <h1>Lista sentieri</h1>
      </div>
    </div>
  </div>
</div>
<div class="row">
  
<div class="description hidden column-map col-12 col-md-3 white">
      <h4>Classificazione</h4>
      <p> {{ trailSelectedObj.classification }}</p>
      <h4>Località</h4>
      <p> <span>{{ trailSelectedObj.startPos.name }}</span> - <span>{{ trailSelectedObj.finalPos.name }} </span> </p>
      <h4>Lunghezza</h4>
      <p>{{ parseInt(trailSelectedObj.statsMetadata.length) }}m</p>
      <h4>Tempo di percorrenza</h4>
      <p>{{ Math.ceil(parseInt(trailSelectedObj.statsMetadata.eta)) }}</p>
      <h4>Dislivello</h4>
      <p>Positivo: {{ parseInt(trailSelectedObj.statsMetadata.totalRise) }}m</p>
      <p>Negativo: {{ parseInt(trailSelectedObj.statsMetadata.totalFall) }}m</p>
      <h4>Altitudine</h4>
      <canvas id="chart-hike"></canvas>
      <h4>Descrizione</h4>
      <p>{{ trailSelectedObj.description }}</p>
      <h4>Altro</h4>
      <div class="row">
      <div class="col-md-2 space-up">
        <svg class="bi" width="32" height="32" fill="red">
          <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#download"/>
        </svg>
        </div>
        <div class="col-md-10 space-up">
            <a v-on:click="downloadGpx" target="_blank">Download</a>
        </div>
        <div class="col-md-2 space-up">
          <svg class="bi" width="32" height="32" fill="red">
           <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#geo-alt"/>
          </svg>
        </div>
        <div class="col-md-10 space-up">
          <label>Apri su Maps</label>
        </div>
      </div>
  </div>

  
  
  <div class="column-hikes column-map col-3 white offset-6 hide">
      <table class="table table-striped interactive-table">
          <thead>
              <tr>
                  <th scope="col">Codice</th>
                  <th scope="col">Localita</th>
                  <th scope="col">Class.</th>
              </tr>
          </thead>
          <tbody>
            <tr v-for="trailPreview in trailPreviewResponse" class="trailPreview">
                <td scope="row" v-on:click="onTrailListClick(event)">{{ trailPreview.code }}</td>
                <td>{{ trailPreview.startPos.name }} - {{ trailPreview.endPos.name }}</td>
                <td>{{ trailPreview.classification }}</td>
            </tr>
          </tbody>
      </table>
  </div>

</div>
</div>
  `,
});

module.exports = MapPage;

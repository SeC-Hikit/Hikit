import mapFull from "../component/full-map";
import dialogInfoNotification from "../component/dialog-info-notification";
import fullTrailPage from "../component/full-trail-page";
import { mounted } from "./notifications";

let MapPage = Vue.component("map-page", {
  props: { id: Number },

  data() {
    return {
      loading: true,

      // Id from the router
      valuerouter: 0,
      tileLayerType: "topo",
      typeTrail: "E",
      trailSelectedObj: {},

      // Selected trail
      points: [],
      downloadLink: "",
      notificationsForTrail: [],

      // All other trails
      trails: [],

      // List of all trails
      error: false,
      trailPreviewResponse: [],

      // Chart data
      chart: {},
      chartOptions: {},

      // Show-hide
      renderAllTrails: true,
      showListTrails: false,
      showTrailPage: false,
      showDetails: false,
    };
  },
  watch: {
    $route(to, from) {
      console.log(to, from);
      // TODO : check whether the trail exists
      this.clickTrail(to.params.id);
    },
    loading: function() { 
      toggleLoading(this.loading);
    }
  },
  components: {
    "preview-map": mapFull,
    "dialog-info-confirmation": dialogInfoNotification,
    "full-trail-page": fullTrailPage,
  },
  created: function () {
    this.valuerouter = this.id ? this.id : 0;
    this.trailSelectedObj = {
      startPos: { name: "" },
      finalPos: { name: "" },
      statsMetadata: { length: 0, eta: 0, totalRise: 0, totalFall: 0 },
    };
    this.showDetails = false;
    this.renderAllTrails = true;
  },
  mounted: function () {
    this.loading = true;
    if (this.valuerouter != 0) {
      this.toggleDetails();
      this.updateTrail(this.valuerouter);
    }
    if (this.renderAllTrails) {
      this.renderTrails();
    }
    // Maximise map view
    let heightWOHeader = window.innerHeight - $(".it-header-wrapper").height();
    let heightDetails = $(".details").height();
    $("#map-full").css("min-height", heightWOHeader);
    $("#fullTrailPage .container").css("max-height", heightWOHeader - 50);
    $(".scrollable").css("max-height", heightWOHeader - heightDetails);
    // Load all trails in list
    axios
      .get(BASE_IMPORTER_ADDRESS + "/preview")
      .then((response) => {
        this.trailPreviewResponse = response.data.trailPreviews;
      })
      .catch((error) => {
        console.log(error);
        this.errored = true;
      })
      .finally(() => (this.loading = false));

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
    toggleLoading(this.loading);
  },
  methods: {
    renderTrails() {
      console.log("Getting trails...");
      this.loading = true;
      axios.get(BASE_IMPORTER_ADDRESS + "/trail").then((response) => {
        if (response.data) {
          this.trails = response.data.trails;
        }
      }).finally(() => (this.loading = false ));
    },
    updateTrail(code) {
      function rotateCoordinates(coordinates){
        return coordinates.map((coord) => [
          coord[1],
          coord[0],
        ]);
      };

      if (code) {
        console.log("Getting trail data for " + code);
        // TODO: get trail points
        this.loading = true;
        axios
          .get(BASE_IMPORTER_ADDRESS + "/trail/" + code)
          .then((response) => {
            if (response.data) {
              var pointsCoordinates = response.data.trails.map(
                (trail) => trail.coordinates
              )[0];
              var coordinates = pointsCoordinates.map((x) => x.values);
              var pointsCoordinatesLatLngs = rotateCoordinates(coordinates);
              this.trailSelectedObj = response.data.trails[0];
              this.typeTrail = response.data.classification;
              this.points = pointsCoordinatesLatLngs; // Triggers Rendering
              this.showNotificationsIconIfPresent(code);
              this.updateChart(
                this.trailSelectedObj.code,
                pointsCoordinates.map((coord) => coord.altitude)
              );
            }
          })
          .catch((error) => {
            console.log(error);
            this.errored = true;
          })
          .finally(() => (this.loading = false));
      }
    },
    showNotificationsIconIfPresent(code) {
      console.log("Checking notifications for trail '" + code + "'");
      axios
        .get(BASE_IMPORTER_ADDRESS + "/notifications/" + code)
        .then((response) => {
          if (response.data) {
            this.notificationsForTrail =
              response.data.accessibilityNotifications;
          }
        })
        .catch((error) => {
          console.log(error);
          this.errored = true;
        })
        .finally(() => (this.loading = false));
    },
    toggleModal() {
      $("#modal_info").modal("toggle");
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
        chart.data.labels = datapointY.map((dp) => "");
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
      var code = $(event.currentTarget).text();
      this.$router.push("/map/" + code);
    },
    clickTrail(code) {
      console.log("Loading trail with code : " + code);
      this.updateTrail(code);
      this.ensureDetailsVisible();
      console.log("Received click: " + code);
    },
    changeTileLayer(layerType) {
      console.log("Updating layer to " + layerType);
      this.tileLayerType = layerType;
    },
    downloadGpx() {
      if (this.trailSelectedObj) {
        var trailCode = this.trailSelectedObj.code;
        axios
          .get(
            BASE_IMPORTER_ADDRESS + "/trail/download/" +
              this.trailSelectedObj.code
          )
          .then((response) => {
            if (response.data) {
              var saveData = (function () {
                var a = document.createElement("a");
                document.body.appendChild(a);
                a.style = "display: none";
                return function (fileName) {
                  var blob = new Blob([response.data], {
                      type: "octet/stream",
                    }),
                    url = window.URL.createObjectURL(blob);
                  a.href = url;
                  a.download = fileName;
                  a.click();
                  window.URL.revokeObjectURL(url);
                };
              })();
              var fileName = trailCode + ".gpx";
              saveData(fileName);
            }
          })
          .catch((error) => {
            console.log(error);
            this.errored = true;
          })
          .finally(() => (this.loading = false));
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
    toggleFullTrailPage() {
      $("#fullTrailPage").fadeIn();
    },
    toggleDetails() {
      if (this.showDetails) {
        $(".details").addClass("hide");
        this.showDetails = false;
      } else {
        $(".details").removeClass("hide");
        this.showDetails = true;
      }
    },
    ensureDetailsVisible() {
      $(".details").removeClass("hide");
      this.showDetails = true;
    },
  },
  template: `
  <div>
  <div class="row relative-map">
    <full-trail-page @clicked="clickTrail" :trailObject='trailSelectedObj' :notificationsForTrail='notificationsForTrail'></full-trail-page>
    <map-full :selectedTrail='trailSelectedObj' :tileLayerType='tileLayerType' :trails='trails'></map-full>
    <div id="absolute-wrapper">
      <div class="row">
        <div class="column-map col-12 col-md-3 white details hide">
          <div class="row clickable" v-on:click="toggleFullTrailPage">
            <div class="col-md-10">
              <h1>{{ trailSelectedObj.code }}</h1>
            </div>
            <div class="col-md-2 space-up">
              <svg class="bi" width="24" height="24" fill="red">
                <use xlink:href="node_modules/bootstrap-icons/bootstrap-icons.svg#arrow-up-right-square"/>
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
            <label v-on:click="toggleAllTrails()" class="btn btn-light active">
            <svg class="bi" width="24" height="24" fill="currentColor">
              <use xlink:href="node_modules/bootstrap-icons/bootstrap-icons.svg#eye"/>
            </svg>
            </label>
            <label v-on:click="toggleList()" class="btn btn-light">
            <svg class="bi" width="24" height="24" fill="currentColor">
            <use xlink:href="node_modules/bootstrap-icons/bootstrap-icons.svg#list-ol"/>
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
        
      <div class="description hidden column-map col-12 col-md-3 white scrollable details hide">
            <h4>Classificazione</h4>
            <p> {{ trailSelectedObj.classification }}</p>
            <h4>Località</h4>
            <p> <span>{{ trailSelectedObj.startPos.name }}</span> - <span>{{ trailSelectedObj.finalPos.name }} </span> </p>
            <h4>Percorribilita'</h4>
            <div v-if="notificationsForTrail.length > 0" class="clickable">
              <div v-on:click="toggleModal">
                <svg class="bi pulse" width="24" height="24" fill="red">
                  <use xlink:href="node_modules/bootstrap-icons/bootstrap-icons.svg#exclamation-triangle-fill"/>
                </svg>
                <p>Clicca visualizzare avvisi di percorribilita'</p>
              </div>
            </div>
            <p v-else>Non ci sono avvisi di percorribilita'.</p>
            <h4>Lunghezza</h4>
            <p>{{ parseInt(trailSelectedObj.statsMetadata.length) }}m</p>
            <h4>Tempo di percorrenza</h4>
            <p>{{ Math.ceil(parseInt(trailSelectedObj.statsMetadata.eta)) }} minuti</p>
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
                <use xlink:href="node_modules/bootstrap-icons/bootstrap-icons.svg#download"/>
              </svg>
              </div>
              <div class="col-md-10 space-up clickable">
                  <a v-on:click="downloadGpx" target="_blank">Download</a>
              </div>
              <div class="col-md-2 space-up clickable">
                <svg class="bi" width="32" height="32" fill="red">
                <use xlink:href="node_modules/bootstrap-icons/bootstrap-icons.svg#geo-alt"/>
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
    <dialog-info-notification :id='trailSelectedObj.code' :unresolvedNotifications='notificationsForTrail'></dialog-info-notification>
  </div>
</div>
  `,
});

module.exports = MapPage;

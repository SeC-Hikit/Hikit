let fullTrailPage = Vue.component("full-trail-page", {
  props: {
    trailObject: Object,
    notificationsForTrail: Array,
  },
  data() {
    return {
      chartOptions : new Object(),
      chart : new Object()
    }
  },
  watch: {
    trailObject: function selectedTrail() {
      let pointsCoordinates = this.trailObject.coordinates;
      let altitudePoints = pointsCoordinates.map(coord => coord.altitude)
      this.updateChart(this.trailObject.code, altitudePoints);
    }
  },
  mounted: function() {
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
    this.chart = new Chart("chart-hike-full", {
      type: "line",
      options: this.chartOptions,
    });
  },
  methods: {
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
    downloadGpx() {
      if (this.trailObject) {
        var trailCode = this.trailObject.code;
        axios.get("http://localhost:8991/app/download/" + this.trailObject.code).then(
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
              let fileName = trailCode + ".gpx";
              saveData(fileName);
            }
          }).catch(error => {
            console.log(error)
          })
          .finally(() => this.loading = false)
      }
    },
    toggleModal(){
      $("#modal_info").modal("toggle");
    },
    toggleThis(){
      $("#fullTrailPage").fadeOut();
    },
  },
  template: `
    <div id="fullTrailPage">
      <div class="container">
        <div class="row space-up">
          <div class="col-12 col-md-6">
            <h1>Sentiero  {{ trailObject.code }}</h1>
          </div>
          <div class="col-1 col-md-1 offset-5 clickable">
            <svg class="bi" width="24" height="24" fill="red" v-on:click="toggleThis">
              <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#x"/>
            </svg>
          </div>
        </div>
        <div class="row">
          <div class="col-12 col-md-6">
          <p> {{ trailObject.classification }}</p>
          </div>
        </div>
        <div class="row">
          <div class="col-12 col-md-6">
          <h2>Descrizione</h2>
          <p> {{ trailObject.description }}</p>
          </div>
          <div class="col-12 col-md-6">
            <h2>Percorribilita'</h2>
            <div v-if="notificationsForTrail.length > 0" class="clickable">
              <div v-on:click="toggleModal">
                <svg class="bi pulse" width="24" height="24" fill="red">
                  <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#exclamation-triangle-fill"/>
                </svg>
                <span>Clicca per visualizzare avvisi di percorribilita'</span>
              </div>
            </div>
            <p v-else>Non ci sono avvisi di percorribilita'.</p>
          </div>
        </div>
        <div class="row space-up">
          <div class="col-12">
            <h2>Dati</h2>
          </div>
        </div>
        <div class="row">
          <div class="col-12 col-md-3">
            <h4>Lunghezza</h4>
            <p>{{ parseInt(trailObject.statsMetadata.length) }}m</p>
          </div>
          <div class="col-12 col-md-4">
            <h4>Tempo di percorrenza stimato</h4>
            <p>{{ Math.ceil(parseInt(trailObject.statsMetadata.eta)) }} minuti</p>
          </div>
        </div>
        <div class="row">
          <div class="col-12 col-md-3">
            <h4>Dislivello</h4>
            <p>Positivo: {{ parseInt(trailObject.statsMetadata.totalRise) }}m</p>
            <p>Negativo: {{ parseInt(trailObject.statsMetadata.totalFall) }}m</p>
          </div>
          <div class="col-12 col-md-6">
            <h4>Profilo Altimetrico</h4>
            <canvas id="chart-hike-full"></canvas>
          </div>
        </div>
        <div class="row space-up">
          <div class="col-12">
            <h4>Altro</h4>
          </div>
        </div>
        <div class="row clickable space-up" v-on:click="downloadGpx">
          <div class="col-md-1">
            <svg class="bi" width="32" height="32" fill="red">
              <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#download"/>
            </svg>
          </div>
          <div class="col-md-4">
            <label>Download .GPX</label>
          </div>
        </div>
        <div class="row clickable">
          <div class="col-md-1">
            <svg class="bi" width="32" height="32" fill="red">
              <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#geo-alt"/>
            </svg>
          </div>
          <div class="col-md-4">
              <label>Apri su Maps</label>
          </div>
          </div>
          <div class="row clickable">
          <div class="col-md-1">
            <svg class="bi" width="32" height="32" fill="red">
              <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#printer"/>
            </svg>
          </div>
          <div class="col-md-4">
              <label>Stampa</label>
          </div>
          </div>
        </div>
      </div>
      
    `,
});

module.exports = fullTrailPage;
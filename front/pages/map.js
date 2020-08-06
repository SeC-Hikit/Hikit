var MapPage = {
  template: `
  <div>
  <div class="row">
  <div class="column-map col-12 col-md-3">
      <div class="btn-group-toggle" data-toggle="buttons">
          <label class="btn btn-light active"><input autocomplete="off" checked type="checkbox">Mostra tutti i sentieri</label>
      </div>
  </div>
  <div class="column-map col-12 col-md-6">
      <div class="btn-group btn-group-toggle" data-toggle="buttons">
          <label class="btn btn-light active"><input autocomplete="off" checked id="option1" name="options" type="radio">Topografico</label> <label class="btn btn-light"><input autocomplete="off" id="option2" name="options" type="radio">Geopolitica</label> <label class="btn btn-light"><input autocomplete="off" id="option3" name="options" type="radio">Geopolitica 2</label>
      </div>
  </div>
  <div class="col-12 col-md-3"></div>
</div>
<div class="row">
  <div class="column-hikes column-map col-12 col-md-3">
      <table class="table table-striped interactive-table">
          <thead>
              <tr>
                  <th scope="col">Codice</th>
                  <th scope="col">Localita</th>
                  <th scope="col">Classificazione</th>
              </tr>
          </thead>
          <tbody>
              <tr v-on:click="updateTrail(1)">
                  <th scope="row">1</th>
                  <td>Calderino - Mt. S.Giovanni - Mt.Pastore</td>
                  <td>E</td>
              </tr>
          </tbody>
      </table>
  </div>
  <div class="col-12 col-md-6" id="map-full"></div>
  <div class="description hidden column-map col-12 col-md-3">
      <h2>Sentiero 100, E</h2>
      <p>Parte di "Sentiero degli Dei"</p>
      <h4>Lunghezza</h4>
      <p>10.4 Km</p>
      <h4>Dislivello</h4>
      <p>Positivo: 1230m</p>
      <h4>Altitudine</h4>
      <canvas id="chart-hike"></canvas>
      <h4>Descrizione</h4>
      <p>Lorem ipsum dolor sit, amet consectetur adipisicing elit. Maxime, eaque. Rem doloremque illum ex iste, culpa tempore veritatis itaque earum veniam optio voluptate placeat esse nihil ullam. Impedit, consectetur nobis!</p>
  </div>
</div>
</div>
  `,
  mounted() {
    var map = L.map("map-full").setView([44.498955, 11.327591], 12);

    //  Topographic:
    //
    L.tileLayer("https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png", {
      attribution:
        '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    }).addTo(map);

    L.marker([44.498955, 11.327591]).addTo(map);
    //   .bindPopup("A pretty CSS3 popup.<br> Easily customizable.")
    //   .openPopup();

    // create a red polyline from an array of LatLng points
    var latlngs = [
      [44.134626399183382, 11.122509399848253],
      [44.13700939929344, 11.129171899797592],
    ];
    var polyline = L.polyline(latlngs, {
      color: "red",
      dashArray: "5, 10",
    }).addTo(map);
    

    function getLayer(layerName){
      switch(layerName){ 
        case "topo":
          return L.tileLayer("https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png", {
            attribution:
              '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
          });
          break;
        case "geopolitic":
          return L.tileLayer("https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png", {
            attribution:
              '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
          });
          break;
        case "geopolitc2":
          return L.tileLayer("https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png", {
            attribution:
              '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
          });
          break;
      }
    }

    function clearMap(){

    }


    function fitBoundToHikeBound() {
      map.fitBounds(polyline.getBounds());
    }

    fitBoundToHikeBound();

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
      chart.data.datasets = [{ 
        label: "Altitudine",
        backgroundColor: "rgb(255, 99, 132)",
        borderColor: "rgb(255, 99, 132)",
        data: datapointY,
        label: "Sentiero 100",
       }];
      chart.update();
    }
    removeData(chart);
    updateChartWithPoints(chart, [1,2,3,4,5,4,3,2,1])





  }, methods: {
    
    updateTrail: function(code){
      console.log(code);
    }

  }
};

module.exports = MapPage;

import mapFull from "../component/full-map";

const newLocal = `
  <div>
  <div class="row">
  <div class="column-map col-12 col-md-3">
      <div class="btn-group-toggle" data-toggle="buttons">
          <label v-on:click="changeTileLayer('loadAllTrails')" class="btn btn-light active"><input autocomplete="off" checked type="checkbox">Mostra tutti i sentieri</label>
      </div>
  </div>
  <div class="column-map col-12 col-md-6">
      <div class="btn-group btn-group-toggle" data-toggle="buttons">
          <label v-on:click="changeTileLayer('topo')" class="btn btn-light active"><input autocomplete="off" checked id="option1" name="options" type="radio">Topografico</label> 
          <label v-on:click="changeTileLayer('geopolitic')" class="btn btn-light"><input autocomplete="off" id="option2" name="options" type="radio">Geopolitica</label> 
          <label v-on:click="changeTileLayer('geopolitic2')" class="btn btn-light"><input autocomplete="off" id="option3" name="options" type="radio">Geopolitica 2</label>
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

  <map-full :points='points' :tileLayerType='tileLayerType'></map-full>
  
  <div class="description hidden column-map col-12 col-md-3">
      <div class="row">
        <div class="col-md-1 space-up">
        <svg class="bi" width="32" height="32" class="pulse" fill="currentColor">
            <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#exclamation-triangle-fill"/>
          </svg>
        </div>
        <div class="col-md-9 ml-1">
        <h2>100, E</h2>
        </div>
        <div class="col-md-1 space-up">
          <svg class="bi" width="24" height="24" fill="red">
            <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#arrow-up-right-square"/>
          </svg>
        </div>
      </div>
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
  `;
var MapPage = Vue.component("map-page", {
  props: { id: Number },

  data() {  
    return {
      points: [],
      valuerouter: 0,
      tileLayerType: "topo"
    };
  },
  components: {
    "preview-map": mapFull,
  },
  created: function () {
    this.valuerouter = this.id ? this.id : 0;
  },
  mounted: function(){
    if(this.valuerouter != 0){
        this.updateTrail(this.id);
    }
  },
  methods: {
    updateTrail(id) {
        if (id) {
        console.log("Getting trail data for " + id);
        // TODO: get trail points
        this.points = [
          [44.134626399183382, 11.122509399848253],
          [44.13700939929344, 11.129171899797592],
        ];
      }
    },
    loadTrailSideBarInfo(infoObj) {
        console.log("Updating trail sidebar...");
    },
    changeTileLayer(layerType) {
        console.log("Updating layer to " + layerType)
        this.tileLayerType = layerType;
    }
  },
  template: newLocal,
});

module.exports = MapPage;

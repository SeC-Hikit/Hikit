import locationPreviewMap from "./location-preview-map";

let positionElementAddable = Vue.component("position-addable", {
  props: {
    number: Number,
    points: Array
  },
  data() {
    return {
      points : [],
      location : [],
      clickedMap: false,
    }
  },
  components: {
    "location-preview-map" : locationPreviewMap
  },
  methods: {
    onClickMap(){
      this.clickedMap = true;
    }
  },
  mounted() {
    this.location = [];
  },
  template: `
  <div v-bind:id="'position-' + number">
    <h3>Localita' n.{{number + 1}}</h3>        
    <div class="col-12 space-up">
      <div class="list-group" v-bind:id="'list-tab-' + number" role="tablist">
        <a class="list-group-item list-group-item-action active" data-toggle="list" v-bind:href="'#list-home-' + number" role="tab" aria-controls="home">Selezione da lista</a>
        <a class="list-group-item list-group-item-action" data-toggle="list" v-bind:href="'#list-profile-' + number" role="tab" aria-controls="profile" v-on:click="onClickMap">Seleziona dalla mappa</a>
      </div>
    </div>
    <div class="col-12 space-up">
      <div class="tab-content" id="nav-tabContent">

      <!-- Auto selection -->
        
        <div class="tab-pane fade show active" v-bind:id="'list-home-' + number" role="tabpanel" aria-labelledby="list-home-list">
          <div class="row form-group space-up">
            <legend class="col-form-label col-sm-1 pt-0">Nome principale</legend>
            <div class="col-sm-5 bootstrap-select-wrapper">
              <label>Selezione la localita'</label>
              <select title="Scegli una opzione" data-live-search="true" data-live-search-placeholder="Cerca opzioni">
                <option value="1">Lorem ipsum dolor sit amet</option>
                <option value="2">Duis vestibulum eleifend libero</option>
                <option value="3">Phasellus pretium orci sed odio tempus</option>
                <option value="4">Vestibulum bibendum ex vel augue porttitor sodales</option>
                <option value="5">Praesent quis elementum turpis</option>
              </select>
            </div>
          </div>
          <div class="row form-group space-up">
          <legend class="col-form-label col-sm-1 pt-0">Altri nomi</legend>
          <div class="col-sm-5 bootstrap-select-wrapper">
              <label>Digita i vari nomi con i quali e' conosciuta la localita' separati da virgola</label>
              <input type="text" class="form-control" v-bind:id="'position-tags-' + number"  v-bind:name="'position-tags-' + number" required>
          </div>
        </div>
      </div>


      <!-- Manual selection from map -->
      <div class="tab-pane fade"  v-bind:id="'list-profile-' + number" role="tabpanel" aria-labelledby="list-profile-list">
        
        <!-- Name -->
        <div class="row form-group space-up">
          <legend class="col-form-label col-sm-1 pt-0">Nome principale</legend>
          <div class="col-sm-5 bootstrap-select-wrapper">
            <label>Digita il nome principale</label>
            <input type="text" class="form-control" v-bind:id="'position-name-' + number"  v-bind:name="'position-name-' + number" required>
          </div>
        </div>

        <!-- Tags -->
        <div class="row form-group space-up">
          <legend class="col-form-label col-sm-1 pt-0">Altri nomi</legend>
          <div class="col-sm-5 bootstrap-select-wrapper">
            <label>Digita i vari nomi con i quali e' conosciuta la localita' separati da virgola</label>
            <input type="text" class="form-control" v-bind:id="'position-tags-' + number"  v-bind:name="'position-tags-' + number" required>
          </div>
        </div>

        <h4>Seleziona localita' dalla mappa</h4>
          <location-preview-map :clicked='clickedMap' :number='number' :points='points'></location-preview-map>
      </div>
    </div>
  </div>
    `,
});

module.exports = positionElementAddable;

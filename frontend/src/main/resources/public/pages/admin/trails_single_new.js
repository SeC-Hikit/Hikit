import previewMap from "../../component/preview-map";
import textOnlyAreaComponent from "../../component/textonly-area";
import positionAddable from "../../component/position-addable";

let TrailManagementPageSingleNew = {
  data() {
    return {
      loading: false,
      // Data on page
      locationChildren: [],
      date: new Object(),
      // Trail preparation
      trailPreparationModel: new Object(),
      coords: [],
    };
  },
  components: {
    "preview-map": previewMap,
    "text-only-area-component": textOnlyAreaComponent,
    "position-addable": positionAddable,
  },
  created: function () {
    this.trailPreparationModel = {
      name: "",
      description: "",
      firstPos: { altitude: "", longitude: "", latitude: "" },
      lastPos: { altitude: "", longitude: "", latitude: "" },
      coordinates: [],
    };
    this.coords = [];
  },
  mounted: function () {
    this.loading = false;
    this.locationChildren = [];
    this.date = moment().format("YYYY-MM-DD");
  },
  methods: {
    onFileUpload: function (e) {
      var customFormData = new FormData();
      // 23/11 TODO: use the event to target this
      const form = document.querySelector("#gpxFileUploader");
      customFormData.append("gpxFile", form.files[0]);
      this.loading = true;
      axios
        .put(BASE_IMPORTER_ADDRESS + "/trail/import/preview", customFormData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        })
        .then((response) => {
          this.trailPreparationModel = response.data;
        })
        .finally(() => (this.loading = false));
    },
    onAddNewLocation: function (e) {
      this.coords = this.trailPreparationModel.coordinates;
      this.locationChildren.push(positionAddable);
    },
    onSubmit: function (e) {
      console.log("Saving...");
    },
    close: function () {
      if (
        confirm(
          "Sei sicuro di voler tornare al menù? \nCliccando 'OK' tutte le modifiche a questo sentiero andranno perse."
        )
      ) {
        this.$router.push("/admin/trails");
      }
    },
  },
  watch: {
    loading: function () {
      toggleLoading(this.loading);
    },
  },
  template: `
  <div class="container my-4">
  <nav-menu-admin></nav-menu-admin>
  <h1 class="space-up">Aggiungi sentiero</h1>
  <p>Compila i seguenti campi per aggiungere un sentiero.</p>
  <div class="table-wrapper space-up">
      <form>
        <div class="form-group row">
            <legend class="col-form-label col-sm-2 pt-0">File GPX</legend>
            <div class="col-sm-8 bootstrap-select-wrapper">
                <input type="file" class="form-control" id="gpxFileUploader" v-on:change="onFileUpload" required>
            </div>
        </div>
      </form>
      <form>
          <div class="form-group row">
            <legend class="col-form-label col-sm-2 pt-0">Codice sentiero</legend>
            <div class="col-sm-8">
                <input type="text" class="form-control" id="trailId" v-bind:value="trailPreparationModel.name" placeholder="Es: 105, 105BO, 106..." required>
            </div>
          </div>
          <div class="form-group row">
            <legend class="col-form-label col-sm-2 pt-0">Nome sentiero (se diverso dal codice)</legend>
            <div class="col-sm-8">
                <input type="text" class="form-control" id="trailName" v-bind:value="trailPreparationModel.name" placeholder="Es: Via delle Fate" required>
            </div>
          </div>
          <div class="form-group row">
              <legend class="col-form-label col-sm-2 pt-0">Descrizione</legend>
              <div class="col-sm-8">
                <textarea-textonly :text='trailPreparationModel.description'></textarea-textonly>
              </div>
          </div>
          <div class="form-group row">
              <legend class="col-form-label col-sm-2 pt-0">Data Rilevazione</legend>
              <div class="col-sm-6">
                  <input class="form-check-input datepicker" type="date" placeholder="dd-mm-yyyy" v-bind:value="date" name="date" id="date" required/>
              </div>
          </div>
          <div class="form-group row">
            <legend class="col-form-label col-sm-2 pt-0">Sezione CAI Responsabile</legend>
            <div class="bootstrap-select-wrapper">
              <select title="Scegli un'opzione" data-live-search="true" data-live-search-placeholder="Cerca opzioni">
                <option value="Value 1">CAI Bologna</option>
                <option value="Value 2">CAI Porretta</option>
                <option value="Value 3">CAI Modena</option>
              </select>
            </div>
          </div>
          <div class="form-group row">
            <legend class="col-form-label col-sm-2 pt-0">Classificazione</legend>
            <div class="bootstrap-select-wrapper">
              <select title="Scegli un'opzione" data-live-search="true" data-live-search-placeholder="Cerca opzioni">
                <option value="T">T</option>
                <option value="E">E</option>
                <option value="EE">EE</option>
                <option value="EEA">EEA</option>
              </select>
            </div>
          </div>


          <h2>Localita'</h2>
          <p class="space-up">Le localita' sono utilizzate per segnalare progresso sui sentieri e, lato di sviluppo, per creare una rete sentieristica interconnessa.</p>
          <h3>Partenza</h3>
          <div class="row form-group space-up">
            <legend class="col-form-label col-sm-1 pt-0">Nome principale</legend>
            <div class="col-sm-5 bootstrap-select-wrapper">
              <label>Digita il nome principale</label>
              <input type="text" class="form-control" id="start-name"  name="start-name" required>
            </div>
          </div>
          <div class="row form-group space-up">
            <legend class="col-form-label col-sm-1 pt-0">Altri nomi</legend>
            <div class="col-sm-5 bootstrap-select-wrapper">
              <label>Digita i vari nomi con i quali e' conosciuta la localita' separati da virgola</label>
              <input type="text" class="form-control" id="start-tags"  name="start-tags">
            </div>
          </div>
          <div class="row form-group space-up">
            <div class="col-sm-3 bootstrap-select-wrapper">
              <label>Longitudine</label>
              <input disabled type="text" class="form-control" id="long-start"  name="long-start" v-bind:value="trailPreparationModel.firstPos.longitude" required>
            </div>
            <div class="col-sm-3 bootstrap-select-wrapper">
              <label>Latitudine</label>
              <input disabled type="text" class="form-control" id="lat-start"  name="lat-start" v-bind:value="trailPreparationModel.firstPos.latitude" required>
            </div>
            <div class="col-sm-3 bootstrap-select-wrapper">
              <label>Altitudine</label>
              <input disabled type="text" class="form-control" id="alt-start"  name="alt-start" v-bind:value="trailPreparationModel.firstPos.altitude" required>
            </div>
            <div class="col-sm-3 bootstrap-select-wrapper">
              <label>Distanza dalla partenza</label>
              <input disabled type="text" class="form-control" id="dist-start"  name="dist-start" v-bind:value="trailPreparationModel.firstPos.distanceFromTrailStart" required>
            </div>
          </div>

          <h3>Destinazione</h3>
          <div class="row form-group space-up">
            <legend class="col-form-label col-sm-1 pt-0">Nome principale</legend>
            <div class="col-sm-5 bootstrap-select-wrapper">
              <label>Digita il nome principale</label>
              <input type="text" class="form-control" id="end-name"  name="end-name" required>
            </div>
          </div>
          <div class="row form-group space-up">
            <legend class="col-form-label col-sm-1 pt-0">Altri nomi</legend>
            <div class="col-sm-5 bootstrap-select-wrapper">
              <label>Digita i vari nomi con i quali e' conosciuta la localita' separati da virgola</label>
              <input type="text" class="form-control" id="end-tags"  name="end-tags" required>
            </div>
          </div>
          <div class="row form-group space-up">
            <div class="col-sm-3 bootstrap-select-wrapper">
              <label>Longitudine</label>
              <input disabled type="text" class="form-control" id="long-end"  name="long-end"  v-bind:value="trailPreparationModel.lastPos.longitude" required>
            </div>
            <div class="col-sm-3 bootstrap-select-wrapper">
              <label>Latitudine</label>
              <input disabled type="text" class="form-control" id="lat-end"  name="lat-end" v-bind:value="trailPreparationModel.lastPos.latitude" required>
            </div>
            <div class="col-sm-3 bootstrap-select-wrapper">
              <label>Altitudine</label>
              <input disabled type="text" class="form-control" id="alt-end"  name="alt-end"  v-bind:value="trailPreparationModel.lastPos.altitude" required>
            </div>
            <div class="col-sm-3 bootstrap-select-wrapper">
              <label>Distanza dalla partenza</label>
              <input disabled type="text" class="form-control" id="dist-end"  name="dist-end" v-bind:value="trailPreparationModel.lastPos.distanceFromTrailStart" required>
            </div>
          </div>


          <h3>Localita' intermedie</h3>
          <template v-for="(child, index) in locationChildren">
            <position-addable :points='coords' :number='index'></position-addable>
          </template>
          <button type="button" v-on:click="onAddNewLocation" class="btn btn-info">Aggiungi una nuova localita'</button>

          
          <div class="form-group row space-up">
              <div class="col-sm-2">
                  <button type="submit" class="btn btn-primary" v-on:click="onSubmit">Salva e pubblica</button>
              </div>
              <div class="col-sm-3"> 
                <button type="button" v-on:click="close" class="btn btn-light">Torna a gestione sentieri</button>
              </div>
          </div>
      </form>
  </div>
</div>`,
};

module.exports = TrailManagementPageSingleNew;

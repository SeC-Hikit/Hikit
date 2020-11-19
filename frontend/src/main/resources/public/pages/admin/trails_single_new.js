import previewMap from "../../component/preview-map";
import textOnlyAreaComponent from "../../component/textonly-area";

let TrailManagementPageSingleNew = {
  data() {
    return {
      points: 0,
    };
  },
  components: {
    "preview-map": previewMap,
    "text-only-area-component": textOnlyAreaComponent,
  },
  methods: {
    onPreview: function (id) {
      console.log("Requested trail id " + id);
      axios.get("abc");
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
  template: `
  <div class="container my-4">
  <nav-menu-admin></nav-menu-admin>
  <h1 class="space-up">Aggiungi sentiero</h1>
  <p>Compila i seguenti campi per aggiungere un sentiero.</p>
  <div class="table-wrapper space-up">
      <form>
          <div class="form-group row">
            <legend class="col-form-label col-sm-2 pt-0">Codice sentiero</legend>
            <div class="col-sm-8">
                <input type="text" class="form-control" id="trailId" placeholder="Es: 105, 105BO, 106..." required>
            </div>
          </div>
          <div class="form-group row">
              <legend class="col-form-label col-sm-2 pt-0">File GPX</legend>
              <div class="col-sm-8 bootstrap-select-wrapper">
                  <input type="file" class="form-control" id="gpxFile" required>
              </div>
          </div>
          <div class="form-group row">
              <legend class="col-form-label col-sm-2 pt-0">Lunghezza (se conosciuta)</legend>
              <div class="col-sm-8">
                  <input type="text" class="form-control" id="distance" placeholder="Distanza in metri (es: 2600m)" required>
              </div>
          </div>
          <div class="form-group row">
              <legend class="col-form-label col-sm-2 pt-0">Descrizione</legend>
              <div class="col-sm-8">
                <textarea-textonly></textarea-textonly>
              </div>
          </div>
          <div class="form-group row">
              <legend class="col-form-label col-sm-2 pt-0">Data Rilevazione</legend>
              <div class="col-sm-6">
                  <input class="form-check-input datepicker" type="date" name="date" id="date" required/>
              </div>
          </div>
          

          <h2>Anteprima</h2>
          <div class="row space-up">
            <div class="col-sm-10">
                <preview-map :points="points"></preview-map>
            </div>
          </div>

          <div class="form-group row space-up">
              <div class="col-sm-2">
                  <button type="submit" class="btn btn-primary">Salva e pubblica</button>
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

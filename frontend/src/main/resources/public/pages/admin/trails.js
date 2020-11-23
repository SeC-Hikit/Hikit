import dialogConfirm from "../../component/dialog-confirm";

var TrailManagementPage = {
  data(){
    return {
      trailsResponse: new Object()
    }
  },
  methods: {
    resolveNotification: function (id) {
      $("#modal_confirm").modal("toggle");
    },
    onDelete: function (id, places) {
      if (
        confirm(
          "Sei sicuro di voler rimuovere il sentiero numero '" +
            id +
            "' con località '" +
            places +
            "'?"
        )
      ) {
        // remove it
      }
    },
  },
  mounted() {
    toggleLoading(true);
    axios
      .get(BASE_IMPORTER_ADDRESS + "/preview")
      .then((response) => {
        this.trailsResponse = response.data.trailPreviews;
      })
      .catch((error) => {
        console.log(error);
        this.errored = true;
      })
      .finally(() => (this.loading = false));
  },
  components: {
    "dialog-confirm": dialogConfirm,
  },
  template: `
    <div class="container my-4">
    <nav-menu-admin></nav-menu-admin>
      <div class="row">
        <div class="col-12 col-md-12">
                <div class="table-wrapper space-up">
                        <div class="table-title">
                            <div class="row">
                                <div class="col-sm-8"><h2>Gestione Sentieri</h2></div>
                                <div class="col-sm-8">
                                <router-link to="/admin/trails/add">  
                                        <button type="button" class="btn btn-primary add-new"><i class="fa fa-plus"></i>Aggiungi</button>
                                </router-link>
                                </div>
                            </div>
                        </div>
                        <table class="table table-striped interactive-table">
                <thead>
                    <tr>
                        <th scope="col">Codice</th>
                        <th scope="col">Localita</th>
                        <th scope="col">Classificazione</th>
                        <th scope="col">Caricato il</th>
                        <th scope="col">Azioni</th>
                    </tr>
                </thead>
                <tbody>
                  <tr v-for="trailPreview in trailsResponse" class="trailPreview">
                    <th scope="row">{{ trailPreview.code }}</th>
                    <td>{{ trailPreview.startPos.name }} - {{ trailPreview.endPos.name }}</td>
                    <td>{{ trailPreview.classification }}</td>
                    <td>{{ moment(trailPreview.date).format("DD/MM/YYYY") }}</td>
                    <td>
                      <svg v-on:click="onDelete" v-bind:id="trailPreview.code" class="bi" width="32" height="32" fill="currentColor">
                              <use xlink:href="node_modules/bootstrap-icons/bootstrap-icons.svg#trash"/>
                      </svg>
                    </td>
                  </tr>
                </tbody>
            </table>
          </div>
        </div>
      </div>
  </div>
        `,
};

module.exports = TrailManagementPage;

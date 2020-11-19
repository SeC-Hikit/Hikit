import dialogConfirm from "../../component/dialog-confirm";

var TrailManagementPage = {
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
                        <tr>
                            <th scope="row">1</th>
                            <td>Calderino - Mt. S.Giovanni - Mt.Pastore</td>
                            <td>E</td>
                            <td>18/05/2020</td>
                            <td>
                            <svg v-on:click="onDelete(1, 'Calderino - Mt. S.Giovanni - Mt.Pastore')" class="bi" width="32" height="32" fill="currentColor">
                                    <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#trash"/>
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

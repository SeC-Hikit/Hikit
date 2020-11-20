import navMenuAdminComponent from "../../component/admin/nav-menu-admin";

var MantainanceManagement = {
  methods: {
    resolveNotification: function (id) {
      $("#modal_confirm").modal("toggle");
    },
    onDelete: function (id, data) {
      if (
        confirm(
          "Sei sicuro di voler rimuovere la manuntenzione con codice '" +
            id +
            "' e data '" +
            data +
            "' dal calendario?"
        )
      ) {
        // remove it
      }
    },
  },
  template: `
    <div class="container my-4">
        <nav-menu-admin></nav-menu-admin>
        <div class="row">
          <div class="col-12 col-md-12">
                  <div class="table-wrapper space-up">
                          <div class="table-title">
                              <div class="row">
                                  <div class="col-sm-8"><h2>Calendario Manuntenzioni</h2></div>
                                  <div class="col-sm-8">
                                    <router-link to="/admin/maintainance/add">
                                        <button type="button" class="btn btn-primary add-new"><i class="fa fa-plus"></i>Aggiungi</button>
                                    </router-link>
                                  </div>
                              </div>
                          </div>
                
                <h3 class="space-up">Manuntenzioni</h3>
                <table class="table table-striped interactive-table">
                  <thead>
                      <tr>
                          <th scope="col">Data</th>
                          <th scope="col">Posto di ritrovo</th>
                          <th scope="col">Data</th>
                          <th scope="col">Descrizione</th>
                          <th scope="col">Contatto</th>
                          <th scope="col">Azioni</th>
                      </tr>
                  </thead>
                  <tbody>
                          <tr>
                              <th scope="row">1</th>
                              <td>Calderino - Mt. S.Giovanni - Mt.Pastore</td>
                              <td>18/05/2020</td>
                              <td>Manuntenzione sponda torrente</td>
                              <td><p>Giuliano Raimondi</p></td>
                              <td>
                              <svg v-on:click="onDelete(1, '18/05/2020')" class="bi" width="32" height="32" fill="currentColor">
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

module.exports = MantainanceManagement;

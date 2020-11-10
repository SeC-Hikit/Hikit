import dialogConfig from "../../component/dialog-confirm";

var NotificationManagement = {
  methods: {
    resolveNotification: function (id) {
      $("#modal_confirm").modal("toggle");
    },
    onDelete: function(id, data){
        if(confirm("Sei sicuro di voler rimuovere l'avviso con codice '" + id + "' e data '" + data + "'?" ))
        {
            // remove it
        }
    }
  },
  template: `
      <div class="container my-4">
      <nav-menu-admin></nav-menu-admin>
        <div class="row space-up">
          <div class="col-12 col-md-12">
                  <div class="table-wrapper">
                          <div class="table-title">
                              <div class="row">
                                  <div class="col-sm-8"><h2>Avvisi Percorribilità</h2></div>
                                  <div class="col-sm-8">
                                    <router-link to="/admin/notifications/add">
                                        <button type="button" class="btn btn-primary add-new"><i class="fa fa-plus"></i>Aggiungi</button>
                                    </router-link>
                                   </div>
                              </div>
                          </div>
                
                <h3 class="space-up">Irrisolti</h3>
                <table class="table table-striped interactive-table">
                  <thead>
                      <tr>
                          <th scope="col">Codice</th>
                          <th scope="col">Localita</th>
                          <th scope="col">Data</th>
                          <th scope="col">Descrizione</th>
                          <th scope="col">Aggirabile?</th>
                          <th scope="col">Azioni</th>
                      </tr>
                  </thead>
                  <tbody>
                          <tr>
                              <th scope="row">1</th>
                              <td>Calderino - Mt. S.Giovanni - Mt.Pastore</td>
                              <td>18/05/2020</td>
                              <td><p>Abc</p></td>
                              <td><p>Si</p></td>
                              <td>
                              <svg v-on:click="resolveNotification(1)" class="bi" width="32" height="32" fill="currentColor">
                                      <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#hammer"/>
                              </svg>
                              <svg v-on:click="onDelete(1, '18/06/2020')" class="bi" width="32" height="32" fill="currentColor">
                                      <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#trash"/>
                              </svg>
                              </td>
                          </tr>
                  </tbody>
              </table>

              <h3>Passati (risolti)</h3>
              <table class="table table-striped interactive-table">
                <thead>
                    <tr>
                        <th scope="col">Codice</th>
                        <th scope="col">Localita</th>
                        <th scope="col">Data</th>
                        <th scope="col">Descrizione</th>
                        <th scope="col">Risoluzione</th>
                        <th scope="col">Azioni</th>
                    </tr>
                </thead>
                <tbody>
                        <tr>
                            <th scope="row">1</th>
                            <td>Calderino - Mt. S.Giovanni - Mt.Pastore</td>
                            <td>18/05/2020</td>
                            <td><p>Abc</p></td>
                            <td><p>def</p></td>
                            <td>
                            <svg v-on:click="onClose(1, '18/05/2020')" class="bi" width="32" height="32" fill="currentColor">
                                    <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#trash"/>
                            </svg>
                            </td>
                        </tr>
                </tbody>
            </table>

            <dialog-config></dialog-config>
                      </div>
                  </div>
              </div>
          </div>
          `,
};

module.exports = NotificationManagement;

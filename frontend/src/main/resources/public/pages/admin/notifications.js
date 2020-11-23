import dialogConfig from "../../component/dialog-confirm";

var NotificationManagement = {
  data: function () {
    return {
      errored: false,
      loading: false,

      itemsSolved: [],
      unresolvedNotifications: [],
      solvedFrom: 0,
      solvedTo: 10,
      momentInstance: new Object(),
    };
  },
  watch: {
    loading: function () {
      toggleLoading(this.loading);
    },
  },
  created: function () {
    this.loading = true;
    this.errored = false;
    this.unresolvedNotifications = [];
    this.momentInstance = moment();
  },
  mounted: function () {
    toggleLoading(true);
    axios
      .get(BASE_IMPORTER_ADDRESS + "/notifications/unsolved")
      .then((response) => {
        this.unresolvedNotifications = response.data.accessibilityNotifications;
        this.renderSolvedNotifications();
      })
      .catch((error) => {
        console.log(error);
        this.errored = true;
      }).finally(() => this.loading = false);
  },
  methods: {
    renderSolvedNotifications() {
      axios
        .get(BASE_IMPORTER_ADDRESS + "/notifications/solved/0/10")
        .then((response) => {
          this.itemsSolved = response.data.accessibilityNotifications;
        })
        .catch((error) => {
          console.log(error);
          this.errored = true;
        })
        .finally(() => (this.loading = false));
    },
    onResolve: function (e) {
      $("#modal_confirm").modal("toggle");
    },
    onDelete: function (e) {
      if (
        confirm(
          "Sei sicuro di voler rimuovere l'avviso con codice '" +
            id +
            "' e data '" +
            data +
            "'?"
        )
      ) {
        // remove it
      }
    },
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
                <table v-if="!loading && unresolvedNotifications.length > 0" class="table table-striped interactive-table">
                  <thead>
                      <tr>
                          <th scope="col">Id</th>
                          <th scope="col">Codice</th>
                          <th scope="col">Data segnalazione</th>
                          <th scope="col">Descrizione</th>
                          <th scope="col">Aggirabile?</th>
                          <th scope="col">Azioni</th>
                      </tr>
                  </thead>
                  <tbody>
                          <tr v-for="notification in unresolvedNotifications">
                            <td class="cut-text">{{ notification._id }}</td>
                            <th scope="row">{{ notification.code }}</th>
                            <td>{{ moment(notification.reportDate).format('DD/MM/YYYY') }}</td>
                            <td>{{ notification.description }}</td>
                            <td>{{ notification.isMinor ? 'si' : 'no' }}</td>
                            <td>
                                <svg v-on:click="onResolve" v-bind:id="notification._id" class="bi" width="32" height="32" fill="currentColor">
                                        <use xlink:href="node_modules/bootstrap-icons/bootstrap-icons.svg#hammer"/>
                                </svg>
                                <svg v-on:click="onDelete" v-bind:id="notification._id" class="bi" width="32" height="32" fill="currentColor">
                                        <use xlink:href="node_modules/bootstrap-icons/bootstrap-icons.svg#trash"/>
                                </svg>
                            </td>
                          </tr>
                  </tbody>
              </table>
              <p v-else>Non ci sono avvisi di percorribilità</p>

              <h3>Passati (risolti)</h3>
              <table v-if="!loading && itemsSolved.length > 0" class="table table-striped interactive-table">
              <thead>
                  <tr>
                      <th scope="col">Codice sentiero</th>
                      <th scope="col">Data segnalazione</th>
                      <th scope="col">Descrizione Problema</th>
                      <th scope="col">Data risoluzione</th>
                      <th scope="col">Risoluzione</th>
                  </tr>
              </thead>
              <tbody>
                  <tr :key="index" v-for="(item,index) in itemsSolved">
                      <th scope="row">{{ item.code }}</th>
                      <td>{{ moment(item.reportDate).format('DD/MM/YYYY') }}</td>
                      <td>{{ item.description }}</td>
                      <td>{{ moment(item.resolutionDate).format('DD/MM/YYYY') }}</td>
                      <td>{{ item.resolution }}</td>
              </tbody>
          </table>
          <p v-else>Non ci sono ancora risoluzioni</p>

            <dialog-config></dialog-config>
                      </div>
                  </div>
              </div>
          </div>
          `,
};

module.exports = NotificationManagement;

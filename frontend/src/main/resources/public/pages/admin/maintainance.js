import navMenuAdminComponent from "../../component/admin/nav-menu-admin";

var MantainanceManagement = {
  data: function () {
    return {
      errored: false,
      loading: true,

      maintenanceList: [],
      pastMaintenanceList: [],
      momentInstance: new Object(),
    };
  },
  watch: {
    loading: function () {
      toggleLoading(this.loading);
    },
  },
  mounted: function () {
    toggleLoading(true);
    axios
      .get(BASE_IMPORTER_ADDRESS + "/maintenance/future")
      .then((response) => {
        this.maintenanceList = response.data.maintenanceList;
        this.renderPastMaintenance();
      })
      .catch((error) => {
        console.log(error);
        this.errored = true;
      })
      .finally(() => (this.loading = false));
    this.momentInstance = moment();
  },
  methods: {
    renderPastMaintenance() {
      axios
        .get(BASE_IMPORTER_ADDRESS + "/maintenance/past/0/10")
        .then((response) => {
          this.pastMaintenanceList = response.data.maintenanceList;
        })
        .catch((error) => {
          console.log(error);
          this.errored = true;
        })
        .finally(() => (this.loading = false));
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
                <table v-if="!loading && maintenanceList.length > 0" class="table table-striped">
                <thead>
                    <tr>
                        <th scope="col">Data</th>
                        <th scope="col">Codice Sentiero</th>
                        <th scope="col">Posto di Ritrovo</th>
                        <th scope="col">Descrizione</th>
                        <th scope="col">Contatto</th>
                        <th scope="col">Azioni</th>
                    </tr>
                </thead>
                <tbody>
                        <tr v-for="maintenance in maintenanceList">
                            <td scope="row">{{ moment(maintenance.date).format("DD/MM/YYYY") }}</td>
                            <td scope="row">{{ maintenance.code }}</td>
                            <td>{{ maintenance.meetingPlace }}</td>
                            <td>{{ maintenance.description }}</td>
                            <td>{{ maintenance.contact }}</td>
                            <td class="clickable">
                              <svg v-on:click="onDelete" v-bind:id="maintenance._id" class="bi" width="32" height="32" fill="currentColor">
                                <use xlink:href="node_modules/bootstrap-icons/bootstrap-icons.svg#trash"/>
                              </svg>
                            </td>
                        </tr>
                </tbody>
            </table>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-12 col-md-12 col-s-6">
            <h3>Manuntenzioni Passate</h3>
            <table v-if="!loading && pastMaintenanceList.length > 0" class="table table-striped">
                <thead>
                    <tr>
                        <th scope="col">Data</th>
                        <th scope="col">Codice Sentiero</th>
                        <th scope="col">Posto di Ritrovo</th>
                        <th scope="col">Descrizione</th>
                        <th scope="col">Contatto</th>
                    </tr>
                </thead>
                <tbody>
                    <tr :key="index" v-for="(maintenance, index) in pastMaintenanceList">
                        <td scope="row">{{ moment(maintenance.date).format("DD/MM/YYYY") }}</td>
                        <td scope="row">{{ maintenance.code }}</td>
                        <td>{{ maintenance.meetingPlace }}</td>
                        <td>{{ maintenance.description }}</td>
                        <td>{{ maintenance.contact }}</td>
                    </tr>
                </tbody>
            </table>
            <p v-else>Non ci sono altre manuntenzioni passate</p>
        </div>
    </div>

  </div>
          `,
};

module.exports = MantainanceManagement;

var MaintainancePage = {
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
      .get("http://localhost:8991/app/maintenance/future")
      .then((response) => {
        this.maintenanceList = response.data.maintenanceList;
        this.renderPastNotifications();
      })
      .catch((error) => {
        console.log(error);
        this.errored = true;
      }).finally(() => this.loading = false);
    this.momentInstance = moment();
  },
  methods: {
    moment() {
      return this.momentInstance;
    },
    renderPastNotifications() {
      axios
        .get("http://localhost:8991/app/maintenance/past/0/10")
        .then((response) => {
          this.pastMaintenanceList = response.data.maintenanceList;
        })
        .catch((error) => {
          console.log(error);
          this.errored = true;
        })
        .finally(() => (this.loading = false));
    },
  },
  template: `
<div class="container my-4">
    <div class="row">
        <div class="col-12 col-md-12 col-s-6">
            <h1>Calendario Manuntenzioni</h1>
            <h2>Prossimi incontri</h2>
            <table v-if="!loading && maintenanceList.length > 0" class="table table-striped">
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
                        <tr v-for="maintenance in maintenanceList">
                            <td scope="row">{{ moment(maintenance.date).format('DD/MM/YYYY') }}</td>
                            <td scope="row">{{ maintenance.code }}</td>
                            <td>{{ maintenance.meetingPlace }}</td>
                            <td>{{ maintenance.description }}</td>
                            <td>{{ maintenance.contact }}</td>
                        </tr>
                </tbody>
            </table>
        </div>
    </div>
    <div class="row">
        <div class="col-12 col-md-12 col-s-6">
            <h2>Incontri Passati</h2>
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
                        <td scope="row">{{ moment(maintenance.date).format('DD/MM/YYYY') }}</td>
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
</div>`,
};

module.exports = MaintainancePage;

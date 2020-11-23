var NotificationsPage = {
  data: function () {
    return {
      errored: false,
      loading: false,

      itemsSolved: [],
      unresolvedNotifications: [],
      solvedFrom: 0,
      solvedTo: 10,
      momentInstance: new Object()
    };
  },
  watch: {
    loading: function () {
      toggleLoading(this.loading);
    }
  },
  created: function () {
    this.loading = true;
    this.errored = false;
    this.unresolvedNotifications = [];
    this.momentInstance = moment()
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
  },
  template: `<div class="container my-4">
    <div class="row">
        <div class="col-12 col-md-12">
            <h1>
            Percorribilità dei sentieri
            </h1>
            <p>Di seguito trovi la lista di avvisi sulla percorribilità della sentieristica. Un esempio di questi potrebbero essere danni naturali o umani provocati a un sentiero, alla sua segnaletica con un possibile rischio alla sicurezza o alla percorribilità dei frequentatori.</p>
            <p>Hai notato un problema? Segnalacelo.</p>
            <table v-if="!loading && unresolvedNotifications.length > 0" class="table table-striped interactive-table">
                <thead>
                    <tr>
                        <th scope="col">Codice sentiero</th>
                        <th scope="col">Data Segnalazione</th>
                        <th scope="col">Descrizione Problema</th>
                        <th scope="col">Aggirabile?</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="notification in unresolvedNotifications">
                            <th scope="row">{{ notification.code }}</th>
                            <td>{{ moment(notification.reportDate).format('DD/MM/YYYY') }}</td>
                            <td>{{ notification.description }}</td>
                            <td>{{ notification.isMinor ? 'si' : 'no' }}</td>
                    </tr>
                </tbody>
            </table>
            <p v-else>Non ci sono avvisi di percorribilità</p>
        </div>
        <div class="col-12 col-md-12">
            <h1>Risoluzioni</h1>
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
        </div> 
    </div>
</div>`,
};

module.exports = NotificationsPage;

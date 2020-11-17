var NotificationsPage = {
  data: function () {
    return {
      itemsSolved: [],
      unresolvedNotifications: [],
      errored: false,
      loading: false,
      solvedFrom: 0,
      solvedTo: 10,
    };
  },
  mounted: function () {
    this.loading = true;
    axios
      .get("http://localhost:8991/app/notifications/unsolved")
      .then((response) => {
        this.unresolvedNotifications = response.data.accessibilityNotifications;
        this.renderSolvedNotifications();
      })
      .catch((error) => {
        console.log(error);
        this.errored = true;
      });
  },
  methods: {
    renderSolvedNotifications() {
      axios
        .get("http://localhost:8991/app/notifications/solved/0/10")
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
                        <th scope="col">Data</th>
                        <th scope="col">Descrizione Problema</th>
                        <th scope="col">Aggirabile?</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="notification in unresolvedNotifications">
                            <th scope="row">{{ notification.code }}</th>
                            <td>{{ notification.reportDate }}</td>
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
                        <th scope="col">Data Riportato</th>
                        <th scope="col">Data Risolto</th>
                        <th scope="col">Descrizione Problema</th>
                        <th scope="col">Risoluzione</th>
                    </tr>
                </thead>
                <tbody>
                    <tr :key="index" v-for="(item,index) in itemsSolved">
                        <th scope="row">{{ item.code }}</th>
                        <td>{{ item.reportDate }}</td>
                        <td>{{ item.resolutionDate }}</td>
                        <td>{{ item.description }}</td>
                        <td>{{ item.resolution }}</td>
                </tbody>
            </table>
            <p v-else>Non ci sono ancora risoluzioni</p>
        </div> 
    </div>
</div>`,
};

module.exports = NotificationsPage;

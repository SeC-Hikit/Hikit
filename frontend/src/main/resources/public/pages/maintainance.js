var MaintainancePage = {
  mounted: {},
  methods: {
    loadUpcomingMaintainance: function () {},
    loadPastMaintainance: function () {},
    loadOlderMaintainance: function(lastLoadedId) {}
  },
  template: `
<div class="container my-4">
    <div class="row">
        <div class="col-12 col-md-12 col-s-6">
            <h1>Calendario Manuntenzioni</h1>
            <h2>Prossimi incontri</h2>
            <table class="table table-striped">
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
                        <tr v-on:click="alert('hello')">
                            <td scope="row">23/09/2020</td>
                            <td scope="row">1</td>
                            <td>Osteria da Pino</td>
                            <td>Messa in sicurezza del sentiero 105B</td>
                            <td>Giuliano Raimondi</td>
                        </tr>
                </tbody>
            </table>
        </div>
    </div>
    <div class="row">
        <div class="col-12 col-md-12 col-s-6">
            <h2>Incontri Passati</h2>
            <table class="table table-striped">
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
                        <tr>
                            <th scope="row">23/09/2018</th>
                            <td>2</td>
                            <td>Osteria da Pino</td>
                            <td>Messa in sicurezza del sentiero 105B</td>
                            <td>Giuliano Raimondi</td>
                        </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>`,
};

module.exports = MaintainancePage;

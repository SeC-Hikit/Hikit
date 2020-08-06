var NotificationsPage = {
  template: `<div class="container my-4">
    <div class="row">
        <div class="col-12 col-md-12">
            <h1>Avvisi sui percorsi</h1>
            <p>Di seguito trovi la lista di avvisi riguardo la sentieristica nella zona di Bologna</p>
            <table class="table table-striped interactive-table">
                <thead>
                    <tr>
                        <th scope="col">Codice sentiero</th>
                        <th scope="col">Localita</th>
                        <th scope="col">Data</th>
                        <th scope="col">Descrizione</th>
                    </tr>
                </thead>
                <tbody>
                        <tr v-on:click="alert('hello')">
                            <th scope="row">1</th>
                            <td>Calderino - Mt. S.Giovanni - Mt.Pastore</td>
                            <td>07/06/2020</td>
                            <td>Il sentiero n1 e' stato interrotto causa smottamento</td>
                        </tr>
                </tbody>
            </table>
        </div>
        <div class="col-12 col-md-12">
            <h1>Risoluzioni</h1>
            <p>Vengono mostrate solo le ultime 10 risoluzioni</p>
            <table class="table table-striped interactive-table">
                <thead>
                    <tr>
                        <th scope="col">Codice sentiero</th>
                        <th scope="col">Localita</th>
                        <th scope="col">Data</th>
                        <th scope="col">Descrizione</th>
                        <th scope="col">Risoluzione</th>
                    </tr>
                </thead>
                <tbody>
                        <tr v-on:click="alert('hello')">
                            <th scope="row">2</th>
                            <td>Mt. S.Giovanni - Mt.Pastore</td>
                            <td>07/09/2019</td>
                            <td>Il sentiero e' stato interrotto causa detriti sul percorso</td>
                            <td>Mantenuto</td>
                        </tr>
                </tbody>
            </table>
        </div>

    </div>
</div>`
}

module.exports = NotificationsPage;
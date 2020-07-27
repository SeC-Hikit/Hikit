var HikesPage = {
  template: `<div class="container my-4">
    <div class="row">
        <div class="col-12 col-md-8">
            <h1>Sentieri</h1>
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th scope="col">Codice</th>
                        <th scope="col">Localita</th>
                        <th scope="col">Classificazione</th>
                    </tr>
                </thead>
                <tbody>
                        <tr v-on:click="alert('hello')">
                            <th scope="row">1</th>
                            <td>Calderino - Mt. S.Giovanni - Mt.Pastore</td>
                            <td>E</td>
                        </tr>
                </tbody>
            </table>

        </div>
        <preview-map></preview-map>
    </div>
</div>`
}

module.exports = HikesPage;
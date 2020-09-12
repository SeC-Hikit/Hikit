import previewMap from "../component/preview-map";

var HikesPage = {
  data() {
    return {
      points : 0,
    };
  },
  methods: {
    onPreview: function (id) {
        console.log("Requested trail id " + id)
        axios.get("abc");
    },
    onOpenToMap: function (id) {
        console.log("go to map");
        this.$router.push("/map/" + id);
    },
  },
  components: {
      'preview-map' : previewMap
  },
  template: `<div class="container my-4">
    <div class="row">
        <div class="col-12 col-md-8">
            <h1>Sentieri</h1>
            <table class="table table-striped interactive-table">
                <thead>
                    <tr>
                        <th scope="col">Codice</th>
                        <th scope="col">Localita</th>
                        <th scope="col">Classificazione</th>
                        <th scope="col">Anteprima</th>
                        <th scope="col">Apri</th>
                    </tr>
                </thead>
                <tbody>
                        <tr>
                            <th scope="row">1</th>
                            <td>Calderino - Mt. S.Giovanni - Mt.Pastore</td>
                            <td>E</td>
                            <td>
                                <svg v-on:click="onPreview(2)" class="bi" width="32" height="32" fill="currentColor">
                                      <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#eye"/>
                                </svg>
                            </td>
                            <td>
                                <svg v-on:click="onOpenToMap(2)" class="bi" width="32" height="32" fill="currentColor">
                                      <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#map-fill"/>
                                </svg>
                            </td>
                        </tr>
                </tbody>
            </table>

        </div>
        <div class="col-12 col-md-4">
            <preview-map :points="points"></preview-map>
        </div>
    </div>
</div>`,
};

module.exports = HikesPage;

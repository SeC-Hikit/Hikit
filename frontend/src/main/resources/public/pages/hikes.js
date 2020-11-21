import previewMap from "../component/preview-map";

let HikesPage = {
    data() {
        return {
            trailsResponse: null,
            errored: false,
            loading: true,
            // List of CoordinatesWithAltitude
            points: 0,
        };
    },
    watch: {    
        loading: function() { 
            toggleLoading(this.loading);
        }
    },
    methods: {
        onPreview: function (event) {
            var id = $(event.currentTarget).parent().children().first().text()
            axios.get("http://localhost:8991/app/preview/" + id).then(
            response => {
                if(response.data != null) {
                    this.points = response.data
                }
            }).catch(error => {
                console.log(error)
                this.errored = true
            })
            .finally(() => this.loading = false)
        },
        onOpenToMap: function (event) {
            var id = $(event.currentTarget).parent().children().first().text()
            this.$router.push("/map/" + id);
        },
        getResp: function (resp) {
            return resp.data;
        }
    },
    mounted () {
        toggleLoading(true);
        axios.get("http://localhost:8991/app/preview").then(
            response => {
                this.trailsResponse = response.data.trailPreviews
            }).catch(error => {
                console.log(error)
                this.errored = true
            })
            .finally(() => this.loading = false)
    },
    components: {
        'preview-map': previewMap
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
                    <tr v-for="trailPreview in trailsResponse" class="trailPreview">
                        <th scope="row">{{ trailPreview.code }}</th>
                        <td>{{ trailPreview.startPos.name }} - {{ trailPreview.endPos.name }}</td>
                        <td>{{ trailPreview.classification }}</td>
                        <td v-on:click='onPreview(event)'>
                            <svg class="bi" width="32" height="32" fill="currentColor">
                                    <use xlink:href="node_modules/bootstrap-icons/bootstrap-icons.svg#eye"/>
                            </svg>
                        </td>
                        <td v-on:click='onOpenToMap(event)'>
                            <svg class="bi" width="32" height="32" fill="currentColor">
                                    <use xlink:href="node_modules/bootstrap-icons/bootstrap-icons.svg#map-fill"/>
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

var NotificationManagementNewSingle = {
  mounted: function () {
      $(".datepicker").datepicker({
        inputFormat: ["dd/MM/yyyy"],
        outputFormat: "gg/MM/aaaa",
      });
  },
  methods: {
    close: function(){
        if(confirm("Sei sicuro di voler tornare al menù? \nCliccando 'OK' tutte le modifiche a questo avviso andranno perse.")){
            this.$router.push("/admin/notifications");
        }
    }
  },
  template: `
  <div class="container my-4">
    <nav-menu-admin></nav-menu-admin>
    <h1 class="space-up">Aggiungi Avviso di percorribilità</h1>
    <p>Compila i seguenti campi per aggiungere un avviso di percorribilità modificata su un determinato sentiero.</p>
    <div class="table-wrapper space-up">
        <form>
            <div class="form-group row">
                <legend class="col-form-label col-sm-2 pt-0">Codice Sentiero</legend>
                <div class="col-sm-8 bootstrap-select-wrapper">
                    <select class="form-control" id="trailId_select">
                        <option>1</option>
                        <option>2</option>
                        <option>3</option>
                        <option>4</option>
                        <option>5</option>
                    </select>
                </div>
            </div>
            <div class="form-group row">
                <legend class="col-form-label col-sm-2 pt-0">Localita interessata/e</legend>
                <div class="col-sm-8">
                    <input type="text" class="form-control" id="localita" placeholder="localita" required>
                </div>
            </div>
            <div class="form-group row">
                <legend class="col-form-label col-sm-2 pt-0">Descrizione</legend>
                <div class="col-sm-8">
                    <textarea name="description" id="description" required></textarea>
                </div>
            </div>
            <div class="form-group row">
                <legend class="col-form-label col-sm-2 pt-0">Data avviso</legend>
                <div class="col-sm-8">
                    <input class="form-check-input datepicker" type="date" name="date" id="date" required/>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2">
                    <button type="submit" class="btn btn-primary">Salva e pubblica</button>
                </div>
                <div class="col-sm-3"> 
                  <button type="button" v-on:click="close" class="btn btn-light">Torna ad 'avvisi percorribilità'</button>
                </div>
            </div>
        </form>
    </div>
</div>

          `,
};

module.exports = NotificationManagementNewSingle;

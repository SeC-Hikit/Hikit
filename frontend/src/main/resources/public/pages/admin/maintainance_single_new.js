var MantainanceManagementNewSingle = {
    methods: {
        close: function(){
            if(confirm("Sei sicuro di voler tornare al menù? \nCliccando 'OK' tutte le modifiche a questo manuntenzione andranno perse.")){
                this.$router.push("/admin/notifications");
            }
        }
      },
      template: `
      <div class="container my-4">
        <nav-menu-admin></nav-menu-admin>
        <h1 class="space-up">Aggiungi uscita di manuntenzione</h1>
        <p>Compila i seguenti campi per aggiungere un'uscita di manuntenzione.</p>
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
                        <option>Non legata a un sentiero</option>
                        </select>
                    </div>
                </div>
                <div class="form-group row">
                    <legend class="col-form-label col-sm-2 pt-0">Posto di ritrovo</legend>
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
                    <legend class="col-form-label col-sm-2 pt-0">Data</legend>
                    <div class="col-sm-8">
                        <input class="form-check-input datepicker" type="date" name="date" id="date" required/>
                    </div>
                </div>
                <div class="form-group row">
                    <legend class="col-form-label col-sm-2 pt-0">Contatto</legend>
                    <div class="col-sm-8 bootstrap-select-wrapper">
                        <input type="text" class="form-control" id="contatto" placeholder="Nominativo (Nome e Cognome) di un responsabile" required>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-2">
                        <button type="submit" class="btn btn-primary">Salva e pubblica</button>
                    </div>
                    <div class="col-sm-3"> 
                      <button type="button" v-on:click="close" class="btn btn-light">Torna a calendario manuntenzioni</button>
                    </div>
                </div>
            </form>
        </div>
    </div>`
  };
  
  module.exports = MantainanceManagementNewSingle;
  
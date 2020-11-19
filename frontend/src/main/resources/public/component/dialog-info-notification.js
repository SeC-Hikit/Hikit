let dialogConfig = Vue.component("dialog-info-notification", {
  props: {
    id: String,
    unresolvedNotifications: Array
  },
  template: `
    <div class="it-modal">
    <div class="modal alert-modal" tabindex="-1" role="dialog" id="modal_info">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <svg class="bi" width="32" height="32" fill="currentColor">
            <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#info"/>
            </svg>
            <h5 class="modal-title">Notifiche e incidenti conosciuti su percorso {{id}}</h5>
          </div>
          <div class="modal-body">
            <table class="table table-striped interactive-table">
                <thead>
                  <tr>
                    <td>Descrizione</td>
                    <td>Raggirabile?</td>
                  </tr>
                </thead>
                <tr v-for="notification in unresolvedNotifications">
                  <td>{{ notification.description }}</td>
                  <td>{{ notification.isMinor ? 'si' : 'no' }}</td>
                </tr>
            </table>
          </div>
          <div class="modal-footer">
          <button class="btn btn-outline-secondary btn-sm" type="button" data-dismiss="modal">Chiudi</button>
          </div>
        </div>
      </div>
    </div>
  </div>
    `,
});

module.exports = dialogConfig;

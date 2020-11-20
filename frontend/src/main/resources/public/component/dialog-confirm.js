let dialogConfirm = Vue.component("dialog-config", {
  props: {
    titleText: String,
    icon: String, 
    centralText: Array,
    onOk: Object
  },
  template: `
    <div class="it-modal">
    <div class="modal alert-modal" tabindex="-1" role="dialog" id="modal_confirm">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <svg class="bi" width="32" height="32" fill="currentColor">
            <use xlink:href="node_modules/bootstrap-icons/bootstrap-icons.svg#hammer"/>
            </svg>
            <h5 class="modal-title">Risolvi Avviso
            </h5>
          </div>
          <div class="modal-body">
            <p>Digita una breve risoluzione</p>
            <input type="text"/>
          </div>
          <div class="modal-footer">
          <button class="btn btn-primary btn-sm" type="button">Risolvi notifica</button>
          <button class="btn btn-outline-secondary btn-sm" type="button" data-dismiss="modal">Chiudi</button>
          </div>
        </div>
      </div>
    </div>
  </div>
    `,
});

module.exports = dialogConfirm;

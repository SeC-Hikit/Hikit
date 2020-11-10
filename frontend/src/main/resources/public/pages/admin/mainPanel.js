import navMenuAdminComponent from "../../component/admin/nav-menu-admin" 

var MainPanelManagement = {
  template: `
      <div class="container my-4">
      <nav-menu-admin></nav-menu-admin>
        <div class="row space-up">
          <div class="col-12 col-md-8">
            <h2>Salve Amministratore,</h2>
            <p>Cosa vuoi gestire oggi? Seleziona una delle voci dal menu soprastante.</p>
          </div>
      </div>
          `,
};

module.exports = MainPanelManagement;

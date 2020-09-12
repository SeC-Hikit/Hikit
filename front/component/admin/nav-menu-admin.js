var navMenuAdminComponent = Vue.component("nav-menu-admin", {
  template: `
    <div>
    <div class="row admin-menu">
    <div class="col-3">
        <label>Gestisci:</label>
    </div>
    <div class="col-3">
    <svg class="bi" width="32" height="32" fill="currentColor">
    <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#map-fill"/>
    </svg>
    <router-link to="/admin/trails">
      <label>Sentieri</label>
      </a>
    </div>
    <div class="col-3">
    <svg class="bi" width="32" height="32" fill="currentColor">
    <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#info-circle-fill"/>
    </svg>
    <router-link to="/admin/notifications">
    <label>Percorribilità</label>
      </a>
     </div>
     <div class="col-3">
     <svg class="bi" width="32" height="32" fill="currentColor">
     <use xlink:href="/node_modules/bootstrap-icons/bootstrap-icons.svg#calendar-week"/>
     </svg>
      <router-link to="/admin/maintainance">
      <label>Calendario manuntenzioni</label>
     </div>
</div>
</div>
</div>`,
});

module.exports = navMenuAdminComponent;

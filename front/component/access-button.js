let accessButtonComponent = Vue.component("access-button", {
  template: `
  <router-link to="/signIn">
    <div class="it-access-top-wrapper">
        <a class="btn btn-primary btn-sm" href="#">Accedi</a>
    </div>
   </router-link>`,
});

module.exports = accessButtonComponent;

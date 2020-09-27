let accessButtonComponent = Vue.component("access-button", {
  template: `
  <router-link to="/signIn">
    <div class="it-access-top-wrapper">
        <a href="#">Accedi</a>
    </div>
   </router-link>`,
});

module.exports = accessButtonComponent;

import SlimHeaderComponent from "../component/slim-header";
import NavMenuComponent from "../component/nav-menu";
import PreviewMap from "../component/preview-map";
import FooterComponent from "../component/footer";

// import HikesPage from "../pages/hikes-page";
import HikesPage from "../pages/hikes-page";
import MapPage from "../pages/map-page";

const Home = { template: '<div>home</div>' }

export default {
  components: {
    SlimHeaderComponent,
    NavMenuComponent,
    FooterComponent,
    PreviewMap,
  },
};

const routes = [
  { path: "/", component: Home },
  { path: "/hikes", component: HikesPage },
  { path: "/map", component: MapPage },
];

// 3. Create the router instance and pass the `routes` option
// You can pass in additional options here, but let's
// keep it simple for now.
const router = new VueRouter({
  routes, // short for `routes: routes`
});

var app = new Vue({
  router
}).$mount("#app");

import SlimHeaderComponent from "../component/slim-header";
import NavMenuComponent from "../component/nav-menu";
import PreviewMap from "../component/preview-map";
import FooterComponent from "../component/footer";
import FullMapComponent from "../component/full-map";


import HikesPage from "../pages/hikes";
import MapPage from "../pages/map";
import LoginPage from "../pages/login";
import MaintainancePage from "../pages/maintainance";
import NotificationsPage from "../pages/notifications";
import TrailInfoPage from "../pages/trails-info";
import PasswordRecoveryPage from "../pages/password-recovery";
import PasswordChangePage from "../pages/password-recovery-change";
import SignInPage from "../pages/sign-in";
import SafetyPage from "../pages/safety";

const Home = { template: '<div>home</div>' }

export default {
  components: {
    SlimHeaderComponent,
    NavMenuComponent,
    FooterComponent,
    PreviewMap,
    FullMapComponent,
  },
};

const routes = [
  { path: "/", component: Home },
  { path: "/hikes", component: HikesPage },
  { path: "/map", component: MapPage },
  { path: "/notifications", component: NotificationsPage },
  { path: "/maintainance", component: MaintainancePage },
  { path: "/login", component: LoginPage },
  { path: "/passwordRecovery", component: PasswordRecoveryPage },
  { path: "/passwordChange", component: PasswordChangePage },
  { path: "/signIn", component: SignInPage },
  { path: "/info", component: TrailInfoPage },
  { path: "/safety", component: SafetyPage },
];

const router = new VueRouter({
  routes, // short for `routes: routes`
});

var app = new Vue({
  router
}).$mount("#app");

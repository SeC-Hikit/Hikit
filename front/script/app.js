import "../component/slim-header";
import "../component/nav-menu";
import "../component/footer";

// Public pages
import MapPage from "../pages/map";
import HikesPage from "../pages/hikes";
import LoginPage from "../pages/login";
import MaintainancePage from "../pages/maintainance";
import NotificationsPage from "../pages/notifications";
import TrailInfoPage from "../pages/trails-info";
import PasswordRecoveryPage from "../pages/password-recovery";
import PasswordChangePage from "../pages/password-recovery-change";
import SignInPage from "../pages/sign-in";
import SafetyPage from "../pages/safety";

// Admin pages
import TrailManagement from "../pages/admin/trails";
import NotificationManagement from "../pages/admin/notifications";
import MaintainanceManagement from "../pages/admin/maintainance";
import MainPanelManagement from "../pages/admin/mainPanel";

import TrailManagementPageSingle from "../pages/admin/trails_single_new";
// import NotificationSinglePageManagement from "../pages/admin/notification_single";
import NotificationManagementNewSingle from "../pages/admin/notifications_single_new";
// import MaintainanceSinglePageManagement from "../pages/admin/maintainance_single";
import MaintainanceSingleNewPageManagement from "../pages/admin/maintainance_single_new";


const Home = { template: '<div>home</div>' }

const routes = [
  { path: "/", component: Home },
  { path: "/hikes", component: HikesPage },
    
  { path: "/map", component: MapPage},
  { path: "/map/:id", component: MapPage, props: true},
  
  { path: "/notifications", component: NotificationsPage },
  { path: "/maintainance", component: MaintainancePage },
  { path: "/login", component: LoginPage },
  { path: "/passwordRecovery", component: PasswordRecoveryPage },
  { path: "/passwordChange", component: PasswordChangePage },
  { path: "/signIn", component: SignInPage },
  { path: "/info", component: TrailInfoPage },
  { path: "/safety", component: SafetyPage },

  // Admin
  { path: "/admin/trails", component: TrailManagement },
  { path: "/admin/trails/add", component: TrailManagementPageSingle },
  { path: "/admin/maintainance", component: MaintainanceManagement },
  { path: "/admin/maintainance/add", component: MaintainanceSingleNewPageManagement },
  { path: "/admin/notifications", component: NotificationManagement },
  { path: "/admin/notifications/add", component: NotificationManagementNewSingle },
  { path: "/admin", component: MainPanelManagement },
];

const router = new VueRouter({
  routes, // short for `routes: routes`
});

var app = new Vue({
  router
}).$mount("#app");

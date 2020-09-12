var navMenuComponent = Vue.component("nav-menu", {
  template: `<div class="it-nav-wrapper">
    <div class="it-header-center-wrapper">
        <div class="container">
            <div class="row">
                <div class="col-12">
                    <div class="it-header-center-content-wrapper">
                        <div class="it-brand-wrapper">
                            <a href="/#/">
                                <img src="static/cai_logo.png" class="icon" />
                                <div class="it-brand-text">
                                    <h2 class="no_toc">Sentieri e Cartografia</h2>
                                    <h3 class="no_toc d-none d-md-block">CAI Bologna</h3>
                                </div>
                            </a>
                        </div>
                        <div class="it-right-zone">
                            <div class="it-header-navbar-wrapper">
                                <div class="container">
                                    <div class="row">
                                        <div class="col-12">
                                            <nav class="navbar navbar-expand-lg has-megamenu">
                                                <div class="navbar-collapsable" id="nav10">
                                                    <div class="overlay"></div>
                                                    <div class="close-div sr-only">
                                                        <button class="btn close-menu" type="button"><span
                                                                class="it-close"></span>close</button>
                                                    </div>
                                                    <div class="menu-wrapper">
                                                        <ul class="navbar-nav">
                                                            <router-link to="/hikes">
                                                                <li class="nav-item"><a
                                                                        class="nav-link"
                                                                        href="#"><span>Sentieri</span>
                                                                        <span class="sr-only">current</span></a>
                                                                </li>
                                                            </router-link>
                                                            <router-link to="/map">
                                                                <li class="nav-item"><a class="nav-link"
                                                                        href="#"><span>Mappa</span></a>
                                                                </li>
                                                            </router-link>
                                                            <router-link to="/notifications">
                                                            <li class="nav-item"><a class="nav-link" href="#">
                                                                    <span>Percorribilità</span></a></li>
                                                            </router-link>
                                                            <router-link to="/maintainance">
                                                            <li class="nav-item"><a class="nav-link" href="#">
                                                                    <span>Calendario Manuntenzioni</span></a></li>
                                                            </router-link>
                                                        </ul>
                                                    </div>
                                                </div>
                                            </nav>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>`
});

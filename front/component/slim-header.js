import accessButtonComponent from "../component/access-button";

let slimHeaderComponent = Vue.component('slim-header', {
    template: `<div class="it-header-slim-wrapper">
            <div class="container">
                <div class="row">
                    <div class="col-12">
                        <div class="it-header-slim-wrapper-content">
                            <a class="d-none d-lg-block navbar-brand" href="https://www.caibo.it/" target="_blank">CAI Bologna</a>
                            <div class="nav-mobile">
                                <nav>
                                    <a class="it-opener d-lg-none" data-toggle="collapse" href="#menu-principale"
                                        role="button" aria-expanded="false" aria-controls="menu-principale">
                                        <span>CAI Bologna</span>
                                        <svg class="icon">
                                            <use
                                                xlink:href="/node_modules/bootstrap-italia/dist/svg/sprite.svg#it-expand">
                                            </use>
                                        </svg>
                                    </a>
                                    <div class="link-list-wrapper collapse" id="menu-principale">
                                        <ul class="link-list">
                                        <router-link to="/info">
                                            <li><a class="list-item" href="#">La sentieristica</a></li>
                                        </router-link>
                                        <router-link to="/safety">
                                            <li><a class="list-item" href="#">Sicurezza</a></li>
                                        </router-link>
                                        </ul>
                                    </div>
                                </nav>
                            </div>
                            <div class="it-header-slim-right-zone">
                                <div class="nav-item dropdown">
                                    <a class="nav-link dropdown-toggle" href="#" data-toggle="dropdown"
                                        aria-expanded="false">
                                        <span>ITA</span>
                                        <svg class="icon d-none d-lg-block">
                                            <use
                                                xlink:href="node_modules/bootstrap-italia/dist/svg/sprite.svg#it-expand">
                                            </use>
                                        </svg>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>`
});


var footerComponent = Vue.component("ext-footer", {
  template: `<footer class="it-footer">
    <div class="it-footer-main">
        <div class="container">
            <section>
                <div class="row clearfix">
                    <div class="col-sm-12">
                        <div class="it-brand-wrapper">
                            <a href="#">
                                <img src="static/cai_logo.png" class="icon" />
                                <div class="it-brand-text">
                                    <h2 class="no_toc">CAI Bologna</h2>
                                    <h3 class="no_toc d-none d-md-block">Sentieri e Cartografia v1.0</h3>
                                </div>
                            </a>
                        </div>
                    </div>
                </div>
            </section>
            <section class="py-4 border-white border-top">
                <div class="row">
                    <div class="col-lg-4 col-md-4 pb-2">
                        <h4><a href="#" title="Vai alla pagina: Contatti">Contatti</a></h4>
                        <p>
                            <strong>CAI Bologna</strong><br>Sezione "Mario Fantin"
                            <br />Via Stalingrado 105 - 40128
                        </p>
                        <div class="link-list-wrapper">
                            <ul class="footer-list link-list clearfix">
                                <li>Tel: 051 23485</li>
                                <li><a class="list-item" href="#"
                                        title="Email Segreteria">segreteria@caibo.it</a>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <div class="col-lg-4 col-md-4 pb-2">
                        <h4><a href="#" title="Vai alla pagina: Lorem Ipsum">Supporto tecnico</a></h4>
                    </div>
                    <div class="col-lg-4 col-md-4 pb-2">
                        <div class="pb-2">
                            <h4><a href="#" title="Vai alla pagina: Seguici su">Seguici su</a></h4>
                            <ul class="list-inline text-left social">
                                <li class="list-inline-item"><a class="p-2 text-white" href="#"
                                        target="_blank"><i style="font-size:24px" class="fa">&#xf09a;</i>
                                    </a><span class="sr-only">CAI Bologna</span></a></li>
                              <span class="sr-only">Facebook</span></a></li>
                            </ul>
                        </div>
                        <div class="pb-2">
                            <h4><a href="#" title="Vai alla pagina: Newsletter">Newsletter</a></h4>
                            <p>Form Newsletter</p>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    </div>
    <div class="it-footer-small-prints clearfix">
        <div class="container">
            <h3 class="sr-only">Sezione Link Utili</h3>
            <ul class="it-footer-small-prints-list list-inline mb-0 d-flex flex-column flex-md-row">
                <li class="list-inline-item"><a href="#" title="Privacy-Cookies">Aggiornamento dati e uso del
                        sito</a></li>
                <li class="list-inline-item"><a href="#" title="Privacy-Cookies">Privacy policy</a></li>
            </ul>
        </div>
    </div>
</footer>`
});

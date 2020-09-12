var SignInPage = {
  template: `<div class="container my-4">
    <div class="row">
        <div class="col-12 col-md-8">
            <h1>Login</h1>
            <form class="login" method="post">
			<p class="-row -row--wide form-row form-row-wide">
				<label for="username">Nome utente<span class="required">*</span></label>
                <input type="text" class="input-text" name="username" id="username" autocomplete="username" required="required" value="">
                </p>
			<p class="-row -row--wide form-row form-row-wide">
				<label for="password">Password&nbsp;<span class="required">*</span></label>
				<input class="input-text" type="password" name="password" id="password" required="required"  autocomplete="current-password">
			</p>

			<p class="form-row">
                <input type="hidden"><input type="hidden" name="_wp_http_referer" value="/">				
                <input class="btn btn-primary btn-lg active" type="submit" name="login" value="Log in"/>
			</p>
		</form>
        </div>
    </div>
</div>`,
};

module.exports = SignInPage;

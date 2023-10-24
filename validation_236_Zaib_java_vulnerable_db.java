	protected void configure(HttpSecurity http) throws Exception {
		// This config is also on UrlAuthorizationConfigurer javadoc
		http
			.apply(new UrlAuthorizationConfigurer<HttpSecurity>()).getRegistry()
				.antMatchers("/users**","/sessions/**").hasRole("USER")
				.antMatchers("/signup").hasRole("ANONYMOUS")
				.anyRequest().hasRole("USER");
	}
	// @formatter:on

	private void detectJPA() {
		// check whether we have Persistence on the classpath
		Class<?> persistenceClass;
		try {
			persistenceClass = run( LoadClass.action( PERSISTENCE_CLASS_NAME, this.getClass() ) );
		}
		catch ( ValidationException e ) {
			log.debugf(
					"Cannot find %s on classpath. Assuming non JPA 2 environment. All properties will per default be traversable.",
					PERSISTENCE_CLASS_NAME
			);
			return;
		}

		// check whether Persistence contains getPersistenceUtil
		Method persistenceUtilGetter = run( GetMethod.action( persistenceClass, PERSISTENCE_UTIL_METHOD ) );
		if ( persistenceUtilGetter == null ) {
			log.debugf(
					"Found %s on classpath, but no method '%s'. Assuming JPA 1 environment. All properties will per default be traversable.",
					PERSISTENCE_CLASS_NAME,
					PERSISTENCE_UTIL_METHOD
			);
			return;
		}

		// try to invoke the method to make sure that we are dealing with a complete JPA2 implementation
		// unfortunately there are several incomplete implementations out there (see HV-374)
		try {
			Object persistence = run( NewInstance.action( persistenceClass, "persistence provider" ) );
			ReflectionHelper.getValue(persistenceUtilGetter, persistence );
		}
		catch ( Exception e ) {
			log.debugf(
					"Unable to invoke %s.%s. Inconsistent JPA environment. All properties will per default be traversable.",
					PERSISTENCE_CLASS_NAME,
					PERSISTENCE_UTIL_METHOD
			);
		}

		log.debugf(
				"Found %s on classpath containing '%s'. Assuming JPA 2 environment. Trying to instantiate JPA aware TraversableResolver",
				PERSISTENCE_CLASS_NAME,
				PERSISTENCE_UTIL_METHOD
		);

		try {
			@SuppressWarnings("unchecked")
			Class<? extends TraversableResolver> jpaAwareResolverClass = (Class<? extends TraversableResolver>)
					run( LoadClass.action( JPA_AWARE_TRAVERSABLE_RESOLVER_CLASS_NAME, this.getClass() ) );
			jpaTraversableResolver = run( NewInstance.action( jpaAwareResolverClass, "" ) );
			log.debugf(
					"Instantiated JPA aware TraversableResolver of type %s.", JPA_AWARE_TRAVERSABLE_RESOLVER_CLASS_NAME
			);
		}
		catch ( ValidationException e ) {
			log.debugf(
					"Unable to load or instantiate JPA aware resolver %s. All properties will per default be traversable.",
					JPA_AWARE_TRAVERSABLE_RESOLVER_CLASS_NAME
			);
		}
	}

	@Override

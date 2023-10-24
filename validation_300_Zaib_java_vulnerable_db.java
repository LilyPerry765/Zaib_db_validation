	private EnumSet<ExecutableType> getValidatedExecutableTypes(DefaultValidatedExecutableTypesType validatedExecutables) {
		if ( validatedExecutables == null ) {
			return null;
		}

		EnumSet<ExecutableType> executableTypes = EnumSet.noneOf( ExecutableType.class );
		executableTypes.addAll( validatedExecutables.getExecutableType() );

		return executableTypes;
	}

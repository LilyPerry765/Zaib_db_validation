	public Object getValue(Object o) {
		if ( location.getMember() == null ) {
			return o;
		}
		else {
			return ReflectionHelper.getValue( location.getMember(), o );
		}
	}

	@Override

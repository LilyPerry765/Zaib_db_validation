	protected void addEmptyValueMapping(DefaultMapper mapper, String field, Object model) {
		ParserContext parserContext = new FluentParserContext().evaluate(model.getClass());
		Expression target = expressionParser.parseExpression(field, parserContext);
		try {
			Class<?> propertyType = target.getValueType(model);
			Expression source = new StaticExpression(getEmptyValue(propertyType));
			DefaultMapping mapping = new DefaultMapping(source, target);
			if (logger.isDebugEnabled()) {
				logger.debug("Adding empty value mapping for parameter '" + field + "'");
			}
			mapper.addMapping(mapping);
		} catch (EvaluationException e) {
		}
	}

	/**
	 * Adds a {@link DefaultMapping} between the given request parameter name and a matching model field.
	 *
	 * @param mapper the mapper to add the mapping to
	 * @param parameter the request parameter name
	 * @param model the model
	 */

    public void register(ContainerBuilder builder, LocatableProperties props)
            throws ConfigurationException {

        builder.factory(com.opensymphony.xwork2.ObjectFactory.class)
                .factory(ActionProxyFactory.class, DefaultActionProxyFactory.class, Scope.SINGLETON)
                .factory(ObjectTypeDeterminer.class, DefaultObjectTypeDeterminer.class, Scope.SINGLETON)

                .factory(XWorkConverter.class, Scope.SINGLETON)
                .factory(XWorkBasicConverter.class, Scope.SINGLETON)
                .factory(ConversionPropertiesProcessor.class, DefaultConversionPropertiesProcessor.class, Scope.SINGLETON)
                .factory(ConversionFileProcessor.class, DefaultConversionFileProcessor.class, Scope.SINGLETON)
                .factory(ConversionAnnotationProcessor.class, DefaultConversionAnnotationProcessor.class, Scope.SINGLETON)
                .factory(TypeConverterCreator.class, DefaultTypeConverterCreator.class, Scope.SINGLETON)
                .factory(TypeConverterHolder.class, DefaultTypeConverterHolder.class, Scope.SINGLETON)

                .factory(FileManager.class, "system", DefaultFileManager.class, Scope.SINGLETON)
                .factory(FileManagerFactory.class, DefaultFileManagerFactory.class, Scope.SINGLETON)
                .factory(ValueStackFactory.class, OgnlValueStackFactory.class, Scope.SINGLETON)
                .factory(ValidatorFactory.class, DefaultValidatorFactory.class, Scope.SINGLETON)
                .factory(ValidatorFileParser.class, DefaultValidatorFileParser.class, Scope.SINGLETON)
                .factory(PatternMatcher.class, WildcardHelper.class, Scope.SINGLETON)
                .factory(ReflectionProvider.class, OgnlReflectionProvider.class, Scope.SINGLETON)
                .factory(ReflectionContextFactory.class, OgnlReflectionContextFactory.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, CompoundRoot.class.getName(), CompoundRootAccessor.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, Object.class.getName(), ObjectAccessor.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, Iterator.class.getName(), XWorkIteratorPropertyAccessor.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, Enumeration.class.getName(), XWorkEnumerationAccessor.class, Scope.SINGLETON)
                .factory(UnknownHandlerManager.class, DefaultUnknownHandlerManager.class, Scope.SINGLETON)

                // silly workarounds for ognl since there is no way to flush its caches
                .factory(PropertyAccessor.class, List.class.getName(), XWorkListPropertyAccessor.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, ArrayList.class.getName(), XWorkListPropertyAccessor.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, HashSet.class.getName(), XWorkCollectionPropertyAccessor.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, Set.class.getName(), XWorkCollectionPropertyAccessor.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, HashMap.class.getName(), XWorkMapPropertyAccessor.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, Map.class.getName(), XWorkMapPropertyAccessor.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, Collection.class.getName(), XWorkCollectionPropertyAccessor.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, ObjectProxy.class.getName(), ObjectProxyPropertyAccessor.class, Scope.SINGLETON)
                .factory(MethodAccessor.class, Object.class.getName(), XWorkMethodAccessor.class, Scope.SINGLETON)
                .factory(MethodAccessor.class, CompoundRoot.class.getName(), CompoundRootAccessor.class, Scope.SINGLETON)

                .factory(TextParser.class, OgnlTextParser.class, Scope.SINGLETON)

                .factory(NullHandler.class, Object.class.getName(), InstantiatingNullHandler.class, Scope.SINGLETON)
                .factory(ActionValidatorManager.class, AnnotationActionValidatorManager.class, Scope.SINGLETON)
                .factory(ActionValidatorManager.class, "no-annotations", DefaultActionValidatorManager.class, Scope.SINGLETON)
                .factory(TextProvider.class, "system", DefaultTextProvider.class, Scope.SINGLETON)
                .factory(TextProvider.class, TextProviderSupport.class, Scope.SINGLETON)
                .factory(LocaleProvider.class, DefaultLocaleProvider.class, Scope.SINGLETON)
                .factory(OgnlUtil.class, Scope.SINGLETON)
                .factory(CollectionConverter.class, Scope.SINGLETON)
                .factory(ArrayConverter.class, Scope.SINGLETON)
                .factory(DateConverter.class, Scope.SINGLETON)
                .factory(NumberConverter.class, Scope.SINGLETON)
                .factory(StringConverter.class, Scope.SINGLETON);
        props.setProperty(XWorkConstants.DEV_MODE, Boolean.FALSE.toString());
        props.setProperty(XWorkConstants.LOG_MISSING_PROPERTIES, Boolean.FALSE.toString());
        props.setProperty(XWorkConstants.ENABLE_OGNL_EXPRESSION_CACHE, Boolean.TRUE.toString());
        props.setProperty(XWorkConstants.RELOAD_XML_CONFIGURATION, Boolean.FALSE.toString());
    }


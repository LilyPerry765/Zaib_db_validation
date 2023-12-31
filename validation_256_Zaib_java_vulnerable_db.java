    public KeyPair generateKeyPair()
    {
        if (!initialised)
        {
            DSAParametersGenerator pGen = new DSAParametersGenerator();

            pGen.init(strength, certainty, random);
            param = new DSAKeyGenerationParameters(random, pGen.generateParameters());
            engine.init(param);
            initialised = true;
        }

        AsymmetricCipherKeyPair pair = engine.generateKeyPair();
        DSAPublicKeyParameters pub = (DSAPublicKeyParameters)pair.getPublic();
        DSAPrivateKeyParameters priv = (DSAPrivateKeyParameters)pair.getPrivate();

        return new KeyPair(new BCDSAPublicKey(pub), new BCDSAPrivateKey(priv));
    }

    private void checkParams()
        throws Exception
    {
        if (vi == null)
        {
            throw new Exception("no layers defined.");
        }
        if (vi.length > 1)
        {
            for (int i = 0; i < vi.length - 1; i++)
            {
                if (vi[i] >= vi[i + 1])
                {
                    throw new Exception(
                        "v[i] has to be smaller than v[i+1]");
                }
            }
        }
        else
        {
            throw new Exception(
                "Rainbow needs at least 1 layer, such that v1 < v2.");
        }
    }

    /**
     * Getter for the number of layers
     *
     * @return the number of layers
     */

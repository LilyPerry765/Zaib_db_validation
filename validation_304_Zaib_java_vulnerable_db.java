    protected final GF2Polynomial[] invertMatrix(GF2Polynomial[] matrix)
    {
        GF2Polynomial[] a = new GF2Polynomial[matrix.length];
        GF2Polynomial[] inv = new GF2Polynomial[matrix.length];
        GF2Polynomial dummy;
        int i, j;
        // initialize a as a copy of matrix and inv as E(inheitsmatrix)
        for (i = 0; i < mDegree; i++)
        {
            try
            {
                a[i] = new GF2Polynomial(matrix[i]);
                inv[i] = new GF2Polynomial(mDegree);
                inv[i].setBit(mDegree - 1 - i);
            }
            catch (RuntimeException BDNEExc)
            {
                BDNEExc.printStackTrace();
            }
        }
        // construct triangle matrix so that for each a[i] the first i bits are
        // zero
        for (i = 0; i < mDegree - 1; i++)
        {
            // find column where bit i is set
            j = i;
            while ((j < mDegree) && !a[j].testBit(mDegree - 1 - i))
            {
                j++;
            }
            if (j >= mDegree)
            {
                throw new RuntimeException(
                    "GF2nField.invertMatrix: Matrix cannot be inverted!");
            }
            if (i != j)
            { // swap a[i]/a[j] and inv[i]/inv[j]
                dummy = a[i];
                a[i] = a[j];
                a[j] = dummy;
                dummy = inv[i];
                inv[i] = inv[j];
                inv[j] = dummy;
            }
            for (j = i + 1; j < mDegree; j++)
            { // add column i to all columns>i
                // having their i-th bit set
                if (a[j].testBit(mDegree - 1 - i))
                {
                    a[j].addToThis(a[i]);
                    inv[j].addToThis(inv[i]);
                }
            }
        }
        // construct Einheitsmatrix from a
        for (i = mDegree - 1; i > 0; i--)
        {
            for (j = i - 1; j >= 0; j--)
            { // eliminate the i-th bit in all
                // columns < i
                if (a[j].testBit(mDegree - 1 - i))
                {
                    a[j].addToThis(a[i]);
                    inv[j].addToThis(inv[i]);
                }
            }
        }
        return inv;
    }

    /**
     * Converts the given element in representation according to this field to a
     * new element in representation according to B1 using the change-of-basis
     * matrix calculated by computeCOBMatrix.
     *
     * @param elem  the GF2nElement to convert
     * @param basis the basis to convert <tt>elem</tt> to
     * @return <tt>elem</tt> converted to a new element representation
     *         according to <tt>basis</tt>
     * @see GF2nField#computeCOBMatrix
     * @see GF2nField#getRandomRoot
     * @see GF2nPolynomial
     * @see "P1363 A.7 p109ff"
     */

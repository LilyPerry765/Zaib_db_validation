    public void processParameters( String str ) {
        int end=str.length();
        int pos=0;
        if( debug > 0)
            log("String: " + str );
        
        do {
            boolean noEq=false;
            int valStart=-1;
            int valEnd=-1;
            
            int nameStart=pos;
            int nameEnd=str.indexOf('=', nameStart );
            int nameEnd2=str.indexOf('&', nameStart );
            if( nameEnd2== -1 ) nameEnd2=end;
            if( (nameEnd2!=-1 ) &&
                ( nameEnd==-1 || nameEnd > nameEnd2) ) {
                nameEnd=nameEnd2;
                noEq=true;
                valStart=nameEnd;
                valEnd=nameEnd;
                if(debug>0) log("no equal " + nameStart + " " + nameEnd + " " +
                        str.substring(nameStart, nameEnd));
            }

            if( nameEnd== -1 ) nameEnd=end;

            if( ! noEq ) {
                valStart=nameEnd+1;
                valEnd=str.indexOf('&', valStart);
                if( valEnd== -1 ) valEnd = (valStart < end) ? end : valStart;
            }
            
            pos=valEnd+1;
            
            if( nameEnd<=nameStart ) {
                continue;
            }
            if( debug>0)
                log( "XXX " + nameStart + " " + nameEnd + " "
                     + valStart + " " + valEnd );
            
            try {
                tmpNameC.append(str, nameStart, nameEnd-nameStart );
                tmpValueC.append(str, valStart, valEnd-valStart );
            
                if( debug > 0 )
                    log( tmpNameC + "= " + tmpValueC);

                if( urlDec==null ) {
                    urlDec=new UDecoder();   
                }

                urlDec.convert( tmpNameC );
                urlDec.convert( tmpValueC );

                if( debug > 0 )
                    log( tmpNameC + "= " + tmpValueC);
                
                addParam( tmpNameC.toString(), tmpValueC.toString() );
            } catch( IOException ex ) {
                ex.printStackTrace();
            }

            tmpNameC.recycle();
            tmpValueC.recycle();

        } while( pos<end );
    }



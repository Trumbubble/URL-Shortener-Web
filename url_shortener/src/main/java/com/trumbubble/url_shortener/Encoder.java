package com.trumbubble.url_shortener;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encoder { //Using Feister Cipher
    
    static long N = (long) Math.pow(62,7);
    static int HALF = 21;
    static int MASK = (1 << 21) - 1;
    static int ROUNDS = 10;
    static byte[] SECRET_KEY = "very secret and not shared key".getBytes();

    static String TEXT = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String encode(long input) //from the index of the url, creates the 7-character string
    {
        long encoded = encoder(input);
        int remainder = (int) (encoded % 62);
        String result = "";
        while(encoded > 0)
        {
            result += TEXT.substring(remainder, remainder + 1);
            encoded /= 62;
            remainder = (int) (encoded % 62);
        }
        
        return result;
    }

    public static long decode(String input) //gets the index of the url using the 7-character string
    {
        long decoded = 0;

        for (int i = 0; i < input.length(); i++)
        {
            String c = input.substring(i, i+1);
            int value = TEXT.indexOf(c);
            decoded = decoded * 62 + value;
        }
        return 0;
    }

    public static long decoder(long input)
    {
        long y = input;
        while (true) { 
            y = feistelInverse(y);
            if (y < N) {
                return y;
            }
        }
    }

    public static long encoder(long input)
    {
        long y = input;
        while (true) {
            y = feistel(y);
            if (y < N) {
                return y;
            }
        }
    }

    public static long feistel(long x)
    {
        long L = (x >>> HALF) & MASK;
        long R = x & MASK;

        for (int i = 0; i < ROUNDS; i++) {
            long newL = R;
            long newR = L ^ roundFunction(R, i);
            L = newL;
            R = newR;
        }
        
        return (L << HALF) | R;
    }

    public static long feistelInverse(long x)
    {
        long L = (x >>> HALF) & MASK;
        long R = x & MASK;

        for (int i = ROUNDS - 1; i >= 0; i++)
        {
            long newR = L;
            long newL = R ^ roundFunction(L, i);
            L = newL;
            R = newR;
        }

        return (L << HALF) | R;
    }

    public static long roundFunction(long r, int round) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            ByteBuffer buf = ByteBuffer.allocate (SECRET_KEY.length + 1 + 3);
            buf.put(SECRET_KEY);
            buf.put((byte) round);
            buf.put((byte) (r >>> 16));
            buf.put((byte) (r >>> 8));
            buf.put((byte) r);

            byte[] hash = md.digest(buf.array());

            long out = ((hash[0] & 0xffL) << 24) | ((hash[1] & 0xffL) << 16) | ((hash[2] & 0xffL) << 8) | (hash[3] & 0xffL);

            return out & MASK;
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

}

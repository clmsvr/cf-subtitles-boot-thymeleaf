package cms.cf.test;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

/******************
 * 
 * Fonte: http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
 * 
 *******************/




/*Solução 1: Gerador de token seguro */
public final class SecureTokenGenerator
{
    private SecureRandom random = new SecureRandom();

    public String nextSessionId()
    {
        return new BigInteger(130, random).toString(32);
    }

    private static String hash(String password) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-512");
        
        byte[] bytes = sha256.digest(password.getBytes());
        
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
          sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
    
    public static void main(String[] args) throws Exception
    {
        System.out.println(new SecureTokenGenerator().nextSessionId());
        System.out.println(new solucao4().randomString(30));
        
        System.out.println(Base64.getEncoder().encodeToString("123".getBytes()));
        
        System.out.println(new String(Base64.getDecoder().decode("MTIz")));
        
        System.out.println(hash("123"));
        
        System.out.println(DigestUtils.sha512Hex("123"));
        
        System.out.println(DigestUtils.sha256Hex("123"));
        
      
    }
}

/* Solucao 2: Inseguro por usar Random ao invez de SecureRandom*/
class RandomString
{
    private static final char[] symbols;

    static
    {
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ++ch)
            tmp.append(ch);
        for (char ch = 'a'; ch <= 'z'; ++ch)
            tmp.append(ch);
        symbols = tmp.toString().toCharArray();
    }

    private final Random random = new Random();

    private final char[] buf;

    public RandomString(int length)
    {
        if (length < 1) throw new IllegalArgumentException("length < 1: " + length);
        buf = new char[length];
    }

    public String nextString()
    {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }
}

/*Solucao 3: nao deve ser usado como token */
class solucao3
{
    public String geraToken()
    {
        String uuid = UUID.randomUUID().toString();
        System.out.println("uuid = " + uuid);
        return uuid;
    }
}

/*Solucao 4: tao boa quanto a primeira */
class solucao4
{
    static final String AB  = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%&*()_+-=[]{}<>:,.;?/|";
    static SecureRandom rnd = new SecureRandom();

    public String randomString(int len)
    {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }
    
    
}



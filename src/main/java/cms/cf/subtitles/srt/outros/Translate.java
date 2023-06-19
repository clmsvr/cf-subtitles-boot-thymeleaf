package cms.cf.subtitles.srt.outros;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

public class Translate
{
    static int generateB() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        Date start;
        Date now;
        try {
            start = sdf.parse("01/01/1970");
            now = new Date();
        } catch (ParseException e) {
            return 402890;
        }
        long diff = now.getTime() - start.getTime();
        long hours = diff / (60 * 60 * 1000) % 24;
        long days = diff / (24 * 60 * 60 * 1000);
        return (int) (hours + days * 24);
    }

    static int shr32(int x, int bits) {
        if (x < 0) {
            long x_l = 0xffffffffl + x + 1;
            return (int) (x_l >> bits);
        }
        return x >> bits;
    }

    static int charCodeAt(String str, int index) {
        return str.charAt(index);
    }

    static int RL(int a, String b) {
        for (int c = 0; c < b.length() - 2; c += 3) {
            int d = b.charAt(c + 2);
            d = d >= 65 ? d - 87 : d - 48;
            d = b.charAt(c + 1) == '+' ? shr32(a, d) : (a << d);
            a = b.charAt(c) == '+' ? (a + d & 0xFFFFFFFF) : a ^ d;
        }
        return a;
    }

    static String TL(String a) {
        int b = generateB();
        int e = 0;
        int f = 0;
        Vector<Integer> d = new Vector<Integer>();
        for (; f < a.length(); f++) {
            int g = charCodeAt(a, f);

            if (0x80 > g) {
                d.add(e++, g);
            } else {
                if (0x800 > g) {
                    d.add(e++, g >> 6 | 0xC0);
                } else {
                    if (0xD800 == (g & 0xFC00) && f + 1 < a.length() && 0xDC00 == (charCodeAt(a, f + 1) & 0xFC00)) {
                        g = 0x10000 + ((g & 0x3FF) << 10) + (charCodeAt(a, ++f) & 0x3FF);
                        d.add(e++, g >> 18 | 0xF0);
                        d.add(e++, g >> 12 & 0x3F | 0x80);
                    } else {
                        d.add(e++, g >> 12 | 0xE0);
                        d.add(e++, g >> 6 & 0x3F | 0x80);
                    }
                }
                d.add(e++, g & 63 | 128);
            }
        }

        int a_i = b;
        for (e = 0; e < d.size(); e++) {
            a_i += d.get(e);
            a_i = RL(a_i, "+-a^+6");
        }
        a_i = RL(a_i, "+-3^+b+-f");
        long a_l;
        if (0 > a_i) {
            a_l = 0x80000000l + (a_i & 0x7FFFFFFF);
        } else {
            a_l = a_i;
        }
        a_l %= Math.pow(10, 6);
        return String.format(Locale.US, "%d.%d", a_l, a_l ^ b);
    }
    
    public static String translate() throws IOException
    {
        //https://github.com/lkuza2/java-speech-api/issues/69
        // fetch
        URL url = new URL("http://translate.google.com/translate_a/single?client=t&hl=en&sl=en&tl=pt" 
             +"dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&dt=t&ie=UTF-8&oe=UTF-8&source=btn&srcrom=1&ssel=0&tsel=3&kc=0&tk="+TL("car")+"&q=car");

             //+"&ie=UTF-8&oe=UTF-8&multires=1&oc=1&otf=2&ssel=0&tsel=0&sc=1&q=" + URLEncoder.encode("car", "UTF-8"));
        URLConnection urlConnection = url.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Something Else");
        BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String result = br.readLine();
        br.close();
        // parse
        System.out.println(result);
        result = result.substring(2, result.indexOf("]]") + 1);
        StringBuilder sb = new StringBuilder();
        String[] splits = result.split("(?<!\\\\)\"");
        for (int i = 1; i < splits.length; i += 8)
            sb.append(splits[i]);
        return sb.toString().replace("\\n", "\n").replaceAll("\\\\(.)", "$1");
    }

    public static void main(String[] args) throws IOException
    {
        System.out.println(TL("car"));
        System.out.println(translate());
    }
}
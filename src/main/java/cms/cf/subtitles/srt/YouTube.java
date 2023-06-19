package cms.cf.subtitles.srt;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

/**
 * Utilidades para acesso ao youtube.
 */
public class YouTube
{
    
    public static String getVideoTitle(String idYoutube)
    throws Exception 
    {
        String url = "https://www.youtube.com/watch?v=" + idYoutube;

        String title = readVideoTitle(url,null,0);
        if (title.equals("")) throw new Exception("Titulo nao encontrado para video [" + idYoutube + "]");
        
        return title;
    }

    private static String readVideoTitle(String url, String hostAddress, int port) 
    throws Exception
    {
        url = url.replace("http://", "https://");
        
        // Create Proxy for the current Video object
        Proxy proxy = null;
        if (hostAddress != null)
        {
            new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(hostAddress, port));
        }
        
        InputStreamReader isr = readURL(url, proxy);
        String sourceText = readStream(isr);
        
        return (sourceText != null) ? getVideoTitleFromSource(sourceText) : "";
    }

    private static InputStreamReader readURL(String s, Proxy proxy) throws MalformedURLException, IOException
    {
        URL url;
        InputStreamReader isr;
        String appName, appVersion;
        URLConnection urlconn;

        appName = "Google2SRT";
        appVersion = "0.7.3";

        url = new URL(s);
        if (proxy != null)
        {
            urlconn = url.openConnection(proxy);
        }
        else
        {
            urlconn = url.openConnection();
        }
        urlconn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        urlconn.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        urlconn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; " + appName + "/" + appVersion + ")");
        urlconn.connect();

        isr = new InputStreamReader(urlconn.getInputStream(), "UTF-8");
        return isr;
    }

    private static String readStream(InputStreamReader isr) throws IOException
    {
        String s;

        StringWriter writer = new StringWriter();
        copyLarge(isr, writer, new char[1024 * 4]);
        s = writer.toString();

        return s;
    }

    /**
     * Copy chars from a large (over 2GB) <code>Reader</code> to a <code>Writer</code>.
     * <p>
     * This method uses the provided buffer, so there is no need to use a
     * <code>BufferedReader</code>.
     * <p>
     *
     * @param input  the <code>Reader</code> to read from
     * @param output  the <code>Writer</code> to write to
     * @param buffer the buffer to be used for the copy
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since 2.2
     */
    private static long copyLarge(Reader input, Writer output, char [] buffer) throws IOException {
        long count = 0;
        int n = 0;
        int EOF = -1;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
    
    private static String getVideoTitleFromSource(String YouTubeWebSource)
    {
        String s;
        String[] strings;

        // "...<title>Video title - YouTube</title>...",
        strings = YouTubeWebSource.split("<title>");
        s = strings[1];
        strings = s.split(" - YouTube</title>");
        s = strings[0];

        return s;
    }


}

package cms.cf.lib;

import java.util.Date;

public class StringUtil
{
    public static String INVALID_CHARS ="�������������������������ݺ��������������������������";
    public static String CONVERT_CHARS ="AAAAAACEEEEIIIINOOOOOUUUUY.aaaaaaceeeeiiiinooooouuuuy";
        
    public static String escapeHtml(String msg)
    {
        String texto = null;
        if (msg != null)
        {
            texto = msg.replaceAll("\"", "&quot;");
            texto = texto.replaceAll("<", "&lt;");
            texto = texto.replaceAll(">", "&gt;");
            texto = texto.replaceAll("'", "&apos;");
            texto = texto.replaceAll(""+(char)10, " ");
            texto = texto.replaceAll(""+(char)13, " ");
        }
        return texto;
    }
    
    
    public static String removeAcentos(String str)
    {   
        StringBuffer buff = new StringBuffer(str);
        char ch ;
        int index;
        
        for (int i=0; i < buff.length(); i++)
        {           
            ch = buff.charAt(i);
            
            if ( (ch < 32 && ch != 13 && ch != 10) || ch > 126)
            {               
                index = INVALID_CHARS.indexOf(ch);             
                if ( index != -1 )
                {                       
                    buff.setCharAt(i,CONVERT_CHARS.charAt(index));
                }
                else
                {
                    buff.setCharAt(i,'?');
                }
            }
        }
        return buff.toString();
    }
    
    
    public static String removeAcentos2(String str)
    {
        if (str == null) return null;
        
        StringBuffer buff = new StringBuffer();
        char c ;
        int index;
        
        for (int i=0; i < str.length(); i++)
        {           
            c = str.charAt(i);
            
            index = INVALID_CHARS.indexOf(c);             
            if ( index != -1 )
            {                       
                buff.append(CONVERT_CHARS.charAt(index));
            }
            else if (c == ' ' || c == '_' || c == '-' || c == '(' 
                || c == ')' || c == '.' || c == ';' || c == '!' 
                || c == '@' || c == '$' || c == '%' || c == '=' 
                || c == '[' || c == ']' || c == ':' || c == '{' 
                || c == '}' || c == '|' || c == '/' || c == '\\' 
                || c == '*' ||  c == ',' ||
                (c >= '0' && c <= '9') || 
                (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z')  )
           {
               buff.append(c);
           }
           else
           {
               buff.append(' ');
           }
        }
        return buff.toString();    
    }
    
    
    public static long daysUntilToday(Date d)
    {
        long milliseconds1 = (new Date()).getTime();
        long milliseconds2 = (d!=null)?d.getTime():milliseconds1;
        long diff = milliseconds1 - milliseconds2;
        
        //long diffSeconds = diff / 1000;
        //long diffMinutes = diff / (60 * 1000);
        ///long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);
        
        return diffDays;
    }
    
    public static void main(String[] args)
    {
        String s = "!@#$%�&*()_+|\\<>,.:;?/[]{}-=qwertyuiopasdfghjklzxcvbnm0123456789����������������������������������������������������";
        
        System.out.println(s);
        System.out.println(removeAcentos(s));
    }
}

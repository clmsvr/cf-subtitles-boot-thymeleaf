package cms.cf.subtitles.srt;

public class SrtItem
{
    /** indice do item da legenda */
    private int index;  
    /** texto do item da legenda */
    private String text; 
    /** tempo de inicio do item da legenda em milisegundos */
    private long begin;
    /** tempo de inicio do item da legenda em milisegundos */
    private String strBegin;
    /** tempo final do item da legenda no formato SRT */
    private long end;        
    /** tempo final do item da legenda no formato SRT */
    private String strEnd;
    
    public SrtItem()
    {
    }

    public SrtItem copy()
    {
        SrtItem it = new SrtItem();
        it.index = index;
        it.text = text;
        it.begin = begin;
        it.strBegin = strBegin;
        it.end = end;
        it.strEnd = strEnd;
        return it;
    }
    
    public void setBegin(long begin)
    {
        this.begin = begin;
        this.strBegin = miliToDisplayString(begin);
    }
    public void setEnd(long end)
    {
        this.end = end;
        this.strEnd = miliToDisplayString(end);
    }
    
    private static String miliToDisplayString(long time)
    {
        long ms = time % 1000;
        long s = time / 1000;
        long m = s / 60;
        s = s % 60;
        long h = m / 60;
        m = m % 60;
        
        if (h > 0) return h+":"+m+":"+format2digit(s)+","+ms/100;
        else       return       m+":"+format2digit(s)+","+ms/100;
    }
    
    private static String format2digit(long number)
    {
        String t = "00"+number;
        return t.substring(t.length()-2, t.length());
    }
    
    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public long getBegin()
    {
        return begin;
    }

    public long getEnd()
    {
        return end;
    }

    public String getStrBegin()
    {
        return strBegin;
    }

    public String getStrEnd()
    {
        return strEnd;
    }
}

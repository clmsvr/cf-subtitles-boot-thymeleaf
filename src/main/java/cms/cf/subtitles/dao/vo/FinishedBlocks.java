package cms.cf.subtitles.dao.vo;

import java.util.ArrayList;

public class FinishedBlocks
{
    private String title;
    private String locale;
    private ArrayList<Block> list = new ArrayList<>();
    
    public FinishedBlocks()
    {
    }

    public void addBlock(Block b)
    {
        list.add(b);
    }
    
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public ArrayList<Block> getList()
    {
        return list;
    }

    public void setList(ArrayList<Block> list)
    {
        this.list = list;
    }

    public String getLocale()
    {
        return locale;
    }

    public void setLocale(String locale)
    {
        this.locale = locale;
    }
    
    
}

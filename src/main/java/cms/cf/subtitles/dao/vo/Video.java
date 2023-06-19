package cms.cf.subtitles.dao.vo;

import java.util.Date;

public class Video
{
    /**
     * opened - não alocado, disponível
     * finished - finalizado pelo usuário
     * ready - verificado e pronto para upload
     */
    public enum Status {OPENED, CLOSED, READY}
    
    private int    id;
    private String idYoutube;
    private String title;

    private Status status = Status.OPENED;
    
    private int    minutes;
    private int    blocks;
    private int    blocksReady;
    
    private Date   sysCreationDate;
    private Date   sysUpdateDate;

    public Video()
    {
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getIdYoutube()
    {
        return idYoutube;
    }

    public void setIdYoutube(String idYoutube)
    {
        this.idYoutube = idYoutube;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    public int getMinutes()
    {
        return minutes;
    }

    public void setMinutes(int minutes)
    {
        this.minutes = minutes;
    }

    public int getBlocks()
    {
        return blocks;
    }

    public void setBlocks(int blocks)
    {
        this.blocks = blocks;
    }

    public int getBlocksReady()
    {
        return blocksReady;
    }

    public void setBlocksReady(int blocksReady)
    {
        this.blocksReady = blocksReady;
    }

    public Date getSysCreationDate()
    {
        return sysCreationDate;
    }

    public void setSysCreationDate(Date sysCreationDate)
    {
        this.sysCreationDate = sysCreationDate;
    }

    public Date getSysUpdateDate()
    {
        return sysUpdateDate;
    }

    public void setSysUpdateDate(Date sysUpdateDate)
    {
        this.sysUpdateDate = sysUpdateDate;
    }


}

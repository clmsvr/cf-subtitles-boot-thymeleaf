package cms.cf.subtitles.dao.vo;

import java.util.Date;

public class Block
{
    public static enum Locale {PT, EN}
    /**
     * opened - alocado ou não alocado, disponível para edicao 
     * finished - finalizado pelo usuário
     * ready - verificado e pronto para upload
     */
    public static enum Status {OPENED, FINISHED, READY}
    
    private int    id;
    private int    idVideo;
    
    private String email; //bloco alocado email != null
    private Date   allocateTime;
    
    private Locale locale;
    
    private String subtitleOri;    //texto original da legenda originado do youtube
    private String subtitleUpdate; //legenda atualizada pelo usuario
    private long   begin;          //tempo de inicio do bloco de legendas em milissegundos
    private long   end;            //tempo de fim do bloco de legendas em milissegundos

    private Status status = Status.OPENED;

    private String updatedBy = ""; //ultimo email que atualizou o bloco    
    private Date   sysCreationDate;
    private Date   sysUpdateDate;

    public Block()
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
    
    public int getIdVideo()
    {
        return idVideo;
    }

    public void setIdVideo(int idVideo)
    {
        this.idVideo = idVideo;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getSubtitleOri()
    {
        return subtitleOri;
    }

    public void setSubtitleOri(String subtitleOri)
    {
        this.subtitleOri = subtitleOri;
    }

    public String getSubtitleUpdate()
    {
        return subtitleUpdate;
    }

    public void setSubtitleUpdate(String subtitleUpdate)
    {
        this.subtitleUpdate = subtitleUpdate;
    }

    public long getBegin()
    {
        return begin;
    }

    public void setBegin(long begin)
    {
        this.begin = begin;
    }

    public long getEnd()
    {
        return end;
    }

    public void setEnd(long end)
    {
        this.end = end;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
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

    public Locale getLocale()
    {
        return locale;
    }

    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    public Date getAllocateTime()
    {
        return allocateTime;
    }

    public void setAllocateTime(Date allocateTime)
    {
        this.allocateTime = allocateTime;
    }

    public String getUpdatedBy()
    {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy)
    {
        this.updatedBy = updatedBy;
    }
}

package cms.cf.subtitles.dao.vo;

import java.util.Date;

public class ResetToken
{
    private String token;
    private String email; 
    private Date   sysCreationDate;

    public ResetToken()
    {
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public Date getSysCreationDate()
    {
        return sysCreationDate;
    }

    public void setSysCreationDate(Date sysCreationDate)
    {
        this.sysCreationDate = sysCreationDate;
    }


}

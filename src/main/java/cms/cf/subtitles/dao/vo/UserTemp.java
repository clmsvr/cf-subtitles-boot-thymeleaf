package cms.cf.subtitles.dao.vo;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class UserTemp
{
    @NotEmpty
    @NotNull
    @Size(max = 200)
    private String token;
    
    @NotEmpty (message="É necessário digirar seu email.") 
    @Pattern(regexp = "[\\w\\-]+(\\.[\\w\\-]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}", message="O formato do email parece ser inválido.")
    @Size(max=100)
    private String email; 
    
    @NotEmpty(message="É necessário digirar uma senha.") 
    @Pattern(regexp = "[^\\s]{6,}", message="A senha deve ter tamanho mínimo 6, sem espaços.")
    @Size(max=50)
    private String pwd;
    
    @Size(max=100)
    private String name;
    
    private int acessos = 0;
    private boolean emailSent = false;
    
    private Date   sysCreationDate;

    public UserTemp()
    {
    }

    public UserTemp(User u, String token)
    {
        email = u.getEmail();
        pwd = u.getPwd();
        name = u.getName();
        this.token = token;
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

    public String getPwd()
    {
        return pwd;
    }

    public void setPwd(String pwd)
    {
        this.pwd = pwd;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Date getSysCreationDate()
    {
        return sysCreationDate;
    }

    public void setSysCreationDate(Date sysCreationDate)
    {
        this.sysCreationDate = sysCreationDate;
    }

    public int getAcessos()
    {
        return acessos;
    }

    public void setAcessos(int acessos)
    {
        this.acessos = acessos;
    }

    public boolean isEmailSent()
    {
        return emailSent;
    }

    public void setEmailSent(boolean emailSent)
    {
        this.emailSent = emailSent;
    }

    
}

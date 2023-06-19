package cms.cf.subtitles.dao.vo;

import java.util.Date;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class User
{
    @NotEmpty (message="É necessário digirar seu email.") 
    @Pattern(regexp = "[\\w\\-]+(\\.[\\w\\-]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}", message="O formato do email parece ser inválido.")
    @Size(max=100)
    private String email; 

// o formulario de usuario nao atualiza sennha    
//    @NotEmpty(message="É necessário digirar uma senha.") 
//    @Pattern(regexp = "[^\\s]{6,}", message="A senha deve ter tamanho mínimo 6, sem espaços.")
//    @Size(max=50)
    private String pwd;
    
    @NotEmpty(message="É necessário digitar seu nome.")
    @Size(max=100)
    private String name;
    
    @Size(max=100)
    private String city;
    
    @Size(message="Valor informado incorreto", min=0, max=2)
    private String state;
    
    private int    numBlocksSubtitled;
    private int    numBlocksTranslated;
    private String comment;        //descricao do proprio usuario
    private Date   sysCreationDate;
    private Date   sysUpdateDate;

    //@DateTimeFormat(pattern="MM/dd/yyyy")
    //@Pattern(regexp = "\\(?\\b([0-9]{2})\\)?[-. ]?([0-9]{4})[-. ]?([0-9]{4})\\b", message="Telefone em formato incorreto")
    //private String telefone;
    //@Range(min = 1, max = 150)
    //int age;
    public User()
    {
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

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
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

    public int getNumBlocksSubtitled()
    {
        return numBlocksSubtitled;
    }

    public void setNumBlocksSubtitled(int numBlocksSubtitled)
    {
        this.numBlocksSubtitled = numBlocksSubtitled;
    }

    public int getNumBlocksTranslated()
    {
        return numBlocksTranslated;
    }

    public void setNumBlocksTranslated(int numBlocksTranslated)
    {
        this.numBlocksTranslated = numBlocksTranslated;
    }


}

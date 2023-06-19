package cms.cf.subtitles.dao.vo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class PwdReset
{
    @NotEmpty
    @NotNull
    @Size(max = 200)
    private String token;
    
    @NotEmpty(message="É necessário digirar a nova senha.") 
    @Pattern(regexp = "[^\\s]{6,}", message="A senha deve ter tamanho mínimo 6, sem espaços.")
    @Size(max=50)
    private String newpwd1;
    
    @NotEmpty(message="É necessário confirmar a senha.") 
    @Size(max=50)
    private String newpwd2;
    
    public PwdReset()
    {
    }


    public void reset()
    {
        newpwd1 = "";
        newpwd2 = "";
    }
    
    
    public String getToken()
    {
        return token;
    }


    public void setToken(String token)
    {
        this.token = token;
    }


    public String getNewpwd1()
    {
        return newpwd1;
    }

    public void setNewpwd1(String newpwd1)
    {
        this.newpwd1 = newpwd1;
    }

    public String getNewpwd2()
    {
        return newpwd2;
    }

    public void setNewpwd2(String newpwd2)
    {
        this.newpwd2 = newpwd2;
    }


}

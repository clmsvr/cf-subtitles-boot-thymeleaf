package cms.cf.subtitles.dao.vo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class Role
{
    @NotEmpty 
    @NotNull
    @Pattern(regexp = "[\\w\\-]+(\\.[\\w\\-]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}", message="O formato do email parece ser inválido.")
    @Size(max=100)
    private String email; 
    
    @NotEmpty
    @NotNull
    @Size(max=20)
    private String role;
    
    public Role()
    {
    }

    public Role(String email, String role)
    {
        this.email = email;
        this.role = role;
    }
    
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole(String role)
    {
        this.role = role;
    }

}

package cms.cf.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.validation.Valid;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cms.cf.conf.CoreBusines;
import cms.cf.controller.exceptions.BadRequestException;
import cms.cf.controller.exceptions.InternalErrorException;
import cms.cf.subtitles.dao.DaoException;
import cms.cf.subtitles.dao.NotFoundException;
import cms.cf.subtitles.dao.impl.BlockDao;
import cms.cf.subtitles.dao.impl.ResetTokenDao;
import cms.cf.subtitles.dao.impl.UserDao;
import cms.cf.subtitles.dao.impl.UserTempDao;
import cms.cf.subtitles.dao.vo.Email;
import cms.cf.subtitles.dao.vo.FinishedBlocks;
import cms.cf.subtitles.dao.vo.PwdChange;
import cms.cf.subtitles.dao.vo.PwdReset;
import cms.cf.subtitles.dao.vo.ResetToken;
import cms.cf.subtitles.dao.vo.User;
import cms.cf.subtitles.dao.vo.UserTemp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class SubtitlesController 
{
    private Logger logger = Logger.getLogger(this.getClass());

    private static HashMap<String, String> states = new HashMap<>();
    static{
        states.put("AC","Acre");
        states.put("AL","Alagoas");
        states.put("AP","Amapá");
        states.put("AM","Amazonas");
        states.put("BA","Bahia");
        states.put("CE","Ceará");
        states.put("DF","Distrito Federal");
        states.put("ES","Espirito Santo");
        states.put("GO","Goiás");
        states.put("MA","Maranhão");
        states.put("MS","Mato Grosso do Sul");
        states.put("MT","Mato Grosso");
        states.put("MG","Minas Gerais");
        states.put("PA","Pará");
        states.put("PB","Paraíba");
        states.put("PR","Paraná");
        states.put("PE","Pernambuco");
        states.put("PI","Piauí");
        states.put("RJ","Rio de Janeiro");
        states.put("RN","Rio Grande do Norte");
        states.put("RS","Rio Grande do Sul");
        states.put("RO","Rondônia");
        states.put("RR","Roraima");
        states.put("SC","Santa Catarina");
        states.put("SP","São Paulo");
        states.put("SE","Sergipe");
        states.put("TO","Tocantins");
    }
    
    public SubtitlesController() 
    {
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
    
    @RequestMapping(value = { "/" }, method = RequestMethod.GET)
    public String root()
    {
    	return "redirect:/legendas";
    }

    @RequestMapping(value = { "/login" }, method = RequestMethod.GET)
    public String login()
    {
    	return "login";
    }
    
    @RequestMapping(value={"/legendas/logout"}, method=RequestMethod.GET)
    public String logout(Model model, HttpSession session) 
    {
        return "redirect:/logout"; 
        //a configuracao do lougout na classe WebSecurityconfig faz um redirect para /legendas 
    }
    
    @RequestMapping(value={"/legendas/erro"}, method=RequestMethod.GET)
    public String errorTeste(Model model) 
    {
        throw new BadRequestException("Teste da pagina de erro");
    }
    

    
    @RequestMapping(value={"/legendas","/legendas/login"}, method=RequestMethod.GET)
    public String home(Model model, HttpServletRequest request) 
    {
        try
        {
            Principal p = request.getUserPrincipal();
            
            if (p!= null)
            {
            	System.out.println("Principal NOT NULL");
            	var b = request.isUserInRole("worker");
            	System.out.println("Principal in role: "+b);
            }
            
            if (p !=  null && request.isUserInRole("worker") || request.isUserInRole("admin"))
            {
                String email = p.getName();
                //verificar se o usuario possui bloco alocado
                int idBlock = BlockDao.hasAllocatedBlock(email);
                
                if (idBlock > 0)
                {
                    model.addAttribute("allocated_block", idBlock);
                }
                
                ArrayList<FinishedBlocks> finishedBlocks = BlockDao.listFinishedBlocks(email);
                model.addAttribute("finishedBlocks", finishedBlocks);
                
                return "subtitles/user";
            }
        }
        catch (DaoException e)
        {
            logger.error(e);
            //deixa pra la
        }
        
        return "subtitles/home";
    }
    
    @RequestMapping(value={"/legendas/register"}, method=RequestMethod.GET)
    public String register(Model model) 
    {
        model.addAttribute("user", new User());
        return "register-form";
    } 

    @RequestMapping(value = { "/legendas/register" }, method = RequestMethod.POST)
    public String registerPost(Model model, @Valid User user, BindingResult result)
    {
//        for (Object object : result.getAllErrors())
//        {
//            if (object instanceof FieldError)
//            {
//                FieldError fieldError = (FieldError) object;
//                System.out.println(fieldError.getField() + ":" + fieldError.getCode());
//            }
//            if (object instanceof ObjectError)
//            {
//                ObjectError objectError = (ObjectError) object;
//            }
//        }

        //PrintBean.print(user, System.out);
        
        if (result.hasErrors())
        {
            return "register-form";
        }

        try
        {
            CoreBusines.geteInstance().regiterUser(user);
            return "redirect:/s/legendas/register-resp";
        }
        catch (Exception e)
        {
            throw new InternalErrorException(e);
        }
    }

    @RequestMapping(value={"/legendas/register-resp"}, method=RequestMethod.GET)
    public String registerResp(Model model) 
    {
        return "register-resp";
    }
    
    
    @RequestMapping(value={"/legendas/confirm"}, method=RequestMethod.GET)
    public String confirm(Model model, @RequestParam("token") String token, HttpServletResponse response) 
    {
        //obter dados do token no banco
        UserTemp ut;
        try
        {
            ut = UserTempDao.get(token);
        }
        catch (NotFoundException e)
        {
            logger.warn("tentativa de confirmar token nao encontrado: " + token);
            throw new BadRequestException(e);
        }
        catch (DaoException e)
        {
            throw new InternalErrorException(e);
        }
        
        ut.setName("");
        ut.setEmail(""); //precisa confirmar o email
        ut.setPwd("");  //precisa confirmar a senha
        model.addAttribute("user", ut);
        return "confirm-form";
    } 

    @RequestMapping(value={"/legendas/confirm"}, method=RequestMethod.POST)
    public String confirm(@ModelAttribute("user") UserTemp user, BindingResult result, Model model, HttpServletRequest request) 
    {
        try
        {
            //Verificar se o usuario est� logado
            if (request.getUserPrincipal() !=  null || request.getRemoteUser() != null)
            {
                logger.warn("usuario logado ["+request.getUserPrincipal()+"] tentando confirmar token ["+user.getToken()+"]");
                throw new BadRequestException();
            }
            
            //obter dados do token no banco
            UserTemp stored;
            try
            {
                stored = UserTempDao.get(user.getToken());
            }
            catch (NotFoundException e)
            {
                logger.warn("tentativa de confirmar token nao encontrado: " + user.getToken());
                throw new BadRequestException(e);
            }
            catch (DaoException e)
            {
                throw new InternalErrorException(e);
            }
            
            if (user.getEmail().trim().equals(stored.getEmail()) == false)
            {
                logger.warn("tentativa de confirmar token [" + user.getToken() + "] com email invalido ["+user.getEmail()+"]");
                //throw new BadRequestException();
                
                user.setPwd("");
                model.addAttribute("user", user);
                result.rejectValue("email", "email", "Email incorreto."); //!!!! NAO funciona sem a declaracao "@ModelAttribute("user")"  acima.
                return "confirm-form";
            }
            
            //verificar senha
            String pwdhash = null;
            if (user.getPwd() != null ) 
            {
                pwdhash = DigestUtils.sha512Hex(user.getPwd());
            }
                
            if (pwdhash == null || pwdhash.equals(stored.getPwd()) == false)
            {
                if (stored.getAcessos() == 300)
                {
                    UserTempDao.delete(stored.getToken()); //remover token do banco.
                    logger.warn("Excesso de tentativa de confirmar token [" + user.getToken() + "]");
                    throw new BadRequestException();                
                }
  
                //atualizar a base para indicar nota tentativa de acesso
                UserTempDao.setAcessos(stored.getToken(), stored.getAcessos()+1);
                
                //enviar erro de senha invalida
                user.setPwd("");
                model.addAttribute("user", user);
                result.rejectValue("pwd", "pwd", "Senha Incorreta."); //!!!! NAO funciona sem a declaracao "@ModelAttribute("user")"  acima.
                //result.addError(new FieldError("user", "pwd", "vamo meu"));
                
                return "confirm-form";
            }

            //ok a senha confere.
            
            CoreBusines.geteInstance().confirmUser(stored);
            //Logar o usuario
            request.login(user.getEmail(), user.getPwd());
            //redirecionar para home login
            return "redirect:/s/legendas";
        }
        catch (Exception e)
        {
            throw new InternalErrorException("falha inesperada",e);
        }
    }     
    


    
    @RequestMapping(value={"/legendas/login/account"}, method=RequestMethod.GET)
    public String account(Model model, HttpServletRequest request) 
    {
        try
        {
            //Verificar se o usuario est� logado
            Principal principal = request.getUserPrincipal(); 
            if (principal ==  null)
            {
                logger.warn("usuario NAO logado tentando ver a pagina de contas.");
                throw new BadRequestException();
            }
            
            User user = UserDao.get(principal.getName());
            user.setPwd("");
            model.addAttribute("user", user);
            model.addAttribute("states", states);
            return "account-form";
        }
        catch (Exception e)
        {
            throw new InternalErrorException("falha inesperada",e);
        }
    }
    
    @RequestMapping(value={"/legendas/login/account"}, method=RequestMethod.POST)
    public String accountPost(Model model, HttpServletRequest request, @Valid User user, BindingResult result) 
    {
        if (result.hasErrors())
        {
            logger.debug("erro no formulario");
            
//          for (Object object : result.getAllErrors())
//          {
//              if (object instanceof FieldError)
//              {
//                  FieldError fieldError = (FieldError) object;
//                  System.out.println(fieldError.getField() + ":" + fieldError.getCode());
//              }
//              if (object instanceof ObjectError)
//              {
//                  ObjectError objectError = (ObjectError) object;
//                  System.out.println(objectError.getObjectName() + ":" + objectError.getCode());
//              }
//          }
            
            model.addAttribute("states", states);
            
            return "account-form";
        }

        try
        {
            UserDao.update(user);
            return "redirect:/s/legendas/login/account?message=1";
        }
        catch (Exception e)
        {
            logger.error("falha atualizando usuario", e);
            throw new InternalErrorException(e);
        }
    }
    
    @RequestMapping(value={"/legendas/login/change-pwd"}, method=RequestMethod.GET)
    public String changePwd(Model model) 
    {
        model.addAttribute("pwd", new PwdChange());
        return "change-pwd-form";
    }
    
    @RequestMapping(value={"/legendas/login/change-pwd"}, method=RequestMethod.POST)
    public String changePwdPost(Model model,@Valid @ModelAttribute PwdChange pwd, BindingResult result, HttpServletRequest request) 
    {
        if (result.hasErrors())
        {
            pwd.reset();
            logger.debug("erro no formulario");
            return "change-pwd-form";
        }
        
        if (pwd.getNewpwd1().equals(pwd.getNewpwd2()) == false)
        {
            pwd.reset();
            result.rejectValue("newpwd2", "newpwd2", "Senhas n�o conferem."); //!!!! NAO funciona sem a declaracao "@ModelAttribute("user")"  acima.
            return "change-pwd-form";
        }
                
        Principal principal = request.getUserPrincipal(); 
        if (principal ==  null)
        {
            logger.warn("usuario NAO logado tentando alterar senha.");
            throw new BadRequestException();
        }
        
        User user;
        try
        {
            user = UserDao.get(principal.getName());
        }
        catch (NotFoundException e)
        {
            logger.warn("tentativa de alterar senha de usuario nao encontrado: " + principal.getName());
            throw new BadRequestException(e);
        }
        catch (DaoException e)
        {
            throw new InternalErrorException(e);
        }
         
        //verificar senha
        String pwdhash = DigestUtils.sha512Hex(pwd.getPwd());
            
        if (pwdhash == null || pwdhash.equals(user.getPwd()) == false)
        {
            //enviar erro de senha invalida
            pwd.reset();
            result.rejectValue("pwd", "pwd", "Senha Incorreta."); //!!!! NAO funciona sem a declaracao "@ModelAttribute("user")"  acima.
            //result.addError(new FieldError("user", "pwd", "vamo meu"));
            
            return "change-pwd-form";
        }

        //ok a senha confere.
        try
        {
            UserDao.setPwd(principal.getName(), pwd.getNewpwd1());
            return "redirect:/s/legendas/login/change-pwd?message=1";
        }
        catch (NotFoundException e)
        {
            logger.warn("tentativa de alterar senha de usuario nao encontrado: " + principal.getName());
            throw new BadRequestException(e);
        }
        catch (DaoException e)
        {
            throw new InternalErrorException(e);
        }        
    }    
    
    @RequestMapping(value={"/legendas/reset-pwd"}, method=RequestMethod.GET)
    public String resetPwd(Model model) 
    {
        model.addAttribute("email", new Email());
        return "reset-pwd-form";
    }
    
    @RequestMapping(value={"/legendas/reset-pwd"}, method=RequestMethod.POST)
    public String resetPwdPost(Model model, @Valid Email email, BindingResult result) 
    {
        if (result.hasErrors())
        {
            logger.debug("erro no formulario");
            return "reset-pwd-form";
        }
        
        //verificar existencia do email
        try
        {
            UserDao.get(email.getEmail());
        }
        catch (Exception e)
        {
            logger.warn("tentatica de reset de senha de email inesistente ["+email+"].");
            //nao indicar erro ao usuario
            return "redirect:/s/legendas/reset-pwd?message=1";
        }
        
        try
        {
            CoreBusines.geteInstance().sendResetPwdEmail(email.getEmail());
        }
        catch(Exception e)
        {
            logger.warn("Falhas enviando email de reset de senha: "+ e.getMessage(), e);
            throw new InternalErrorException(e);
        }
        return "redirect:/s/legendas/reset-pwd?message=1";
    }
        
    @RequestMapping(value={"/legendas/reset-confirm"}, method=RequestMethod.GET)
    public String resetConfirm(Model model, @RequestParam("token") String token) 
    {
        //preparar o token
        String htoken = DigestUtils.sha512Hex(token);
        
        //obter dados do token no banco
        ResetToken rt;
        try
        {
            rt = ResetTokenDao.get(htoken);
        }
        catch (NotFoundException e)
        {
            logger.warn("tentativa de reset com token nao encontrado: " + token);
            throw new BadRequestException(e);
        }
        catch (DaoException e)
        {
            throw new InternalErrorException(e);
        }
        
        //verificar se o token est� expirado por tempo.
        long diff = System.currentTimeMillis() - rt.getSysCreationDate().getTime();
        if (diff > 24*60*60*1000)
        {
            logger.warn("tentativa de reset com token EXPIRADO: " + token);
            throw new BadRequestException();
        }
        
        PwdReset pwd = new PwdReset();
        pwd.setToken(token);
        
        model.addAttribute("pwd", pwd);
        return "reset-confirm-form";
    }
    
    @RequestMapping(value={"/legendas/reset-confirm"}, method=RequestMethod.POST)
    public String resetConfirmPost(Model model, @Valid @ModelAttribute("pwd")  PwdReset pwd, BindingResult result, HttpServletRequest request) 
    {
        if (result.hasErrors())
        {
            logger.debug("erro no formulario");
            return "reset-confirm-form";
        }
        
        if (pwd.getNewpwd1().equals(pwd.getNewpwd2()) == false)
        {
            result.rejectValue("newpwd2", "newpwd2", "Senhas n�o conferem."); //!!!! NAO funciona sem a declaracao "@ModelAttribute("user")"  acima.
            return "reset-confirm-form";
        }
        
        Principal principal = request.getUserPrincipal(); 
        if (principal !=  null)
        {
            logger.warn("usuario logado tentando reset senha.");
            throw new BadRequestException();
        }
        
        //preparar o token
        String htoken = DigestUtils.sha512Hex(pwd.getToken());
        
        //obter dados do token no banco
        ResetToken rt;
        try
        {
            rt = ResetTokenDao.get(htoken);
        }
        catch (NotFoundException e)
        {
            logger.warn("tentativa de reset com token nao encontrado: " + pwd.getToken());
            throw new BadRequestException(e);
        }
        catch (DaoException e)
        {
            throw new InternalErrorException(e);
        }
        
        try
        {
            UserDao.get(rt.getEmail());
        }
        catch (NotFoundException e)
        {
            logger.warn("tentativa de alterar senha de usuario nao encontrado: " + rt.getEmail());
            throw new BadRequestException(e);
        }
        catch (DaoException e)
        {
            throw new InternalErrorException(e);
        }
        
        try
        {
            UserDao.setPwd(rt.getEmail(), pwd.getNewpwd1());
            ResetTokenDao.delete(htoken);
            return "redirect:/s/legendas/reset-confirm-resp";
        }
        catch (NotFoundException e)
        {
            logger.warn("falha tentando alterar senha de usuario: " + rt.getEmail());
            throw new BadRequestException(e);
        }
        catch (DaoException e)
        {
            throw new InternalErrorException(e);
        }        
    }   
    
    @RequestMapping(value={"/legendas/reset-confirm-resp"}, method=RequestMethod.GET)
    public String resetConfirm(Model model) 
    {
        return "reset-confirm-resp";
    }
}

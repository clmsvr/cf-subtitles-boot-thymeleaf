package cms.cf.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;


/**
 * CoreInitializer
 * 
 * Implementations of the interface ServletContextListener 
 * receive notifications about changes to the servlet 
 * context of the web application they are part of. 
 * 
 * To receive notification events, 
 * the implementation class must be configured in the 
 * deployment descriptor for the web application.
 * 
 * The lifecycle event listeners are defined using 
 * <listener> tags. The order you define the listener 
 * classes does matter. They will be executed in the 
 * order you specify. 
 * 
 * Note: There are a few items to notice. 
 *       The listener tag comes before the servlet definitions 
 *       in the web.xml file.
 * 
 * Exemplo:
 *  
 *  <web-app>
 *  
 *  <!-- Define application events listeners -->
 *  <listener>
 *  <listener-class>
 *      web.ContextInitializer
 *  </listener-class>
 *  </listener>
 *  
 *  ...
 *  
 *  </web-app>
 */
public class ContextInitializer implements ServletContextListener
{    
    /**
     * Notification that the web application initialization 
     * process is starting. 
     * All ServletContextListeners are notified of 
     * context initialization before any filter or 
     * servlet in the web application is initialized.
     */
    public void contextInitialized(ServletContextEvent event)
    {
        try
        {   
            /* identificacao de diretorio de configuracao */
            String setupDir = initConfigDir(event);          
            /* inicializacao correta do log4j */
            initLog4j();
            
            Logger logger = Logger.getLogger(ContextInitializer.class); 
            logger.info("INIT: Setup Dir: '"+setupDir+"'");
            logger.info("INIT: log4j inicializado.");
            logger.info("INIT: Contexto Web inicializado. OK !");
            
            /* inicializacao especifica da aplicacao*/
            CoreBusines.geteInstance().startCleanUpThread();
        }
        catch(Exception e)
        {
            Logger logger = Logger.getLogger(ContextInitializer.class);  
            logger.error("Inicializacao FALHOU. Erro: "+e.toString(),e);  
            throw new RuntimeException("INIT Fail: "+e.toString());
        }        
    }
    

    /**
     * Notification that the servlet context is about 
     * to be shut down. 
     * All servlets and filters have been destroy()ed before 
     * any ServletContextListeners are notified of 
     * context destruction.
     */
    public void contextDestroyed(ServletContextEvent event)
    {
        Logger logger = Logger.getLogger(ContextInitializer.class);
        try
        {  
            logger.info("SHUTDOWN do Contexto Web!");    
            
            /* especifica da aplicacao*/
            CoreBusines.geteInstance().stopCleanUpThread();
        }
        catch(Throwable e)
        {
            logger.error("SHUTDOWN ERROR: "+e.toString(),e);           
        }  
    }

    /**
     * Verifica se o diretorio de arquivos de configura��es foi definido
     * nas variaveis de configura��o:
     * 
     * - "cms.web.CONFIG_DIR : indica o diretorio onde se encontram 
     * os arquivos de configuracao da aplicacao.
     * 
     * - "cms.web.SYSTEM_PROPERY_TO_CONFIG_DIR" : indica o nome da 
     * propriedade usada na inicializacao do servidor (jvm) que indica 
     * diretorio onde se encontram os arquivos de configuracao da aplicacao.
     * 
     *  @param event
     *  @return
     */
    private String initConfigDir(ServletContextEvent event)
    {
        String setupDir = event.getServletContext().getInitParameter(
                ConfigHelper.CONFIG_DIR);
        if (setupDir == null || setupDir.trim().equals(""))
        {
            String tmp = event.getServletContext().getInitParameter(
                    ConfigHelper.SYSTEM_PROPERY_TO_CONFIG_DIR);
            if (tmp != null && !tmp.trim().equals(""))
            {
                setupDir = System.getProperty(tmp);
            }
        }

        if (setupDir == null || setupDir.trim().equals(""))
        {
            return null;
        }
        else
        {
            System.setProperty(ConfigHelper.CONFIG_DIR, setupDir);           
            return setupDir;
        }
    }
    
    /**
     * Busca o arquivo de configura��o do Log4j e inicializa a configura��o.
     * 
     * @throws IOException
     */
    private void initLog4j() 
    throws IOException
    {
        String log4jfile = "log4j.properties";
        
        /* busca no diretorio de configuracao */
        InputStream is = null;
        try
        {
            is = ConfigHelper.getConfigFileStream(log4jfile,false);
            Properties props = new Properties();
            props.load(is);
            /* Atribuir configura��o*/
            if (props.size() > 0 )
            {
                PropertyConfigurator.configure(props);
            }
            Logger logger = Logger.getLogger(ContextInitializer.class); 
            logger.info("INIT: O  arquivo: '"+log4jfile+"' foi encontrado no diretorio de configuracao.");
            return;
        }
        catch (IOException e) 
        {
            System.out.println("O arquivo '"+log4jfile+"' nao foi encontrado no diretorio de configuracao. Buscando no CLASSPATH...");
        }
        finally
        {
            try{if ( is != null )is.close();}catch (Exception e){}
        } 

        /* Busca no CLASSPATH da aplicacao */
        Properties props = ConfigHelper.getClassPathFileProperties(log4jfile);
        /* Atribuir configura��o*/
        if (props.size() > 0 )
        {
            PropertyConfigurator.configure(props);
        }  
        Logger logger = Logger.getLogger(ContextInitializer.class); 
        logger.info("INIT: O  arquivo: '"+log4jfile+"' foi encontrado no CLASSPATH da aplicacao.");
    }
    
    
}

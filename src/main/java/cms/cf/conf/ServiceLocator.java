package cms.cf.conf;

import java.util.HashMap;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ServiceLocator
{
    private static ServiceLocator       obj = null;
    private HashMap<String, DataSource> cache;

    public static ServiceLocator getInstance()
    {
        if (obj == null)
        {
            synchronized (ServiceLocator.class)
            {
                if (obj == null) obj = new ServiceLocator();
            }
        }
        return obj;
    }

    private ServiceLocator()
    {
        this.cache = new HashMap<String, DataSource>();
    }

    public DataSource getDataSource(String dataSourceName) throws NamingException
    {
        DataSource datasource = null;
        if (this.cache.containsKey(dataSourceName))
        {
            datasource = (DataSource) this.cache.get(dataSourceName);
        }
        else
        {
            datasource = getds(dataSourceName, null);
            this.cache.put(dataSourceName, datasource);
        }
        return datasource;
    }
    
    private static javax.sql.DataSource getds(String name, Properties properties) 
    throws NamingException
    {
        Context jndiContext = new InitialContext(properties);
        if (name.startsWith("java:/comp/env"))
        {
            return (javax.sql.DataSource) jndiContext.lookup(name);
        }
        else
        {
            try
            {
                return (javax.sql.DataSource) jndiContext.lookup(name);
            }
            catch (Exception e)
            {
            }
            return (javax.sql.DataSource) jndiContext.lookup("java:/comp/env/"+name);
        }
        
    }
}
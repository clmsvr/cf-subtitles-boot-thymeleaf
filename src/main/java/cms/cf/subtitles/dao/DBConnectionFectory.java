package cms.cf.subtitles.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import cms.cf.conf.ConfigHelper;

public class DBConnectionFectory {

    /**
     * Retorna uma conex�o com o banco de dados de acordo com os dados definidos 
     * no arquivo "datasource.ini" procurado no CLASSPATH.
     * 
     * @return Connection
     * @throws DaoException
     */
    protected static Connection getLocalConnection() throws DaoException
    {
        try
        {
            Properties props = ConfigHelper.getConfigFileProperties("datasource.ini", true);            
            
            String user = props.getProperty("jdbc.user") ;
            String pwd = ConfigHelper.decode64(props.getProperty("jdbc.pwd"));
            String url = props.getProperty("jdbc.url");
            String driver = props.getProperty("jdbc.driver");
    
            Class.forName(driver).newInstance();
            //DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            return conn;
        }
        catch (Exception ex)
        {
            throw new DaoException(ex.getMessage(), ex);
        }
    }
    
}

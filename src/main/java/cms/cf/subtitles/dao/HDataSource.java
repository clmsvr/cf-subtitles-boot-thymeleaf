package cms.cf.subtitles.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import cms.cf.conf.ConfigHelper;

public class HDataSource {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    
    static {
    	
        try {
			Properties props = ConfigHelper.getClassPathFileProperties("datasource.ini");
			String user = props.getProperty("jdbc.user");
			String pwd = ConfigHelper.decode64(props.getProperty("jdbc.pwd"));
			String url = props.getProperty("jdbc.url");
			String driver = props.getProperty("jdbc.driver");
			
			config.setDriverClassName(driver);
			config.setJdbcUrl(url);
			config.setUsername(user);
			config.setPassword(pwd);
			config.setMinimumIdle(1);
			config.addDataSourceProperty("cachePrepStmts", "true");
			config.addDataSourceProperty("prepStmtCacheSize", "250");
			config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
			config.setAutoCommit(false);
			
			/*
			autoCommit
			connectionTimeout
			idleTimeout
			maxLifetime
			connectionTestQuery
			connectionInitSql
			validationTimeout
			maximumPoolSize
			poolName
			allowPoolSuspension
			readOnly
			transactionIsolation
			leakDetectionThreshold			
			 */
			
			ds = new HikariDataSource(config);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

    private HDataSource() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
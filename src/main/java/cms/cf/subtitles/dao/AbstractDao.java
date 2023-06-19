package cms.cf.subtitles.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.sql.DataSource;

public abstract class AbstractDao
{
    private static final String OVERFLOW_MSG = "ID-OVEFLOW";

    protected AbstractDao()
    {
    }

    public static Connection getConnection() throws DaoException {
    	
    	//return DBConnectionFectory.getLocalConnection();
    	
    	try {
			return HDataSource.getConnection();
			
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}
	
    public static void setTestMode() 
    {	
	}    
	
    private static Connection getConnection(DataSource datasource) throws DaoException
    {
        try
        {
            Connection conn = datasource.getConnection();

            if (conn.getAutoCommit() == true)
                throw new DaoException("JDBC DATASOURCE AUTOCOMMIT set to TRUE !!!!") ;
            
            return conn;
        }
        catch (Exception ex)
        {
            throw new DaoException(ex.getMessage(), ex);
        }
    }

    public static void closeConnection(Connection conn)
    {
        try
        {
            if (conn != null) conn.close();
        }
        catch (Exception e)
        {
            conn = null;
        }
    }
    
    public static void rollbackConnection(Connection conn)
    {
        try
        {
            if (conn != null) conn.rollback();
        }
        catch (Exception e)
        {
            conn = null;
        }
    }
    
    protected static void closeConnection(Connection conn, Statement ps, ResultSet rs)
    {
        try
        {
            if (rs != null) rs.close();
        }
        catch (Exception e)
        {
            rs = null;
        }

        try
        {
            if (ps != null) ps.close();
        }
        catch (Exception e)
        {
            ps = null;
        }

        try
        {
            if (conn != null) conn.close();
        }
        catch (Exception e)
        {
            conn = null;
        }
    }

    protected static void closeConnection(Connection conn, Statement ps)
    {
        try
        {
            if (ps != null) ps.close();
        }
        catch (Exception e)
        {
            ps = null;
        }

        try
        {
            if (conn != null) conn.close();
        }
        catch (Exception e)
        {
            conn = null;
        }
    }

    /**
     * Retorna campo do ResultSet como String n�o nulla
     */
    protected static String getString(ResultSet rs, String campo) throws SQLException
    {
        String value = rs.getString(campo);
        if (value != null) return value.trim();
        return "";
    }
    
    /**
     * Retorna campo do ResultSet como String n�o nulla
     */
    protected static String getString(ResultSet rs, int campo) throws SQLException
    {
        String value = rs.getString(campo);
        if (value != null) return value.trim();
        return "";
    }

    /**
     * Retorna campo do ResultSet como Char.
     */
    protected static char getChar(ResultSet rs, String campo) throws SQLException
    {
        String value = rs.getString(campo);
        if (value != null) return value.charAt(0);
        return '?';
    }

    /**
     * Converte java.util.Date para java.sql.Date
     */
    protected static java.sql.Timestamp getSQLDate(java.util.Date date)
    {
        if (date != null) return new java.sql.Timestamp(date.getTime());
        return null;
    }
    
    protected static void setTimestampNullValue(PreparedStatement preparedStatement, int index) throws SQLException
    {
        preparedStatement.setNull(index, Types.TIMESTAMP);
    }

    protected static void setTimestampValue(PreparedStatement preparedStatement, int index, Date object) throws SQLException
    {
        if (object == null)
        {
            setTimestampNullValue(preparedStatement, index);
        }
        else
        {
            preparedStatement.setTimestamp(index, new Timestamp(object.getTime()));
        }
    }

    protected static void setMoney(PreparedStatement preparedStatement, int index, Money obj) throws SQLException
    {
        if (obj == null)
        {
            setTimestampNullValue(preparedStatement, index);
        }
        else
        {
            preparedStatement.setBigDecimal(index, obj.getAmount());
        }
    }
    
    protected static Money getMoney(ResultSet rs, String fieldname) throws SQLException
    {
        //BigDecimal v = rs.getBigDecimal(fieldname);
        String v = rs.getString(fieldname);
        if (v == null) 
            return null;
        else
            return new Money(v.toString());
    }
    
    /**
     * identado � esquerda
     */
    public static String getStringAsCharField(String value, int fieldLen)
    {
        char[] blanks = new char[fieldLen];
        Arrays.fill(blanks, ' ');
        StringBuffer bf = new StringBuffer();
        bf.append(value);
        bf.append(blanks);
        return bf.toString().substring(0, fieldLen);
    }

    /**
     * R -> Identado � direita
     */
    public static String getStringAsCharFieldR(String value, int fieldLen)
    {
        char[] blanks = new char[fieldLen];
        Arrays.fill(blanks, ' ');
        StringBuffer bf = new StringBuffer();
        bf.append(blanks);
        bf.append(value);
        return bf.toString().substring(bf.length()-fieldLen, bf.length());
    }

    public static String restrictLength(String value, int maxlen)
    {
        if (maxlen <= 0) return null;

        if (value != null && value.length() > maxlen)
        {
            value = value.substring(0, maxlen);
        }
        return value;
    }
    
    protected static String trim(String value)
    {
        if (value != null)
            return value.trim();
        else
            return null;
    }

    protected static String verifyOverflow(String value, int maxlen)
    {
        if (maxlen <= 0) return null;

        if (value != null && value.length() > maxlen)
        {
            value = OVERFLOW_MSG;
        }
        return value;
    }

    /**
     * Obt�m dia da semana de uma data.
     */
    protected static int getDayOfWeek(Date data)
    {
        if (data == null) return 0;

        GregorianCalendar cal1 = new GregorianCalendar();

        cal1.setTime(data);
        return cal1.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Obt�m hora e minuto de uma data.
     * @param data
     *            - Data que representa hora e minuto
     * @return int - Hora e Minuto no formato (hhmm)
     */
    protected static int getHourAndMinute(Date data)
    {
        if (data == null) return 0;

        GregorianCalendar cal1 = new GregorianCalendar();

        cal1.setTime(data);
        int hour = cal1.get(Calendar.HOUR_OF_DAY);
        int minute = cal1.get(Calendar.MINUTE);

        return (hour * 100 + minute);
    }
    
    /**
     * @param refdate_ data format YYYYMMDD
     * @return
     */
    protected static String subtractOneDay(String refdate)
    {
        if (refdate == null || refdate.length() != 8) return "error-"+refdate;
        int ref = Integer.parseInt(refdate);
        int y = ref/10000;
        int m = (ref%10000)/100;
        int d = (ref%10000)%100;
        GregorianCalendar c = new GregorianCalendar();
        c.set(y, m-1, d);
        //System.out.println(c.getTime().toString());
        c.add(Calendar.DAY_OF_MONTH, -1);
        //System.out.println(c.getTime().toString());
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
        
        return formater.format(c.getTime());
    }

}
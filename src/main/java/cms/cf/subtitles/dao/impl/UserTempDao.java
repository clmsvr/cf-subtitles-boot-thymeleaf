package cms.cf.subtitles.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import cms.cf.subtitles.dao.AbstractDao;
import cms.cf.subtitles.dao.DaoException;
import cms.cf.subtitles.dao.NotFoundException;
import cms.cf.subtitles.dao.vo.UserTemp;

public class UserTempDao extends AbstractDao
{ 
    static UserTemp set(ResultSet rs) throws SQLException
    {
        UserTemp vo = new UserTemp();

        vo.setToken(rs.getString("token"));
        vo.setEmail(rs.getString("email"));
        vo.setPwd(rs.getString("pwd"));
        vo.setName(rs.getString("name"));
        vo.setAcessos(rs.getInt("acessos"));
        vo.setEmailSent(rs.getBoolean("email_sent"));
        vo.setSysCreationDate(rs.getTimestamp("sys_creation_date"));
        
        return vo;
    }
    
    
    public static UserTemp get(String token) 
    throws NotFoundException, DaoException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            return get(conn, token);            
        }
        catch(DaoException e)
        {
            throw e;
        }
        finally
        {
            closeConnection(conn);
        }
    }
    
    private static String getsql = "SELECT * FROM user_temp WHERE token = ?";
    
    public static UserTemp get(Connection conn, String token) 
    throws NotFoundException, DaoException
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try
        {
            ps = conn.prepareStatement(getsql);
            ps.setString(1, token);
            
            rs = ps.executeQuery();
            if (!rs.next())
            {
                throw new NotFoundException("Object not found [" + token + "]");
            }
            
            UserTemp u = set(rs);
            
            return u;
        }
        catch (SQLException e)
        {
            throw new DaoException(e.getMessage(),e);
        }
        finally
        {
            closeConnection(null, ps,rs);
            ps = null;
            rs = null;
        }
    }
    
    public static ArrayList<UserTemp> list() 
    throws DaoException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            return list(conn);
        }
        catch(DaoException e)
        {
            throw e;
        }
        finally
        {
            closeConnection(conn);
            conn = null;
        }
    }

    
    private static String listsql = "SELECT * FROM user_temp u order by email";
    
    public static ArrayList<UserTemp> list(Connection conn) 
    throws DaoException
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try
        {
            ps = conn.prepareStatement(listsql);

            rs = ps.executeQuery();
            if (!rs.next())
            {
                return new ArrayList<UserTemp>();
            }
            ArrayList<UserTemp> list = new ArrayList<UserTemp>();
            do
            {
                UserTemp o = set(rs);
                list.add(o);
            }
            while (rs.next());
            
            return list;
        }
        catch (SQLException e)
        {
            throw new DaoException(e.getMessage(),e);
        }
        finally
        {
            closeConnection(null, ps, rs);
            ps = null;
            rs = null;
        }
    }
    
    
    public static void insert(UserTemp vo) throws DaoException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            insert(conn, vo);
            
            //COMMIT
            conn.commit();
            return;
        }
        catch (SQLException e)
        {
            throw new DaoException(e.getMessage(),e);
        }
        catch(DaoException e)
        {
            throw e;
        }
        finally
        {
            closeConnection(conn);
            conn = null;
        }
    }
    
    
    private static String insertsql = 
            "INSERT INTO user_temp(token, email, pwd, name, acessos, email_sent, sys_creation_date) "
             + " VALUES(?, ?, sha2(?,512), ?, ?, ?, sysdate())";
    
    public static void insert(Connection conn, UserTemp vo) throws DaoException
    {
        PreparedStatement ps = null;
        
        try
        {
            ps = conn.prepareStatement(insertsql);//, PreparedStatement.RETURN_GENERATED_KEYS);

            ps.setString(1, vo.getToken());
            ps.setString(2, vo.getEmail());
            ps.setString(3, vo.getPwd());
            ps.setString(4, vo.getName());
            ps.setInt(5, vo.getAcessos());
            ps.setBoolean(6, vo.isEmailSent());
            
            ps.executeUpdate();
            
            return;
        }
        catch (SQLException e)
        {
            try{conn.rollback();} catch (Exception e1){}
            throw new DaoException(e.getMessage(),e);
        }
        finally
        {
            //closeConnection(null, ps, rs);
            ps = null;
        }
    }
    
    
    public static void delete(String token) throws DaoException, NotFoundException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            delete(conn, token);
            
            //COMMIT
            conn.commit();
        }
        catch (SQLException e)
        {
            throw new DaoException(e.getMessage(),e);
        }
        catch(DaoException e)
        {
            throw e;
        }
        finally
        {
            closeConnection(conn);
            conn = null;
        }
    }
    
    
    private static String deletesql= "DELETE FROM user_temp WHERE token = ?  ";
    
    public static void delete(Connection conn, String token) throws DaoException, NotFoundException
    {
        PreparedStatement ps = null;

        try
        {
            ps = conn.prepareStatement(deletesql);

            ps.setString(1, token);
            int count = ps.executeUpdate();
            
            if (count == 0 )
            {
                throw new NotFoundException("Object not found ["+token+"] .");
            }  
        }
        catch (SQLException e)
        {
            try{conn.rollback();} catch (Exception e1){}
            throw new DaoException(e.getMessage(),e);
        }
        finally
        {
            closeConnection(null,ps);
            ps = null;
        }
    }
    
    public static void setAcessos(String token, int acessos) throws DaoException, NotFoundException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            setAcessos(conn, token, acessos);
            
            //COMMIT
            conn.commit();
        }
        catch (SQLException e)
        {
            throw new DaoException(e.getMessage(),e);
        }
        catch(DaoException e)
        {
            throw e;
        }
        finally
        {
            closeConnection(conn);
            conn = null;
        }
    }
    
    private static String updateAcessossql = 
            "UPDATE user_temp SET acessos = ? WHERE token = ? ";
    
    public static void setAcessos(Connection conn, String token, int acessos) throws DaoException, NotFoundException
    {
        PreparedStatement ps = null;

        try
        {
            ps = conn.prepareStatement(updateAcessossql);

            ps.setInt   (1, acessos);
            ps.setString(2, token);

            int count = ps.executeUpdate();
            
            if (count == 0 )
            {
                throw new NotFoundException("Object not found ["+token+"] .");
            }  
            
            //SEM COMMIT
        }
        catch (SQLException e)
        {
            try{conn.rollback();} catch (Exception e1){}
            throw new DaoException(e.getMessage(),e);
        }
        finally
        {
            closeConnection(null, ps);
            ps = null;
        }
    }


    public static void setEmailSent(String token, boolean emailSent) throws DaoException, NotFoundException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            setEmailSent(conn, token, emailSent);
            
            //COMMIT
            conn.commit();
        }
        catch (SQLException e)
        {
            throw new DaoException(e.getMessage(),e);
        }
        catch(DaoException e)
        {
            throw e;
        }
        finally
        {
            closeConnection(conn);
            conn = null;
        }
    }
    
    private static String updateEmailSentSql = 
            "UPDATE user_temp SET email_sent = ? WHERE token = ? ";
    
    public static void setEmailSent(Connection conn, String token, boolean emailSent) throws DaoException, NotFoundException
    {
        PreparedStatement ps = null;

        try
        {
            ps = conn.prepareStatement(updateEmailSentSql);

            ps.setBoolean(1, emailSent);
            ps.setString (2, token);

            int count = ps.executeUpdate();
            
            if (count == 0 )
            {
                throw new NotFoundException("Object not found ["+token+"] .");
            }  
            
            //SEM COMMIT
        }
        catch (SQLException e)
        {
            try{conn.rollback();} catch (Exception e1){}
            throw new DaoException(e.getMessage(),e);
        }
        finally
        {
            closeConnection(null, ps);
            ps = null;
        }
    }


}

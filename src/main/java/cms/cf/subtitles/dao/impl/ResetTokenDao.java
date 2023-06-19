package cms.cf.subtitles.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import cms.cf.subtitles.dao.AbstractDao;
import cms.cf.subtitles.dao.DaoException;
import cms.cf.subtitles.dao.NotFoundException;
import cms.cf.subtitles.dao.vo.ResetToken;

public class ResetTokenDao extends AbstractDao
{ 
    static ResetToken set(ResultSet rs) throws SQLException
    {
        ResetToken vo = new ResetToken();

        vo.setToken(rs.getString("token"));
        vo.setEmail(rs.getString("email"));
        vo.setSysCreationDate(rs.getTimestamp("sys_creation_date"));
        
        return vo;
    }
    
    
    public static ResetToken get(String token) 
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
    
    private static String getsql = "SELECT * FROM reset_token WHERE token = ?";
    
    public static ResetToken get(Connection conn, String token) 
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
            
            ResetToken u = set(rs);
            
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
    
    public static ArrayList<ResetToken> list() 
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

    
    private static String listsql = "SELECT * FROM reset_token order by email";
    
    public static ArrayList<ResetToken> list(Connection conn) 
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
                return new ArrayList<ResetToken>();
            }
            ArrayList<ResetToken> list = new ArrayList<ResetToken>();
            do
            {
                ResetToken o = set(rs);
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
    
    
    public static void insert(ResetToken vo) throws DaoException
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
            "INSERT INTO reset_token(token, email, sys_creation_date) "
             + " VALUES(sha2(?,512), ?, sysdate())";
    
    public static void insert(Connection conn, ResetToken vo) throws DaoException
    {
        PreparedStatement ps = null;
        
        try
        {
            ps = conn.prepareStatement(insertsql);//, PreparedStatement.RETURN_GENERATED_KEYS);

            ps.setString(1, vo.getToken());
            ps.setString(2, vo.getEmail());
            
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
    
    
    private static String deletesql= "DELETE FROM reset_token WHERE token = ?  ";
    
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
}

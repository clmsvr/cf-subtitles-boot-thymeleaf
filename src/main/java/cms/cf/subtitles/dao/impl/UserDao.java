package cms.cf.subtitles.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import cms.cf.subtitles.dao.AbstractDao;
import cms.cf.subtitles.dao.DaoException;
import cms.cf.subtitles.dao.NotFoundException;
import cms.cf.subtitles.dao.vo.User;

public class UserDao extends AbstractDao
{ 
    static User set(ResultSet rs) throws SQLException
    {
        User vo = new User();

        vo.setEmail(rs.getString("email"));
        vo.setPwd(rs.getString("pwd"));
        vo.setName(rs.getString("name"));
        vo.setCity(rs.getString("city"));
        vo.setState(rs.getString("state"));
        vo.setNumBlocksSubtitled(rs.getInt("num_blocks_subtitled"));
        vo.setNumBlocksTranslated(rs.getInt("num_blocks_translated"));
        vo.setComment(rs.getString("comment"));
        vo.setSysCreationDate(rs.getTimestamp("sys_creation_date"));
        vo.setSysUpdateDate(rs.getTimestamp("sys_update_date"));

        return vo;
    }
    
    public static User get(String email) 
    throws NotFoundException, DaoException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            return get(conn, email);            
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
    
	private static String getsql = "SELECT * FROM user WHERE email = ?";
    
    public static User get(Connection conn, String email) 
    throws NotFoundException, DaoException
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try
        {
            ps = conn.prepareStatement(getsql);
            ps.setString(1, email);
            
            rs = ps.executeQuery();
            if (!rs.next())
            {
                throw new NotFoundException("Object not found [" + email + "]");
            }
            
            User u = set(rs);
            
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
    
    public static ArrayList<User> list() 
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

    
    private static String listsql = "SELECT * FROM user u order by name";
    
    public static ArrayList<User> list(Connection conn) 
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
                return new ArrayList<User>();
            }
            ArrayList<User> list = new ArrayList<User>();
            do
            {
                User o = set(rs);
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
    
    
    public static void insert(User vo) throws DaoException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            insert(conn, vo);
            
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
    
    
    private static String insertsql = 
            "INSERT INTO user(email, pwd, name, city, state, num_blocks_subtitled, "
             + "num_blocks_translated, comment, sys_creation_date, sys_update_date) "
             + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, sysdate(), sysdate())";
    
    public static void insert(Connection conn, User vo) throws DaoException
    {
        PreparedStatement ps = null;
        
        try
        {
            ps = conn.prepareStatement(insertsql);

            ps.setString(1, vo.getEmail());
            ps.setString(2, vo.getPwd());
            ps.setString(3, vo.getName());
            ps.setString(4, vo.getCity());
            ps.setString(5, vo.getState());
            ps.setInt   (6, vo.getNumBlocksSubtitled());
            ps.setInt   (7, vo.getNumBlocksTranslated());
            ps.setString(8, vo.getComment());
            
            ps.executeUpdate();
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
    
    
    public static void update(User vo) throws DaoException, NotFoundException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            update(conn, vo);
            
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
    
    //NAO serao atualizados todos os campos.
    private static String updatesql = 
            "UPDATE user SET name=?, city=?, state=?, comment=?, sys_update_date=sysdate() "
            + "WHERE email = ? ";
    
    public static void update(Connection conn, User vo) throws DaoException, NotFoundException
    {
        PreparedStatement ps = null;

        try
        {
            ps = conn.prepareStatement(updatesql);

            ps.setString(1, vo.getName());
            ps.setString(2, vo.getCity());
            ps.setString(3, vo.getState());
            ps.setString(4, vo.getComment());
            
            ps.setString(5, vo.getEmail());

            int count = ps.executeUpdate();
            
            if (count == 0 )
            {
                throw new NotFoundException("Object not found ["+vo.getEmail()+"] .");
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
    

    private static String updateNumBlocksSubtitledSql = 
            "UPDATE user SET num_blocks_subtitled=?, sys_update_date=sysdate() "
            + "WHERE email = ? ";
    
    public static void setNumBlocksSubtitled(Connection conn, String email, int numBlocksSubtitled) 
    throws DaoException, NotFoundException
    {
        PreparedStatement ps = null;

        try
        {
            ps = conn.prepareStatement(updateNumBlocksSubtitledSql);

            ps.setInt(1, numBlocksSubtitled);
            ps.setString(2, email);

            int count = ps.executeUpdate();
            
            if (count == 0 )
            {
                throw new NotFoundException("Object not found ["+email+"] .");
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

    
    private static String updateNumBlocksTranslatedSql = 
            "UPDATE user SET num_blocks_translated=?, sys_update_date=sysdate() "
            + "WHERE email = ? ";
    
    public static void setNumBlocksTranslated(Connection conn, String email, int numBlocksTranslated) 
    throws DaoException, NotFoundException
    {
        PreparedStatement ps = null;

        try
        {
            ps = conn.prepareStatement(updateNumBlocksTranslatedSql);

            ps.setInt(1, numBlocksTranslated);
            ps.setString(2, email);

            int count = ps.executeUpdate();
            
            if (count == 0 )
            {
                throw new NotFoundException("Object not found ["+email+"] .");
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
    
    public static void delete(String email) throws DaoException, NotFoundException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            delete(conn, email);
            
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
    
    
    private static String deletesql= "DELETE FROM user WHERE email = ?  ";
    
    public static void delete(Connection conn, String email) throws DaoException, NotFoundException
    {
        PreparedStatement ps = null;

        try
        {
            ps = conn.prepareStatement(deletesql);

            ps.setString(1, email);
            int count = ps.executeUpdate();
            
            if (count == 0 )
            {
                throw new NotFoundException("Object not found ["+email+"] .");
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

    public static void setPwd( String email, String newpwd) throws DaoException, NotFoundException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            setPwd(conn, email, newpwd);
            
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
    
    private static String updatePwdSql = 
            "UPDATE user SET pwd=sha2(?,512), sys_update_date=sysdate() "
            + "WHERE email = ? ";
    
    public static void setPwd(Connection conn, String email, String newpwd) 
    throws DaoException, NotFoundException
    {
        PreparedStatement ps = null;

        try
        {
            ps = conn.prepareStatement(updatePwdSql);

            ps.setString(1, newpwd);
            ps.setString(2, email);

            int count = ps.executeUpdate();
            
            if (count == 0 )
            {
                throw new NotFoundException("Object not found ["+email+"] .");
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

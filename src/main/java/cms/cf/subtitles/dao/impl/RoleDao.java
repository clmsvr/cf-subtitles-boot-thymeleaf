package cms.cf.subtitles.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import cms.cf.subtitles.dao.AbstractDao;
import cms.cf.subtitles.dao.DaoException;
import cms.cf.subtitles.dao.NotFoundException;
import cms.cf.subtitles.dao.vo.Role;

public class RoleDao extends AbstractDao
{ 
    static Role set(ResultSet rs) throws SQLException
    {
        Role vo = new Role();

        vo.setEmail(rs.getString("email"));
        vo.setRole(rs.getString("role"));

        return vo;
    }
    
    
    public static Role get(String email, String role) 
    throws NotFoundException, DaoException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            return get(conn, email, role);            
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
    
    private static String getsql = "SELECT * FROM user_role WHERE email = ? and role = ?";
    
    public static Role get(Connection conn, String email, String role) 
    throws NotFoundException, DaoException
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try
        {
            ps = conn.prepareStatement(getsql);
            ps.setString(1, email);
            ps.setString(2, role);
            
            rs = ps.executeQuery();
            if (!rs.next())
            {
                throw new NotFoundException("Object not found:  email[" + email + "]  role["+role+"]");
            }
            
            Role u = set(rs);
            
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
    
    public static ArrayList<Role> list() 
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

    
    private static String listsql = "SELECT * FROM user_role order by email";
    
    public static ArrayList<Role> list(Connection conn) 
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
                return new ArrayList<Role>();
            }
            ArrayList<Role> list = new ArrayList<Role>();
            do
            {
                Role o = set(rs);
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
    
    
    public static void insert(String email, String role) throws DaoException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            insert(conn, email, role);
            
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
    
    
    private static String insertsql = "INSERT INTO user_role(email, role)  VALUES(?, ?)";
    
    public static void insert(Connection conn, String email, String role) throws DaoException
    {
        PreparedStatement ps = null;
        try
        {
            ps = conn.prepareStatement(insertsql);//, PreparedStatement.RETURN_GENERATED_KEYS);

            ps.setString(1, email);
            ps.setString(2, role);
            
            ps.executeUpdate();
            
            //SEM COMMIT
            return ;
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
        
    
    public static void delete(String email , String role) throws DaoException, NotFoundException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            delete(conn, email, role);
            
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
    
    
    private static String deletesql= "DELETE FROM user_role WHERE email = ? and role = ? ";
    
    public static void delete(Connection conn, String email , String role) throws DaoException, NotFoundException
    {
        PreparedStatement ps = null;

        try
        {
            ps = conn.prepareStatement(deletesql);

            ps.setString(1, email);
            ps.setString(2, role);
            
            int count = ps.executeUpdate();
            
            if (count == 0 )
            {
                throw new NotFoundException("Object not found:  email[" + email + "]  role["+role+"].");
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


	public static ArrayList<String> getRoles(String email)
	throws DaoException
	{
        Connection conn = null;
        try
        {
            conn = getConnection();
            return getRoles(conn, email);
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
	
    private static String getRolsSql = "select role from user_role where email = ?";
    
    public static ArrayList<String> getRoles(Connection conn, String email) 
    throws DaoException
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try
        {
            ps = conn.prepareStatement(getRolsSql);
            ps.setString(1, email);

            rs = ps.executeQuery();
            if (!rs.next())
            {
                return new ArrayList<String>();
            }
            ArrayList<String> list = new ArrayList<String>();
            do
            {
                list.add(rs.getString("role"));
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
}

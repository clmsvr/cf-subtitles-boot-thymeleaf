package cms.cf.subtitles.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import cms.cf.subtitles.dao.AbstractDao;
import cms.cf.subtitles.dao.DaoException;
import cms.cf.subtitles.dao.NotFoundException;
import cms.cf.subtitles.dao.vo.Video;
import cms.cf.subtitles.dao.vo.Video.Status;

public class VideoDao extends AbstractDao
{ 
    static Video set(ResultSet rs) throws SQLException
    {
        Video vo = new Video();

        vo.setId(rs.getInt("id_video"));
        vo.setIdYoutube(rs.getString("id_youtube"));
        vo.setTitle(rs.getString("title"));
        vo.setMinutes(rs.getInt("minutes"));
        vo.setBlocks(rs.getInt("blocks"));
        vo.setBlocksReady(rs.getInt("blocks_ready"));
        vo.setSysCreationDate(rs.getTimestamp("sys_creation_date"));
        vo.setSysUpdateDate(rs.getTimestamp("sys_update_date"));

        vo.setStatus(Status.valueOf(rs.getString("status").toUpperCase()));
        
        return vo;
    }
    
    
    public static Video get(int id) 
    throws NotFoundException, DaoException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            return get(conn, id);            
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
    
    private static String getsql = "SELECT * FROM video v WHERE v.id_video = ?";
    
    public static Video get(Connection conn, int id) 
    throws NotFoundException, DaoException
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try
        {
            ps = conn.prepareStatement(getsql);
            ps.setInt(1, id);
            
            rs = ps.executeQuery();
            if (!rs.next())
            {
                throw new NotFoundException("Object not found [" + id + "]");
            }
            
            Video u = set(rs);
            
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
    
    public static Video getByYouTubeID(String youtubeID) 
    throws NotFoundException, DaoException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            return getByYouTubeID(conn, youtubeID);            
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
    
    private static String getYouTubesql = "SELECT * FROM video v WHERE v.id_youtube = ?";
    
    public static Video getByYouTubeID(Connection conn, String youtubeID) 
    throws NotFoundException, DaoException
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try
        {
            ps = conn.prepareStatement(getYouTubesql);
            ps.setString(1, youtubeID);
            
            rs = ps.executeQuery();
            if (!rs.next())
            {
                throw new NotFoundException("Object not found [" + youtubeID + "]");
            }
            
            Video u = set(rs);
            
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
    
    
    public static ArrayList<Video> list() 
    throws DaoException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            return list(conn);
        }
        finally
        {
            closeConnection(conn);
            conn = null;
        }
    }

    
    private static String listsql = "SELECT * FROM video v order by title";
    
    public static ArrayList<Video> list(Connection conn) 
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
                return new ArrayList<Video>();
            }
            ArrayList<Video> list = new ArrayList<Video>();
            do
            {
                Video o = set(rs);
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
    
    
    public static int insert(Video vo) throws DaoException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            int id = insert(conn, vo);
            
            //COMMIT
            conn.commit();
            return id;
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
            "INSERT INTO video( id_youtube, title, status, minutes, blocks, blocks_ready, sys_creation_date, sys_update_date) "
            + "VALUES(?, ?, ?, ?, ?, ?, sysdate(), sysdate())";
            
    public static int insert(Connection conn, Video vo) throws DaoException
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try
        {
            ps = conn.prepareStatement(insertsql, PreparedStatement.RETURN_GENERATED_KEYS);

            ps.setString(1, vo.getIdYoutube());
            ps.setString(2, vo.getTitle());
            ps.setString(3, vo.getStatus().toString());
            ps.setInt   (4, vo.getMinutes());
            ps.setInt   (5, vo.getBlocks());
            ps.setInt   (6, vo.getBlocksReady());
            
            ps.executeUpdate();
            
            rs = ps.getGeneratedKeys();

            if (rs.next()) 
            {
                int id = rs.getInt(1);
                vo.setId(id);
                
                //SEM COMMIT
                return id;
            } 
            else 
            {
                throw new DaoException("Nao foi possivel recuperar a CHAVE gerada na criacao do registro no banco de dados");
            }            
        }
        catch (SQLException e)
        {
            try{conn.rollback();} catch (Exception e1){}
            throw new DaoException(e.getMessage(),e);
        }
        finally
        {
            closeConnection(null, ps, rs);
            ps = null;
            rs = null;
        }
    }
    
    
    public static void update(Video vo) throws DaoException, NotFoundException
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
    
    private static String updatesql = 
            "UPDATE video SET id_youtube=?, title=?, status=?, minutes=?, blocks=?, blocks_ready=?,  sys_update_date=sysdate() "
            + " WHERE id_video = ? ";
    
    public static void update(Connection conn, Video vo) throws DaoException, NotFoundException
    {
        PreparedStatement ps = null;

        try
        {
            ps = conn.prepareStatement(updatesql);

            ps.setString(1, vo.getIdYoutube());
            ps.setString(2, vo.getTitle());
            ps.setString(3, vo.getStatus().toString());
            ps.setInt   (4, vo.getMinutes());
            ps.setInt   (5, vo.getBlocks());
            ps.setInt   (6, vo.getBlocksReady());
            
            ps.setInt   (7, vo.getId());

            int count = ps.executeUpdate();
            
            if (count == 0 )
            {
                throw new NotFoundException("Object not found ["+vo.getId()+"] .");
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
    
    
    public static void delete(int id) throws DaoException, NotFoundException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            delete(conn, id);
            
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
    
    
    private static String deletesql= "DELETE FROM video WHERE id_video = ?  ";
    
    public static void delete(Connection conn, int id) throws DaoException, NotFoundException
    {
        PreparedStatement ps = null;

        try
        {
            ps = conn.prepareStatement(deletesql);

            ps.setInt(1, id);
            int count = ps.executeUpdate();
            
            if (count == 0 )
            {
                throw new NotFoundException("Object not found ["+id+"] .");
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

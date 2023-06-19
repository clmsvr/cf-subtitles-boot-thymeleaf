package cms.cf.subtitles.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import cms.cf.subtitles.dao.AbstractDao;
import cms.cf.subtitles.dao.DaoException;
import cms.cf.subtitles.dao.NotFoundException;
import cms.cf.subtitles.dao.vo.Block;
import cms.cf.subtitles.dao.vo.Block.Locale;
import cms.cf.subtitles.dao.vo.Block.Status;
import cms.cf.subtitles.dao.vo.FinishedBlocks;

public class BlockDao extends AbstractDao
{ 
    static Block set(ResultSet rs) throws SQLException
    {
        Block vo = new Block();

        vo.setId(rs.getInt("id_block"));
        vo.setEmail(rs.getString("email"));
        vo.setAllocateTime(rs.getTimestamp("allocate_time"));
        vo.setIdVideo(rs.getInt("id_video"));
        vo.setSubtitleOri(rs.getString("subtitle_ori"));
        vo.setSubtitleUpdate(rs.getString("subtitle_update"));
        vo.setBegin(rs.getLong("begin"));
        vo.setEnd(rs.getLong("end"));
        vo.setSysCreationDate(rs.getTimestamp("sys_creation_date"));
        vo.setSysUpdateDate(rs.getTimestamp("sys_update_date"));

        /*
         * MyEnum.valueOf() can throw IllegalArgumentException if there's no mapping from the string, 
         * or NullPointerException if you get a null value from the db. 
         */
        vo.setStatus(Status.valueOf(rs.getString("status").toUpperCase()));
        vo.setLocale(Locale.valueOf(rs.getString("locale").toUpperCase()));
        vo.setUpdatedBy(rs.getString("updated_by"));
        
        return vo;
    }
    
    
    public static Block get(int id) 
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
    
    private static String getsql = "SELECT * FROM block b WHERE b.id_block = ?";
    
    public static Block get(Connection conn, int id) 
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
            
            Block u = set(rs);
            
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
    
    public static ArrayList<Block> list() 
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

    
    private static String listsql = "SELECT * FROM block b order by id_video, begin";
    
    public static ArrayList<Block> list(Connection conn) 
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
                return new ArrayList<Block>();
            }
            ArrayList<Block> list = new ArrayList<Block>();
            do
            {
                Block o = set(rs);
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
    
    
    public static int insert(Block vo) throws DaoException
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
            "INSERT INTO block(id_video, email, locale, subtitle_ori, subtitle_update, begin, end, status, sys_creation_date, sys_update_date) " 
           +" VALUES(?, ?, ?, ?, ?, ?, ?, ?, sysdate(), sysdate())";
    
    public static int insert(Connection conn, Block vo) throws DaoException
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try
        {
            ps = conn.prepareStatement(insertsql, PreparedStatement.RETURN_GENERATED_KEYS);

            ps.setInt   (1, vo.getIdVideo());
            ps.setString(2, vo.getEmail());
            ps.setString(3, vo.getLocale().toString());
            ps.setString(4, vo.getSubtitleOri());
            ps.setString(5, vo.getSubtitleUpdate());
            ps.setLong  (6, vo.getBegin());
            ps.setLong  (7, vo.getEnd());
            ps.setString(8, vo.getStatus().toString());
            
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
    
    
    private static String deletesql= "DELETE FROM block WHERE id_block = ?  ";
    
    public static void deleteByBlockID(Connection conn, int id) throws DaoException, NotFoundException
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
    
    private static String deleteByVideoIdsql= "DELETE FROM block WHERE id_video = ?  ";
    
    public static void deleteByVideoID(Connection conn, int videoid) throws DaoException, NotFoundException
    {
        PreparedStatement ps = null;

        try
        {
            ps = conn.prepareStatement(deleteByVideoIdsql);

            ps.setInt(1, videoid);
            int count = ps.executeUpdate();
            
            if (count == 0 )
            {
                throw new NotFoundException("Object not found ["+videoid+"] .");
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
    
    public static ArrayList<Block> listByVideo(int idVideo, Locale locale) 
    throws DaoException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            return listByVideo(conn, idVideo, locale);
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
    
    public static ArrayList<Block> listByUser(int idUser) 
    throws DaoException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            return listByUser(conn, idUser);
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

    private static String listByVideoSqlPT = "SELECT * FROM block b WHERE id_video = ? and locale = 'PT' order by begin";
    private static String listByVideoSqlEN = "SELECT * FROM block b WHERE id_video = ? and locale = 'EN' order by begin";
    
    public static ArrayList<Block> listByVideo(Connection conn, int videoid, Locale locale) 
    throws DaoException
    {
        if (locale == Locale.EN)
            return listBy(conn, videoid, listByVideoSqlEN);
        else 
            return listBy(conn, videoid, listByVideoSqlPT);
    }
    
    private static String listByUserSql =  "SELECT * FROM block b WHERE id_user = ? order by sys_update_date desc";
    
    public static ArrayList<Block> listByUser(Connection conn, int userid) 
    throws DaoException
    {
        return listBy(conn, userid, listByUserSql);
    }
    
    private static ArrayList<Block> listBy(Connection conn, int id, String sql) 
    throws DaoException
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try
        {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            
            rs = ps.executeQuery();
            if (!rs.next())
            {
                return new ArrayList<Block>();
            }
            ArrayList<Block> list = new ArrayList<Block>();
            do
            {
                Block o = set(rs);
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
    
    
    public static void updateSubtitle( int idBlock, String subtitle, String updatedBy) 
    throws DaoException, NotFoundException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            updateSubtitle(conn, idBlock, subtitle, updatedBy);
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
    
    private static String updateSubtitleSql = 
            "UPDATE block set subtitle_update=? , updated_by = ? ,sys_update_date=sysdate() WHERE id_block = ? ";
    
    public static void updateSubtitle(Connection conn, int idBlock, String subtitle, String updatedBy) 
    throws DaoException, NotFoundException
    {
        PreparedStatement ps = null;
        try
        {
            ps = conn.prepareStatement(updateSubtitleSql);

            ps.setString(1, subtitle);
            ps.setString(2, updatedBy);
            ps.setInt(3, idBlock);

            int count = ps.executeUpdate();
            
            if (count == 0 )
            {
                throw new NotFoundException("Object not found ["+idBlock+"] .");
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
    
    public static void updateStatus( int idBlock, Status status) 
    throws DaoException, NotFoundException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            updateStatus(conn, idBlock, status);
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
    
    private static String updateStatusSql = 
            "UPDATE block set status=?,  sys_update_date=sysdate() WHERE id_block = ? ";
    
    public static void updateStatus(Connection conn, int idBlock, Status status) throws DaoException, NotFoundException
    {
        PreparedStatement ps = null;

        try
        {
            ps = conn.prepareStatement(updateStatusSql);

            ps.setString(1, status.toString());
            ps.setInt(2, idBlock);

            int count = ps.executeUpdate();
            
            if (count == 0 )
            {
                throw new NotFoundException("Object not found ["+idBlock+"] .");
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
    
    public static ArrayList<Block> listAvailableBlocks(Locale locale) 
    throws DaoException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            return listAvailableBlocks(conn, locale);
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

    
    private static String listAvailableBlocksSql = 
           "SELECT b.* "
        + " FROM video v, block b "
        + " WHERE v.id_video = b.id_video and locale = ? and b.status = 'OPENED' and v.status = 'OPENED' "
        + " and b.email is null "
        + " ORDER by v.id_video , b.id_block "
        + " LIMIT 10";
            
    public static ArrayList<Block> listAvailableBlocks(Connection conn, Locale locale) 
    throws DaoException
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try
        {
            ps = conn.prepareStatement(listAvailableBlocksSql);
            ps.setString(1, locale.toString());
            
            rs = ps.executeQuery();
            if (!rs.next())
            {
                return new ArrayList<Block>();
            }
            ArrayList<Block> list = new ArrayList<Block>();
            do
            {
                Block o = set(rs);
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

    public static void allocateBlock( int idBlock, String email) 
    throws DaoException, NotFoundException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            allocateBlock(conn, idBlock, email);
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
    
    private static String allocateBlockSql = 
            "UPDATE block set email=?, allocate_time=sysdate(), sys_update_date=sysdate() "
            + " WHERE email is null and status = 'OPENED' and id_block = ? ";
    
    public static void allocateBlock(Connection conn, int idBlock, String email)
    throws DaoException, NotFoundException
    {
        PreparedStatement ps = null;

        try
        {
            ps = conn.prepareStatement(allocateBlockSql);

            ps.setString(1, email);
            ps.setInt(2, idBlock);

            int count = ps.executeUpdate();
            
            if (count == 0 )
            {
                throw new NotFoundException("Object not found ["+idBlock+"] .");
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
    
    /**
     * verifica se h� bloco alocado.
     * Se h�, retorna o id do bloco.
     * Caso n�o haja, retorna -1
     */
    public static int hasAllocatedBlock(String email) 
    throws DaoException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            return hasAllocatedBlock(conn, email);
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

    
    private static String hasAllocagtedSql = "select id_block from block where email = ? and status = 'OPENED'";
    
    public static int hasAllocatedBlock(Connection conn , String email) 
    throws DaoException
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try
        {
            ps = conn.prepareStatement(hasAllocagtedSql);
            ps.setString(1,email);

            rs = ps.executeQuery();
            if (rs.next())
            {
                return rs.getInt("id_block");
            }
            return -1;
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


    private static String timeoutSql = "update block set email = null, allocate_time = null WHERE status = 'OPENED' and allocate_time is not null and TIMESTAMPDIFF(MINUTE,allocate_time, sysdate()) > ? ";
    
    public static int cleanUpTimeoutBlocks(int maxAllocationTime) 
    throws DaoException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        try
        {
            conn = getConnection();
            ps = conn.prepareStatement(timeoutSql);
            ps.setInt(1, maxAllocationTime);
            
            int count = ps.executeUpdate();
            conn.commit();
            return count;
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
            closeConnection(conn,ps);
            conn = null;
        }
    }
    
    
    private static String cleanupBlockSql = "update block set email = null, allocate_time = null where id_block = ? and status = 'OPENED' ";
    
    public static int cleanUpBlock(int idBlock) 
    throws DaoException, NotFoundException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        try
        {
            conn = getConnection();
            ps = conn.prepareStatement(cleanupBlockSql);
            ps.setInt(1, idBlock);
            int count = ps.executeUpdate();
            
            if (count == 0 )
            {
                throw new NotFoundException("Object not found ["+idBlock+"] .");
            }  
            conn.commit();
            return count;
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
            closeConnection(conn,ps);
            conn = null;
        }
    }
    
    private static String finalizeBlockSql = "update block set status = 'FINISHED' where id_block = ? and status = 'OPENED' ";
    
    public static int finalizeBlock(int idBlock) 
    throws DaoException, NotFoundException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        try
        {
            conn = getConnection();
            ps = conn.prepareStatement(finalizeBlockSql);
            ps.setInt(1, idBlock);
            int count = ps.executeUpdate();
            
            if (count == 0 )
            {
                throw new NotFoundException("Object not found ["+idBlock+"] .");
            }  
            conn.commit();
            return count;
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
            closeConnection(conn,ps);
            conn = null;
        }
    }
    
    
    public static Block getPtBlock(int blockId) 
    throws NotFoundException, DaoException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            return getPtBlock(conn, blockId);            
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
    
    private static String getPtBlockSql = 
                  "select * from block where locale = 'PT' and id_block in (  "+
                  "  (select min(id_block) from block where "+
                  "     locale = 'PT' and id_video in (select id_video from block where id_block = ? ) "+ 
                  "  )+(select ? - min(id_block) from block where "+
                  "     id_video in (select id_video from block where id_block = ? ) and locale = 'EN'  "+
                  "  )) ";     
    
    public static Block getPtBlock(Connection conn, int blockId) 
    throws NotFoundException, DaoException
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try
        {
            ps = conn.prepareStatement(getPtBlockSql);
            ps.setInt(1, blockId);
            ps.setInt(2, blockId);
            ps.setInt(3, blockId);
            
            rs = ps.executeQuery();
            if (!rs.next())
            {
                throw new NotFoundException("Object not found [" + blockId + "]");
            }
            
            Block u = set(rs);
            
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
    
    
    public static ArrayList<FinishedBlocks> listFinishedBlocks(String email) 
    throws DaoException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            return listFinishedBlocks(conn, email);
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
    
    private static String listFinishedBlocksSql = 
            " select v.title, b.* from block b, video v " +
            " where b.id_video = v.id_video and b.email = ? and b.status = 'FINISHED' "+
            " order by b.id_video, b.locale, b.id_block";
    
    public static ArrayList<FinishedBlocks> listFinishedBlocks(Connection conn, String email) 
    throws DaoException
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<FinishedBlocks> finalList = new ArrayList<>();
        
        try
        {
            ps = conn.prepareStatement(listFinishedBlocksSql);
            ps.setString(1, email);
            
            rs = ps.executeQuery();
            int vid = -1;
            String locale = "";
            FinishedBlocks fbs = null;
            
            while (rs.next())
            {
                Block b = set(rs);
                if (b.getIdVideo() != vid || b.getLocale().toString().equals(locale) == false)
                {
                    if (fbs != null) finalList.add(fbs);
                    fbs = new FinishedBlocks();
                    fbs.setTitle(rs.getString("title"));
                    fbs.setLocale(b.getLocale().toString());
                    fbs.addBlock(b);
                    locale = b.getLocale().toString();
                    vid = b.getIdVideo();
                }
                else
                {
                    fbs.addBlock(b);
                }
            }
            if (fbs != null) finalList.add(fbs);
            
            return finalList;
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

package cms.cf.subtitles.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import cms.cf.subtitles.dao.AbstractDao;
import cms.cf.subtitles.dao.DaoException;
import cms.cf.subtitles.dao.NotFoundException;
import cms.cf.subtitles.dao.vo.Block;
import cms.cf.subtitles.dao.vo.Block.Locale;
import cms.cf.subtitles.dao.vo.Video;
import cms.cf.subtitles.dao.vo.VideoWrap;
import cms.cf.subtitles.srt.Srt;

public class VideoWrapDao extends AbstractDao
{ 
    public static VideoWrap get(int videoid, Locale locale) 
    throws NotFoundException, DaoException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            
            Video v = VideoDao.get(conn, videoid);
            ArrayList<Block> bs = BlockDao.listByVideo(conn, videoid, locale);
            
            return new VideoWrap(v, bs);
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
    
    public static int insertVideo(String idYoutube, String title, Srt[] blocks, Block.Locale locale)
    throws Exception
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            int id = insertVideo(conn, idYoutube, title, blocks, locale);
            
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
    
    public static int insertVideo(Connection conn, String idYoutube, String title, Srt[] srtBlocks, Block.Locale locale) 
    throws Exception
    {
        conn.setAutoCommit(false);
        try
        {
            Video video = new Video();
            video.setIdYoutube(idYoutube);
            video.setBlocks(srtBlocks.length);
            video.setTitle(title);

//            Srt lastblock = srts.get(srts.size()-1);
//            ArrayList<SrtItem> itens = lastblock.getItens();
//            SrtItem lastItem = itens.get(itens.size()-1);
//            video.setMinutes((int) lastItem.end / 1000 / 60);
            
            ArrayList<Block> blocks = new ArrayList<>();
            for (int i = 0; i < srtBlocks.length; i++)
            {
                Srt srt = srtBlocks[i];
                Block b = new Block();
                b.setBegin(srt.getBegin());
                b.setEnd(srt.getEnd());;
                String text = srt.toString();
                b.setSubtitleOri(text);
                b.setSubtitleUpdate(text);
                b.setLocale(locale);
                b.setEmail(null);
                
                video.setMinutes((int) b.getEnd() / 1000 / 60); //o ultimo define finalmente
                blocks.add(b);
            }
            
            int id = VideoDao.insert(conn, video);
            for (int i = 0; i < blocks.size(); i++)
            {
                Block b = blocks.get(i);
                b.setIdVideo(id);
                BlockDao.insert(conn,b);
            }
            
            //COMMIT
            //conn.commit();
            return id;
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
        }
    }
    
    public static void insertBlocks(String idYoutube, Srt[] engBlocks, Locale locale)
    throws Exception
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            insertBlocks(conn, idYoutube, engBlocks, locale);
            
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
    
    public static void insertBlocks(Connection conn, String idYoutube, Srt[] engBlocks, Locale locale)
    throws Exception
    {
        conn.setAutoCommit(false);
        try
        {
            Video v = VideoDao.getByYouTubeID(conn,idYoutube);
            
            for (int i = 0; i < engBlocks.length; i++)
            {
                Srt blockSrt = engBlocks[i];
                
                Block b = new Block();
                b.setBegin(blockSrt.getBegin());
                b.setEnd(blockSrt.getEnd());;
                String text = blockSrt.toString();
                b.setSubtitleOri(text);
                b.setSubtitleUpdate(text);
                b.setLocale(locale);
                b.setEmail(null);
                
                b.setIdVideo(v.getId());
                BlockDao.insert(conn,b);
            }
            
            //COMMIT
            //conn.commit();
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
        }
    }
    
    public static void delete(int videoid) throws DaoException, NotFoundException
    {
        Connection conn = null;
        try
        {
            conn = getConnection();
            delete(conn, videoid);
            
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
    
    
    public static void delete(Connection conn, int videoid) throws DaoException, NotFoundException
    {
        try
        {
            conn.setAutoCommit(false);
            
            BlockDao.deleteByVideoID(conn, videoid);
            VideoDao.delete(conn, videoid);

            //conn.commit();
        }
        catch (SQLException e)
        {
            try{conn.rollback();} catch (Exception e1){}
            throw new DaoException(e.getMessage(),e);
        }
        finally
        {
        }
    }
    
    public static void mainxx(String[] args)
    {
        try
        {
            AbstractDao.setTestMode();
            delete(4);
            //delete(5);
        }
        catch (DaoException e)
        {
            e.printStackTrace();
        }
    }
}

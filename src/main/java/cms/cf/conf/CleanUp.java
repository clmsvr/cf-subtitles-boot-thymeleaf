package cms.cf.conf;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import cms.cf.subtitles.dao.DaoException;
import cms.cf.subtitles.dao.impl.BlockDao;

/**
 * Thread papra expiracao de blocos alocados e nao finalizados.
 */
public class CleanUp
{
    private int           RELEASETIMER_ = 1000 * 30 * 1;                        //a cada 30 seg
    
    /**tempo maximo em MINUTOS permitido para um bloco de legendas ficar alocado para o usuario.*/
    public static int MAX_ALLOCATION_TIME = 120; 
    
    private Logger        logger        = Logger.getLogger(CleanUp.class);
    private boolean       isClosed_     = false;
    private CleanUpThread thread        = null;

    class CleanUpThread extends Thread
    {
        Timer releaseTimer_ = null;

        public void run()
        {
            try
            {
                releaseTimer_ = new Timer(true);
                releaseTimer_.schedule(new ReleaseTask(), 10000, RELEASETIMER_);
            }
            catch (Exception e)
            {
                logger.error("Falha nao esperada na THREAD de timeout", e);
            }
        }

        private class ReleaseTask extends TimerTask
        {
            public void run()
            {
                try
                {
                    doit();
                }
                catch (Throwable e)
                {
                    logger.error("Falha nao esperada na THREAD de CleanUp", e);
                }
            }
        }
    }

    /**
     * Construtor
     */
    public CleanUp() throws Exception
    {
        thread = new CleanUpThread();
        thread.start();
    }

    public void close()
    {
        isClosed_ = true;
        if (thread != null && thread.releaseTimer_ != null)
        {
            thread.releaseTimer_.cancel();
        }
    }

    private void doit() throws DaoException
    {
        if (isClosed_ == true) return;
        
        int count = BlockDao.cleanUpTimeoutBlocks(MAX_ALLOCATION_TIME);
        
        logger.info("Cleanup atualizou ["+count+"] blocos.");
    }

}

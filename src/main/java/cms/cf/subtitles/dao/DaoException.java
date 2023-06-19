package cms.cf.subtitles.dao;

/**
 * Thrown by a DAO implementation to indicate a data access error or other
 * errors. 
 */
public class DaoException extends Exception
{
    private static final long serialVersionUID = 1L;

    public DaoException()
    {
        super();
    }

    public DaoException(String message)
    {
        super(message);
    }

    public DaoException(Throwable cause)
    {
        super(cause);
    }

    public DaoException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
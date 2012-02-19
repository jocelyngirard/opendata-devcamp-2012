package net.team10.server.dao;

import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

/**
 * @author Édouard Mercier
 * @since 2012.02.18
 */
public abstract class BasisDal
{

  public abstract static class DalException
      extends Exception
  {

    private static final long serialVersionUID = 7862421308378167147L;

    protected static final Logger logger = Logger.getLogger(BasisDal.class.getName());

    public final String status;

    public DalException(String message)
    {
      super(message);
      status = null;
    }

    public DalException(String message, String status)
    {
      super(message);
      this.status = status;
    }
  }

  public final static class NotAuhorizedException
      extends DalException
  {

    private static final long serialVersionUID = -2552039353850003840L;

    public NotAuhorizedException(String message, String status)
    {
      super(message, status);
    }

  }

  public final static class DuplicateException
      extends DalException
  {

    private static final long serialVersionUID = 3413623418669983315L;

    public DuplicateException(String message, String status)
    {
      super(message, status);
    }
  }

  public final static class IllegalArgumentException
      extends DalException
  {

    private static final long serialVersionUID = 5920980845632966465L;

    public IllegalArgumentException(String message, String status)
    {
      super(message, status);
    }
  }

  protected final static class PMF
  {

    public static final PersistenceManagerFactory INSTANCE = JDOHelper.getPersistenceManagerFactory("transactions-optional");

    private PMF()
    {
    }

  }

  public final static class TransactionWrapper
  {

    protected final PersistenceManager persistenceManager;

    private Transaction transaction;

    public TransactionWrapper()
    {
      persistenceManager = PMF.INSTANCE.getPersistenceManager();
    }

    public void begin()
    {
      if (transaction == null)
      {
        transaction = persistenceManager.currentTransaction();
      }
      transaction.begin();
    }

    public void commit()
    {
      transaction.commit();
    }

    public void close()
    {
      if (transaction != null && transaction.isActive())
      {
        transaction.rollback();
      }
      persistenceManager.close();
    }

  }

}

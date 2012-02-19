package net.team10.server.resource;

import java.util.logging.Level;

import net.team10.bo.Account;
import net.team10.server.dao.ReparonsParisDal.BadAccountException;
import net.team10.server.ws.ReparonsParisServices;

import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

/**
 * @author Édouard Mercier
 * @since 2012.02.19
 */
public final class AccountResources
{

  public final static class PoiReportsResource
      extends ReparonsParisBasisResource
  {

    @Post
    public Representation post(Representation entity)
        throws ResourceException
    {
      final Account account = deserializeJson(getPostData(entity, "account"), Account.class);
      if (logger.isLoggable(Level.INFO))
      {
        logger.info("Creating the account with UID '" + account.getUid() + "'");
      }

      try
      {
        return generateObjectJsonRepresentation(ReparonsParisServices.getInstance().createAccount(account), "Account created");
      }
      catch (BadAccountException exception)
      {
        throw handleDalException(exception, "Could not create the account!");
      }
    }

  }

}

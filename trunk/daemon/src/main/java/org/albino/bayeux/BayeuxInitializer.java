package org.albino.bayeux;

import java.io.IOException;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.albino.DojoCommunicationHandler;
import org.apache.log4j.Logger;
import org.cometd.Bayeux;

@SuppressWarnings("serial")
public class BayeuxInitializer extends GenericServlet
{
	Logger logger = Logger.getLogger(BayeuxInitializer.class);
	
    public void init() throws ServletException
    {
        Bayeux bayeux = (Bayeux)getServletContext().getAttribute(Bayeux.ATTRIBUTE);
        logger.debug("init: Initializing DojoCommunicationHandler");
        new DojoCommunicationHandler(bayeux);
    }

    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException
    {
        throw new ServletException();
    }
}

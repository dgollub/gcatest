package de.kurashigegollub.dev.gcatest;

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import de.kurashigegollub.com.google.calender.CalendarUrl;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Copyright by Daniel Kurashige-Gollub, 2011
 * @author Daniel Kurashige-Gollub, daniel@kurashige-gollub.de
 */
public class BaseServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(BaseServlet.class.getSimpleName());

    public static final String REFRESH_TOKEN    = "refreshToken";
    public static final String ACCESS_TOKEN     = "accessToken";
    public static final String CLIENT_ID        = "clientId";
    public static final String CLIENT_SECRET    = "clientSecret";
    public static final String APP_NAME         = "appName";
    public static final String SESSION_USER     = "sessionUser";

    protected String clientId;
    protected String clientSecret;
    protected String appName;
    
    protected void process(HttpServletRequest request, HttpServletResponse response, HttpSession session)
    throws Exception 
    {
        throw new Exception("You have to override this method in subclasses.");
    }
    
    protected String redirectSession(HttpServletRequest request, HttpServletResponse response) 
    throws Exception
    {
        HttpSession session = getSession(request);
        
        //check if we need to login
        String user = (String)session.getAttribute(SESSION_USER);
        //we have to take care of the login servlet here, otherwise this will end up in an endless 
        //redirect attempt
        if (user == null && request.getContextPath().indexOf("/Login") != -1) {
            //log.info("New session detected.");
            return request.getContextPath()+"/Login";
        }       
        
        return null;
    }
    
    protected HttpSession getSession(HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            //log.info("New session");
            session = request.getSession(true);            
        }
        
        clientId = (String) session.getAttribute(CLIENT_ID);
        if (clientId == null) {
            clientId = readFromConfigAsString(CLIENT_ID);
            session.setAttribute(CLIENT_ID, clientId);
        }
        clientSecret = (String) session.getAttribute(CLIENT_SECRET);
        if (clientSecret == null) {
            clientSecret = readFromConfigAsString(CLIENT_SECRET);
            session.setAttribute(CLIENT_SECRET, clientSecret);
        }
        appName = (String) session.getAttribute(APP_NAME);
        if (appName == null) {
            appName = readFromConfigAsString(APP_NAME);
            session.setAttribute(APP_NAME, appName);
        }
        
        return session;
    }
    
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String redirect = redirectSession(request, response);
            if (Utils.isEmpty(redirect))
                process(request, response, getSession(request));
            else //redirect to login page
                response.sendRedirect(redirect);
        }
        catch (Exception ex) {
            //TODO: fix this exception handling
            System.err.println(ex.getMessage());
            try {
                HttpSession session = request.getSession();
                session.invalidate();
            } catch (Exception e) {
                //ignore this one
            }
            response.sendError(500, "Internal Problem: " + ex.getMessage());           
        }
    }

    protected static String readFromConfigAsString(String string) throws IOException {
        return Utils.readFromPropertiesAsString("/gcatest.properties", string);
    }
    protected String getRedirectUrlForGoogleCallback(HttpServletRequest request) {
        //TODO: build the local redirect URL -> google will return to this address after
        //      auth is done or if an error occoured
        return request.getContextPath() + "/Request"; //"http://localhost:8080/GCATest/Callback";
    }

    protected String buildAuthUrlForLogin(HttpServletRequest request) throws IOException {
        return buildAuthUrl(clientId, getRedirectUrlForGoogleCallback(request), CalendarUrl.CALENDER_ROOT_URL);
    }
    protected static String buildAuthUrl(String clientId, String redirectUrl, String scope)
    throws IOException {
        String authorizationUrl = new GoogleAuthorizationRequestUrl(clientId, redirectUrl, scope).build();
        log.fine(String.format("AuthorizationUrl: %s", authorizationUrl));
        return authorizationUrl;
    }
        
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}

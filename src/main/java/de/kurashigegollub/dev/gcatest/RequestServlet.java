package de.kurashigegollub.dev.gcatest;

import com.google.api.client.auth.oauth2.draft10.AccessTokenErrorResponse;
import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.logging.Level;
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
public class RequestServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(RequestServlet.class.getSimpleName());

    public static final String CALENDER_ROOT_URL = "https://www.google.com/calendar/feeds";
    
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String CLIENT_ID = "clientId";
    public static final String CLIENT_SECRET = "clientSecret";

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        log.info(String.format("session: %s", session));
        if (session == null) {
            log.info("New session");
            session = request.getSession(true);
        }

        //https://accounts.google.com/o/oauth2/auth
        //check if we are already logged in with google
        String accessToken = (String) session.getAttribute(ACCESS_TOKEN);
        String refreshToken = (String) session.getAttribute(REFRESH_TOKEN);

        String clientId = (String) session.getAttribute(CLIENT_ID);
        if (clientId == null) {
            clientId = readFromConfigAsString(CLIENT_ID);
        }
        String clientSecret = (String) session.getAttribute(CLIENT_SECRET);
        if (clientSecret == null) {
            clientSecret = readFromConfigAsString(CLIENT_SECRET);
        }

        log.info(String.format("accessToken:  %s", accessToken));
        log.info(String.format("refreshToken: %s", refreshToken));
        log.info(String.format("clientId:     %s", clientId));
        log.info(String.format("clientSecret: %s", clientSecret));

        if (Utils.isEmpty(accessToken)) { //we don't have a secure token yet, so we are not logged in yet

            log.info("Requesting access to google");
            try {
                accessGoogle(request, response, clientId, clientSecret, accessToken, refreshToken);
            } catch (Exception ex) {
                log.log(Level.SEVERE, "Error:{0}", ex.getMessage());
            }

            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet RequestServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RequestServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    private void accessGoogle(HttpServletRequest request, HttpServletResponse response, String clientId, String clientSecret, 
                              String accessToken, String refreshToken) throws Exception {
        
        GoogleAccessProtectedResource accessProtectedResource = 
                authorizeWithGoogle(clientId, clientSecret, CALENDER_ROOT_URL);
        
        //accessProtectedResource.getTransport().createRequestFactory().
        
    }

    private static GoogleAccessProtectedResource authorizeWithGoogle(String clientId, String clientSecret, String scope) 
    throws Exception {
        
        String redirectUrl = getRedirectUrl();
        buildAuthUrl(clientId, redirectUrl, scope);

        AccessTokenResponse response = exchangeCodeForAccessToken(clientId, clientSecret, redirectUrl);
        
        return new GoogleAccessProtectedResource(response.accessToken, Utils.getHttpTransport(),
                Utils.getJsonFactory(), clientId,
                clientSecret, response.refreshToken)
        {
            @Override
            protected void onAccessToken(String accessToken) {
                //TODO: save accessToken to current session 
            }
        };
    }

    private static AccessTokenResponse exchangeCodeForAccessToken(String clientId, String clientSecret, String redirectUrl) throws IOException {
        String code = null;//TODO: get the code from googles answer for our request //receiver.waitForCode();
        try {
            // exchange code for an access token
            return new GoogleAuthorizationCodeGrant(new NetHttpTransport(), Utils.getJsonFactory(), clientId, 
                                                    clientSecret, code, redirectUrl).execute();
        } catch (HttpResponseException ex) {
            AccessTokenErrorResponse response = ex.getResponse().parseAs(AccessTokenErrorResponse.class);
            log.log(Level.SEVERE, "Error: {0}", response.error);
            return null;
        }
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

    private String readFromConfigAsString(String string) throws IOException {
        return Utils.readFromPropertiesAsString("/gcatest.properties", string);
    }
    
    private static String getRedirectUrl() {
        //TODO: build the local redirect URL -> google will return to this address after
        //      auth is done or if an error occoured
        return "http://localhost:8080/GCATest/Callback";
    }

    private static String buildAuthUrl(String clientId, String redirectUrl, String scope)
    throws IOException {
        String authorizationUrl = new GoogleAuthorizationRequestUrl(clientId, redirectUrl, scope).build();
        log.fine(String.format("AuthorizationUrl: %s", authorizationUrl));
        return authorizationUrl;
    }

}

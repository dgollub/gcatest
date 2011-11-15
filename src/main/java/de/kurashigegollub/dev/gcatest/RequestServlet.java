package de.kurashigegollub.dev.gcatest;

import de.kurashigegollub.com.google.calender.CalendarUrl;
import de.kurashigegollub.com.google.calender.CalendarClient;
import de.kurashigegollub.com.google.calender.CalendarCmdlineRequestInitializer;
import com.google.api.client.auth.oauth2.draft10.AccessTokenErrorResponse;
import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import de.kurashigegollub.com.google.calender.CalendarFeed;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Copyright by Daniel Kurashige-Gollub, 2011
 * @author Daniel Kurashige-Gollub, daniel@kurashige-gollub.de
 */
public class RequestServlet extends BaseServlet {

    private static final Logger log = Logger.getLogger(RequestServlet.class.getSimpleName());

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void process(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws Exception {

        //https://accounts.google.com/o/oauth2/auth
        
        //This servlet will be called once the user permits our application to use part of his
        //google profile --> in other words: oauth2 was done. Whether or not it was successfull needs
        //to be checked now.
        
        
//              code = request.getParameter("code");

        
        
        String accessToken = (String) session.getAttribute(ACCESS_TOKEN);
        String refreshToken = (String) session.getAttribute(REFRESH_TOKEN);

        log.info(String.format("accessToken:  %s", accessToken));
        log.info(String.format("refreshToken: %s", refreshToken));
        log.info(String.format("clientId:     %s", clientId));
        log.info(String.format("clientSecret: %s", clientSecret));
        log.info(String.format("appName:      %s", appName));

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
        
            out.println("<html>");
            out.println("<head>");
            out.println("<title>");
            out.println(appName);
            out.println("</title>");
            out.println("</head>");
            out.println("<body>");                       
            

            log.info("Requesting access to google");
            out.println("what now?");
            try {
                accessGoogle(request, response, clientId, clientSecret, appName, accessToken, refreshToken);
            } catch (Exception ex) {
                log.log(Level.SEVERE, "Error:{0}", ex.getMessage());
                out.println("<div class=\"error\">");
                out.println("<span class=\"bold\">Error during execution: </span>");
                out.println(ex.getMessage());
                out.println("</div>");
            }

            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }
    
   

    private void accessGoogle(HttpServletRequest request, HttpServletResponse response, 
                              String clientId, String clientSecret, String appName,
                              String accessToken, String refreshToken) throws Exception {
        
//        GoogleAccessProtectedResource accessProtectedResource = 
//            authorizeWithGoogle(clientId, clientSecret, CalendarUrl.CALENDER_ROOT_URL);
        
        CalendarClient client =  new CalendarClient(
            new CalendarCmdlineRequestInitializer(null).createRequestFactory());
        client.setPrettyPrint(true);
        client.setApplicationName(appName);
        
        PrintWriter out = response.getWriter();
        
        try {
            
            GoogleCalendar gc = new GoogleCalendar(client);
            
            CalendarFeed cf = gc.listCalendarsAll();
            if (cf.getEntries().isEmpty()) {
                //empty calendar
                out.println("<div class=\"nodata\">No calendars found in your Google profile.</div>");
            }
            else {
                String html = HtmlView.createFeedHtml(cf);
                out.println(html);
            }
        } 
        catch (Exception ex) {
            if (ex instanceof HttpResponseException)
                log.severe(((HttpResponseException)ex).getResponse().parseAsString());
            else
                log.severe(ex.getMessage());
            throw ex;
        }
    }

    private static GoogleAccessProtectedResource authorizeWithGoogle(String clientId, String clientSecret, String scope) 
    throws Exception {
        
        String redirectUrl = null;//getRedirectUrlForGoogleCallback();
        String authorizationUrl = buildAuthUrl(clientId, redirectUrl, scope);

        AccessTokenResponse response = exchangeCodeForAccessToken(clientId, clientSecret, redirectUrl);
        
        return new GoogleAccessProtectedResource(response.accessToken, Utils.getHttpTransport(),
                Utils.getJsonFactory(), clientId, clientSecret, response.refreshToken)
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

    
   

}

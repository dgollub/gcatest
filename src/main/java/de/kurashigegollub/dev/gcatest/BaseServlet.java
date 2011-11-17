/**
 * Copyright (C) 2011 Daniel Kurashige-Gollub, daniel@kurashige-gollub.de
 * Please see the README file for details.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package de.kurashigegollub.dev.gcatest;

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.google.api.client.http.GenericUrl;
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
    public static final String APP_STATE        = "appState";
    public static final String ACCESS_CODE      = "accessCode";
    public static final String ERROR            = "error";
    public static final String AUTH_CODE_OBJ    = "authCodeObj";
    
    public enum AppState {
        
        LOGIN(0, "login"),
        CALENDAR_LIST(1, "list"), //state we reach after the callback from google
        CALENDAR_ENTRIES(2, "entries"), //state after user clicked on one calendar
        CALL_GMAIL(3, "gmail") //after user clicked on "send mail" button
        ;
            
        private final int state;
        private final String stateName;
        
        private AppState(int state, String stateName) {
            this.state     = state;
            this.stateName = stateName;
        }
        
        public String getStateName() { return stateName; }
        public int    getState()     { return state; }
        
        public static AppState fromString(String stateName) {
            try {
                for (AppState as : AppState.values()) {
                    if (as.getStateName().equalsIgnoreCase(stateName))
                        return as;
                }
                return valueOf(stateName);
            } catch (Exception ex) {
                return LOGIN;
            }
        }
    }

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
        
        //Check if we need to login
        AppState appState = (AppState)session.getAttribute(APP_STATE);
        
        //we have to take care of the login servlet here, otherwise this will end up in an endless 
        //redirect attempt (same rules apply to the Error servlet)
        String path = request.getContextPath();
        
        if ((appState == null || appState == AppState.LOGIN) && 
            path.indexOf("/Login") != -1 &&
            path.indexOf("/Error") != -1)
        {
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
            //TODO: remove this
            ex.printStackTrace();
        }
    }

    protected static String readFromConfigAsString(String string) throws IOException {
        return Utils.readFromPropertiesAsString("/gcatest.properties", string);
    }

    //Build the local redirect URL -> google will return to this address after
    //auth is done or if an error occoured.
    //Important: this url has to be the same every time we make this call! It is the one that
    //is saved in the Google API Console Access tab.
    protected String getRedirectUrlForGoogleCallback(HttpServletRequest request) {        
        //url += "/Request"; //"http://localhost:8080/GCATest/Request";
        GenericUrl url = new GenericUrl(Utils.reconstructURL(request, false, false));
        url.getPathParts().add("Request");
        return url.build();
    }
    protected String getRedirectUrlLogin(HttpServletRequest request) {
        return Utils.reconstructURL(request, false, false) + "/Login";
    }
    protected String getRedirectUrlReadme(HttpServletRequest request) {
        return Utils.reconstructURL(request, false, false) + "/Readme";
    }
//    protected String getRedirectUrlCalendarEvents(HttpServletRequest request, String calendarId, AppState appState) {
//        GenericUrl url = new GenericUrl(Utils.reconstructURL(request, false, false));
//        url.getPathParts().add("Request");
//        //url.getPathParts().add("calendarId="+calendarId);
//        //url.getPathParts().add("state="+appState.getStateName());
//        return url.build() + "?calendarId="+calendarId+"&state="+appState.getStateName();
//    }
//    protected String getRedirectUrlEventGmail(HttpServletRequest request, String eventId, AppState appState) {
//        GenericUrl url = new GenericUrl(Utils.reconstructURL(request, false, false));
//        url.getPathParts().add("Request");
//        //url.getPathParts().add("calendarId="+calendarId);
//        //url.getPathParts().add("state="+appState.getStateName());
//        return url.build() + "?eventId="+eventId+"&state="+appState.getStateName();
//    }
    protected String getAppStateBaseUrl(HttpServletRequest request, AppState appState) {
        GenericUrl url = new GenericUrl(Utils.reconstructURL(request, false, false));
        url.getPathParts().add("Request");
        //url.getPathParts().add("state="+appState.getStateName());
        return url.build() + "?state="+appState.getStateName();
    }
    
//    protected String buildAuthUrlForCalendarAccess(HttpServletRequest request, String calendarId, AppState appState) 
//    throws IOException {
//        return buildAuthUrl(clientId, getRedirectUrlCalendarEvents(request, calendarId),
//                            CalendarUrl.CALENDER_ROOT_URL, appState);
//    }
    
    protected String buildAuthUrlForLogin(HttpServletRequest request)
    throws IOException {
        return buildAuthUrl(clientId, getRedirectUrlForGoogleCallback(request),
                            //appState indicates the state the app should be in next
                            CalendarUrl.CALENDER_ROOT_URL, AppState.CALENDAR_LIST);
    }
    protected String buildAuthUrl(String clientId, String redirectUrl, String scope, AppState appState)
    throws IOException {
        GoogleAuthorizationRequestUrl garu = new GoogleAuthorizationRequestUrl(clientId, redirectUrl, scope);
        garu.state = appState.getStateName();
        String authorizationUrl = garu.build();
        log.info(String.format("buildAuthUrl: %s", authorizationUrl));
        return authorizationUrl;
    }
    
//    protected String buildCalendarEventsUrl(String clientId, String redirectUrl, String scope, AppState appState) 
//    throws IOException {
//        GoogleAuthorizationRequestUrl garu = new GoogleAuthorizationRequestUrl(clientId, redirectUrl, scope);
//        garu.state = appState.getStateName();
//        String authUrl = garu.build();
//        log.info(String.format("buildCalendarEventsUrl: %s", authUrl));
//        return authUrl;
//    }
    
    protected String createBasicHtmlHeader(HttpServletRequest request, String title) {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("<html>\n<head>\n<title>");
        sb.append(title);
        sb.append("</title>\n");
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
        
        String baseUrl   = Utils.reconstructURL(request, false, false);
        String cssUrl    = baseUrl + "/css/style.css";
        String jsUrl     = baseUrl + "/js/script.js";
        String jQueryUrl = baseUrl + "/js/jquery-1.7.min.js";
                
        sb.append("<link href=\"").append(cssUrl).append("\" rel=\"stylesheet\" type=\"text/css\">\n");
        
        //TODO: Actually, this should probably happen near the end of the page, so loading times on the
        //      client side are faster.
        //      Read the following link for a bit of an overview regarding loading times, optimizations, etc.
        //      done on Google+
        //      https://plus.google.com/u/0/115060278409766341143/posts/ViaVbBMpSVG
        sb.append("<script src=\"").append(jsUrl).append("\" type=\"text/javascript\"></script>\n");
        sb.append("<script src=\"").append(jQueryUrl).append("\" type=\"text/javascript\"></script>\n");
        
        sb.append("</head>\n<body>\n");
        
        return sb.toString();
    }
    
    protected String createBasicHtmlFooter(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("\n<br><br><hr><br>\n");
        sb.append("<div class=\"footer\">Please see the <a href='");
        sb.append(getRedirectUrlReadme(request));
        sb.append("'>README</a> for further details about this application.");
        sb.append("</div>");
        sb.append("\n</body>");
        sb.append("\n</html>");
        
        return sb.toString();
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

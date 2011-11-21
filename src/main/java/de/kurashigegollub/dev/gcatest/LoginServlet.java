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

import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Copyright by Daniel Kurashige-Gollub, 2011
 * @author Daniel Kurashige-Gollub, daniel@kurashige-gollub.de
 */
public class LoginServlet extends BaseServlet {

    private static final Logger log = Logger.getLogger(LoginServlet.class.getSimpleName());    
    
    @Override
    protected void process(HttpServletRequest request, HttpServletResponse response, HttpSession session)
    throws Exception {

        //This servlet will simply greet the user and proceed to redirect him to the oauth login page of google,
        //where he should grant this application the access rights, so we can proceed with the calendar part.
        
        AppState appState = (AppState)session.getAttribute(APP_STATE);
        if (appState != null)
            log.log(Level.INFO, "AppState = {0}", appState.getStateName());
        
        //check if we have a valid session - if yes, move on to the Request servlet
        if (appState != null && appState != AppState.LOGIN) {
            log.info("Found a valid session - moving on to request page");
            response.sendRedirect(getRedirectUrlForGoogleCallback(request));
            return;
        }
        
        //Check if we have all the needed configuration to access Google's API
        boolean configOk = true;
        if (Utils.isEmpty(clientId) || Utils.isEmpty(clientSecret) || Utils.isEmpty(appName)) {
            log.severe("YOU HAVE TO CONFIGURE THIS APPLICATION FIRST!");
            configOk = false;
        }
        
        
        session.setAttribute(APP_STATE, AppState.CALENDAR_LIST);
        
        String redirectUrl = buildAuthUrlForLogin(request);
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            
            out.println(createBasicHtmlHeader(request, appName));
            
//            out.print("<META http-equiv=\"refresh\" CONTENT=\"5;URL=\"");
//            out.print(redirectUrl);
//            out.println("\">");
            if (configOk && !ERROR_TIMEOUT.equalsIgnoreCase(request.getParameter(ERROR))) {
                out.println("<script type=\"text/javascript\">");
                out.println("<!--");
                out.println("setTimeout(\"redirect()\", 5000);");
                out.println("function redirect(){");
                out.println("   location.href='"+redirectUrl+"';");
                out.println("}");
                out.println("-->");
                out.println("</script>");
            }
            
            out.println("</head>");
            out.println("<body>");

            out.print("<h1>Welcome to ");
            out.print(Utils.isEmpty(appName) ? "NOT CONFIGURED" : appName);
            out.println("</h1>");
            
            //TODO depending on whether or not the user granted us permission or he is not logged in, the following
            //     text should be different
            
            if (configOk && !ERROR_TIMEOUT.equalsIgnoreCase(request.getParameter(ERROR))) {
                out.println("<p>Please grant this application access to your Google calendars. You will be redirected to the access login page now.</p>");
                out.println("<br>");
                out.println("<p>Or you can click here: <a href=\""+redirectUrl+"\">Access Page</a></p>");
                out.println("<br>");
            }
            else {
                if (ERROR_TIMEOUT.equalsIgnoreCase(request.getParameter(ERROR))) {
                    out.println("<div class=\"error\">Your session is no longer valid. Please grant access to your calendar data again.</div>");
                }
                if (!configOk) {
                    out.println("<div class=\"error\">You need to configure this application first. Please set your Google API clientId, "
                              + "clientSecret and application name in the src/main/resource/gcatest.properties file before you compile and"
                              + " deploy this application.</div>");
                }
            }
            
            
            out.println("<hr>");
            out.println("<p>");
            out.println("For developers: you may need to setup your <a href='https://code.google.com/apis/console'>API Access in the API Console</a>");
            out.println("<br>");
            out.println("Also: don't forget to enter the clientId, clientSecret and the application name in the gcatest.properties file before you compile and deploy this Tomcat application.");
            out.println("</p>");
            
            out.println(createBasicHtmlFooter(request));
        }        
        finally {
            out.close();
        }
    }
}

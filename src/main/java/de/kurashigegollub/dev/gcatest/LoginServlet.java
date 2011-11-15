package de.kurashigegollub.dev.gcatest;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Copyright by Daniel Kurashige-Gollub, 2011
 * @author Daniel Kurashige-Gollub, daniel@kurashige-gollub.de
 */
public class LoginServlet extends BaseServlet {

    @Override
    protected void process(HttpServletRequest request, HttpServletResponse response, HttpSession session)
    throws Exception {

        //This servlet will simply greet the user and proceed to redirect him to the oauth login page of google,
        //where he should grant this application the access rights, so we can proceed with the calendar part.
        
        String redirectUrl = buildAuthUrlForLogin(request);
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<html>");
            out.println("<head>");
            out.print("<title>Welcome to ");
            out.print(appName);
            out.println("</title>");
            out.print("<META http-equiv=\"refresh\" CONTENT=\"5;URL=\"");
            out.print(redirectUrl);
            out.println("\">");
            out.println("</head>");
            out.println("<body>");

            out.print("<h1>Welcome to ");
            out.print(appName);
            out.println("</h1>");
            
            //TODO depending on whether or not the user granted us permission or he is not logged in, the following
            //     text should be different
            
            out.println("<p>Please login to your Google account. You will be redirected to the login page now.</p>");
            out.println("<br>");
            out.println("Or you can click here: <a href=\""+redirectUrl+"\">Login</a>");
            out.println("");
            
            session.setAttribute(SESSION_USER, null);

            out.println("</body>");
            out.println("</html>");
        }        
        finally {
            out.close();
        }
    }
}

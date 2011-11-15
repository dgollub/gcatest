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
            
//            out.print("<META http-equiv=\"refresh\" CONTENT=\"5;URL=\"");
//            out.print(redirectUrl);
//            out.println("\">");
            
            out.println("<script type=\"text/javascript\">");
            out.println("<!--");
            out.println("setTimeout(\"redirect()\", 5000);");
            out.println("function redirect(){");
            out.println("   location.href='"+redirectUrl+"';");
            out.println("}");
            out.println("-->");
            out.println("</script>");
            
            out.println("</head>");
            out.println("<body>");

            out.print("<h1>Welcome to ");
            out.print(appName);
            out.println("</h1>");
            
            //TODO depending on whether or not the user granted us permission or he is not logged in, the following
            //     text should be different
            
            out.println("<p>Please login to your Google account. You will be redirected to the login page now.</p>");
            out.println("<br>");
            out.println("<p>Or you can click here: <a href=\""+redirectUrl+"\">Login</a></p>");
            out.println("<br>");
            out.println("<hr>");
            out.println("<p>");
            out.println("For developers: you may need to setup your <a href='https://code.google.com/apis/console'>API Access in the API Console</a>");
            out.println("<br>");
            out.println("Also: don't forget to enter the clientId, clientSecret and the application name in the gcatest.properties file before you compile and deploy this Tomcat application.");
            out.println("</p>");
            
            session.setAttribute(SESSION_USER, null);

            out.println("</body>");
            out.println("</html>");
        }        
        finally {
            out.close();
        }
    }
}

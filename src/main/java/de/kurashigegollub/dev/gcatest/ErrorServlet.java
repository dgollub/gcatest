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
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Copyright by Daniel Kurashige-Gollub, 2011
 * @author Daniel Kurashige-Gollub, daniel@kurashige-gollub.de
 * 
 * This servlet will display some HTML page with an error description.
 */
public class ErrorServlet extends BaseServlet {

    private static final Logger log = Logger.getLogger(ErrorServlet.class.getSimpleName());

    @Override
    protected void process(HttpServletRequest request, HttpServletResponse response, HttpSession session)
    throws Exception {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {            
            out.println(createBasicHtmlHeader(request, "Error!"));
            out.println("<h1>Error!</h1>");

            out.println("<p class=\"error\">");
            
            String error = request.getParameter(ERROR);
            if (Utils.isEmpty(error))
                out.println("No error found. This is strange.");
            else {
                out.println(error);
                if ("access_denied".equalsIgnoreCase(error)) {
                    out.println("<br>");
                    out.println("<p>You have to grant this application access to your Google Calendar profile, "
                              + "if you want to use it.</p>");
                }
            }
            
            out.println("</div>");
            
            out.println("<div>");
            out.println("<p>Try <a href='"+Utils.reconstructURL(request, false, false)+"'>again</a>?</p>");
            out.println("</div>");
            
            out.println(createBasicHtmlFooter(request));
        } finally {            
            out.close();
        }
    }
}

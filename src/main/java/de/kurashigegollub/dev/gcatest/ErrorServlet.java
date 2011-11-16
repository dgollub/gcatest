package de.kurashigegollub.dev.gcatest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.ServletException;
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

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
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
            
            out.println("</body>");
            out.println("</html>");
        } finally {            
            out.close();
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

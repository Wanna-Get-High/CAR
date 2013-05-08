/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import ejb.Book;
import ejb.BookFacade;
import ejb.SessionBookManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 *
 * @author WannaGetHigh
 */
@WebServlet(name = "ListAuthors", urlPatterns = {"/ListAuthors"})
public class ListAuthors extends HttpServlet {
    
    @EJB
    private SessionBookManager sessionBookManager;
    
    @EJB
    private BookFacade bookFacade;

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.getSession(true);
        
        response.setContentType("text/html;charset=UTF-8");
        
        PrintWriter out = response.getWriter();
        
        
        // retrieve the data of the search form
        String requestedTitle = request.getParameter("title");
        String requestedAuthor = request.getParameter("author");
        String requestedYearOfRelease = request.getParameter("yearOfRelease");
        
        if (requestedAuthor == null) requestedAuthor = "";
        if (requestedTitle == null) requestedTitle = "";
        if (requestedYearOfRelease == null) requestedYearOfRelease = "";

        try {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ListAuthors</title>");            
            out.println("</head>");
            out.println("<body>");
            
            out.println("<h3> Search :</h3>");
            out.println("<form>");
            
            out.println("Title: <input type='text' name='title' value= '"+requestedTitle+"'><br/>");
            out.println("Author: <input type='text' name='author' value= '"+requestedAuthor+"'><br/>");
            out.println("Year: <input type='text' name='yearOfRelease' value= '"+requestedYearOfRelease+"'><br/>");
            out.println("<input type='submit'> ");
            out.println("<input type='reset'><br/>");
            out.println("<br/>");
            out.println("If the form is empty when submited it will show all the books of the database<br/>");
            out.println("</form>");
            
            out.println("<h3>The books:</h3>");
            
            List books = bookFacade.findAll();
            
            out.println("<dl>");
            
            for (Iterator it = books.iterator(); it.hasNext();) {
                Book elem = (Book) it.next();
                
                String title = elem.getId();
                String author = elem.getAuthor();
                String yearOfRelease = elem.getYearOfRelease();
                
                // uncomment to remove all
                // then quit and relaunch
                //bookFacade.remove(elem);
                
                
//                System.out.println("title.equals(requestedTitle) :"+title.equals(requestedTitle));
//                System.out.println("author.equals(requestedAuthor) :"+author.equals(requestedAuthor));
//                System.out.println("yearOfRelease.equals(yearOfRelease) :"+yearOfRelease.equals(requestedYearOfRelease));
                
                if ( (requestedTitle.isEmpty() && requestedAuthor.isEmpty() && requestedYearOfRelease.isEmpty())
                        
                    || title.equals(requestedTitle)
                    || author.equals(requestedAuthor)
                    || yearOfRelease.equals(requestedYearOfRelease)) {
                    
                    out.println("<dt><b>Title:</b> "+title+"</dt>");
                    out.println("<dd><b>Author:</b> "+author+"</dd>");
                    out.println("<dd><b>Year of release:</b> "+yearOfRelease+"</dd>");
                }
            }
            
            
            out.println("</dl>");
            out.println("<br>");
            
            // TODO Ajouter verification ADMIN sinn pas de redirection possible
            out.println("<a href='AddBook'>Add new Book</a>");
            
            out.println("<br><br>");
            
            out.println("</body>");
            out.println("</html>");
        } finally {            
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
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
     * Handles the HTTP
     * <code>POST</code> method.
     *
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
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}

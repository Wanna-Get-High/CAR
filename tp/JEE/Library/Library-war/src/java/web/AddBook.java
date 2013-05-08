/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package web;

import ejb.Book;
import ejb.BookFacade;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author WannaGetHigh
 */
@WebServlet(name="AddBook", urlPatterns={"/AddBook"})
public class AddBook extends HttpServlet {
   
    @Resource(mappedName="jms/MyMessageFactory")
    private  ConnectionFactory connectionFactory;

    @Resource(mappedName="jms/MyMessage")
    private  Queue queue;
    
    @EJB
    private BookFacade bookFacade;

    
    
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String yearOfRelease = request.getParameter("yearOfRelease");
        
        PrintWriter out= response.getWriter();
        
        try {
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AddBook</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h3>Adding a book</h3>");
            
            // The form to add a book
            out.println("<form>");
            out.println("Title: <input type='text' name='title' ><br/>");
            out.println("Author: <input type='text' name='author' ><br/>");
            out.println("Year of release: <input type='text' name='yearOfRelease'><br/>");
            out.println("<input type='submit'>");
            out.println("<input type='reset'><br/>");
            out.println("</form>");
            
            if ((title!=null) && (author!=null) && (yearOfRelease != null)
                    && !title.isEmpty() && !author.isEmpty() && !yearOfRelease.isEmpty()) {

                Connection connection = connectionFactory.createConnection();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageProducer messageProducer = session.createProducer(queue);

                ObjectMessage message = session.createObjectMessage();
                
                // here we create the Book, that will be sent in JMS message
                List books = bookFacade.findAll();
                Boolean alreadyThere = false;
                
                for (Object currentBook : books) {
                    if ( ((Book)currentBook).getId().equals(title)) {
                        alreadyThere = true;
                    }
                }
                
                if (!alreadyThere) {
                    Book book = new Book();
                    book.setId(title);
                    book.setAuthor(author);
                    book.setYearOfRelease(yearOfRelease);

                    message.setObject(book);                
                    messageProducer.send(message);
                    
                    out.println("<br>");
                    out.println("<b> this book is succesfully added to the database </b>");
                    
                } else {
                    out.println("<br>");
                    out.println("<b> this book is already in the database </b>");
                }
                
                messageProducer.close();
                connection.close();
                
            } else {
                out.println("<br>");
                
                if ((title!=null) && (author!=null) && (yearOfRelease != null))
                    out.println("<b> Unable to add the book because some of the fields are not filled </b>");
            }
            
            out.println("</body>");
            out.println("</html>");
            
        } catch (JMSException ex) {
            System.err.println("the JMS provider fails to create the connection due to some internal error at addBook");
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
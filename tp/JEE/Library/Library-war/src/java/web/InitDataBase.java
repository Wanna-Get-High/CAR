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
import java.util.logging.Level;
import java.util.logging.Logger;
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
@WebServlet(name = "InitDataBase", urlPatterns = {"/InitDataBase"})
public class InitDataBase extends HttpServlet {

    @Resource(mappedName="jms/MyMessageFactory")
    private  ConnectionFactory connectionFactory;

    @Resource(mappedName="jms/MyMessage")
    private  Queue queue;
    
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
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet InitDataBase</title>");
            out.println("</head>");
            out.println("<body>");
            
            // remove the previously added book in the database
            List<Book> books = bookFacade.findAll();

            for (Book currentBook : books) {
                bookFacade.remove(currentBook);
            }
            
            // we connect to the database
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer messageProducer = session.createProducer(queue);
            ObjectMessage message = session.createObjectMessage();
            
            // Add some books in the database
            Book book = new Book();
            
            book.setId("Plop");
            book.setAuthor("Helloworld");
            book.setYearOfRelease("1998");
            message.setObject(book);                
            messageProducer.send(message);

            book = new Book();
            book.setId("Plip");
            book.setAuthor("Helloworld");
            book.setYearOfRelease("1999");
            message.setObject(book);                
            messageProducer.send(message);
            
            book = new Book();
            book.setId("Ploup");
            book.setAuthor("Helloworld");
            book.setYearOfRelease("2000");
            message.setObject(book);                
            messageProducer.send(message);
            
            
            book = new Book();
            book.setId("How not to live your life");
            book.setAuthor("The Lord of the Loutre");
            book.setYearOfRelease("1234");
            message.setObject(book);                
            messageProducer.send(message);
            
            book = new Book();
            book.setId("Bufu");
            book.setAuthor("damn it");
            book.setYearOfRelease("2010");
            message.setObject(book);                
            messageProducer.send(message);

            book = new Book();
            book.setId("Foo");
            book.setAuthor("Bar");
            book.setYearOfRelease("1978");
            message.setObject(book);                
            messageProducer.send(message);
            
            book = new Book();
            book.setId("B**** N****");
            book.setAuthor("The Lord of the Loutre");
            book.setYearOfRelease("1990");
            message.setObject(book);                
            messageProducer.send(message);
            
            // close the connections
            messageProducer.close();
            connection.close();
            
            out.println("Books successfully added to the database");
        } catch (JMSException ex) {
            out.println("Failed to add the books to the database because the JMS provider fails to create the connection due to some internal error");
            Logger.getLogger(InitDataBase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.println("</body>");
            out.println("</html>");
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

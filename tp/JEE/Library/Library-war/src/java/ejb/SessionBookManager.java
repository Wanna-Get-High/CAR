/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.servlet.http.HttpSessionEvent;

/**
 *
 * @author WannaGetHigh
 */
@Singleton
@LocalBean
public class SessionBookManager {

   private static int counter = 0;

    public void sessionCreated(HttpSessionEvent se) {
        counter++;
    }

    public void sessionDestroyed(HttpSessionEvent se) {
        counter--;
    }

    public int getActiveSessionsCount() {
        return counter;
    }
}

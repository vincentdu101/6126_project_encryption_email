package hello;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

/**
 * Created by vincentdu on 7/25/17.
 */
@Service
public class SessionService {

    public boolean isLoggedIn(HttpSession session) {
        User loggedIn = (User) session.getAttribute("currentUser");
        return loggedIn != null && loggedIn.getUsername() != null;
    }

}

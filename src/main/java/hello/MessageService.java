package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by vdu on 7/22/17.
 */
@Service
public class MessageService {

//    @Autowired

    public void sendMessage(Message message) {
        System.out.println("sending message " + message.getPlaintext());
    }


}

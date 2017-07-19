package hello;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@Controller
public class WebController extends WebMvcConfigurerAdapter {

    private PgpAlgorithmUtils pgp = new PgpAlgorithmUtils();
    private KeyUtils keyUtils = new KeyUtils();

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/allMessages").setViewName("allMessages");
    }

    @GetMapping("/")
    public String signinForm(final ModelMap model) {
        model.addAttribute("user", new User());
        return "signin";
    }

    @GetMapping("/register")
    public String registerForm(final ModelMap model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @GetMapping("/receivedMessages")
    public String viewReceivedMessages(final ModelMap model) {

        List<Message> messages = new ArrayList<>();
        User sender = new User("omega@ymail.com", "omg");
        messages.add(new Message(1, "HI", "odssdfosdf34234234", sender));
        messages.add(new Message(2, "HI there world", "odssdfosdasdsdf34234234", sender));
        messages.add(new Message(3, "Hello world", "odssdfossddsfdasdsdf34234234", sender));

        model.addAttribute("messages", messages);

        return "receivedMessages";
    }

    @GetMapping("/sentMessages")
    public String viewSentMessages(final ModelMap model) {

        List<Message> messages = new ArrayList<>();
        User sender = new User("omega@ymail.com", "omg");
        messages.add(new Message(1, "HI", "odssdfosdf34234234", sender));
        messages.add(new Message(2, "HI there world", "odssdfosdasdsdf34234234", sender));
        messages.add(new Message(3, "Hello world", "odssdfossddsfdasdsdf34234234", sender));

        model.addAttribute("messages", messages);

        return "sentMessages";
    }

    @PostMapping("/")
    public String checkPersonInfo(@Valid User user, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "form";
        }



        return "redirect:/messages";
    }

    @PostMapping("/register")
    public String registerUser(@Valid User user, BindingResult bindingResult) {

        Map<String, String> keys = new HashMap<>();

//        if (bindingResult.hasErrors()) {
//            return "register";
//        }

        try {
            keys = keyUtils.createNewUserKeys("1", user.getPassword());
        } catch(Exception exception) {
            System.out.println(exception.getMessage());
            return "register";
        }

        System.out.println(keys.get("public") + " " + keys.get("private"));

        return "redirect:/receivedMessages";
    }
}

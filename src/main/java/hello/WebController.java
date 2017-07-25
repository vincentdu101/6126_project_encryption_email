package hello;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.bouncycastle.openpgp.PGPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import hello.UserService.NonUniqueUsernameException;

@Controller
public class WebController extends WebMvcConfigurerAdapter {

	User currentUser = new User();

	@Autowired
	private UserService userService;

	@Autowired
	private MessageService messageService;

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/allMessages").setViewName("allMessages");
	}

	@GetMapping("/")
	public String signinForm(final ModelMap model) {
		currentUser = new User();
		model.addAttribute("user", currentUser);
		return "signin";
	}

	@GetMapping("/register")
	public String registerForm(final ModelMap model) {
		currentUser = new User();
		model.addAttribute("user", currentUser);
		return "register";
	}

	@GetMapping("/receivedMessages")
	public String viewReceivedMessages(final ModelMap model) {
		if (currentUser.getUsername() == null) {
			return "redirect:/";
		}
		List<Message> messages = new ArrayList<>();
		messages = messageService.getUsersReceivedMessages(currentUser);
		model.addAttribute("messages", messages);
		return "receivedMessages";
	}

	@GetMapping("/sentMessages")
	public String viewSentMessages(final ModelMap model) {

		if (currentUser.getUsername() == null) {
			return "redirect:/";
		}
	
		List<Message> messages = new ArrayList<>();
		messages = messageService.getUsersSentMessages(currentUser);
		model.addAttribute("messages", messages);
		return "sentMessages";
	}

	@PostMapping("/")
	public String loginAction(@RequestParam String username, @RequestParam String password) {
		User logIn = userService.verifyPassword(username, password);
		if (logIn != null) {
			currentUser = logIn;
			currentUser.setPassword(password);
			return "redirect:/receivedMessages";
		}
		else {
			return "redirect:/";
		}
		
        /*try {
            User user = new User(username, password);
            User foundUser = userService.getUser(username);
            if (userService.verifyPassword(foundUser, user)) {
                currentUser = foundUser;
            } else {
                return "redirect:/";
            }
        } catch(Exception exception) {
            System.out.println(exception.getMessage());
            return "redirect:/";
        }

		return "redirect:/receivedMessages";*/
	}

	@PostMapping("/register")
	public String registerUser(@Valid User user, BindingResult bindingResult) {
		try {
			userService.addUser(user);
			currentUser = user;
		} catch (NonUniqueUsernameException e) {
			bindingResult.rejectValue("username", "username", e.getMessage());
			return "register";
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
			return "register";
		}
		return "redirect:/receivedMessages";
	}
	
	public String redirectToReceived() {
		return "redirect:/receivedMessages";
	}

	// TEST URL FOR NEW USER
	@GetMapping("/checknewuser")
	public ModelAndView checkNewUser() {
		User user = userService.getUser("user");
		return new ModelAndView("checknewuser", "user", user);
	}

	@GetMapping("/newMessage")
	public String newMessage(final ModelMap model) {
	    List<User> users = userService.getAllUsers(currentUser);
		model.addAttribute("users", users);
		model.addAttribute("message", new Message());
		return "newMessage";
	}


	@PostMapping("/newMessage")
	public String sendNewMessage(@RequestParam String receiverId, @RequestParam String plaintext) {
	    Message message = new Message();
	    message.setSender(currentUser);
	    message.setReceiver(userService.getUserById(receiverId));
	    message.setPlaintext(plaintext);

		try {
			messageService.addMessage(message);
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
			return "newMessage";
		}

		return "redirect:sentMessages";
	}
	
	// ROUTE FOR ADDING TEST USER AND MESSAGE VALUES
	@GetMapping("/addtestvalues")
	public String addTestValues() throws NoSuchAlgorithmException, NoSuchProviderException, PGPException, IOException, NonUniqueUsernameException {
		User user = new User("user", "abc");
		userService.addUser(user);
		
		User omega = new User("omega@ymail.com", "omg");
		userService.addUser(omega);
		User alpha = new User("alpha@ymail.com", "alp");
		userService.addUser(alpha);

		Message m1 = new Message("HI", omega, alpha);
		messageService.addMessage(m1);
		Message m2 = new Message("Hi there alpha", omega, alpha);
		messageService.addMessage(m2);
		Message m3 = new Message("Hello omega", alpha, omega);
		messageService.addMessage(m3);
		
		return "redirect:/receivedMessages";
	}

	@RequestMapping("/validateuser")
	public ModelAndView displayStuff() {
		String s = null;
		System.out.println("S: " + s);
		
		User loggingIn = userService.getUser("omega@ymail.com");
		String username = loggingIn.getUsername();
		String pwordHash = loggingIn.getPasswordHash();
		
		//valid user
		s = userService.validateUser(username, pwordHash);
		//s = userService.validateUser("user","$2a$10$j2bNPIpbbERHK/B.ZKSukOzMU1U7/pG/t1g9avfU3QEe5JPBbLvF6");
		
		//non-valid user
		//s = userService.validateUser("user","$2a$10$j2bNPIpbbERHK/B.ZKSukOzMU1U7/pG/t1g9avfU3QEe5");
		
		if(s == null) {s = "0";}
		System.out.println("S: " + s);
		ModelAndView MV = new ModelAndView("/sentMessages");
		if(s == "1") 
			{
			MV = new ModelAndView("/sentMessages");
			}
		else if(s == "0") 
			{
			User UnValuser = userService.getUser("");
			MV = new ModelAndView("signin","user",UnValuser);
			}
		
		return MV;
	}
	
	@RequestMapping("/hello")
	public ModelAndView sayHi() {
		User user = userService.getUser("user");
		return new ModelAndView("signin","user",user);
	}
	
	
	@RequestMapping("/12345")
	public String display2Stuff() {
		return "123";
	}

}

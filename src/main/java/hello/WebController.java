package hello;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.http.protocol.HTTP;
import org.bouncycastle.openpgp.PGPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

	@Autowired
	private UserService userService;

	@Autowired
	private MessageService messageService;

	@Autowired
    private SessionService sessionService;

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/allMessages").setViewName("allMessages");
	}

	@GetMapping("/")
	public String signinForm(final ModelMap model) {
		model.addAttribute("user", new User());
		return "signin";
	}
	
	@GetMapping("/signinfail")
	public String signinForm2(final ModelMap model) {
		model.addAttribute("user", new User());
		return "signinfail";
	}

	@GetMapping("/register")
	public String registerForm(final ModelMap model) {
		model.addAttribute("user", new User());
		return "register";
	}

	@GetMapping("/receivedMessages")
	public String viewReceivedMessages(final ModelMap model, HttpSession session) {
	    if (!sessionService.isLoggedIn(session)) {
			return "redirect:/";
		}
		User currentUser = (User) session.getAttribute("currentUser");
		List<Message> messages = messageService.getUsersReceivedMessages(currentUser);
		model.addAttribute("messages", messages);
		model.addAttribute("currentUser", session.getAttribute("currentUser"));
		return "receivedMessages";
	}

	@GetMapping("/sentMessages")
	public String viewSentMessages(final ModelMap model, HttpSession session) {

		if (!sessionService.isLoggedIn(session)) {
			return "redirect:/";
		}

        User currentUser = (User) session.getAttribute("currentUser");
		List<Message> messages = messageService.getUsersSentMessages(currentUser);
		model.addAttribute("messages", messages);
		model.addAttribute("currentUser", session.getAttribute("currentUser"));
		return "sentMessages";
	}

	@PostMapping("/")
	public String loginAction(@RequestParam String username, @RequestParam String password, HttpSession session) {
		User logIn = userService.verifyPassword(username, password);
		if (logIn != null) {
			logIn.setPassword(password);
			session.setAttribute("currentUser", logIn);
			return "redirect:/receivedMessages";
		}
		else {
			System.out.println("Bad Login Info");
			session.removeAttribute("currentUser");
			return "redirect:/signinfail";
		}
	}

	@PostMapping("/signinfail")
	public String loginAction2(@RequestParam String username, @RequestParam String password, HttpSession session) {
		User logIn = userService.verifyPassword(username, password);
		if (logIn != null) {
			logIn.setPassword(password);
			session.setAttribute("currentUser", logIn);
			return "redirect:/receivedMessages";
		}
		else {
			System.out.println("Bad Login Info");
			session.removeAttribute("currentUser");
			return "redirect:/signinfail";
		}
	}
	
	@PostMapping("/register")
	public String registerUser(@Valid User user, BindingResult bindingResult, HttpSession session) {
		try {
			userService.addUser(user);
			session.setAttribute("currentUser", user);
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

	@GetMapping("/newMessage")
	public String newMessage(final ModelMap model, HttpSession session) {
        if (!sessionService.isLoggedIn(session)) {
            return "redirect:/";
        }

        User currentUser = (User) session.getAttribute("currentUser");
	    List<User> users = userService.getAllUsers(currentUser);
		model.addAttribute("users", users);
		model.addAttribute("message", new Message());
		model.addAttribute("currentUser", session.getAttribute("currentUser"));
		return "newMessage";
	}


	@PostMapping("/newMessage")
	public String sendNewMessage(@RequestParam String receiverId, @RequestParam String plaintext, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
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
		User user = new User("user", "abcabcd");
		userService.addUser(user);
		
		User omega = new User("omega@ymail.com", "omgabcd");
		userService.addUser(omega);
		User alpha = new User("alpha@ymail.com", "alpabcd");
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
	
	
	@RequestMapping("/logout")
    public String logoutSession(HttpSession session) {
	    session.removeAttribute("currentUser");
	    return "redirect:/";
}
	@RequestMapping("/decryptMessages")
	public ModelAndView display2Stuff() {
		User user = userService.getUser("IanFogelman@gmail.com");
		return new ModelAndView("decryptMessages","user",user);
	}

}

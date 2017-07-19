package hello;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.ArrayList;

@Controller
public class WebController extends WebMvcConfigurerAdapter {

	@Autowired
	private UserService userService;

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

		// if (bindingResult.hasErrors()) {
		// return "register";
		// }

		try {
			UserKeyPair keyPair = new UserKeyPair(user.getUsername(), user.getPassword());
			user.setPublicKey(keyPair.getPublicKey());
			user.setPrivateKey(keyPair.getPrivateKey());
			userService.addUser(user);
			System.out.println(keyPair.getPublicKey() + " " + keyPair.getPrivateKey());
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
			return "register";
		}

		return "redirect:/receivedMessages";
	}

	// TEST URL FOR NEW USER
	@GetMapping("/checknewuser")
	public ModelAndView checkNewUser() {
		User user = userService.getUser("user");
		return new ModelAndView("checknewuser", "user", user);
	}
}

package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepo;
	
	public User getUser(String username) {
		return userRepo.findByUsername(username);
	}
	
	public void addUser(User user) {
		userRepo.save(user);
	}
}

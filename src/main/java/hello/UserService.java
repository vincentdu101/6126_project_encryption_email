package hello;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import org.bouncycastle.openpgp.PGPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepo;

	private User parseUserWithKeys(User user, UserKeyPair keyPair) {
        user.setPublicKey(keyPair.getPublicKey());
        user.setPrivateKey(keyPair.getPrivateKey());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        return user;
    }

	public User getUser(String username) {
		return userRepo.findByUsername(username);
	}

	public User getUserById(String userId) {
	    return userRepo.findOne(Integer.parseInt(userId));
    }

	public void addUser(User user) throws NoSuchAlgorithmException, NoSuchProviderException, PGPException, IOException,
			NonUniqueUsernameException {
		if (getUser(user.getUsername()) != null) {
			throw new NonUniqueUsernameException("duplicate username");
		}
        UserKeyPair keyPair = new UserKeyPair(user.getUsername(), user.getPassword());
		userRepo.save(parseUserWithKeys(user, keyPair));
	}

	public List<User> getAllUsers(User currentUser) {
		List<User> users = new ArrayList<>();
		userRepo.findAll().forEach(users::add);
		return users.stream().filter(e -> e.getId() != currentUser.getId())
				.collect(Collectors.toList());
	}

	class NonUniqueUsernameException extends Exception {
		public NonUniqueUsernameException(String message) {
			super(message);
		}
	}

	@Transactional 
	public String validateUser(String user,String pass) {
		System.out.println("validateUser Method Starting...");
		//Initalizing the vars used for validation
		User var = null;
		String var2 = "";

		// set the var to the toString of validate user, if the user is valid it will be something other than null
		// which means that the user has a valid user name and password.
		 var =  userRepo.validateUser(user,pass);
		 
		 if(var != null) {var2 = "1";}else {var2 = "0";}
		 
		if(var2.equals("1")) 
		{
			System.out.println("Authenticated User");
		}
		else
		{
			System.out.println("Non-valid User");
		}
		System.out.println("validateUser method done...");
		return var2;
	}

	public User verifyPassword(String username, String password) {
		User loggingIn = getUser(username);
		if (loggingIn == null) {
			return null;
		}
		else {
			String hash = loggingIn.getPasswordHash();
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			if (passwordEncoder.matches(password, hash)) {
				return loggingIn;
			}
			else {
				return null;
			}
		}
    }
}

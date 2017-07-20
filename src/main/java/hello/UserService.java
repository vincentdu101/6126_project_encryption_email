package hello;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.bouncycastle.openpgp.PGPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepo;

	public User getUser(String username) {
		return userRepo.findByUsername(username);
	}

	public void addUser(User user) throws NoSuchAlgorithmException, NoSuchProviderException, PGPException, IOException,
			NonUniqueUsernameException {
		if (getUser(user.getUsername()) != null) {
			throw new NonUniqueUsernameException("duplicate username");
		}

		UserKeyPair keyPair = new UserKeyPair(user.getUsername(), user.getPassword());
		user.setPublicKey(keyPair.getPublicKey());
		user.setPrivateKey(keyPair.getPrivateKey());

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		user.setPasswordHash(passwordEncoder.encode(user.getPassword()));

		userRepo.save(user);
	}

	class NonUniqueUsernameException extends Exception {
		public NonUniqueUsernameException(String message) {
			super(message);
		}
	}
}

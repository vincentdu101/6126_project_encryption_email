package hello;

import java.awt.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface UserRepository extends CrudRepository<User, Integer> {
	
	public User findByUsername(String username);
	//public User findByPassword_hash(String password);
	
	@Autowired

	@Transactional(readOnly = true)
	@Query(value = "select u from User u WHERE username = :user and password_hash = :password" )
    public User validateUser(@Param("user") String user,@Param("password") String password);
	
}

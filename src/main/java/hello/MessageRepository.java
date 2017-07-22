package hello;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message, Integer> {

	List<Message> findByReceiver(User receiver);

	List<Message> findBySender(User sender);
}
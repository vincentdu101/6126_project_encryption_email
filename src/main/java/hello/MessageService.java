package hello;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

	@Autowired
	private MessageRepository messageRepo;

	public void addMessage(Message message) {
		String sendCiphertext = PgpAlgorithmUtils.encrypt(message.getPlaintext(), message.getSender().getPublicKey());
		message.setSendCiphertext(sendCiphertext);

		String recCiphertext = PgpAlgorithmUtils.encrypt(message.getPlaintext(), message.getReceiver().getPublicKey());
		message.setRecCiphertext(recCiphertext);

		messageRepo.save(message);
	}

	public List<Message> getUsersReceivedMessages(User receiver) {
		return messageRepo.findByReceiver(receiver);
	}

	public List<Message> getUsersSentMessages(User sender) {
		return messageRepo.findBySender(sender);
	}
}
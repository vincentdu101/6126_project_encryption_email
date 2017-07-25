package hello;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * Created by vincentdu on 7/17/17.
 */

@Entity
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Transient
	private String plaintext;

	@Lob
	private String sendCiphertext;

	@Lob
	private String recCiphertext;
	
	@Transient
	private String decryptedText;

	//String timeStamp = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
	
	//private LocalDateTime created = LocalDateTime.now();
	private String created = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
	//private LocalDateTime modified = LocalDateTime.now();
	private String modified = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
	
	//private String modified = timeStamp;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id")
	private User sender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id")
	private User receiver;

	public Message() {
	}

	public Message(String plaintext, User sender, User receiver) {
		this.plaintext = plaintext;
		this.sender = sender;
		this.receiver = receiver;
	}

	public Message(User receiver, String plaintext) {
		this.plaintext = plaintext;
		this.receiver = receiver;
	}

	// probably can remove these 2 ctors
	/*
	 * public Message(int id, String plaintext, String ciphertext, LocalDate
	 * created, LocalDate modified, User sender) { this.id = id; this.plaintext
	 * = plaintext; this.ciphertext = ciphertext; this.created = created;
	 * this.modified = modified; this.sender = sender; }
	 * 
	 * public Message(int id, String plaintext, String ciphertext, User sender)
	 * { this.id = id; this.plaintext = plaintext; this.ciphertext = ciphertext;
	 * this.sender = sender; }
	 */

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	public String getPlaintext() {
		return plaintext;
	}

	public void setPlaintext(String plaintext) { this.plaintext = plaintext; }

	public String getSendCiphertext() {
		return sendCiphertext;
	}

	public void setSendCiphertext(String sendCiphertext) {
		this.sendCiphertext = sendCiphertext;
	}

	public String getRecCiphertext() {
		return recCiphertext;
	}

	public void setRecCiphertext(String recCiphertext) {
		this.recCiphertext = recCiphertext;
	}

	public String getDecryptedText() {
		return decryptedText;
	}

	public void setDecryptedText(String decryptedText) {
		this.decryptedText = decryptedText;
	}

}

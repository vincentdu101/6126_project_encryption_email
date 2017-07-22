package hello;

import java.time.LocalDate;

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

	// @Transient
	private String plaintext;

	@Lob
	private String sendCiphertext;

	@Lob
	private String recCiphertext;

	private LocalDate created = LocalDate.now();
	private LocalDate modified = LocalDate.now();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id")
	private User sender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id")
	private User receiver;

	protected Message() {
	}

	public Message(String plaintext, User sender, User receiver) {
		this.plaintext = plaintext;
		this.sender = sender;
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

	public LocalDate getCreated() {
		return created;
	}

	public void setCreated(LocalDate created) {
		this.created = created;
	}

	public LocalDate getModified() {
		return modified;
	}

	public void setModified(LocalDate modified) {
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
}

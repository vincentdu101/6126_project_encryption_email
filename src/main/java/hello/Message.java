package hello;

import java.time.LocalDate;

/**
 * Created by vincentdu on 7/17/17.
 */
public class Message {

    private int id;
    private final String title;
    private final String plaintext;
    private String ciphertext;
    private LocalDate created = LocalDate.now();
    private LocalDate modified = LocalDate.now();
    private User sender;

    public Message(int id, String plaintext, String ciphertext, LocalDate created, LocalDate modified, User sender, String title) {
        this.id = id;
        this.title = title;
        this.plaintext = plaintext;
        this.ciphertext = ciphertext;
        this.created = created;
        this.modified = modified;
        this.sender = sender;
    }

    public Message(int id, String plaintext, String ciphertext, User sender, String title) {
        this.id = id;
        this.title = title;
        this.plaintext = plaintext;
        this.ciphertext = ciphertext;
        this.sender = sender;
    }

    public Message(String title, String plaintext) {
        this.title = title;
        this.plaintext = plaintext;
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

    public User getSender() {
        return sender;
    }

    public void setModified(LocalDate modified) {
        this.modified = modified;
    }

    public String getPlaintext() {
        return plaintext;
    }

    public String getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(String ciphertext) {
        this.ciphertext = ciphertext;
    }

    public void setSender(User user) {
        this.sender = user;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}

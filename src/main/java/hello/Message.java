package hello;

import java.time.LocalDate;

/**
 * Created by vincentdu on 7/17/17.
 */
public class Message {

    private final int id;
    private final String plaintext;
    private final String ciphertext;
    private LocalDate created = LocalDate.now();
    private LocalDate modified = LocalDate.now();
    private final User sender;

    public Message(int id, String plaintext, String ciphertext, LocalDate created, LocalDate modified, User sender) {
        this.id = id;
        this.plaintext = plaintext;
        this.ciphertext = ciphertext;
        this.created = created;
        this.modified = modified;
        this.sender = sender;
    }

    public Message(int id, String plaintext, String ciphertext, User sender) {
        this.id = id;
        this.plaintext = plaintext;
        this.ciphertext = ciphertext;
        this.sender = sender;
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

    public int getId() {
        return id;
    }
}

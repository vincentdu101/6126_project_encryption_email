package hello;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.persistence.Lob;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
	
    @NotNull
    @Size(min=2, max=30)
    private String username;

    @NotNull
    @Min(10)
    private String password;
    
    @Lob
    private byte[] publicKey;
    @Lob
    private byte[] privateKey;

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    //TODO - salt and hash before adding to database
    public void setPassword(String password) {
        this.password = password;
    }
    
	public byte[] getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}

	public byte[] getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(byte[] privateKey) {
		this.privateKey = privateKey;
	}
	
	@Override
    public String toString() {
        return String.format(
                "User[id=%d, username='%s', password='%s', public key=%s, private key=%s]",
                id, username, password, publicKey, privateKey);
    }
}

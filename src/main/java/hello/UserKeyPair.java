package hello;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Date;

import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;

public class UserKeyPair {
	private byte[] privateKey;
	private byte[] publicKey;
	private String id;
	private String password;
	
	public UserKeyPair(String id, String password) throws NoSuchAlgorithmException, NoSuchProviderException, PGPException, IOException {
		this.id = id;
		this.password = password;
		createNewUserKeyStrings();
	}
	
	
	/*
	 * Creates a new pair of RSA public/private keys for a new user and returns
	 * the keys as byte array. 
	 * 
	 * @parameter id the user's id
	 * 
	 * @parameter password the user's password
	 * 
	 * code based on
	 * https://github.com/bcgit/bc-java/blob/master/pg/src/main/java/org/
	 * bouncycastle/openpgp/examples/RSAKeyPairGenerator.java
	 */
	public void createNewUserKeyStrings()
			throws PGPException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
		char[] pwArray = password.toCharArray();
		KeyPair kp = createKeyPair();
		PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);
		PGPKeyPair keyPair = new JcaPGPKeyPair(PGPPublicKey.RSA_GENERAL, kp, new Date());
		PGPSecretKey secretKey = new PGPSecretKey(PGPSignature.DEFAULT_CERTIFICATION, keyPair, id, sha1Calc, null, null,
				new JcaPGPContentSignerBuilder(keyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1),
				new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_128, sha1Calc).setProvider("BC")
						.build(pwArray));

		this.publicKey = secretKey.getPublicKey().getEncoded();
		this.privateKey = secretKey.getEncoded();
	}
	
	/*
	 * Creates a new key pair
	 * 
	 * @return the new key pair
	 * 
	 */
	private static KeyPair createKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
		kpg.initialize(1024);
		KeyPair kp = kpg.generateKeyPair();
		return kp;
	}
	
	public byte[] getPrivateKey() {
		return privateKey;
	}
	
	public byte[] getPublicKey() {
		return publicKey;
	}	
}

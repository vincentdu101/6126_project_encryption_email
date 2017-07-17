package hello.KeyStrings;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Date;
import java.util.Iterator;

import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;
import org.bouncycastle.util.encoders.Base64;

public class KeyUtilsForKeyStrings {
	/*
	 * Creates a new pair of RSA public/private keys for a new user and returns
	 * the keys as base-64 encoded strings. The key strings are returned in a
	 * hashmap with the keys "public" and "private".
	 * 
	 * @parameter id the user's id
	 * 
	 * @parameter password the user's password
	 * 
	 * return UserKeyPairStrings object that contain the public and private keys
	 * as base-64 encoded strings.
	 * 
	 * code based on
	 * https://github.com/bcgit/bc-java/blob/master/pg/src/main/java/org/
	 * bouncycastle/openpgp/examples/RSAKeyPairGenerator.java
	 */
	public static UserKeyPairStrings createNewUserKeyStrings(String id, String password)
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

		UserKeyPairStrings keyStrings = new UserKeyPairStrings();
		keyStrings.setPublicKey(Base64.toBase64String(secretKey.getPublicKey().getEncoded()));
		keyStrings.setPrivateKey(Base64.toBase64String(secretKey.getEncoded()));
		return keyStrings;
	}

	/*
	 * Creates the PGPPublicKey object from the public key string.
	 * 
	 * @param publicKeyStr a base-64 encoded public key string
	 * 
	 * @return a PGPPublicKey object
	 */
	public static PGPPublicKey getPublicKeyFromStr(String publicKeyStr) throws IOException, PGPException {
		byte[] encKey = Base64.decode(publicKeyStr);
		InputStream inStream = new ByteArrayInputStream(encKey);
		PGPPublicKey key = null;

		PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(inStream),
				new JcaKeyFingerprintCalculator());

		Iterator keyRingIter = pgpPub.getKeyRings();
		while (keyRingIter.hasNext()) {
			PGPPublicKeyRing keyRing = (PGPPublicKeyRing) keyRingIter.next();
			Iterator keyIter = keyRing.getPublicKeys();
			key = (PGPPublicKey) keyIter.next();
		}
		return key;
	}

	/*
	 * Creates the PGPPrivateKey object from the secret key string.
	 * 
	 * @param privateKeyStr a base-64 encoded private key string
	 * 
	 * @return a PGPPublicKey object
	 */
	public static PGPPrivateKey getPrivateKeyFromStr(String privateKeyStr, String password)
			throws IOException, PGPException {
		char[] passwordArray = password.toCharArray();
		byte[] encSKey = Base64.decode(privateKeyStr);
		InputStream sInStream = new ByteArrayInputStream(encSKey);
		PGPSecretKey sKey = null;

		PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(sInStream),
				new JcaKeyFingerprintCalculator());

		Iterator keyRingIter = pgpSec.getKeyRings();
		while (keyRingIter.hasNext()) {
			PGPSecretKeyRing keyRing = (PGPSecretKeyRing) keyRingIter.next();
			Iterator keyIter = keyRing.getSecretKeys();
			sKey = (PGPSecretKey) keyIter.next();
		}
		PGPPrivateKey pKey = sKey
				.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(passwordArray));
		return pKey;
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
}

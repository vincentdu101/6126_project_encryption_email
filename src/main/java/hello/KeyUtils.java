package hello;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Date;
import java.util.Iterator;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
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

public class KeyUtils {

	/*
	 * Creates a new pair of RSA public/private keys for a new user and stores
	 * the keys in the Public and Secret key ring collections.
	 * 
	 * @parameter id the user's id
	 * 
	 * @parameter password the user's password
	 * 
	 */
	public static void createNewUserKeys(String id, String password)
			throws PGPException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
		char[] pwArray = password.toCharArray();
		KeyPair kp = createKeyPair();
		PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);
		PGPKeyPair keyPair = new JcaPGPKeyPair(PGPPublicKey.RSA_GENERAL, kp, new Date());
		PGPKeyRingGenerator keyRingGen = new PGPKeyRingGenerator(PGPSignature.DEFAULT_CERTIFICATION, keyPair, id,
				sha1Calc, null, null,
				new JcaPGPContentSignerBuilder(keyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1),
				new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_128, sha1Calc).setProvider("BC")
						.build(pwArray));

		PGPPublicKeyRing pubKeyRing = keyRingGen.generatePublicKeyRing();
		PGPSecretKeyRing secKeyRing = keyRingGen.generateSecretKeyRing();

		PGPPublicKeyRingCollection pubRings = getPublicKeyRingColl();
		PGPPublicKeyRingCollection updatedPubRings = PGPPublicKeyRingCollection.addPublicKeyRing(pubRings, pubKeyRing);

		PGPSecretKeyRingCollection secRings = getSecretKeyRingColl();
		PGPSecretKeyRingCollection updatedSecRings = PGPSecretKeyRingCollection.addSecretKeyRing(secRings, secKeyRing);

		saveCollectionFiles(updatedPubRings, updatedSecRings);
	}

	/*
	 * Retrieves the user's public key from the key ring collection stored in
	 * pub.pkr
	 * 
	 * @parameter id the user's id
	 * 
	 * @return the user's public key
	 * 
	 */
	public static PGPPublicKey getPublicKey(String id) throws FileNotFoundException, IOException, PGPException {
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
		PGPPublicKeyRingCollection pkrColl = getPublicKeyRingColl();
		Iterator<PGPPublicKeyRing> pkrIter = pkrColl.getKeyRings(id);
		PGPPublicKey publicKey = null;

		PGPPublicKeyRing keyRing = pkrIter.next();
		publicKey = keyRing.getPublicKey();
		return publicKey;
	}

	/*
	 * Retrieves the user's private key from the key ring collection stored in
	 * sec.skr
	 * 
	 * @parameter id the user's id
	 * 
	 * @parameter password the user's password
	 * 
	 * @return the user's public key
	 * 
	 */
	public static PGPPrivateKey getPrivateKey(String id, String password)
			throws FileNotFoundException, IOException, PGPException {
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
		PGPSecretKeyRingCollection skrColl = getSecretKeyRingColl();
		Iterator<PGPSecretKeyRing> skrIter = skrColl.getKeyRings(id);
		PGPSecretKey secretKey = null;

		PGPSecretKeyRing keyRing = skrIter.next();
		secretKey = keyRing.getSecretKey();

		char[] pwArray = password.toCharArray();
		try {
			PGPPrivateKey pKey = secretKey
					.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(pwArray));
			return pKey;
		} catch (PGPException e) {
			return null;
		}
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

	/*
	 * Saves the key ring collection objects to files pub.pkr and sec.skr
	 * 
	 * @parameter pkr the public key ring collection
	 * 
	 * @parameter skr the secret key ring collection
	 * 
	 */
	private static void saveCollectionFiles(PGPPublicKeyRingCollection pkr, PGPSecretKeyRingCollection skr)
			throws IOException {
		OutputStream pubOut = new FileOutputStream("pub.pkr");
		pubOut = new ArmoredOutputStream(pubOut);
		pkr.encode(pubOut);
		pubOut.close();

		OutputStream secOut = new FileOutputStream("sec.skr");
		secOut = new ArmoredOutputStream(secOut);
		skr.encode(secOut);
		secOut.close();
	}

	/*
	 * Retrieves the public key ring collection from pub.pkr
	 * 
	 * @return the public key ring collection
	 * 
	 */
	private static PGPPublicKeyRingCollection getPublicKeyRingColl()
			throws FileNotFoundException, IOException, PGPException {
		File file = new File("pub.pkr");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileInputStream pubInputStream = new FileInputStream(file);
		PGPPublicKeyRingCollection pubRings = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(pubInputStream),
				new JcaKeyFingerprintCalculator());
		pubInputStream.close();
		return pubRings;
	}

	/*
	 * Retrieves the secret key ring collection from sec.skr
	 * 
	 * @return the secret key ring collection
	 * 
	 */
	private static PGPSecretKeyRingCollection getSecretKeyRingColl()
			throws FileNotFoundException, IOException, PGPException {
		File file = new File("sec.skr");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileInputStream secInputStream = new FileInputStream(file);
		PGPSecretKeyRingCollection secRings = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(secInputStream),
				new JcaKeyFingerprintCalculator());
		secInputStream.close();
		return secRings;
	}
}

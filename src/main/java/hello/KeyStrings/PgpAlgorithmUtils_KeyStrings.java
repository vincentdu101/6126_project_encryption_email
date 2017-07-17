package hello.KeyStrings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.bouncycastle.util.io.Streams;

public class PgpAlgorithmUtils_KeyStrings {

	/*
	 * Decrypts a ciphertext String using the PGP algorithm.
	 * 
	 * @param ciphertext the ciphertext to be decrypted
	 * 
	 * @param privateKeyStr the receiver's private key as a base-64 encoded
	 * string
	 * 
	 * @param password the receiver's password
	 * 
	 * @return the decrypted text
	 * 
	 * code based on
	 * https://github.com/bcgit/bc-java/blob/master/pg/src/main/java/org/
	 * bouncycastle/openpgp/examples/ByteArrayHandler.java
	 */
	public static String decryptWithKeyString(String ciphertext, String privateKeyStr, String password)
			throws IOException, NoSuchProviderException, PGPException {
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
		PGPPrivateKey privateKey = null;
		try {
			privateKey = KeyUtilsForKeyStrings.getPrivateKeyFromStr(privateKeyStr, password);
			if (privateKey == null) {
				return "***ERROR: Unable to decrypt message - problem with key***";
			}
		} catch (PGPException e) {
			return "***ERROR: Unable to decrypt message - problem with key***";
		}
		InputStream in = new ByteArrayInputStream(ciphertext.getBytes());
		in = PGPUtil.getDecoderStream(in);

		JcaPGPObjectFactory pgpF = new JcaPGPObjectFactory(in);
		PGPEncryptedDataList enc;

		Object o = pgpF.nextObject();
		//
		// the first object might be a PGP marker packet.
		//
		if (o instanceof PGPEncryptedDataList) {
			enc = (PGPEncryptedDataList) o;
		} else {
			enc = (PGPEncryptedDataList) pgpF.nextObject();
		}

		PGPPublicKeyEncryptedData pbe = (PGPPublicKeyEncryptedData) enc.get(0);
		try {
			InputStream clear = pbe
					.getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC").build(privateKey));

			JcaPGPObjectFactory plainFact = new JcaPGPObjectFactory(clear);
			PGPCompressedData cData = (PGPCompressedData) plainFact.nextObject();
			plainFact = new JcaPGPObjectFactory(cData.getDataStream());
			PGPLiteralData ld = (PGPLiteralData) plainFact.nextObject();

			byte[] output = Streams.readAll(ld.getInputStream());
			return new String(output);
		} catch (PGPException e) {
			return "***ERROR: Unable to decrypt message***";
		}
	}

	/*
	 * Encrypts a plainttext String using the PGP algorithm.
	 * 
	 * @param plaintext the plaintext to be encrypted
	 * 
	 * @param publicKeyStr the receiver's public key as a base-64 encoded string
	 * 
	 * @return the encrypted text code based on
	 * https://github.com/bcgit/bc-java/blob/master/pg/src/main/java/org/
	 * bouncycastle/openpgp/examples/ByteArrayHandler.java
	 */
	public static String encryptWithKeyString(String plaintext, String publicKeyStr) throws IOException, PGPException {
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
		PGPPublicKey publicKey = KeyUtilsForKeyStrings.getPublicKeyFromStr(publicKeyStr);

		byte[] original = plaintext.getBytes();
		int algorithm = PGPEncryptedDataGenerator.AES_128;

		byte[] compressedData = compress(original, PGPLiteralData.CONSOLE, CompressionAlgorithmTags.ZIP);

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		OutputStream out = bOut;
		out = new ArmoredOutputStream(out);

		PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(
				new JcePGPDataEncryptorBuilder(algorithm).setSecureRandom(new SecureRandom()).setProvider("BC"));
		encGen.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(publicKey).setProvider("BC"));

		OutputStream encOut = encGen.open(out, compressedData.length);
		encOut.write(compressedData);
		encOut.close();
		out.close();

		byte[] output = bOut.toByteArray();
		return new String(output);
	}

	/*
	 * code from
	 * https://github.com/bcgit/bc-java/blob/master/pg/src/main/java/org/
	 * bouncycastle/openpgp/examples/ByteArrayHandler.java
	 */
	private static byte[] compress(byte[] clearData, String fileName, int algorithm) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(algorithm);
		OutputStream cos = comData.open(bOut); // open it with the final
												// destination

		PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();

		OutputStream pOut = lData.open(cos, // the compressed output stream
				PGPLiteralData.BINARY, fileName, // "filename" to store
				clearData.length, // length of clear data
				new Date() // current time
		);
		pOut.write(clearData);
		pOut.close();
		comData.close();
		return bOut.toByteArray();
	}

	public static void main(String[] args)
			throws IOException, PGPException, NoSuchProviderException, NoSuchAlgorithmException {

		// create user1 keys
		String user1 = "user1";
		String password1 = "password1";
		UserKeyPairStrings pair1 = KeyUtilsForKeyStrings.createNewUserKeyStrings(user1, password1);
		String public1 = pair1.getPublicKey();
		String private1 = pair1.getPrivateKey();

		// create user2 with the same password
		String user2 = "user2";
		String password2 = "password2";
		UserKeyPairStrings pair2 = KeyUtilsForKeyStrings.createNewUserKeyStrings(user2, password2);
		String public2 = pair2.getPublicKey();
		String private2 = pair2.getPrivateKey();

		// test message to user1
		String plaintext1 = "Hi user1.";
		String encrypted1 = encryptWithKeyString(plaintext1, public1);
		String decrypted1 = decryptWithKeyString(encrypted1, private1, password1);
		System.out.println("user1 input: " + plaintext1 + "\ndecrypted: " + decrypted1 + " " + "\nmatch: "
				+ plaintext1.equals(decrypted1) + "\n");

		// test message to user2
		String plaintext2 = "Hi user 2.";
		String encrypted2 = encryptWithKeyString(plaintext2, public2);
		String decrypted2 = decryptWithKeyString(encrypted2, private2, password2);
		System.out.println("user2 input: " + plaintext2 + "\ndecrypted: " + " " + decrypted2 + "\nmatch: "
				+ plaintext2.equals(decrypted2) + "\n");
	}
}
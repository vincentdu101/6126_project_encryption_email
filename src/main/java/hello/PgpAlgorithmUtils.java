package hello;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

public class PgpAlgorithmUtils {

	/*
	 * Decrypts a ciphertext String using the PGP algorithm.
	 * 
	 * @param ciphertext the ciphertext to be decrypted
	 * 
	 * @param privateKeyBytes the receiver's private key as a byte array
	 * 
	 * @param password the receiver's password
	 * 
	 * @return the decrypted text
	 * 
	 * code based on
	 * https://github.com/bcgit/bc-java/blob/master/pg/src/main/java/org/
	 * bouncycastle/openpgp/examples/ByteArrayHandler.java
	 */
	public static String decrypt(String ciphertext, byte[] privateKeyBytes, String password) {
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
		PGPPrivateKey privateKey = KeyUtils.getPrivateKey(privateKeyBytes, password);
		;
		if (privateKey == null) {
			return "***ERROR: Unable to decrypt message - problem with key***";
		}
		try {
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

			InputStream clear = pbe
					.getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC").build(privateKey));

			JcaPGPObjectFactory plainFact = new JcaPGPObjectFactory(clear);
			PGPCompressedData cData = (PGPCompressedData) plainFact.nextObject();
			plainFact = new JcaPGPObjectFactory(cData.getDataStream());
			PGPLiteralData ld = (PGPLiteralData) plainFact.nextObject();

			byte[] output = Streams.readAll(ld.getInputStream());
			return new String(output);
		} catch (Exception e) {
			return "***ERROR: Unable to decrypt message***";
		}
	}

	/*
	 * Encrypts a plainttext String using the PGP algorithm.
	 * 
	 * @param plaintext the plaintext to be encrypted
	 * 
	 * @param publicKeyBytes the receiver's public key as a byte array
	 * 
	 * @return the encrypted text code based on
	 * https://github.com/bcgit/bc-java/blob/master/pg/src/main/java/org/
	 * bouncycastle/openpgp/examples/ByteArrayHandler.java
	 */
	public static String encrypt(String plaintext, byte[] publicKeyBytes) {
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
		PGPPublicKey publicKey = KeyUtils.getPublicKey(publicKeyBytes);

		byte[] original = plaintext.getBytes();
		int algorithm = PGPEncryptedDataGenerator.AES_128;

		try {
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
		} catch (Exception e) {
			return "***ERROR: Unable to encrypt message***";
		}
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
}
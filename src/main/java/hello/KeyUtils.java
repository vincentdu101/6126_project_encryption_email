package hello;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;

import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

public class KeyUtils {

	/*
	 * Creates the PGPPublicKey object from the public key string.
	 * 
	 * @param publicKeyBytes a public key as a byte array
	 * 
	 * @return a PGPPublicKey object
	 */
	public static PGPPublicKey getPublicKey(byte[] publicKeyBytes) {
		InputStream inStream = new ByteArrayInputStream(publicKeyBytes);
		PGPPublicKey key = null;
		try {
			PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(inStream),
					new JcaKeyFingerprintCalculator());

			Iterator<PGPPublicKeyRing> keyRingIter = pgpPub.getKeyRings();
			while (keyRingIter.hasNext()) {
				PGPPublicKeyRing keyRing = (PGPPublicKeyRing) keyRingIter.next();
				Iterator<PGPPublicKey> keyIter = keyRing.getPublicKeys();
				key = (PGPPublicKey) keyIter.next();
			}
		} catch (Exception e) {
			return null;
		}
		return key;
	}

	/*
	 * Creates the PGPPrivateKey object from the secret key string.
	 * 
	 * @param privateKeyStr a private key as a byte array
	 * 
	 * @return a PGPPublicKey object
	 */
	public static PGPPrivateKey getPrivateKey(byte[] privateKeyBytes, String password) {
		char[] passwordArray = password.toCharArray();
		InputStream sInStream = new ByteArrayInputStream(privateKeyBytes);
		PGPSecretKey sKey = null;
		PGPPrivateKey pKey = null;

		try {
			PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(sInStream),
					new JcaKeyFingerprintCalculator());

			Iterator<PGPSecretKeyRing> keyRingIter = pgpSec.getKeyRings();
			while (keyRingIter.hasNext()) {
				PGPSecretKeyRing keyRing = (PGPSecretKeyRing) keyRingIter.next();
				Iterator<PGPSecretKey> keyIter = keyRing.getSecretKeys();
				sKey = (PGPSecretKey) keyIter.next();
				pKey = sKey.extractPrivateKey(
						new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(passwordArray));
			}
		} catch (Exception e) {
			return null;
		}
		return pKey;
	}
}

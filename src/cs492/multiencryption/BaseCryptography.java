package cs492.multiencryption;

import org.apache.commons.io.FileUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;



public class BaseCryptography {

	// Class variables
	static final SecureRandom RANDOM = new SecureRandom();
	private static final int COUNT = 1000;
	static final String ALGORITHM = "PBEWithHmacSHA256AndAES_256";

	// For the purpose of demonstration, we will limit the file input/output to
	// only one
	private static final String FILENAME = "volume.txt";


	public static String getAlgorithm() {
	 return ALGORITHM;
	}

	// Generate random number according to size
	// Output: randomly generated volume
	public static byte[] randomZeroes (int size) {
		// Create array according to size
		byte[] array = new byte[size];


		// char variable for storing random char generated by SecureRandom() temporarily
		byte pudding;
		// fill the array with random number
		for (int i = 0; i < size; i++) {
			// Generate a random int and put it into pudding
			pudding = (byte) RANDOM.nextInt();
			// Put the current pudding into array[index]
			array[i] = pudding;
		}

		return array;
	} // end randomZeroes()

	// output a string as text file (UTF-16)
	public static void saveVolume(byte[] b) throws IOException {
		FileUtils.writeByteArrayToFile(new File(FILENAME), b);
	}

	// Read file content (UTF-16)
	public static byte[] loadVolume()throws IOException {
		File file = new File(FILENAME);
		return FileUtils.readFileToByteArray(file);
	} // end loadVolume

	// Get random salt
	static byte[] getSalt() {
		// use SecureRandom to generate bytes
		byte[] retVal = new byte[16];
		RANDOM.nextBytes(retVal);

		return retVal;
	} // end getSalt()


	// Password hashing with salt
	static SecretKey passwordHash(char[] password)
		throws NoSuchAlgorithmException, InvalidKeySpecException {

		PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
		SecretKeyFactory keyFac = SecretKeyFactory.getInstance(ALGORITHM);

		return keyFac.generateSecret(pbeKeySpec);
	} // end passwordHash()

	// Get random IV
	static IvParameterSpec getIV(byte[] b) {
		return new IvParameterSpec(b);
	} // end getIV()






	// Encrypt txt using PBE method with salt
	public static CryptoData encryptVolume(SecretKey key, CryptoData data,
	                                       byte[] salt)
	       throws InvalidAlgorithmParameterException, InvalidKeyException,
	       NoSuchPaddingException, NoSuchAlgorithmException,
	       BadPaddingException, IllegalBlockSizeException, IOException {

		PBEParameterSpec params = new PBEParameterSpec(salt, COUNT);

		// Encryption
		Cipher pbeCipher = Cipher.getInstance(ALGORITHM);
		pbeCipher.init(Cipher.ENCRYPT_MODE, key, params);

		return new CryptoData(pbeCipher.doFinal(data.getCryptoByte()), pbeCipher);

	} // end encryptVolume()

	// Get the parameter of encryption cipher
	// then convert it into AlgorithmParameters
	static AlgorithmParameters getEncryptCipher(SecretKey key, byte[] salt)
	        throws NoSuchPaddingException, NoSuchAlgorithmException,
	               InvalidAlgorithmParameterException, InvalidKeyException {

		PBEParameterSpec params = new PBEParameterSpec(salt, COUNT);
		// Encryption
		Cipher c = Cipher.getInstance(ALGORITHM);
		c.init(Cipher.ENCRYPT_MODE, key, params);

		return c.getParameters();
	} // end getEncryptCipher()

	/*
	   Decryption
	   Using the same PBEParameterSpec for decryption doesn't work
	   We will use different approach when it comes to decryption
	*/
	public static CryptoData decryptVolume(SecretKey key, CryptoData data,
	                                       byte[] salt)
	       throws NoSuchPaddingException, NoSuchAlgorithmException,
	              BadPaddingException, IllegalBlockSizeException, IOException,
	              InvalidAlgorithmParameterException, InvalidKeyException {

		AlgorithmParameters params;

		// if data.getParams() is null,
		if (data.getParams() == null) {
			params = getEncryptCipher(key, salt);
		} else { // else use data.params
			params = data.getParams();
		}

		// Specify algorithm
		Cipher pbeCipher = Cipher.getInstance(ALGORITHM);
		// Initializing it with PBE Cipher with key and parameters
		pbeCipher.init(Cipher.DECRYPT_MODE, key, params);

		return new CryptoData(pbeCipher.doFinal(data.getCryptoByte()), pbeCipher);
	} // end encryptVolume()

} // end class()

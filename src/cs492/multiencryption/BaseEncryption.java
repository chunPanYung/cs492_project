package cs492.multiencryption;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class BaseEncryption extends Tea {

	// Class variables
	private static final SecureRandom RANDOM = new SecureRandom();
	private static final int KEYLEN = 256;


	// Generate random number according to size
	// Output: randomly generated volume
	public static char[] randomZeroes (int size) {
		// Create array according to size
		char[] array = new char[size];


		// char variable for storing random char generated by SecureRandom() temporarily
		char pudding;
		// fill the array with random number
		for (int i = 0; i < size; i++) {
			// Generate a random int and put it into pudding
			pudding = (char) RANDOM.nextInt();
			// Put the current pudding into array[index]
			array[i] = pudding;
		}

		return array;
	} // end randomZeroes()

	// output a string as text file (UTF-16)
	public static void saveVolume(String input) throws IOException {
		// Specify FileOutputStream
		FileOutputStream outputStream = new FileOutputStream("Pudding.txt");
		// Specify encoding
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-16BE");
		// BufferedWriter
		BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

		// Write file
		bufferedWriter.write(input);
		// close file
		bufferedWriter.close();


	}

	// Read file content (UTF-16)
	public static void loadVolume(String file) throws IOException {
		// Specify input stream
		FileInputStream inputStream = new FileInputStream(file);
		// Specify encoding
		InputStreamReader reader = new InputStreamReader(inputStream, "UTF-16BE");

		// Read character one by one and print them out on screen
		int character;
		while ( (character = reader.read()) != -1 ) {
			System.out.print((char)character);
		}
		// close file reader
		reader.close();
	} // end loadVolume



	// Hashing password and return 256 bits (long[2])
	// Output long array
	static String passwordHash(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {

		PBEKeySpec spec = new PBEKeySpec(password, salt, Short.MAX_VALUE, KEYLEN);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		byte[] hash = keyFactory.generateSecret(spec).getEncoded();
		Base64.Encoder enc = Base64.getEncoder();

		// return String
		return enc.encodeToString(hash);
	}


	// Get the randomly generated salt
	// Package-private so we can test this method
	static byte[] getNextSalt() {
		// byte array
		byte[] salt = new byte[16];
		RANDOM.nextBytes(salt);

		// return salt
		return salt;
	}

	// Casting and concatenate byte[] into long[]
	// return long[]
	// It's assume that char[] is in the multiplication of 8 (need improvement)
	// It maybe useless for the moment
	private static long[] toLongArray(byte[] arr) {
		// Array should be in the number of 8x,
		// because long int is 8 bytes long
		// and char is 2 bytes long
		if (arr.length % 8 != 0) {
			throw new ArrayIndexOutOfBoundsException();
		} // end if statement

		// int i: length of return array == (arr.length / 8)
		int longSize = arr.length / 8;
		// Initializing long array
		long[] retVal = new long[longSize];

		// establish i as the last array index of long int,
		// and k as the last array index of char.
		int i = longSize - 1;
		int k = arr.length - 1;
		int byteInLong = 0;

		// use for loop to cast char[] into long[]
		while (i >= 0) {
			// For every long(64-bit), we put 8 bytes (8-bit each) into it
			while (byteInLong < 16) {
				// shift left by 8 bits
				retVal[i] = retVal[i] << 8;
				// Casting 8 bytes into single long int
				retVal[i] = retVal[i] & arr[k];
				// increment charInLong: it indicates how many char has cast into long so far
				byteInLong++;
				// decrease current index of char array
				k--;
			} // end while loop

			// Decrease i
			i--;
		} // end while loop

		return retVal;
	} // end toLongArray()

	// Encrypt the text using Tea Encryption and hashed password
	// The length of hash is 44
	public void encryptVolume(char[] arr, String hash) {



	}

} // end class()

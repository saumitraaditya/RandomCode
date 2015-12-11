
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import sun.misc.BASE64Encoder;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security._;

object secure extends App {
  Security.addProvider(new com.sun.crypto.provider.SunJCE());
  var  strDataToEncrypt = new String();
  var strCipherText = new String();
  var strDecryptedText = new String();
  val keyGen:KeyGenerator = KeyGenerator.getInstance("AES");
  keyGen.init(256);
  val secretKey = keyGen.generateKey();
  val AES_KEYLENGTH = 256;
  var iv = Array.fill[Byte](16)(1.toByte); // use SRNG here.
  val prng = new SecureRandom();
  prng.nextBytes(iv);
  val aesCipherForEncryption = Cipher.getInstance("AES/CBC/PKCS5PADDING"); 
  aesCipherForEncryption.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
  strDataToEncrypt = "Hello World of Encryption using AES the date today is Friday 11 December 2015";
  var byteDataToEncrypt = strDataToEncrypt.getBytes();
  val byteCipherText = aesCipherForEncryption.doFinal(byteDataToEncrypt);
  strCipherText = new BASE64Encoder().encode(byteCipherText);
  System.out.println("Cipher Text generated using AES with CBC mode and PKCS5 Padding is " +strCipherText);
  
  
  val aesCipherForDecryption = Cipher.getInstance("AES/CBC/PKCS5PADDING");
  aesCipherForDecryption.init(Cipher.DECRYPT_MODE, secretKey,new IvParameterSpec(iv));
  val byteDecryptedText = aesCipherForDecryption.doFinal(byteCipherText);
  strDecryptedText = new String(byteDecryptedText);
  System.out.println(" Decrypted Text message is " + strDecryptedText);
  
}

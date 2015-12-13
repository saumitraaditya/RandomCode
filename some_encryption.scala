import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security._;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

object secure extends App {
  
  
  //-----------------------AES----------------------------------
  /*Security.addProvider(new com.sun.crypto.provider.SunJCE());
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
  System.out.println(" Decrypted Text message is " + strDecryptedText);*/
  //------------------------------------------------------------  
  
  //---------------------- RSA -----------------------------
  Security.addProvider(new com.sun.crypto.provider.SunJCE());
  val kpg = KeyPairGenerator.getInstance("RSA");
  kpg.initialize(2048);
  val kp = kpg.genKeyPair();
  var publicKey = kp.getPublic();
  val privateKey = kp.getPrivate();
  val fact = KeyFactory.getInstance("RSA");
  //--------------- public key to string and back--------------------
  val strPkey = new BASE64Encoder().encode(publicKey.getEncoded());
  val PkeyBytes = new BASE64Decoder().decodeBuffer(strPkey);
  val spec =  new X509EncodedKeySpec(PkeyBytes);
  publicKey = fact.generatePublic(spec);
  //-----------------------------------------------------------------
  val cipher = Cipher.getInstance("RSA");
  cipher.init(Cipher.ENCRYPT_MODE, publicKey);
  val strDataToEncrypt = "Hello World of Encryption using AES the date today is Friday 11 December 2015";
  var byteDataToEncrypt = strDataToEncrypt.getBytes();
  val byteCipherText = cipher.doFinal(byteDataToEncrypt);
  var strCipherText = new BASE64Encoder().encode(byteCipherText);
  System.out.println("Cipher Text generated using RSA is " +strCipherText);
  
  
  cipher.init(Cipher.DECRYPT_MODE, privateKey);
  val byteDecipherText = new BASE64Decoder().decodeBuffer(strCipherText);
  val byteDecryptedText = cipher.doFinal(byteDecipherText);
  val strDecryptedText = new String(byteDecryptedText);
  System.out.println(" Decrypted Text message is " + strDecryptedText);
  
  //----------------------------------------------------------
}

/*
 * Links
 * http://stackoverflow.com/questions/22900570/key-from-string-in-java-rsa
 * http://pastebin.com/TEpMBJK5
 * http://www.javamex.com/tutorials/cryptography/rsa_encryption_2.shtml
 * http://www.javamex.com/tutorials/cryptography/rsa_encryption.shtml
 * http://stackoverflow.com/questions/28809225/send-rsa-public-key-in-java-to-php-server
 * http://www.javamex.com/tutorials/cryptography/rsa_encryption.shtml
 * http://stackoverflow.com/questions/29396164/retrieving-rsa-modulus-and-exponent-from-byte-array
 * https://javadigest.wordpress.com/2012/08/26/rsa-encryption-example/
 * */
 

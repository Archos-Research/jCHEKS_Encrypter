package com.archosResearch.jCHEKS.encrypter;

import com.archosResearch.jCHEKS.concept.encrypter.AbstractEncrypter;
import com.archosResearch.jCHEKS.concept.exception.EncrypterException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 *
 * @author Thomas Lepage thomas.lepage@hotmail.ca
 */
public class RijndaelEncrypter extends AbstractEncrypter{

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String ALGORITHM = "AES";
    
    private final Cipher cipher;
    
    private final int keyLenght = 16;
    private final int ivLenght = 16;
    
    public RijndaelEncrypter() throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.cipher = Cipher.getInstance(TRANSFORMATION);
    }
    
    @Override
    public String encrypt(String text, byte[] keyByte) throws EncrypterException {

        byte[] encryptedData;
        
        byte[] key = new byte[this.keyLenght];
        byte[] iv = new byte[this.ivLenght];
        
        System.arraycopy(keyByte, 0, key, 0, this.keyLenght);
        System.arraycopy(keyByte, this.keyLenght - 1, iv, 0, this.ivLenght);
        
        try {
            IvParameterSpec IVParamSpec = new IvParameterSpec(iv);
            SecretKey password = new SecretKeySpec(key, ALGORITHM);

            this.cipher.init(Cipher.ENCRYPT_MODE, password, IVParamSpec);
            encryptedData = this.cipher.doFinal(text.getBytes("UTF8"));
            
            return Base64.getEncoder().encodeToString(encryptedData);
            
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException ex) {
            throw new EncrypterException("Rijndael Encryption error", ex);
        }
    }

    @Override
    public String decrypt(String text, byte[] keyByte) throws EncrypterException {        
        
        try {
            byte[] key = new byte[this.keyLenght];
            byte[] iv = new byte[this.ivLenght];

            System.arraycopy(keyByte, 0, key, 0, this.keyLenght);
            System.arraycopy(keyByte, this.keyLenght - 1, iv, 0, this.ivLenght);
            
            IvParameterSpec IVParamSpec = new IvParameterSpec(iv);
            SecretKey password = new SecretKeySpec(key, ALGORITHM);

            this.cipher.init(Cipher.DECRYPT_MODE, password, IVParamSpec);
            byte[] decodedData = Base64.getDecoder().decode(text);
            byte[] decryptedData = this.cipher.doFinal(decodedData);
            
            return new String(decryptedData);          
            
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            throw new EncrypterException("Rijndael Decryption error", ex);
        }
    }

    @Override
    public int getByteNeeded() {
        return (this.keyLenght + this.ivLenght) * 8;
    }
}

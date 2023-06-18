package com.example.hentaiminesweeper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Map.Entry;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI;
import org.jasypt.util.text.BasicTextEncryptor;

import com.example.hentaiminesweeper.structs.User;
import com.google.gson.Gson;

public class Utils {

    protected static String encryptionKey = null;

    protected static void getEncryptionKey(){

        try {
            
            File keyFile = new File(System.getProperty("user.dir")).getParentFile();
            Scanner scanner = new Scanner(new File(keyFile.getAbsolutePath() + "/hms-key.txt"));

            String key = scanner.nextLine();
            Utils.encryptionKey = key;
            scanner.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static User getUserAccount(){

        File loginFile = new File(System.getProperty("user.dir") + "/login.dat");
        if(loginFile.exists()){

            try {
                
                FileInputStream fis;
                fis = new FileInputStream(loginFile);
                ObjectInputStream ois = new ObjectInputStream(fis);

                User myAccount = (User) ois.readObject();
                
                System.out.println("Logged in as " + myAccount.username);
                Main.account = myAccount;

                return myAccount;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String encryptPasswordValue(String password){
	
        BasicTextEncryptor enc = new BasicTextEncryptor();
        enc.setPasswordCharArray(encryptionKey.toCharArray());

        String encryptedText = enc.encrypt(password);
        return encryptedText;
    }

    public static String decryptPasswordValue(String password){

        BasicTextEncryptor enc = new BasicTextEncryptor();
        enc.setPasswordCharArray(encryptionKey.toCharArray());

        String decryptedText = enc.decrypt(password);
        return decryptedText;
    }

    public static void login(String username, String password){

        DatabaseConnection.getUserAccountOfName(username, new DatabaseConnection.SynchronousQueryCompletionListener() {
            
            @Override
            public void queryFinished(Object[] returnValue) {

                if(returnValue == null){

                    Window.sendMessage("Login was not found","The account you seem to be trying to log into does not exist", false);
                    return;
                }
            
                User myAccount = (User) returnValue[0];
                // Validate login
                if(password.equals(decryptPasswordValue(myAccount.password))){

                    // Login success
                    Main.account = myAccount;
                    myAccount.writeMyData();

                    Window.sendMessage("Login success", "You were logged in successfully", false);
                }else{

                    Window.sendMessage("Passwords do not match","The inputed password does not match the registered password for this account", false);
                }
            }
        });
    }
}

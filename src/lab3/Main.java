package lab3;

public class Main {

    public static void main(String[] args) {

        KeyGenPair pair = new KeyGenPair();

        System.out.println("Testing keys");

        String msg = "Hello nigga bobby!";

        String enMsg = RSA.encrypt(pair.getPublicKey(),msg);
        System.out.println("Encrypted msg: "+enMsg);

        String deMsg = RSA.decrypt(pair.getPrivateKey(),enMsg);
        System.out.println("Decrypted Message: "+deMsg);


    }
}

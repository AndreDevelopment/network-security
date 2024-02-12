package lab2.project3;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class Helper {

    public static byte[] sign(String message, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(message.getBytes());
            return signature.sign();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X", b));
        }
        return hexString.toString();
    }

    public static boolean verifySignature(String message, byte[] signature, PublicKey publicKey) {
        try {
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify(publicKey);
            sign.update(message.getBytes());
            return sign.verify(signature);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean verifyTimestamp(long timestamp, long maxAllowedTime) {
        long currentTime = System.currentTimeMillis();
        // allowing max time diff of 5 sec
        return Math.abs(currentTime - timestamp) <= maxAllowedTime;
    }
}

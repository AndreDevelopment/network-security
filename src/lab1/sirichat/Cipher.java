package lab1.sirichat;



public class Cipher {

//    public static void main(String[] args) {
//        Cipher cipher = new Cipher();
//
//        String msg = "I'm siri";
//        String mod_en  =cipher.encryption(msg);
//
//        System.out.println("The Mod encrypt is: "+mod_en);
//        System.out.println("The decrypted message is: "+cipher.decryption(mod_en));
//    }


    /*
    * Offset is used as a means of converting the ascii code value -> alphabet value (0-25)
    * keyIdx is simply a pointer to which letter we are using to encrypt the current msg Letter
    * */
    public static String encryption(String msg){

        StringBuilder encryptedMsg = new StringBuilder();

        String key = "tmu";
        int offset,keyIdx =0;

        for (char c : msg.toCharArray()){

            if (Character.isAlphabetic(c)){

                offset = Character.isUpperCase(c)? 'A':'a';
                key = Character.isUpperCase(c)? key.toUpperCase() : key.toLowerCase();

                int sum = ((c - offset) + (key.charAt(keyIdx) - offset)) % 26;
                encryptedMsg.append((char)(sum+offset));

                //keyIdx iterates through our key, wrap back if keyIdx is 2
                keyIdx = keyIdx == 2? 0:keyIdx+1;

            }else{
                encryptedMsg.append(c);
            }

        }//end of the for loop

        return encryptedMsg.toString();
    }

    /*
     * Offset is used as a means of converting the ascii code value -> alphabet value (0-25)
     * keyIdx is simply a pointer to which letter we are using to encrypt the current msg Letter
     * */
    public static String decryption(String msg){

        StringBuilder decryptedMsg= new StringBuilder();
        String key = "tmu";
        int offset,keyIdx =0;

        for (char c : msg.toCharArray()){

            if (Character.isAlphabetic(c)){

                offset = Character.isUpperCase(c)? 'A':'a';
                key = Character.isUpperCase(c)? key.toUpperCase():key.toLowerCase();

                int diff = ((c - offset) - (key.charAt(keyIdx) - offset)) % 26;
                //Check for wrap back
                if (diff < 0)
                    diff += 26;
                decryptedMsg.append((char)(diff+offset));

                //keyIdx iterates through our key, wrap back if keyIdx is 2
                keyIdx = keyIdx == 2? 0:keyIdx+1;

            }else{
                decryptedMsg.append(c);
            }
        }

        return decryptedMsg.toString();
    }
}

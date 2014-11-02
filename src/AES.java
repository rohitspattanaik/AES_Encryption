/*
 * This class contains the main method for running the program.
 * It contains objects of Encryptor and Decryptor which handle the
 * actual encryption and decryption. Arguments are parsed and
 * passed on to the appropriate function.
 *
 * To run the program, type the following into the command line:
 * java AES option keyFile inputFile
 *      option    e     option to encrypt
 *                d     option to decrypt
 *      keyFile         file containing 128 bit key already in hex
 *      inputFile       file containing plaintext. Each line of
 *                      the text should be 128 bit hex
 *
 */

public class AES {

    private static String flag;
    private static String keyFile;
    private static String inputFile;

    private static Encryptor encryptor;
    private static Decryptor decryptor;


    public static void main(String args[]) {

        /*
        if(!parseArgs(args)) {
            System.out.println("Initialization failed. Exiting");
            return;
        }
        */

        /*testing code*/
        String temp = "3243f6a8885a308d313198a2e0370734";
        encryptor = new Encryptor();
        encryptor.addToMatrix(temp);
        encryptor.printState();
        /*end testing code*/


    }

    private static boolean parseArgs(String args[]) {
        /*
         * Expecting the following structure from the command line:
         * java AES option keyFile inputFile
         */
        if(args.length != 3) {
            System.out.println("Error- Incorrect number of arguments");
            return false;
        }

        flag = args[0].toLowerCase();
        if(!flag.equals("e") || !flag.equals("d")) {
            System.out.println("Error- Encryption/Decryption flag incorrect");
            return false;
        }

        keyFile = args[1];

        inputFile = args[2];

        return true;
    }


}

import java.io.BufferedWriter;
import java.io.File;
import java.util.Scanner;


public class Decryptor {
    private String key;
    private Scanner keyScanner;

    private String input;
    private Scanner fileScanner;

    private File outputFile;
    private String output;
    private BufferedWriter outputWriter;

    private int cipherTextMatrix[][] = new int[4][4];
    private int keyMatrix[][] = new int[4][4];
    private int roundKeyMatrix[][] = new int[4][44]; //collection of roundKeys for all rounds.

    private static final int nk = 4; //constant for 128 bit implementation
}

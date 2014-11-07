import java.io.*;
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

    /*Following print methods useful for debugging and keeping track of progress of encryption*/
    public void printState() {
        for(int x = 0; x < 4; x++) {
            for(int y = 0; y < 4; y++) {
                String temp = Integer.toHexString(cipherTextMatrix[x][y]);
                if(temp.length() < 2) {
                    temp = "0" + temp;
                }
                System.out.print(temp + " ");
            }
            System.out.println();
        }
    }

    public void printKey() {
        for(int x = 0; x < 4; x++) {
            for(int y = 0; y < 4; y++) {
                String temp = Integer.toHexString(keyMatrix[x][y]);
                if(temp.length() < 2) {
                    temp = "0" + temp;
                }
                System.out.print(temp + " ");
            }
            System.out.println();
        }
    }

    public void printRoundKeys() {
        for(int x = 0; x < 4; x++) {
            for(int y = 0; y < 44; y++) {
                String temp = Integer.toHexString(roundKeyMatrix[x][y]);
                if(temp.length() < 2) {
                    temp = "0" + temp;
                }
                if(y%4 == 0) {
                    System.out.print(" ");
                }
                System.out.print(temp + " ");
            }
            System.out.println();
        }
    }

    public boolean addFiles(String inputFile, String keyFile) {
        input = inputFile;
        output = inputFile + ".dec";
        key = keyFile;
        return readFiles();
    }

    /*Opens files and returns success if scanners and writer opened*/
    public boolean readFiles() {
        File keyFile = new File(key);
        File inputFile = new File(input);
        outputFile = new File(output);

        try {
            fileScanner = new Scanner(inputFile);
            keyScanner = new Scanner(keyFile);
        }
        catch(FileNotFoundException e) {
            System.out.println("Error: Input/Key File Not Found");
            outputFile.delete();
            return false;
        }

        try {
            outputWriter = new BufferedWriter(new FileWriter(outputFile));
        }
        catch(IOException e) {
            System.out.println("Error: Unable to create output writer");
            outputFile.delete();
            return false;
        }
        return true;
    }

    /*Converts matrix into a string following a column major pattern*/
    public String revertMatrix(int[][] matrix) {
        String text = "";
        for(int col = 0; col < matrix[0].length; col++) {
            for(int row = 0; row < matrix.length; row++) {
                String temp = Integer.toHexString(matrix[row][col]);
                temp = temp.toUpperCase();
                if(temp.length() < 2) {
                    temp = "0" + temp;
                }
                text = text + temp;
            }
        }
        text = text + "\n";
        return text;
    }

    /*Inserts a given line into plainTextMatrix by column*/
    public void addToCipherMatrix(String inputLine) {
        if(inputLine.length() < 32) {
            int diff = 32 - inputLine.length();
            String tempPad = "";
            for(int i = 0; i < diff; i++) {
                tempPad = tempPad + "0";
            }
            inputLine = inputLine + tempPad;
        }

        if(inputLine.length() > 32) {
            inputLine = inputLine.substring(0, 32);
        }

        if(inputLine.length() != 32) {
            outputFile.delete();
            assert false; //Panic! Cannot continue if here
        }

        //Adding elements by column
        for(int y = 0; y < 4; y++) {
            for(int x = 0; x < 4; x++) {
                cipherTextMatrix[x][y] = Integer.parseInt(inputLine.substring(0,2), 16);
                if(inputLine.length() != 0) {
                    inputLine = inputLine.substring(2);
                }
            }
        }
    }

    /*Same as above except for keyMatrix*/
    public void addToKeyMatrix(String inputLine) {
        if(inputLine.length() != 32) {
            outputFile.delete();
            assert false; //Panic! Cannot continue if here
        }


        //Adding elements by column
        for(int y = 0; y < 4; y++) {
            for(int x = 0; x < 4; x++) {
                keyMatrix[x][y] = Integer.parseInt(inputLine.substring(0,2), 16);
                if(inputLine.length() != 0) {
                    inputLine = inputLine.substring(2);
                }

            }
        }
    }

    /*Rotates a given column of the matrix upwards*/
    public void rotateCol(int col, int[][] matrix) {
        int temp = matrix[0][col];
        int row = 1;
        while(row < matrix.length) {
            matrix[row - 1][col] = matrix[row++][col];
        }
        matrix[row - 1][col] = temp;
    }

    /*subBytesAux only used for roundkeys here. See inverseSubBytes otherwise*/
    public void subBytesAux(int y, int[][] matrix) {
        for(int x = 0; x < matrix.length; x++) {
            String temp = Integer.toHexString(matrix[x][y]);
            if(temp.length() != 2) {
                temp = "0" + temp;
            }
            int xVal = Integer.parseInt(temp.substring(0, 1), 16);
            int yVal = Integer.parseInt(temp.substring(1), 16);
            matrix[x][y] = Tables.S_BOX[xVal][yVal];
        }

    }

    /*Calculates all roundkeys and populates the matrix*/
    public void setRoundKeys() {
        int row, col;
        for(col = 0; col < 4; col++) {
            for(row = 0; row < 4; row++) {
                roundKeyMatrix[row][col] = keyMatrix[row][col];
            }
        }
        for(; col < roundKeyMatrix[0].length; col++) {
            if(col % 4 == 0) {
                for(row = 0; row < 4; row++) {
                    roundKeyMatrix[row][col] = roundKeyMatrix[row][col - 1];
                }
                rotateCol(col, roundKeyMatrix);
                subBytesAux(col, roundKeyMatrix);
                int[] rcon = {(Tables.RCON[col/nk]), 0x0, 0x0, 0x0};
                for(row = 0; row < 4; row++) {
                    roundKeyMatrix[row][col] = roundKeyMatrix[row][col - 4] ^ roundKeyMatrix[row][col] ^ rcon[row];
                }
            }
            else {
                for(row = 0; row < 4; row++) {
                    roundKeyMatrix[row][col] = roundKeyMatrix[row][col - 4] ^ roundKeyMatrix[row][col - 1];
                }

            }
        }
    }

    /*Adds appropriate roundkey based on round*/
    public void addRoundKey(int round) {
        //round = 10 - round;
        for(int col = 0; col < cipherTextMatrix[0].length; col++) {
            for(int row = 0; row < cipherTextMatrix.length; row++) {
                cipherTextMatrix[row][col] = cipherTextMatrix[row][col] ^ roundKeyMatrix[row][(round * 4) + col];
            }
        }
    }

    /*Calls rotateRow for each column*/
    public void inverseShiftRows() {
        for(int row = 0; row < cipherTextMatrix.length; row++) {
            for(int count = 0; count < row; count++) {
                inverseRotateRow(row, cipherTextMatrix);
            }
        }
    }

    /*Rotates a given row of the matrix to the left*/
    public void inverseRotateRow(int row, int[][] matrix) {
        int temp = matrix[row][matrix.length - 1];
        int col = matrix.length - 2;
        while(col >= 0) {
            matrix[row][col + 1] = matrix[row][col--];
        }
        matrix[row][col + 1] = temp;
    }

    /*Substitutes elements in matrix using S_BOX in Tables*/
    public void inverseSubBytes() {
        for(int col = 0; col < cipherTextMatrix.length; col++) {
            inverseSubBytesAux(col, cipherTextMatrix);
        }
    }

    private void inverseSubBytesAux(int y, int[][] matrix) {
        for(int x = 0; x < matrix.length; x++) {
            String temp = Integer.toHexString(matrix[x][y]);
            if(temp.length() != 2) {
                temp = "0" + temp;
            }
            int xVal = Integer.parseInt(temp.substring(0, 1), 16);
            int yVal = Integer.parseInt(temp.substring(1), 16);
            matrix[x][y] = Tables.INVERSE_S_BOX[xVal][yVal];
        }

    }
    /*Multiplication for mixColumns*/
    private byte mul (int a, int b) {
        int inda = (a < 0) ? (a + 256) : a;
        int indb = (b < 0) ? (b + 256) : b;

        if ( (a != 0) && (b != 0) ) {
            int index = (Tables.LogTable[inda] + Tables.LogTable[indb]);
            byte val = (byte)(Tables.AlogTable[ index % 255 ] );
            return val;
        }
        else
            return 0;
    }

    public void inverseMixColumns() {
        for(int col = 0; col < cipherTextMatrix.length; col++) {
            inverseMixColumnsAux(col);
        }
    }

    public void inverseMixColumnsAux (int c) {
        int a[] = new int[4];

        // note that a is just a copy of st[.][c]
        for (int i = 0; i < 4; i++)
            a[i] = (cipherTextMatrix[i][c] & 0xFF);

        cipherTextMatrix[0][c] = ((mul(0xE,a[0]) ^ mul(0xB,a[1]) ^ mul(0xD, a[2]) ^ mul(0x9,a[3]))) & 0xFF;
        cipherTextMatrix[1][c] = ((mul(0xE,a[1]) ^ mul(0xB,a[2]) ^ mul(0xD, a[3]) ^ mul(0x9,a[0]))) & 0xFF;
        cipherTextMatrix[2][c] = ((mul(0xE,a[2]) ^ mul(0xB,a[3]) ^ mul(0xD, a[0]) ^ mul(0x9,a[1]))) & 0xFF;
        cipherTextMatrix[3][c] = ((mul(0xE,a[3]) ^ mul(0xB,a[0]) ^ mul(0xD, a[1]) ^ mul(0x9,a[2]))) & 0xFF;
    }

    public boolean decrypt() {
        String key = keyScanner.next();
//        if(keyScanner.hasNextLine()) {
//            System.out.println("Error: More than one key found in key file. Aborting Encryption");
//            return false;
//        }
        if(key.length() != 32) {
            System.out.println("Error: Key is not 128 bits. Aborting Decryption");
            return false;
        }
        addToKeyMatrix(key);
        setRoundKeys();
        //System.out.println("Round keys");
        //printRoundKeys();

        while(fileScanner.hasNextLine()) {
            String cipherText = fileScanner.nextLine();
            addToCipherMatrix(cipherText);

            //System.out.println("Starting with");
            //printState();


            for(int i = 10; i > 0; i--) {
                //System.out.println(i);
                addRoundKey(i);
                //System.out.println("After roundkey " + i);
                //printState();
                if(i != 10) {
                    inverseMixColumns();
                    //System.out.println("After iMix");
                    //printState();
                }
                inverseShiftRows();
                //System.out.println("After iShift");
                //printState();
                inverseSubBytes();
                //System.out.println("After iSub");
                //printState();
            }
            addRoundKey(0);
            //System.out.println("After roundkey 0");
            //printState();

            try {
                outputWriter.write(revertMatrix(cipherTextMatrix));
            } catch (IOException e) {
                System.out.println("Error writing to file. Decryption Failed");
                outputFile.delete();
                return false;
            }
        }

        try {
            outputWriter.close();
        }
        catch (IOException e) {
            System.out.println("Unable to close outputWriter. Deleting decrypted file");
            outputFile.delete();
            return false;
        }
        keyScanner.close();
        fileScanner.close();
        System.out.println("Decryption Complete");
        return true;
    }

}

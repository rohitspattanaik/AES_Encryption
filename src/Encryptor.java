import java.io.*;
import java.util.Scanner;


public class Encryptor {

    private String key;
    private Scanner keyScanner;

    private String input;
    private Scanner fileScanner;
    private double inputSize = 0;

    private File outputFile;
    private String output;
    private BufferedWriter outputWriter;

    private int plainTextMatrix[][] = new int[4][4];
    private int keyMatrix[][] = new int[4][4];
    private int roundKeyMatrix[][] = new int[4][44]; //collection of roundKeys for all rounds.

    private static final int nk = 4; //constant for 128 bit implementation

    /*Following print methods useful for debugging and keeping track of progress of encryption*/
    public void printState() {
        for(int x = 0; x < 4; x++) {
            for(int y = 0; y < 4; y++) {
                String temp = Integer.toHexString(plainTextMatrix[x][y]);
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

    /*Opens files and returns success if scanners and writer opened*/
    private boolean readFiles() {
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
    private String revertMatrix(int[][] matrix) {
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
    private void addToTextMatrix(String inputLine) {
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
                plainTextMatrix[x][y] = Integer.parseInt(inputLine.substring(0,2), 16);
                if(inputLine.length() != 0) {
                    inputLine = inputLine.substring(2);
                }
            }
        }
    }

    /*Same as above except for keyMatrix*/
    private void addToKeyMatrix(String inputLine) {
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

    /*Substitutes elements in matrix using S_BOX in Tables*/
    private void subBytes() {
        for(int col = 0; col < plainTextMatrix.length; col++) {
            subBytesAux(col, plainTextMatrix);
        }
    }

    /*subBytesAux provides the opportunity to reuse code while producing roundkeys*/
    private void subBytesAux(int y, int[][] matrix) {
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

    /*Calls rotateRow for each column*/
    private void shiftRows() {
        for(int row = 0; row < plainTextMatrix.length; row++) {
            for(int count = 0; count < row; count++) {
                rotateRow(row, plainTextMatrix);
            }
        }
    }

    /*Rotates a given row of the matrix to the left*/
    private void rotateRow(int row, int[][] matrix) {
        int temp = matrix[row][0];
        int col = 1;
        while(col < matrix.length) {
            matrix[row][col - 1] = matrix[row][col++];
        }
        matrix[row][col - 1] = temp;
    }

    /*Rotates a given column of the matrix upwards*/
    private void rotateCol(int col, int[][] matrix) {
        int temp = matrix[0][col];
        int row = 1;
        while(row < matrix.length) {
            matrix[row - 1][col] = matrix[row++][col];
        }
        matrix[row - 1][col] = temp;
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

    private void mixColumns() {
        for(int col = 0; col < plainTextMatrix.length; col++) {
            mixColumnsAux(col);
        }
    }

    private void mixColumnsAux (int c) {
        int a[] = new int[4];
        //a[] is a copy of plainTextMatrix
        for (int i = 0; i < 4; i++)
            a[i] = (plainTextMatrix[i][c] & 0xFF);

        plainTextMatrix[0][c] = ((mul(2,a[0]) ^ a[2] ^ a[3] ^ mul(3,a[1]))) & 0xFF;
        plainTextMatrix[1][c] = ((mul(2,a[1]) ^ a[3] ^ a[0] ^ mul(3,a[2]))) & 0xFF;
        plainTextMatrix[2][c] = ((mul(2,a[2]) ^ a[0] ^ a[1] ^ mul(3,a[3]))) & 0xFF;
        plainTextMatrix[3][c] = ((mul(2,a[3]) ^ a[1] ^ a[2] ^ mul(3,a[0]))) & 0xFF;
    }

    /*Calculates all roundkeys and populates the matrix*/
    private void setRoundKeys() {
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
    private void addRoundKey(int round) {
        for(int col = 0; col < plainTextMatrix[0].length; col++) {
            for(int row = 0; row < plainTextMatrix.length; row++) {
                plainTextMatrix[row][col] = plainTextMatrix[row][col] ^ roundKeyMatrix[row][(round * 4) + col];
            }
        }
    }

    /*Stored file names and calls readFiles*/
    public boolean addFiles(String inputFile, String keyFile) {
        input = inputFile;
        output = inputFile + ".enc";
        key = keyFile;
        return readFiles();
    }

    /*Method called to allow encryption*/
    public boolean encrypt() {
        double time = 0;
        double start = System.currentTimeMillis();
        String key = keyScanner.next();
//        if(keyScanner.hasNextLine()) {
//            System.out.println("Error: More than one key found in key file. Aborting Encryption");
//            return false;
//        }
         if(key.length() != 32) {
            System.out.println("Error: Key is not 128 bits. Aborting Encryption");
             return false;
         }
        addToKeyMatrix(key);
        setRoundKeys();
        time += System.currentTimeMillis() - start;

        while(fileScanner.hasNextLine()) {

            inputSize += 16;
            String plainText = fileScanner.nextLine();
            start = System.currentTimeMillis();
            addToTextMatrix(plainText);

            addRoundKey(0);
            for(int i = 1; i < 11; i++) {
                subBytes();
                shiftRows();
                if(i != 10) {
                    mixColumns();
                }
                addRoundKey(i);
            }
            try {
                outputWriter.write(revertMatrix(plainTextMatrix));
            } catch (IOException e) {
                System.out.println("Error writing to file. Encryption Failed");
                outputFile.delete();
                return false;
            }

            time += System.currentTimeMillis() - start;
        }

        try {
            outputWriter.close();
        }
        catch (IOException e) {
            System.out.println("Unable to close outputWriter. Deleting encrypted file");
            outputFile.delete();
            return false;
        }
        keyScanner.close();
        fileScanner.close();
        //time = System.currentTimeMillis() - time;
        time /= 1000; //in seconds
        System.out.println("Encryption Complete");
        System.out.println("Bytes processed: " + inputSize);
        System.out.println("Time taken (s): " + time);
        inputSize /= (1024*1024);
        double throughput = inputSize/time;
        System.out.println("Throughput: " + throughput  + "MB/s");
        return true;
    }



}

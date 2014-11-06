import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Encryptor {

    private File keyFile;
    private String key;

    private File inputFile;
    private String input;
    private Scanner scanner;

    private int plainTextMatrix[][] = new int[4][4]; //make private
    private int keyMatrix[][] = new int[4][4];
    private int roundKeyMatrix[][] = new int[4][44]; //collection of roundKeys for all rounds.

    private static final int nk = 4; //constant for 128 bit implementation

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
                System.out.print(temp + " ");
            }
            System.out.println();
        }
    }

    private boolean readFiles() {
        keyFile = new File(key);
        inputFile = new File(input);

        try {
            scanner = new Scanner(inputFile);
        }
        catch(FileNotFoundException e) {
            System.out.println("Error: Input File Not Found");
            return false;
        }
        return true;
    }

    public void addToTextMatrix(String inputLine) {

        if(inputLine.length() < 32) {
            int diff = 32 - inputLine.length();
            String tempPad = "0";
            for(int i = 0; i < diff; i++) {
                tempPad = tempPad + "0";
            }
            inputLine = inputLine + tempPad;
        }

        if(inputLine.length() > 32) {
            inputLine = inputLine.substring(0, 31);
        }

        assert inputLine.length() == 32;

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

    public void addToKeyMatrix(String inputLine) {

        //System.out.println("entered function");

        if(inputLine.length() < 32) {
            int diff = 32 - inputLine.length();
            String tempPad = "0";
            for(int i = 0; i < diff; i++) {
                tempPad = tempPad + "0";
            }
            inputLine = inputLine + tempPad;
        }

        if(inputLine.length() > 32) {
            inputLine = inputLine.substring(0, 31);
        }

        assert inputLine.length() == 32;

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



    public void subBytes() {
        for(int col = 0; col < plainTextMatrix.length; col++) {
            subBytesAux(col, plainTextMatrix);
        }
    }

    /*
     * subBytesAux provides the opportunity to reuse code while producing roundkeys
     */
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

    public void shiftRows() {
        for(int row = 0; row < plainTextMatrix.length; row++) {
            for(int count = 0; count < row; count++) {
                rotateRow(row, plainTextMatrix);
            }
        }
    }

    private void rotateRow(int row, int[][] matrix) {
        int temp = matrix[row][0];
        int col = 1;
        while(col < matrix.length) {
            matrix[row][col - 1] = matrix[row][col++];
        }
        matrix[row][col - 1] = temp;
    }

    private void rotateCol(int col, int[][] matrix) {
        int temp = matrix[0][col];
        int row = 1;
        while(row < matrix.length) {
            matrix[row - 1][col] = matrix[row++][col];
        }
        matrix[row - 1][col] = temp;
    }

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

    public void mixColumns() {
        for(int col = 0; col < plainTextMatrix.length; col++) {
            mixColumnsAux(col);
        }
    }

    public void mixColumnsAux (int c) {

        int a[] = new int[4];

        //a[] is a copy of plainTextMatrix
        for (int i = 0; i < 4; i++)
            a[i] = (plainTextMatrix[i][c] & 0xFF);

        plainTextMatrix[0][c] = ((mul(2,a[0]) ^ a[2] ^ a[3] ^ mul(3,a[1]))) & 0xFF;
        plainTextMatrix[1][c] = ((mul(2,a[1]) ^ a[3] ^ a[0] ^ mul(3,a[2]))) & 0xFF;
        plainTextMatrix[2][c] = ((mul(2,a[2]) ^ a[0] ^ a[1] ^ mul(3,a[3]))) & 0xFF;
        plainTextMatrix[3][c] = ((mul(2,a[3]) ^ a[1] ^ a[2] ^ mul(3,a[0]))) & 0xFF;
    }

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

    public void addRoundKey(int round) {
        for(int col = 0; col < plainTextMatrix[0].length; col++) {
            for(int row = 0; row < plainTextMatrix.length; row++) {
                plainTextMatrix[row][col] = plainTextMatrix[row][col] ^ roundKeyMatrix[row][(round * 4) + col];
            }
        }
    }

    public boolean addFiles(String inputFile, String keyFile) {
        input = inputFile;
        key = keyFile;
        return readFiles();
    }

    public void encrypt() {

    }


}

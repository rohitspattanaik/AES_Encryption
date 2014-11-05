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
                System.out.print(Integer.toHexString(keyMatrix[x][y]) + " ");
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

    public void addToMatrix(String inputLine) {

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
                plainTextMatrix[x][y] = (char)Integer.parseInt(inputLine.substring(0,2), 16);
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
        for(int x=0;x<matrix[0].length;x++) {
            //System.out.println(Integer.parseInt(String.valueOf(plainTextMatrix[x][y]), 16));
            //int temp = Integer.parseInt(Integer.toHexString(plainTextMatrix[x][y]));
            //System.out.println(plainTextMatrix[x][y]);
            String temp = Integer.toHexString(matrix[x][y]);
            //System.out.println(temp);
            if(temp.length() != 2) {
                temp = "0" + temp;
            }
            int xVal = Integer.parseInt(temp.substring(0, 1), 16);
            int yVal = Integer.parseInt(temp.substring(1), 16);
            //System.out.println("x: "+xVal+" y: "+yVal);
            matrix[x][y] = Tables.S_BOX[xVal][yVal];
                //System.out.println("Replaced :"+plainTextMatrix[x][y]);
        }

    }

    public void shiftRows()
    {
        for(int row = 0; row < plainTextMatrix.length; row++) {
            for(int count = 0; count < row; count++) {
                rotate(row);
            }
        }
    }

    private void rotate(int row) {
        int temp = plainTextMatrix[row][0];
        int col = 1;
        while(col < plainTextMatrix.length) {
            plainTextMatrix[row][col - 1] = plainTextMatrix[row][col++];
        }
        plainTextMatrix[row][col - 1] = temp;
    }
/*
    public void mixColumns() {
        for(int col = 0; col < plainTextMatrix.length; col++) {
            plainTextMatrix[0][col] = (2*plainTextMatrix[0][col]) ^ (3*plainTextMatrix[1][col]) ^ (plainTextMatrix[2][col]) ^ (plainTextMatrix[3][col]);
            plainTextMatrix[1][col] = (plainTextMatrix[0][col]) ^ (2*plainTextMatrix[1][col]) ^ (3*plainTextMatrix[2][col]) ^ (plainTextMatrix[3][col]);
            plainTextMatrix[2][col] = (plainTextMatrix[0][col]) ^ (plainTextMatrix[1][col]) ^ (2*plainTextMatrix[2][col]) ^ (3*plainTextMatrix[3][col]);
            plainTextMatrix[3][col] = (3*plainTextMatrix[0][col]) ^ (plainTextMatrix[1][col]) ^ (plainTextMatrix[2][col]) ^ (2*plainTextMatrix[3][col]);
        }
    }
*/
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
        // This is another alternate version of mixColumn, using the
        // logtables to do the computation.

        int a[] = new int[4];

        // note that a is just a copy of st[.][c]
        for (int i = 0; i < 4; i++)
            a[i] = (plainTextMatrix[i][c] & 0xFF);

        // This is exactly the same as mixColumns1, if
        // the mul columns somehow match the b columns there.
        plainTextMatrix[0][c] = ((mul(2,a[0]) ^ a[2] ^ a[3] ^ mul(3,a[1]))) & 0xFF;
        plainTextMatrix[1][c] = ((mul(2,a[1]) ^ a[3] ^ a[0] ^ mul(3,a[2]))) & 0xFF;
        plainTextMatrix[2][c] = ((mul(2,a[2]) ^ a[0] ^ a[1] ^ mul(3,a[3]))) & 0xFF;
        plainTextMatrix[3][c] = ((mul(2,a[3]) ^ a[1] ^ a[2] ^ mul(3,a[0]))) & 0xFF;
    }

    public boolean addFiles(String inputFile, String keyFile) {
        input = inputFile;
        key = keyFile;
        return readFiles();
    }

    public void encrypt() {

    }


}

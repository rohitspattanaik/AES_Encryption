import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by rsp615 on 10/28/14.
 */
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
                System.out.print(Integer.toHexString(plainTextMatrix[x][y]) + " ");
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
                plainTextMatrix[x][y] = Integer.parseInt(inputLine.substring(0,2), 16);
                if(inputLine.length() != 0) {
                    inputLine = inputLine.substring(2);
                }

            }
        }
    }

    public void subBytes()
    {
        for(int x=0;x<plainTextMatrix.length;x++)
        {
            for(int y=0;y<plainTextMatrix[0].length;y++)
            {
                //System.out.println(Integer.parseInt(String.valueOf(plainTextMatrix[x][y]), 16));
                //int temp = Integer.parseInt(Integer.toHexString(plainTextMatrix[x][y]));
                //System.out.println(plainTextMatrix[x][y]);
                String temp = Integer.toHexString(plainTextMatrix[x][y]);
                //System.out.println(temp);
                if(temp.length() != 2) {
                    temp = "0" + temp;
                }
                int xVal = Integer.parseInt(temp.substring(0, 1), 16);
                int yVal = Integer.parseInt(temp.substring(1), 16);
                //System.out.println("x: "+xVal+" y: "+yVal);
                plainTextMatrix[x][y] = Tables.S_BOX[xVal][yVal];
                //System.out.println("Replaced :"+plainTextMatrix[x][y]);
            }
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

    public void mixColumns() {
        for(int col = 0; col < plainTextMatrix.length; col++) {
            plainTextMatrix[0][col] = (2*plainTextMatrix[0][col]) ^ (3*plainTextMatrix[1][col]) ^ (plainTextMatrix[2][col]) ^ (plainTextMatrix[3][col]);
            plainTextMatrix[1][col] = (plainTextMatrix[0][col]) ^ (2*plainTextMatrix[1][col]) ^ (3*plainTextMatrix[2][col]) ^ (plainTextMatrix[3][col]);
            plainTextMatrix[2][col] = (plainTextMatrix[0][col]) ^ (plainTextMatrix[1][col]) ^ (2*plainTextMatrix[2][col]) ^ (3*plainTextMatrix[3][col]);
            plainTextMatrix[3][col] = (3*plainTextMatrix[0][col]) ^ (plainTextMatrix[1][col]) ^ (plainTextMatrix[2][col]) ^ (2*plainTextMatrix[3][col]);
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

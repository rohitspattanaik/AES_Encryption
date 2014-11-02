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

        //The pattern to add elements in column major
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
                int xVal = plainTextMatrix[x][y]>>4;
                int yVal = plainTextMatrix[x][y]>>5;
                //System.out.println("x: "+xVal+" y: "+yVal);
                plainTextMatrix[x][y] = Tables.S_BOX[xVal][yVal];
                //System.out.println("Replaced :"+plainTextMatrix[x][y]);
            }
        }
    }

    public void shiftRows()
    {

        for(int x=0;x<plainTextMatrix.length;x++)
        {
            for(int n =0;n<x;n++)
            {
                int temp = plainTextMatrix[x][plainTextMatrix[0].length-1];
                for(int y =plainTextMatrix[0].length-1;y>0;y--)
                {
                    plainTextMatrix[x][y] = plainTextMatrix [x][y-1];

                }
                plainTextMatrix[x][0]= temp;
            }
        }

    }

    public boolean addFiles(String inputFile, String keyFile) {
        input = inputFile;
        key = keyFile;
        if(!readFiles()) {
           return false;
        }
        return true;
    }

    public void encrypt() {

    }


}

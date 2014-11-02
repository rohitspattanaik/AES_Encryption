import java.io.File;

/**
 * Created by rsp615 on 10/28/14.
 */
public class Encryptor {

    private File keyFile;
    private String key;

    private File inputFile;
    private String input;

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

    private void readFiles() {
        keyFile = new File(key);

        inputFile = new File(input);
    }

    public void addToMatrix(String inputLine) {

        if(inputLine.length() < 32) {
            int diff = 32 - (int)inputLine.length();
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
                //System.out.println(inputLine.substring(0,2));
                plainTextMatrix[x][y] = Integer.parseInt(inputLine.substring(0,2), 16);
                //System.out.println(plainTextMatrix[x][y]);
                if(inputLine.length() != 0) {
                    inputLine = inputLine.substring(2);
                }

            }
        }
    }


}

/**
 * Created by rsp615 on 10/28/14.
 */
public class Decryptor {
	private int cipherTextMatrix[][] = new int[4][4]; //make private
	  
	public void subBytes()
	    {
	        for(int x=0;x<cipherTextMatrix.length;x++)
	        {
	            for(int y=0;y<cipherTextMatrix[0].length;y++)
	            {
	                //System.out.println(Integer.parseInt(String.valueOf(plainTextMatrix[x][y]), 16));
	                //int temp = Integer.parseInt(Integer.toHexString(plainTextMatrix[x][y]));
	                //System.out.println(plainTextMatrix[x][y]);
	                String temp = Integer.toHexString(cipherTextMatrix[x][y]);
	                //System.out.println(temp);
	                if(temp.length() != 2) {
	                    temp = "0" + temp;
	                }
	                int xVal = Integer.parseInt(temp.substring(0, 1), 16);
	                int yVal = Integer.parseInt(temp.substring(1), 16);
	                //System.out.println("x: "+xVal+" y: "+yVal);
	                cipherTextMatrix[x][y] = Tables.INVERSE_S_BOX[xVal][yVal];
	                //System.out.println("Replaced :"+plainTextMatrix[x][y]);
	            }
	        }
	    }
	  public void shiftRows()
	    {

	        for(int x=0;x<cipherTextMatrix.length;x++)
	        {
	            for(int n =0;n<((x-cipherTextMatrix[0].length)-cipherTextMatrix[0].length);n++)
	            {
	                int temp = cipherTextMatrix[x][cipherTextMatrix[0].length-1];
	                for(int y =cipherTextMatrix[0].length-1;y>0;y--)
	                {
	                	cipherTextMatrix[x][y] = cipherTextMatrix [x][y-1];

	                }
	                cipherTextMatrix[x][0]= temp;
	            }
	        }

	    }
}

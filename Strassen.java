package Strassen;

import java.io.*;
import java.text.DecimalFormat;


public class Strassen {
    
	
	public static void main(String[] args) throws Exception {
		String input1 = new String(args[0]);
		String input2 = new String(args[1]);
		double[][] a = Matrix(input1);
		double[][] b = Matrix(input2);
		String file = input1.replace("input", "output").replace("_a", "");
		double[][] ax = padMatrix(a, a.length);
		double[][] bx = padMatrix(b, b.length);	
    	PrintWriter output = new PrintWriter(file.replace("input", "output").replace("_a", ""));
		
		double [][] result1 = naiveMult(a,b);
		String title1 = "Matrix C (normal multiplication):";
		printMatrix(result1,output,title1);
		System.out.println();
		output.println();
		double [][] result2 = unpadMatrix(StrassenMult(ax,bx),a.length);
		String title2 = "Matrix C (Strassen multiplication):";
		printMatrix(result2,output,title2);
		output.close();
	}
	//Generate a matrix from input file
    public static double[][] Matrix(String fileName) throws Exception {
		File file = new File(fileName);
		BufferedReader scan = new BufferedReader(new FileReader(file));
		
		
		String line = scan.readLine();
		String[] set = line.split("\\s+");
		int n = Integer.parseInt(set[0]);
		final double [][] matrix = new double[n][n];
		int row = 0;
		while ((line = scan.readLine())!=null){
			String [] cells = line.split("\\s+");
			for(int i = 0;i< cells.length;i++){
				double data = Double.parseDouble(cells[i]);
				matrix[row][i] = data;
			}
			row++;
			if(row>n){
				break;
			}
		}
		scan.close();
        return matrix;
    }
    //naive way for matrix calculation
    public static double[][] naiveMult(double[][] a, double[][] b){
    	double[][] result = new double[a.length][b[0].length];
    	for(int i =0;i<a.length;i++){
    		for(int j=0;j<b[0].length;j++){
    			for(int k=0;k<a.length;k++){
    				result[i][j] += a[i][k]*b[k][j];
    			}
    			
    		}
    	}
    		
    	return result;
    }
    //Strassen's algorithm for matrix calculation
    public static double[][] StrassenMult(double[][] a, double[][] b){
    	int size = a.length;
    	if (size == 1){
    		return multSingleMatrix(a,b);
    	} else {
    		double[][] a00 = subMatrix(a,0,0);
    		double[][] a01 = subMatrix(a,0,1);
    		double[][] a10 = subMatrix(a,1,0);
    		double[][] a11 = subMatrix(a,1,1);
    		double[][] b00 = subMatrix(b,0,0);
    		double[][] b01 = subMatrix(b,0,1);
    		double[][] b10 = subMatrix(b,1,0);
    		double[][] b11 = subMatrix(b,1,1);
    	
    		double[][] M1 = StrassenMult(addMatrix(a00, a11), 
    				addMatrix(b00, b11));    // M1 = (a00+a11) * (b00+b11)
         
    		double[][] M2 = StrassenMult(addMatrix(a10, a11), b00);   	  // M2 = (a10+a11) * (b00)
        
    		double[][] M3 = StrassenMult(a00, subtractMatrix(b01, b11));   	 // M3 = (a00) * (b01 - b11)
  
    		double[][] M4 = StrassenMult(a11, subtractMatrix(b10, b00));    		// M4 = (a11) * (b10 - b00)
  
    		double[][] M5 = StrassenMult(addMatrix(a00, a01), b11);   	  	// M5 = (a00+a01) * (b11)
 
    		double[][] M6 = StrassenMult(subtractMatrix(a10, a00), 
    				addMatrix(b00, b01));   	 // M6 = (a10-a00) * (b00+b01)
  
    		double[][] M7 = StrassenMult(subtractMatrix(a01, a11), 
    				addMatrix(b10, b11)); 	   // M7 = (a01-a11) * (b10+b11)
  
    		double[][] c01 = addMatrix(M3, M5); 				// c01 = M3 + M5
    		
    		double[][] c10 = addMatrix(M2, M4); 				// c10 = M2 + M4

    		double[][] c00 = subtractMatrix(addMatrix(addMatrix(M1, M4), M7), M5);		// c00 = M1 + M4 - M5 + M7

    		double[][] c11 = subtractMatrix(addMatrix(addMatrix(M1, M3), M6), M2);        // c11 = M1 + M3 - M2 + M6
    		
    		double[][] result = groupMatrix(c00,c01,c10,c11);
    		return result;
    	}
    }
    //Group 4 n/2 x n/2 submatrices into one n x n matrix
    public static double[][] groupMatrix (double[][] a, double[][] b,double[][] c, double[][] d){   	
    	int size = a.length;
    	double[][] result = new double[size*2][size*2];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				result[i][j] = a[i][j];
				result[i][j + size] = b[i][j];
				result[i + size][j] = c[i][j];
				result[i + size][j + size] = d[i][j];
			}
		}
		return result;
    }
  //singular matrix multiplication (base case)
    public static double[][] multSingleMatrix (double[][] a, double[][] b){
	    if(a.length != 1){
	    	return null;
	    }
	    double[][] result = new double[1][1];
	    result [0][0] = a[0][0]*b[0][0];
	    return result;
    }
    //matrix addition
    public static double[][] addMatrix(double[][] a,double [][] b) {
        double [][] result = new double[a.length][a.length];
        for(int i=0;i<a.length;i++) {
            for(int j=0;j<a.length;j++) {
                result[i][j]=a[i][j]+b[i][j];
            }
        }
        return result;
    }
    //matrix subtraction
    public static double[][] subtractMatrix(double[][] a, double[][] b) {
    	double [][] result = new double[a.length][a.length];
    	for (int i = 0; i < a.length; i++) {
    		for (int j = 0; j < a.length; j++) {
    			result [i][j] = a[i][j] - b[i][j];
    			}
    	}
    	return result;
    }
  //generate 4 submatrices with n/2 x n/2 size from n x n matrix
    public static double[][] subMatrix(double[][] a, int m,int n){
    	double [][] result = new double[a.length/2][a.length/2];
    	int x =0;
    	for(int i =m*a.length/2;i<m*a.length/2+a.length/2;i++){
    		int y=0;
    		for(int j =n*a.length/2;j<n*a.length/2+a.length/2;j++){
    			result[x][y] = a[i][j];
    			y++;
    		}
    		x++;
    	}
    	return result;
    }
  //Pad a matrix to 2-power sized square matrix by adding rows and columns of 0s
    public static double[][] padMatrix(double[][] a, int n){
    	int size = n + (int) Math.pow(2, (int) Math.ceil(Math.log(n) / Math.log(2))) - n;
    	if(size == n){
    		return a;
    	}
    	double [][] result = new double [size][size];
    	for(int i = 0; i<size;i++){
    		for(int j = 0; j<size;j++){
    			if(i<n && j < n){
    				result[i][j]=a[i][j];
    			} else{
    				result[i][j] = 0;
    			}
    		}
    	}
    	return result;
    }
    //Unpad a matrix
    public static double[][] unpadMatrix(double[][] a, int size){
    	double [][] result = new double[size][size];
        for (int i = 0; i < size; i++) {        	
            for (int j = 0; j < size; j++) {
            	result[i][j] = a[i][j];
            }
        }
        return result;
    }
    //Print final matrix to file
    public static void printMatrix(double[][] a, PrintWriter output, String title) throws Exception{
    	System.out.println(title);
    	output.println(title);

        for (int i = 0; i < a.length; i++) {
        	System.out.print("|");
        	 output.print("|");
            for (int j = 0; j < a.length; j++) {
            	System.out.print(new DecimalFormat("##.##").format(a[i][j])+"\t");
            	output.print(new DecimalFormat("##.##").format(a[i][j])+"\t");
            }            
            System.out.println("|");
            output.println("|");
    	}
    }
  //Print matrix for testing purpose
    public static void onlyprintMatrix(double[][] a) {
        for (int i = 0; i < a.length; i++) {
        	System.out.print("|");
            for (int j = 0; j < a.length; j++) {
            	System.out.print(new DecimalFormat("##.##").format(a[i][j])+"\t");
            	}  
            System.out.println("|");
        }
    }
}
        
        
        
        
        
        
        
        
        
        
        
        
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


public class TrainingDataGenerator {

	public static int countfiles = 0;
	
	public static void main(String [] args) throws IOException{
		// startParse();
		calculateDistribution();
		calculateRawDistribution();
	}
	
	
	public static void startParse() throws IOException{

		File folder = new File("data");
		File[] listOfFiles = folder.listFiles();

		FileWriter fwOutputPrices = new FileWriter("trainingData.arff");
		PrintWriter pwOutputPrices = new PrintWriter(new BufferedWriter(fwOutputPrices));
		pwOutputPrices.println("@relation SPOT\n"
				+ "@attribute numberofbrokers real\n"
				+ "@attribute day_date real\n"
				+ "@attribute month_date real\n"
				+ "@attribute day real\n"
				+ "@attribute hour real\n"
				+ "@attribute hourAhead real\n"
				+ "@attribute Temperature real\n"
				+ "@attribute CloudCover real\n"
				+ "@attribute WindDirection real\n"
				+ "@attribute WindSpeed real\n"
				+ "@attribute PrevHourClearingPrice real\n"
				+ "@attribute YesterdayClearingPrice real\n"
				+ "@attribute PrevOneWeekClearingPrice real\n"
				+ "@attribute aWeekSameHourAverageClearingPrice real\n"
				+ "@attribute PreviousHourN_1Price real\n"
				+ "@attribute YesterdayN_1Price real\n"
				+ "@attribute AWeekAgoN_1Price real\n"
				+ "@attribute PredictedClearingPrice real\n"
				+ "@attribute ClearingPrice real\n\n"
				+ "@data\n");
		
		System.out.println("Total file to process " + listOfFiles.length);
		
		for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	    	System.out.println("File " + listOfFiles[i].getName());
	        parseDataToFile("data/"+listOfFiles[i].getName(), pwOutputPrices);
	      }
	    }
		
		pwOutputPrices.close();
		fwOutputPrices.close();
	}

	public static void parseDataToFile(String inputFile, PrintWriter pwOutputLoads){
   
        BufferedReader br = null;
        
        try {

        	File gFile = new File(inputFile);
            if(!gFile.exists()){
                System.out.println("File doesn't exist");
            	return;
            }
            
			String sCurrentLine;

			br = new BufferedReader(new FileReader(gFile));

			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.charAt(0) != 'n'){
					pwOutputLoads.println(sCurrentLine);
				}
			}
			countfiles++;
			System.out.print(countfiles + ", ");
        } 
        catch (IOException e) {
			e.printStackTrace();
		} 
        
        finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
    }
	
	/* Calculate Distribution */
	public static void calculateDistribution(){
		
		double [] posDistribution = new double[100];
		double [] negDistribution = new double[100];
		
        try
        {
            File readFile = new File("trainningDataset-Reptree2016-1game.arff");
            if(!readFile.exists()){
                System.out.println("File doesn't exist");
            	return;
            }
            
            FileWriter fwOutputPrices = new FileWriter("distribution-Reptree2016-1game.csv");
    		PrintWriter pwOutputPrices = new PrintWriter(new BufferedWriter(fwOutputPrices));
            
			CSVParser parser = CSVParser.parse(readFile, StandardCharsets.US_ASCII, CSVFormat.DEFAULT);
            for (CSVRecord csvRecord : parser) {
                Iterator<String> itr = csvRecord.iterator();
                // Time Stamp	
                String strCheck = itr.next();
                
                if(strCheck.charAt(0) == '@' || strCheck.charAt(0) == '\n' || strCheck.charAt(0) == ' ' || strCheck.charAt(0) == 'n'){
                	// do nothing
                }
                else{
	                for(int i = 1; i <= 17; i++)
	                	itr.next();
	                
	                // Predicted clearing price	
	                double dblPredictedPrice = Double.parseDouble(itr.next());
	                // Actual Price
	                double dblActualPrice = Double.parseDouble(itr.next());
	                
	                int deviation = (int) Math.round(dblActualPrice - dblPredictedPrice);
	                
	                if(deviation >= 0)
	                	if(deviation >= 100)
	                		posDistribution[99]++;
	                	else
	                		posDistribution[deviation]++;
	                else{
	                	if(deviation <= -100)
	                		negDistribution[99]++;
	                	else {
	                		deviation *= -1;
	                		negDistribution[deviation]++;
	                	}
	                }
                }
            }
            parser.close();
            
            // Print Distribution in file
            for(int i = 99; i > 0; i--){
            	int iTemp = i*-1;
            	pwOutputPrices.println(iTemp + "," +negDistribution[i]);
            }
            
            for(int i = 0; i < 100; i++){
            	pwOutputPrices.println(i + "," + posDistribution[i]);
            }
            
            pwOutputPrices.close();
            fwOutputPrices.close();
            
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
	}

	
	/* Calculate Raw Distribution */
	public static void calculateRawDistribution(){
		
		try
        {
            File readFile = new File("trainningDataset-Reptree2016-1game.arff");
            if(!readFile.exists()){
                System.out.println("File doesn't exist");
            	return;
            }
            
            FileWriter fwOutputPrices = new FileWriter("rawDistribution-Reptree2016-1game.csv");
    		PrintWriter pwOutputPrices = new PrintWriter(new BufferedWriter(fwOutputPrices));
            
			CSVParser parser = CSVParser.parse(readFile, StandardCharsets.US_ASCII, CSVFormat.DEFAULT);
            for (CSVRecord csvRecord : parser) {
                Iterator<String> itr = csvRecord.iterator();
                // Time Stamp
                String strCheck = itr.next();
            
                if(strCheck.charAt(0) == '@' || strCheck.charAt(0) == '\n' || strCheck.charAt(0) == ' ' || strCheck.charAt(0) == 'n'){
                	// do nothing
                }
                else{
	                for(int i = 1; i <= 17; i++)
	                	itr.next();
	                
	                // Predicted clearing price
	            	double dblPredictedPrice = Double.parseDouble(itr.next());
	                // Actual Price
	                double dblActualPrice = Double.parseDouble(itr.next());
	                
	                int deviation = (int) Math.round(dblActualPrice - dblPredictedPrice);
	                pwOutputPrices.println(deviation);
                }
            
            }
            parser.close();
            
            // Print Distribution in file
            
            pwOutputPrices.close();
            fwOutputPrices.close();
            
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
	}

	
	
}

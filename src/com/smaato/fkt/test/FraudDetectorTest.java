package com.smaato.fkt.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import weka.core.Instances;

import com.smaato.fkt.FraudDetector;

/**
 * @author sven
 *
 */
public class FraudDetectorTest {

	private final static Logger LOGGER = Logger.getLogger(FraudDetector.class.getName()); 	

	FraudDetector detector;

	/**
	 *
	 * 
	 */
	@Before
	public void initialize() {
		detector = new FraudDetector();
		LOGGER.setLevel(Level.WARNING);
	}

	/**
	 * 1 Test: load from CSV File to Weka data
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoadCSV2WekaData() throws Exception {
		LOGGER.info("\n=== 1. Test: load from CSV File to Weka data  ===\n");
		File dir1 = new File (".");
		File test = new File(dir1.getCanonicalPath() + "/test_data/");
		String csvFile = test.toString() + "/data_20151126/1_final.csv";
		Instances data = detector.loadCSV2WekaData(new File(csvFile));
		detector.printInstancesBasic(data, csvFile);
	}

	/**
	 * 2 Test: load from ARFF File to Weka data
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoadARFF2WekaData() throws Exception {
		LOGGER.info("\n=== 2. Test: load from ARFF File to Weka data ===\n");
		File dir1 = new File (".");
		File test = new File(dir1.getCanonicalPath() + "/test_data/");
		String arffFile = test.toString() + "/data_20150928/2_pruned.csv.arff";
		Instances data = detector.loadARFF2WekaData(new File(arffFile));
		detector.printInstancesBasic(data, arffFile);

	}

	/**
	 * 3 Test: build model from CSV file
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBuildModel() throws Exception {
		LOGGER.info("\n=== 3. Test: build model from CSV file ===\n");
		File dir1 = new File (".");
		File test = new File(dir1.getCanonicalPath() + "/test_data/");
		String modelCSVFile = test.toString() + "/data_20151126/1_final.csv";

		detector.buildModel(modelCSVFile);

		// Or build model from a File
		// detector.buildModel(new File(modelCSVFile));

	}
	
	/**
	 * 4 Test: build model from CSV file
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBuildModelBadRatio() throws Exception {
		LOGGER.info("\n=== 4. Test: build model with bad ratio from CSV file ===\n");
		File dir1 = new File (".");
		File test = new File(dir1.getCanonicalPath() + "/test_data/");
		String modelCSVFile = test.toString() + "/data_20151126/8_bad_ratio.csv";

		detector.buildModel(modelCSVFile);

		// Or build model from a File
		// detector.buildModel(new File(modelCSVFile));

	}

	/**
	 * 5 Test: build model from CSV file with attributes pruning
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBuildModelWithPruning() throws Exception {
		LOGGER.info("\n=== 5. Test: build model from CSV file with attributes pruning ===\n");
		File dir1 = new File (".");
		File test = new File(dir1.getCanonicalPath() + "/test_data/");
		String modelCSVFile = test.toString() + "/data_20151126/1_final.csv";

		String filter = "2,3,4,6,8,9,10,12,13,14,15,17";

		// Or config the filter with range
		// String filter = "2-4,6,8-10,12-15,17";

		detector.buildModel(new File(modelCSVFile), filter);

	}

	/**
	 * 6 Test: save model
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSaveModel() throws Exception {
		LOGGER.info("\n=== 6. Test: save model ===\n");
		File dir1 = new File (".");
		File test = new File(dir1.getCanonicalPath() + "/test_data/");
		String modelCSVFile = test.toString() + "/data_20151126/1_final.csv";

		// config the filter with range
		String filter = "2-4,6,8-10,12-15,17";
		detector.buildModel(new File(modelCSVFile), filter);

		String modelPath = test.toString() + "/test_result/jrip.model";
		detector.saveModel(modelPath);

	}

	/**
	 * 7 Test: load model
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoadModel() throws Exception {
		LOGGER.info("\n=== 7. Test: load model ===\n");
		File dir1 = new File (".");
		File test = new File(dir1.getCanonicalPath() + "/test_data/");

		String modelPath = test.toString() + "/test_result/jrip.model";
		detector.loadModel(modelPath);
		detector.printModel();

	}

	/**
	 * 8 Test: evaluation with n-Cross Validation
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEvaluationCV() throws Exception {
		LOGGER.info("\n=== 8. Test: evaluation with n-Cross Validation ===\n");
		File dir1 = new File (".");
		File test = new File(dir1.getCanonicalPath() + "/test_data/");

		String modelCSVFile = test.toString() + "/data_20151126/1_final.csv";
		String filter = "2-4,6,8-10,12-15,17";

		detector.evaluateCV(new File(modelCSVFile), 10, filter);

	}

	/**
	 * 9 Test: evaluation with n-Cross Validation
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEvaluationTrainTest() throws Exception {
		LOGGER.info("\n=== 9. Test: evaluation with train and test dataset ===\n");
		File dir1 = new File (".");
		File test = new File(dir1.getCanonicalPath() + "/test_data/");

		String trainCSVFile = test.toString() + "/data_20150928/2.csv";
		String testCSVFile = test.toString() + "/data_20150928/3.csv";

		String filter = "2-4,6,8-10,12-15,17";

		detector.evaluateCSVs(new File(trainCSVFile), new File(testCSVFile), filter);

	}

	/**
	 * 10 Test: detect fraud from CSV file
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDetectFraud() throws Exception {
		LOGGER.info("\n=== 10. Test: detect fraud from CSV file ===\n");
		File dir1 = new File (".");
		File test = new File(dir1.getCanonicalPath() + "/test_data/");

		String filter = "2-4,6,8-10,12-15,17";

		String modelPath = test.toString() + "/test_result/jrip.model";

		detector.loadModel(modelPath);
		detector.printModel();

		String testCSVFile_Mini = test.toString() + "/data_20151126/test_example_new.csv";

		detector.detectFraud(new File(testCSVFile_Mini), filter);

	}
	
	/**
	 * 10 Test: complete workflow
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDetectFraudWorkflow() throws Exception {
		LOGGER.info("\n=== 10. Test: complete worklfow ===\n");

		
		File dir1 = new File (".");
		File test = new File(dir1.getCanonicalPath() + "/test_data/");
		String modelCSVFile = test.toString() + "/data_20151126/1_final.csv";
		String filter = "2-4,6,8-10,12-15,17";
		
        // Step 1: build a model with a given CSV file with the training data
        //         and a filter with the attributes to be pruned 
		detector.buildModel(new File(modelCSVFile), filter);

		// Step 2: print the model
		detector.printModel();
		
		// To save time from training, by just loading a model
//		String modelPath = test.toString() + "/test_result/jrip.model";
//		detector.loadModel(modelPath);
//		detector.printModel();

		// Step 3: detect fraud in a given test CSV file
		String testCSVFile_Mini = test.toString() + "/data_20151126/test_example_new.csv";
		double[] result = detector.detectFraud(new File(testCSVFile_Mini), filter);
		
		// Step 4: write the fraud detection with confidence in a result CSV file 
		List<String> lines = new ArrayList<String>();
		StringBuffer textBuffer = new StringBuffer();
		try {
			final InputStream is = new FileInputStream(testCSVFile_Mini);
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));

			// read the first line of attributes information
			String line = reader.readLine();
			
			// Insert the confidence column in the attributes information line
			String newLine = line.replaceFirst("FRAUD,", "FRAUD,Confidence,");
			textBuffer.append(newLine + "\n");
			
			
			// Add the fraud detection result and confidence in the front of the original line
			int resultIndex = 0;
			while ((line = reader.readLine()) != null) {
				String fraud = "NF"; 
				if (result[resultIndex] > 0.5d) {
					fraud = "FRAUD";
				} 
				textBuffer.append(fraud + "," + result[resultIndex] + line + "\n");
				resultIndex++;
			}

			reader.close();
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
			
	    // write in a result file
		String resultCSVFile = testCSVFile_Mini.substring(0, testCSVFile_Mini.lastIndexOf(".csv")) + "_result.csv";
		Writer fw = null;
		try {
			fw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(resultCSVFile), "UTF-8"));
			fw.write(textBuffer.toString());
//			fw.append(System.getProperty("line.separator")); // e.g. "\n"
		} catch (IOException e) {
			System.err.println("Write operations failed!");
			e.printStackTrace();
		} finally {
			if (fw != null)
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

}

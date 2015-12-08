package com.smaato.fkt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.JRip;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 * 
 * @author Huiyan Huang 
 * Copyright (C) 2015 Smaato-FKT@Xpertim
 *
 */

public class FraudDetector {
	/**
	 * The real classifier
	 */
	private Classifier classifier;

	/**
	 * The data for build the model
	 */
	private Instances data4Model;

	// /**
	// * First only JRip
	// */
	// public FraudDetector(String aFraudDetectorType) {
	// type = aFraudDetectorType;
	// }

	public static void main(String[] args) {
		try {
			FraudDetector detector = new FraudDetector();
			System.out.println("FraudDetector successfully created!");

			// 1. Test load from CSV Fiel to weka data 
			String csvFile = "C:/STO/Data_20151126/1_final.csv";
//			detector.loadCSV2WekaData(new File(csvFile));
			
			
			
			
			
			
//			String trainCSVFile = "C:/STO/Data_20151126/2_final.csv";
//			String testCSVFile = "C:/STO/Data_20151126/3_final.csv";
			
//			String trainCSVFile = "C:/STO/data_20150928/2.csv";
//			String testCSVFile = "C:/STO/data_20150928/3.csv";
			
//			String trainARFFFile = "C:/STO/data_20150928/2_pruned.csv.arff";
//			String testARFFFile = "C:/STO/data_20150928/3_pruned.csv.arff";

//	    	detector.buildModel(modelCSVFile);
//			String filter = "2,3,6,8,9,12,13,14,15,17";
			String filter = "2-4,6,8-10,12-15,17";

//			detector.buildModel(new File(modelCSVFile), filter);
			
			String modelPath = "C:/STO/Weka_Test_Result/jrip.model";
//			detector.saveModel(modelPath);
			
			detector.loadModel(modelPath);
			detector.printModel();
			
			String testCSVFile_Mini = "C:/STO/Data_20151126/test_example_new.csv";
			
			detector.detectFraud(new File(testCSVFile_Mini), filter);

//			detector.evaluateCV(new File(modelCSVFile), 10, filter);

//		    detector.evaluateCSVs(new File(trainCSVFile), new File(testCSVFile));

//			detector.evaluateCSVs(new File(trainCSVFile), new File(testCSVFile), filter);
//			detector.evaluateARFFs(new File(trainARFFFile), new File(testARFFFile));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Default is JRip Fraud Detector
	 */
	public FraudDetector() {
		classifier = new JRip();
	}

	/**
	 * 
	 * @param aCSVFilePath
	 */
	public void buildModel(String aCSVFilePath) throws Exception {
		buildModel(new File(aCSVFilePath));
	}

	/**
	 * 
	 * @param aCSVFile
	 */
	public void buildModel(File aCSVFile) throws Exception {
		buildModel(aCSVFile, "");
	}
	
	/**
	 * 
	 * @param aCSVFile
	 * @param attributesFilter
	 * @return
	 * @throws Exception
	 */
	public double[] detectFraud(File aCSVFile, String attributesFilter) throws Exception {
		
		Instances data2Classify = loadCSV2WekaData(aCSVFile);
		printInstancesBasic(data2Classify, aCSVFile.getAbsolutePath());
		int size = data2Classify.numInstances();
		double[] result = new double[size];
		
		Instances newData2Classify;
		if (!attributesFilter.isEmpty()) {
			newData2Classify = removeAttributes(data2Classify, attributesFilter);
			printInstancesBasic(newData2Classify, aCSVFile.getAbsolutePath());
		} else {
			newData2Classify = data2Classify;
		}
		System.out.println("\nDetecting the fraud from: \t" + aCSVFile.getAbsolutePath()+"\n");
		for (int i=0; i<size; i++) {
			Instance instance = newData2Classify.instance(i);
			double predict = 1 - classifier.classifyInstance(instance);
			double[] confidences = classifier.distributionForInstance(instance); 
			result[i] = predict;
			System.out.println("Instance:\t" + i + "\tprediction result is:\t" + predict 
					+ "\tConfidences Distribution:\t" + confidences[0] + " : " + confidences[1]);
		}
		
		return result;
	}
	
	
	/**
	 * 
	 * @param aCSVFile
	 * @param attributesFilter
	 * @throws Exception
	 */
	public void buildModel(File aCSVFile, String attributesFilter) throws Exception {
		data4Model = loadCSV2WekaData(aCSVFile);
		printInstancesBasic(data4Model, aCSVFile.getAbsolutePath());
		
		AttributeStats frauStats = data4Model.attributeStats(0);
		int[] fraudCounts = frauStats.nominalCounts;
		int nfCount = fraudCounts[0];
		int fraudCount = fraudCounts[1];
		if (fraudCounts[0] < fraudCounts[1]) {
			nfCount = fraudCounts[1];
			fraudCount = fraudCounts[0];
		}
		float fraudRate = new Float(Math.round(fraudCount*1000000/(nfCount+fraudCount))/100)/100;
		
		System.out.println("\nNon fraud counts: " + nfCount);
		System.out.println("Fraud counts: " + fraudCount);
		System.out.println("The fraud rate is: " + fraudRate +" %\n");
		
		
		if ((fraudRate) < 3) {
			System.out.println("\n=== WARNING WARNING WARNING ===");
			System.out.println("=== The traning dataset has too low fraud rate of: " + fraudRate + " % ===");
			System.out.println("=== We strongly suggest to use a traning set with a fraud rate of 3% at least for the model building!!! ===\n");
		}
		
		
		Instances newData4Model;
		if (!attributesFilter.isEmpty()) {
			newData4Model = removeAttributes(data4Model, attributesFilter);
			printInstancesBasic(newData4Model, aCSVFile.getAbsolutePath());
		} else {
			newData4Model = data4Model;
		}
		
		System.out.println("\nBuilding model from:\t" + aCSVFile.getAbsolutePath());
		classifier.buildClassifier(newData4Model);
		System.out.println("The model is successfully built from:\t" + aCSVFile.getAbsolutePath()+"\n");
		System.out.println("\n=== Classifier model (full training set) ===\n");
		printModel();
	}

	
	
	/**
	 * 
	 * @param anInstance
	 * @return
	 * @throws Exception
	 */
//	public double detectFraud(Instance anInstance) throws Exception {
//		double prediction = classifier.classifyInstance(anInstance);
//		return prediction;
//	}

	
	/**
	 * 	
	 * @throws Exception
	 */
	public void evaluate10CV() throws Exception {
		evaluateCV(10);
	}

	
	/**
	 * 
	 * @param n_CrossValiation
	 * @throws Exception
	 */
	public void evaluateCV(int n_CrossValiation) throws Exception {
		Evaluation eval = new Evaluation(data4Model);
		eval.crossValidateModel(classifier, data4Model, n_CrossValiation, new Random(1));
		System.out.println("\n=== Stratified cross-validation ===\n");		
		printEvaluation(eval);
	}
	
	/**
	 * 
	 * @param aCSVFile
	 * @param n_CrossValiation
	 * @throws Exception
	 */
	public void evaluateCV(File aCSVFile, int n_CrossValiation)  throws Exception {
		evaluateCV(aCSVFile, n_CrossValiation, "");
		
	}

	/**
	 * 
	 * @param aCSVFile
	 * @param n_CrossValiation
	 * @param attributesFilter
	 * @throws Exception
	 */
	public void evaluateCV(File aCSVFile, int n_CrossValiation, String attributesFilter) throws Exception {

		Instances data2Eval = loadCSV2WekaData(aCSVFile);
		printInstancesBasic(data2Eval, aCSVFile.getAbsolutePath());
		
		Instances newData2Eval;
		
		if (!attributesFilter.isEmpty()) {
			newData2Eval = removeAttributes(data2Eval, attributesFilter);
			printInstancesBasic(newData2Eval, aCSVFile.getAbsolutePath());
		} else {
			newData2Eval = data2Eval;
		}
		
		Classifier cls = new JRip();

		Evaluation eval = new Evaluation(newData2Eval);
		eval.crossValidateModel(cls, newData2Eval, n_CrossValiation, new Random(1));
		System.out.println("\n=== Stratified cross-validation ===\n");
		printEvaluation(eval);
	}

	/**
	 * 
	 * @param aTrainCSVFile
	 * @param aTestCSVFile
	 * @throws Exception
	 */
	public void evaluateCSVs(File aTrainCSVFile, File aTestCSVFile) throws Exception {

		Instances train = loadCSV2WekaData(aTrainCSVFile);
		Instances test = loadCSV2WekaData(aTestCSVFile);

		// train classifier
		Classifier cls = new JRip();
		cls.buildClassifier(train);

		// evaluate classifier and print some statistics
		Evaluation eval = new Evaluation(train);
		eval.evaluateModel(cls, test);
		System.out.println("\n=== Stratified cross-validation ===\n");
		printEvaluation(eval);
	}
	
	/**
	 * 
	 * @param aTrainCSVFile
	 * @param aTestCSVFile
	 * @param attributesFilter
	 * @throws Exception
	 */
	public void evaluateCSVs(File aTrainCSVFile, File aTestCSVFile, String attributesFilter) throws Exception {

		Instances train = loadCSV2WekaData(aTrainCSVFile);
		printInstancesBasic(train, aTrainCSVFile.getAbsolutePath());
		Instances newTrain = removeAttributes(train, attributesFilter);
		printInstancesBasic(newTrain, aTrainCSVFile.getAbsolutePath());

		
		Instances test = loadCSV2WekaData(aTestCSVFile);
		printInstancesBasic(test, aTestCSVFile.getAbsolutePath());
		Instances newTest = removeAttributes(test, attributesFilter);
		printInstancesBasic(newTest, aTestCSVFile.getAbsolutePath());

		// train classifier
		Classifier cls = new JRip();
		cls.buildClassifier(newTrain);

		// evaluate classifier and print some statistics
		Evaluation eval = new Evaluation(newTrain);
		eval.evaluateModel(cls, newTest);
		System.out.println("\n=== Evaluation on test set ===\n");
		printEvaluation(eval);
	}

	/**
	 * 
	 * @param aTrainARFFFile
	 * @param aTestARFFFile
	 * @throws Exception
	 */
	public void evaluateARFFs(File aTrainARFFFile, File aTestARFFFile) throws Exception {

		Instances train = loadARFF2WekaData(aTrainARFFFile);
		printInstancesBasic(train, aTrainARFFFile.getAbsolutePath());
		Instances test = loadARFF2WekaData(aTestARFFFile);
		printInstancesBasic(test, aTestARFFFile.getAbsolutePath());
		
		// train classifier
		Classifier cls = new JRip();
		cls.buildClassifier(train);

		// evaluate classifier and print some statistics
		Evaluation eval = new Evaluation(train);
		eval.evaluateModel(cls, test);
		System.out.println("\n=== Evaluation on test set ===\n");
		printEvaluation(eval);

	}

	/**
	 * 
	 * @param aCSVFile
	 * @return
	 * @throws Exception
	 */
	public Instances loadCSV2WekaData(File aCSVFile) throws Exception {
		
		System.out.println("\nLoading CSV file from:\t" + aCSVFile + " to Weka data");


		// load CSV
		CSVLoader loader = new CSVLoader();
		loader.setSource(aCSVFile);
		Instances data = loader.getDataSet();

		// setting class attribute if the data format does not provide this
		// information
		// For example, the XRFF format saves the class attribute information as
		// well
		if (data.classIndex() == -1)
			data.setClassIndex(0);
		
		System.out.println("The CSV file is successfully loaded from:\t" + aCSVFile+"\n");
		return data;
	}

	/**
	 * 
	 * @param anARFFFile
	 * @return
	 * @throws Exception
	 */
	public Instances loadARFF2WekaData(File anARFFFile) throws Exception {
		
		System.out.println("\nLoading ARFF file from:\t" + anARFFFile + " to Weka data");
		BufferedReader reader = new BufferedReader(new FileReader(anARFFFile));
		Instances data = new Instances(reader);
		reader.close();

		// setting class attribute if the data format does not provide this
		// information
		if (data.classIndex() == -1)
			data.setClassIndex(0);
		System.out.println("The ARFF file is successfully loaded from:\t" + anARFFFile+"\n");
		return data;
	}


	/**
	 * takes 2 parameters: - CSV input file - ARFF output file
	 * @param aCSVFile
	 * @param anArffFile
	 * @throws Exception
	 */
	public static void loadCSV2Arff(File aCSVFile, File anArffFile) throws Exception {

		// load CSV
		CSVLoader loader = new CSVLoader();
		loader.setSource(aCSVFile);
		Instances data = loader.getDataSet();

		// save ARFF
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(anArffFile);
		saver.setDestination(anArffFile);
		saver.writeBatch();
	}
	
	/**
	 * 
	 * @param anEval
	 * @throws Exception
	 */
	private void printEvaluation(Evaluation anEval) throws Exception  {
		System.out.println(anEval.toSummaryString("\n=== Results Summary ===\n", true));
		System.out.println(anEval.toClassDetailsString("\n=== Detailed Accuracy By Class ===\n"));
		System.out.println(anEval.toMatrixString("\n=== Confusion Matrix ===\n"));
	}
	
	/**
	 * 
	 * @param data
	 * @param aName
	 */
	public void printInstancesBasic(Instances data, String aName){
		System.out.println("The basic information of the dataset:\t" + aName);
		System.out.println("Instances:\t" + data.numInstances());
		System.out.println("Attributes:\t" + data.numAttributes());
	}
	
	/**
	 * 
	 * @param aData
	 * @param attributesFilter "1-2,4" or "1,2,4"
	 * @return
	 * @throws Exception
	 */
	private Instances removeAttributes(Instances aData, String attributesFilter) throws Exception {
		
			
		System.out.println("\nRemoving Attributes:\t" + attributesFilter+"\n");
		if (!attributesFilter.isEmpty()) {

		String[] options = new String[2];
		 options[0] = "-R";                                    // "range"
		 options[1] = attributesFilter;             // first attribute
		 Remove remove = new Remove();                         // new instance of filter
		 remove.setOptions(options);                           // set options
		 remove.setInputFormat(aData);                          // inform filter about dataset **AFTER** setting options
		 Instances newData = Filter.useFilter(aData, remove); 
		 return newData;
		} else {
			return aData;
		} 
		
	}
	
	/**
	 * 
	 * @param aModelPath
	 * @throws Exception
	 */
	public void saveModel(String aModelPath) throws Exception {
		System.out.println("\nSaving model to:\t" + aModelPath);		
		SerializationHelper.write(aModelPath, classifier);
		System.out.println("The model is successfully saved to:\t" + aModelPath+"\n");
	}
	
	
	/**
	 * 
	 * @param aModelPath
	 * @throws Exception
	 */
	public void loadModel(String aModelPath) throws Exception {
		System.out.println("\nLoading model from:\t" + aModelPath);
		classifier = (Classifier) SerializationHelper.read(aModelPath);
		System.out.println("The model is successfully loaded from:\t" + aModelPath+"\n");
	}
	
	
	/**
	 * 
	 */
	public void printModel() {

		System.out.println(classifier.toString());
	}
}

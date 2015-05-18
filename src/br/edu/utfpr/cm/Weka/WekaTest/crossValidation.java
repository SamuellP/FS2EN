/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

import br.edu.utfpr.cm.Weka.WekaTest.Cluster;
import br.edu.utfpr.cm.Weka.WekaTest.Element;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GeneticSearch;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

/**
 *
 * @author samuel
 */
public class crossValidation {
    
    public ASEvaluation evalCfs = new CfsSubsetEval();
    public ASEvaluation evalInfoGain = new InfoGainAttributeEval();    
    
    public ASSearch searchBestFirst = new BestFirst();
    public ASSearch searchGreedyStepwise = new GreedyStepwise();
    public ASSearch searchGeneticSearch = new GeneticSearch();
    public ASSearch searchRanker = new Ranker();
    
    public NaiveBayes classifierNaiveBayes = new NaiveBayes();
    public RandomForest classifierRandomForest = new RandomForest();
    public SimpleLogistic classifierSimpleLogistic = new SimpleLogistic();
    public J48 classifierJ48 = new J48();
    public DecisionTable classifierDecisionTable = new DecisionTable();
    
    /**
     * 
     * @param clusters
     * @param directory
     * @return
     * Essa função recebe como parâmetro um HASH contendo todos os clusters e uma STRING que diz em qual diretório
     * está contido todos os arquivos ARFF's (Arquivos utilizados para montar o CLUSTER).
     * 
     * A função retorna um HASH contendo todos os arquivos selecionados como TESTE para cada CLUSTER. A escolha
     * dos arquivos de teste é feita da seguinte forma: É selecionado o arquivo que contém o menor número de
     * features dentro do cluster. A chave do HASH representa o número do CLUSTER.
     */
    public HashMap<Integer,File> getAllTests(HashMap<Integer,Cluster> clusters, String directory, String directoryTests, HashMap<Integer,Double> rocFilesTests, int option) throws FileNotFoundException, IOException{
        HashMap<Integer,File> tests = new HashMap<Integer,File>();
        Cluster cluster;
        Element elementTest = null;
        
        /** Limpando a pasta de destino **/
        for(File file: new File(directoryTests).listFiles())
            file.delete();
        
        for(Integer key: clusters.keySet()){// Percorrendo todos os clusters //
            cluster = clusters.get(key);
            
            if(cluster.getQtdVectors() > 1){
                if(option == 0)
                    elementTest = getMinElement(cluster,directory);
                else if(option == 1)
                    elementTest = getMaxElement(cluster,directory);

                rocFilesTests.put(key, elementTest.getRoc());

                /** Copiando o arquivo selecionado para outro diretorio **/
                File source = new File(directory + "/" + elementTest.getDataset());
                File destination = new File(directoryTests + "/" + "cluster " + key + " - " + elementTest.getDataset());
                FileWriter fw = new FileWriter(destination);
                BufferedReader br = new BufferedReader(new FileReader(source));

                while(br.ready()){
                    String line = br.readLine();
                    fw.write(line + "\n");
                }
                fw.flush();
                tests.put(key, destination);
            }
        }
        return tests;
    }
    
    public HashMap<Integer,File> getAllTrains(HashMap<Integer,Cluster> clusters, String directory, String directoryTrains, HashMap<Integer,ArrayList<String>> filesSelectedForTraining, int option) throws IOException{
        HashMap<Integer,File> trains = new HashMap<Integer,File>();
        Cluster cluster;
        Element elementTest = null;
        String line;
        boolean writingAttributes = true;
        
        /** Limpando a pasta de destino **/
        for(File file: new File(directoryTrains).listFiles())
            file.delete();
        
        for(Integer key: clusters.keySet()){
            cluster = clusters.get(key);
            
            if(cluster.getQtdVectors() > 1){
                if(option == 0)
                    elementTest = getMinElement(cluster,directory);
                else if(option == 1)
                    elementTest = getMaxElement(cluster, directory);

                FileWriter fw = new FileWriter(new File(directoryTrains + "/" + "cluster " + key + " - " + "treino.arff"));
                fw.write("@relation cluster" + key + "-train\n"); // Escrevendo a relação no arquivo arff
                trains.put(key, new File(directoryTrains + "/" + "cluster " + key + " - " + "treino.arff"));
                writingAttributes = true;
                filesSelectedForTraining.put(key, new ArrayList<String>());
                for(Element element: cluster.getVectors()){
                    if(element != elementTest){
                        filesSelectedForTraining.get(key).add(element.getDataset());
                        BufferedReader br = new BufferedReader(new FileReader(new File(directory + "/" + element.getDataset())));
                        br.readLine();

                        while(br.ready()){
                            line = br.readLine();
                            if(writingAttributes && (line.contains("@") || line.length() == 0))
                                fw.write(line + "\n");
                            if(!line.contains("@") && line.length() > 0){
                                writingAttributes = false;
                                fw.write(line + "\n");
                            }
                        }
                    }
                }
                fw.flush();
                writingAttributes = true;
            }
        }
        return trains;
    }
    
    public Element getMinElement(Cluster cluster, String directory) throws FileNotFoundException, IOException{
        Element minElement = null;
        int minAmountInstances = Integer.MAX_VALUE;
        
        for(Element element: cluster.getVectors()){
            Instances instances = new Instances(new BufferedReader(new FileReader(new File(directory + "/" + element.getDataset()))));
            
            if(instances.numInstances() < minAmountInstances){
                minAmountInstances = instances.numInstances();
                minElement = element;
            }
        }
        return minElement;
    }
    
    public Element getMaxElement(Cluster cluster, String directory) throws FileNotFoundException, IOException{
        Element maxElement = null;
        int maxAmountInstances = Integer.MIN_VALUE;
        
        for(Element element: cluster.getVectors()){
            Instances instances = new Instances(new BufferedReader(new FileReader(new File(directory + "/" + element.getDataset()))));
            
            if(instances.numInstances() > maxAmountInstances){
                maxAmountInstances = instances.numInstances();
                maxElement = element;
            }
        }
        return maxElement;
    }
    
    public void writeRelation(HashMap<Integer,ArrayList<String>> filesSelectedForTraining, FileWriter fwRelation) throws IOException{
        
        for(Integer key: filesSelectedForTraining.keySet()){
            for(String dataset: filesSelectedForTraining.get(key))
                fwRelation.write(key + ";" + dataset + "\n");
        }
    }
    
    /** CROSS-PROJECT PREDICTION sem FEATURE SELECTION **/
    public void runCrossValidation(HashMap<Integer,Cluster> clusters, HashMap<Integer,File> tests, HashMap<Integer,File> trains, Classifier classifier, String classifierOption, String attributeEvaluatorOptions, String searchEvaluatorOptions, FileWriter result, FileWriter relation, HashMap<Integer,Double> rocFilesTests) throws FileNotFoundException, IOException, Exception{
        File train;
        File test;
        Instances trainInstances;
        Instances testInstances;
        Evaluation eval;
        DecimalFormat df = new DecimalFormat("#.000");
        try{
            for(Integer keyTrain: trains.keySet()){
                train = trains.get(keyTrain);
                trainInstances = new Instances(new BufferedReader(new FileReader(train)));
                trainInstances.setClassIndex(trainInstances.numAttributes() - 1);

                System.out.println(train.getName());

                Cluster cluster = clusters.get(keyTrain);

                for(Integer keyTest: tests.keySet()){
                    eval = new Evaluation(trainInstances);
                    classifier.buildClassifier(trainInstances);

                    test = tests.get(keyTest);
                    testInstances = new Instances(new BufferedReader(new FileReader(test)));
                    testInstances.setClassIndex(testInstances.numAttributes() - 1);

                    eval.evaluateModel(classifier, testInstances);

                    result.write(classifierOption + ";" + attributeEvaluatorOptions + ";" + searchEvaluatorOptions + ";Cluster" + keyTrain + ";" + test.getName().replaceAll(" ","") + ";" + keyTrain + ";" + df.format(cluster.getCentroid()) + ";" + df.format(eval.weightedAreaUnderROC()) + ";" + df.format(eval.weightedPrecision()) + ";" + df.format(eval.weightedRecall()) + ";" + df.format(eval.weightedFMeasure()) + "\n");
                }
            }
        }catch(Exception e){
                System.out.println("Erro ao executar CROSS VALIDATION");
        }
    }
    
    /** CROSS-PROJECT PREDICTION com FEATURE SELECTION **/
    public void runCrossValidation(HashMap<Integer,Cluster> clusters, HashMap<Integer,File> tests, HashMap<Integer,File> trains, Classifier classifier, ASEvaluation evaluation, ASSearch search, String classifierOption, String attributeEvaluatorOptions, String searchEvaluatorOptions, FileWriter result, FileWriter relation, HashMap<Integer,Double> rocFilesTests) throws FileNotFoundException, IOException, Exception{
        File train;
        File test;
        Instances trainInstances;
        Instances testInstances;
        AttributeSelectedClassifier attsel;
        Evaluation eval;
        DecimalFormat df = new DecimalFormat("#.000");
        try{
            for(Integer keyTrain: trains.keySet()){
                train = trains.get(keyTrain);
                trainInstances = new Instances(new BufferedReader(new FileReader(train)));
                trainInstances.setClassIndex(trainInstances.numAttributes() - 1);

                System.out.println(train.getName());

                Cluster cluster = clusters.get(keyTrain);

                for(Integer keyTest: tests.keySet()){
                    eval = new Evaluation(trainInstances);
                    //classifier.buildClassifier(trainInstances);
                    
                    attsel = new AttributeSelectedClassifier();
                    attsel.setClassifier(classifier);
                    attsel.setEvaluator(evaluation);
                    attsel.setSearch(search);
                    attsel.buildClassifier(trainInstances);

                    test = tests.get(keyTest);
                    testInstances = new Instances(new BufferedReader(new FileReader(test)));
                    testInstances.setClassIndex(testInstances.numAttributes() - 1);

                    eval.evaluateModel(attsel, testInstances);

                    result.write(classifierOption + ";" + attributeEvaluatorOptions + ";" + searchEvaluatorOptions + ";Cluster" + keyTrain + ";" + test.getName().replaceAll(" ","") + ";" + keyTrain + ";" + df.format(cluster.getCentroid()) + ";" + df.format(eval.weightedAreaUnderROC()) + ";" + df.format(eval.weightedPrecision()) + ";" + df.format(eval.weightedRecall()) + ";" + df.format(eval.weightedFMeasure()) + "\n");
                }
            }
        }catch(Exception e){
                System.out.println("Erro ao executar CROSS VALIDATION");
        }
    }
    
    public void run(HashMap<Integer,Cluster> clusters, AlgorithmsOptions ao, AttributeEvaluatorOptions aeo, SearchMethodOptions smo, File directory, File directoryTests, File directoryTrains, File result, File relation) throws IOException, FileNotFoundException, Exception{
        int option;        
        Scanner read = new Scanner(System.in);
        System.out.println("Qual deve ser o arquivo de teste? 0 - Menor 1 - Maior");
        option = read.nextInt();
        
        FileWriter fw = new FileWriter(result);
        FileWriter fwRelation = new FileWriter(relation);
        
        HashMap<Integer,ArrayList<String>> filesSelectedForTraining = new HashMap<Integer,ArrayList<String>>();
        HashMap<Integer,Double> rocFilesTests = new HashMap<Integer,Double>();
        HashMap<Integer,File> tests = getAllTests(clusters, directory.toString(), directoryTests.toString(), rocFilesTests, option);
        HashMap<Integer,File> trains = getAllTrains(clusters, directory.toString(), directoryTrains.toString(),filesSelectedForTraining, option);
        
        fw.write("Algoritmo;Attribute;SearchMethod;Cluster;FileTest;FilesTrain;RocCluster;RocTeste;PrecisionTeste;RecallTeste;F-MeasureTeste\n");
        fwRelation.write("Code;File\n");
        writeRelation(filesSelectedForTraining, fwRelation);
        
        //String attributeEvaluatorOptions = null;
        //String searchEvaluatorOptions = null;
        
        System.out.println("Deseja executar CROSS-PROJECT PREDICTION com FEATURE SELECTION? 0 - Não 1 - Sim");
        if(read.nextInt() == 0){
            /*if(aeo == AttributeEvaluatorOptions.CFS)
                attributeEvaluatorOptions = "CFS";
            else if(aeo == AttributeEvaluatorOptions.InfoGain)
                attributeEvaluatorOptions = "InfoGain";

            if(smo == SearchMethodOptions.BestFirst)
                searchEvaluatorOptions = "BestFirst";
            else if(smo == SearchMethodOptions.GeneticSearch)
                searchEvaluatorOptions = "GeneticSearch";
            else if(smo == SearchMethodOptions.GreedyStepwise)
                searchEvaluatorOptions = "GreedyStepwise";
            else if(smo == SearchMethodOptions.Ranker)
                searchEvaluatorOptions = "Ranker";*/

            String classifierOption;
            if(ao == AlgorithmsOptions.J48){
                classifierOption = "J48";
                runCrossValidation(clusters,tests, trains, this.classifierJ48, classifierOption, "N/D", "N/D", fw, fwRelation, rocFilesTests);
            }else if(ao == AlgorithmsOptions.DecisionTable){
                classifierOption = "DecisionTable";
                runCrossValidation(clusters,tests, trains, this.classifierDecisionTable, classifierOption, "N/D", "N/D", fw, fwRelation, rocFilesTests);
            }else if(ao == AlgorithmsOptions.NaiveBayes){
                classifierOption = "NaiveBayes";
                runCrossValidation(clusters,tests, trains, this.classifierNaiveBayes, classifierOption, "N/D", "N/D", fw, fwRelation, rocFilesTests);
            }else if(ao == AlgorithmsOptions.RandomForest){
                classifierOption = "RandomForest";
                runCrossValidation(clusters,tests, trains, this.classifierRandomForest, classifierOption, "N/D", "N/D", fw, fwRelation, rocFilesTests);
            }else if(ao == AlgorithmsOptions.SimpleLogistic){
                classifierOption = "SimpleLogistic";
                runCrossValidation(clusters,tests, trains, this.classifierSimpleLogistic, classifierOption, "N/D", "N/D", fw, fwRelation, rocFilesTests);
            }
        }else{
            if(ao == AlgorithmsOptions.J48){
                if(aeo == AttributeEvaluatorOptions.CFS){
                    if(smo == SearchMethodOptions.BestFirst){
                        runCrossValidation(clusters,tests, trains, this.classifierJ48, this.evalCfs, this.searchBestFirst, "J48", "CFS", "BestFirst", fw, fwRelation, rocFilesTests);
                    }
                    else if(smo == SearchMethodOptions.GeneticSearch){
                        runCrossValidation(clusters,tests, trains, this.classifierJ48, this.evalCfs, this.searchGeneticSearch, "J48", "CFS", "GeneticSearch", fw, fwRelation, rocFilesTests);
                    }
                    else if(smo == SearchMethodOptions.GreedyStepwise){
                        runCrossValidation(clusters,tests, trains, this.classifierJ48, this.evalCfs, this.searchGreedyStepwise, "J48", "CFS", "GreedyStepwise", fw, fwRelation, rocFilesTests);
                    }
                }
                else if(aeo == AttributeEvaluatorOptions.InfoGain){
                    if(smo == SearchMethodOptions.Ranker){
                        runCrossValidation(clusters,tests, trains, this.classifierJ48, this.evalInfoGain, this.searchRanker, "J48", "InfoGain", "Ranker", fw, fwRelation, rocFilesTests);
                    }
                }
            }
            
            else if(ao == AlgorithmsOptions.DecisionTable){
                if(aeo == AttributeEvaluatorOptions.CFS){
                    if(smo == SearchMethodOptions.BestFirst){
                        runCrossValidation(clusters,tests, trains, this.classifierDecisionTable, this.evalCfs, this.searchBestFirst, "DecisionTable", "CFS", "BestFirst", fw, fwRelation, rocFilesTests);
                    }
                    else if(smo == SearchMethodOptions.GeneticSearch){
                        runCrossValidation(clusters,tests, trains, this.classifierDecisionTable, this.evalCfs, this.searchGeneticSearch, "DecisionTable", "CFS", "GeneticSearch", fw, fwRelation, rocFilesTests);
                    }
                    else if(smo == SearchMethodOptions.GreedyStepwise){
                        runCrossValidation(clusters,tests, trains, this.classifierDecisionTable, this.evalCfs, this.searchGreedyStepwise, "DecisionTable", "CFS", "GreedyStepwise", fw, fwRelation, rocFilesTests);
                    }
                }
                else if(aeo == AttributeEvaluatorOptions.InfoGain){
                    if(smo == SearchMethodOptions.Ranker){
                        runCrossValidation(clusters,tests, trains, this.classifierDecisionTable, this.evalInfoGain, this.searchRanker, "DecisionTable", "InfoGain", "Ranker", fw, fwRelation, rocFilesTests);
                    }
                }
            }
            
            else if(ao == AlgorithmsOptions.NaiveBayes){
                if(aeo == AttributeEvaluatorOptions.CFS){
                    if(smo == SearchMethodOptions.BestFirst){
                        runCrossValidation(clusters,tests, trains, this.classifierNaiveBayes, this.evalCfs, this.searchBestFirst, "NaiveBayes", "CFS", "BestFirst", fw, fwRelation, rocFilesTests);
                    }
                    else if(smo == SearchMethodOptions.GeneticSearch){
                        runCrossValidation(clusters,tests, trains, this.classifierNaiveBayes, this.evalCfs, this.searchGeneticSearch, "NaiveBayes", "CFS", "GeneticSearch", fw, fwRelation, rocFilesTests);
                    }
                    else if(smo == SearchMethodOptions.GreedyStepwise){
                        runCrossValidation(clusters,tests, trains, this.classifierNaiveBayes, this.evalCfs, this.searchGreedyStepwise, "NaiveBayes", "CFS", "GreedyStepwise", fw, fwRelation, rocFilesTests);
                    }
                }
                else if(aeo == AttributeEvaluatorOptions.InfoGain){
                    if(smo == SearchMethodOptions.Ranker){
                        runCrossValidation(clusters,tests, trains, this.classifierNaiveBayes, this.evalInfoGain, this.searchRanker, "NaiveBayes", "InfoGain", "Ranker", fw, fwRelation, rocFilesTests);
                    }
                }
            }
            
            else if(ao == AlgorithmsOptions.RandomForest){
                if(aeo == AttributeEvaluatorOptions.CFS){
                    if(smo == SearchMethodOptions.BestFirst){
                        runCrossValidation(clusters,tests, trains, this.classifierRandomForest, this.evalCfs, this.searchBestFirst, "RandomForest", "CFS", "BestFirst", fw, fwRelation, rocFilesTests);
                    }
                    else if(smo == SearchMethodOptions.GeneticSearch){
                        runCrossValidation(clusters,tests, trains, this.classifierRandomForest, this.evalCfs, this.searchGeneticSearch, "RandomForest", "CFS", "GeneticSearch", fw, fwRelation, rocFilesTests);
                    }
                    else if(smo == SearchMethodOptions.GreedyStepwise){
                        runCrossValidation(clusters,tests, trains, this.classifierRandomForest, this.evalCfs, this.searchGreedyStepwise, "RandomForest", "CFS", "GreedyStepwise", fw, fwRelation, rocFilesTests);
                    }
                }
                else if(aeo == AttributeEvaluatorOptions.InfoGain){
                    if(smo == SearchMethodOptions.Ranker){
                        runCrossValidation(clusters,tests, trains, this.classifierRandomForest, this.evalInfoGain, this.searchRanker, "RandomForest", "InfoGain", "Ranker", fw, fwRelation, rocFilesTests);
                    }
                }
            }
            
            else if(ao == AlgorithmsOptions.SimpleLogistic){
                if(aeo == AttributeEvaluatorOptions.CFS){
                    if(smo == SearchMethodOptions.BestFirst){
                        runCrossValidation(clusters,tests, trains, this.classifierSimpleLogistic, this.evalCfs, this.searchBestFirst, "SimpleLogistic", "CFS", "BestFirst", fw, fwRelation, rocFilesTests);
                    }
                    else if(smo == SearchMethodOptions.GeneticSearch){
                        runCrossValidation(clusters,tests, trains, this.classifierSimpleLogistic, this.evalCfs, this.searchGeneticSearch, "SimpleLogistic", "CFS", "GeneticSearch", fw, fwRelation, rocFilesTests);
                    }
                    else if(smo == SearchMethodOptions.GreedyStepwise){
                        runCrossValidation(clusters,tests, trains, this.classifierSimpleLogistic, this.evalCfs, this.searchGreedyStepwise, "SimpleLogistic", "CFS", "GreedyStepwise", fw, fwRelation, rocFilesTests);
                    }
                }
                else if(aeo == AttributeEvaluatorOptions.InfoGain){
                    if(smo == SearchMethodOptions.Ranker){
                        runCrossValidation(clusters,tests, trains, this.classifierSimpleLogistic, this.evalInfoGain, this.searchRanker, "SimpleLogistic", "InfoGain", "Ranker", fw, fwRelation, rocFilesTests);
                    }
                }
            }
        }
        
        
        fw.flush();
        fwRelation.flush();
    }
}

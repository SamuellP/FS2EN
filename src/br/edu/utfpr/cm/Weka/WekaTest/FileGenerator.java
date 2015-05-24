/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;
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
public class FileGenerator {
    
    public AlgorithmsOptions algorithmOption;
    public AttributeEvaluatorOptions attributeEvaluatorOption;
    public SearchMethodOptions searchMethodOption;
    
    public AttributeSelectedClassifier attsel = new AttributeSelectedClassifier();
    
    public AttributeSelection2 featsel; // Satin: Nossa implementaçao da Classe!
    
    private StringBuffer forPredictionsPrinting = new StringBuffer();
    private weka.core.Range attsToOutput = null; 
    private Boolean outputDistribution = true;
    
    public ASEvaluation evalCfs = new CfsSubsetEval();
    public ASEvaluation evalInfoGain = new InfoGainAttributeEval();    
    //public CfsSubsetEval evalCfs = new CfsSubsetEval();
    //public InfoGainAttributeEval evalInfoGain = new InfoGainAttributeEval();    
    
    public ASSearch searchBestFirst = new BestFirst();
    public ASSearch searchGreedyStepwise = new GreedyStepwise();
    public ASSearch searchGeneticSearch = new GeneticSearch();
    public ASSearch searchRanker = new Ranker();
    //public BestFirst searchBestFirst = new BestFirst();
    //public GreedyStepwise searchGreedyStepwise = new GreedyStepwise();
    //public GeneticSearch searchGeneticSearch = new GeneticSearch();
    //public Ranker searchRanker = new Ranker();
    
    public NaiveBayes classifierNaiveBayes = new NaiveBayes();
    public RandomForest classifierRandomForest = new RandomForest();
    public SimpleLogistic classifierSimpleLogistic = new SimpleLogistic();
    public J48 classifierJ48 = new J48();
    public DecisionTable classifierDecisionTable = new DecisionTable();
    //public Classifier classifierNaiveBayes = new NaiveBayes();
    //public Classifier classifierRandomForest = new RandomForest();
    //public Classifier classifierSimpleLogistic = new SimpleLogistic();
    //public Classifier classifierJ48 = new J48();
    //public Classifier classifierDecisionTable = new DecisionTable();
    
    FileGenerator(AlgorithmsOptions aoOptions, AttributeEvaluatorOptions aeOptions, SearchMethodOptions smOptions) throws Exception{
        this.algorithmOption = aoOptions;
        this.attributeEvaluatorOption = aeOptions;
        this.searchMethodOption = smOptions;
    }
    
    public void writeFields(FileWriter fields, String dataset, Instances instances, ASEvaluation eval, ASSearch search, String classifierName, String evalName, String searchName) throws Exception{
        try {
            featsel = new AttributeSelection2();
            featsel.setEvaluator(eval);            
            featsel.setSearch(search);
            featsel.SelectAttributes(instances);           
            fields.write(featsel.getAttributsCFS(dataset, classifierName, evalName, searchName));
        } catch (Exception e) {            
            System.out.println("Erro na geração dos fields: Dataset: " + dataset);
        }
    }
    
    /** Rodando com o CLASSIFICADOR mais FEATURE SELECTION **/
    public void writeRoc(File dataset, FileWriter rocFile, FileWriter fields, Classifier classifier, ASEvaluation eval, ASSearch search, String ao, String aeo, String smo) throws IOException{
        DecimalFormat df = new DecimalFormat("#.000");
        
        try{
            
            Instances instances = new Instances(new BufferedReader(new FileReader(dataset)));
            instances.setClassIndex(instances.numAttributes() - 1);
            
            attsel.setClassifier(classifier);
            attsel.setEvaluator(eval);
            attsel.setSearch(search);
            
            Evaluation evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
            
            if(instances.numInstances() >= 5){
                  /*rocFile.write(dataset.getName() + ";" + dataset.getPath() + ";" + ao + ";" + aeo + 
                                ";" + smo + ";" + 
                                df.format(evaluation.weightedAreaUnderROC()) + ";" +
                                df.format(evaluation.weightedPrecision()) + ";" +
                                df.format(evaluation.weightedRecall()) + ";" +
                                df.format(evaluation.weightedFMeasure()) + "\n"
                          );*/
                  
                  rocFile.write(dataset.getName() + ";" + dataset.getPath() + ";" + ao + ";" + aeo + 
                                ";" + smo + ";" + 
                                df.format(evaluation.weightedAreaUnderROC()) + ";" +
                                df.format(evaluation.weightedPrecision()) + ";" +
                                df.format(evaluation.weightedRecall()) + ";" +
                                df.format(evaluation.weightedFMeasure()) + ";" +
                                df.format(evaluation.weightedTruePositiveRate()) + ";" +
                                df.format(evaluation.weightedFalsePositiveRate()) + ";" +
                                (int)evaluation.numInstances() + ";" +
                                (int)evaluation.correct() + ";" +
                                df.format(evaluation.pctCorrect()) + ";" +
                                (int)evaluation.incorrect() + ";" +
                                df.format(evaluation.pctIncorrect()) + ";" +
                                (int)(evaluation.numTrueNegatives(1) + evaluation.numFalseNegatives(0)) + ";" +
                                (int)(evaluation.numFalsePositives(0) + evaluation.numTruePositives(1)) + ";" +
                                (int)evaluation.numTrueNegatives(1) + ";" +
                                (int)evaluation.numFalseNegatives(0) + ";" + (int)evaluation.numFalsePositives(0) + ";" +  
                                (int)evaluation.numTruePositives(1) + "\n"
                          );
                  /*
                  if(afc == AttributesForClustering.Roc)
                      rocFile.write(";" + df.format(evaluation.weightedAreaUnderROC()));
                  else if(afc == AttributesForClustering.Roc_And_F_Measure)
                      rocFile.write(";" + df.format(evaluation.weightedAreaUnderROC()) + ";" + df.format(evaluation.weightedFMeasure()));
                  else if(afc == AttributesForClustering.Roc_And_Precision)
                      rocFile.write(";" + df.format(evaluation.weightedAreaUnderROC()) + ";" + df.format(evaluation.weightedPrecision()));
                  else if(afc == AttributesForClustering.Roc_And_Recall)
                      rocFile.write(";" + df.format(evaluation.weightedAreaUnderROC()) + ";" + df.format(evaluation.weightedRecall()));
                  else if(afc == AttributesForClustering.Recall)
                      rocFile.write(";" + df.format(evaluation.weightedRecall()));
                  else if(afc == AttributesForClustering.Recall_And_F_Measure)
                      rocFile.write(";" + df.format(evaluation.weightedRecall()) + ";" + df.format(evaluation.weightedFMeasure()));
                  else if(afc == AttributesForClustering.Precision)
                      rocFile.write(";" + df.format(evaluation.weightedPrecision()));
                  else if(afc == AttributesForClustering.Precision_And_F_Measure)
                      rocFile.write(";" + df.format(evaluation.weightedPrecision()) + ";" + df.format(evaluation.weightedFMeasure()));
                  else if(afc == AttributesForClustering.Precision_And_Recall)
                      rocFile.write(";" + df.format(evaluation.weightedPrecision()) + ";" + df.format(evaluation.weightedRecall()));
                  else if(afc == AttributesForClustering.F_Measure)
                      rocFile.write(";" + df.format(evaluation.weightedFMeasure()));*/
                  
                  writeFields(fields,dataset.getName(),instances, eval, search, ao, aeo, smo); // gravando os campos
                  
                  System.out.println("instância processada: nome: " + dataset.getName()); // Satin: Gerando log em tela de quanto já foi executado do programa!
            }else{
                  System.out.println("Not computed: Less than 5 instances....");
            }
            
        }catch(Exception e){
            System.out.println("Tratando exceção para o arquivo: " + dataset.getName());
        }
    }
    
    /** Rodando somente com o CLASSIFICADOR, sem FEATURE SELECTION */
    public void writeRoc(File dataset, FileWriter rocFile, Classifier classifier, String ao) throws IOException{
        DecimalFormat df = new DecimalFormat("#.000");
        
        try{
            
            Instances instances = new Instances(new BufferedReader(new FileReader(dataset)));
            instances.setClassIndex(instances.numAttributes() - 1);
            
            //attsel.setClassifier(classifier);
            //attsel.setEvaluator(eval);
            //attsel.setSearch(search);
            
            Evaluation evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(classifier,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
            
            if(instances.numInstances() >= 5){
                  /*rocFile.write(dataset.getName() + ";" + dataset.getPath() + ";" + ao + ";" + "N/D" + 
                                ";" + "N/D" + ";" + 
                                df.format(evaluation.weightedAreaUnderROC()) + ";" +
                                df.format(evaluation.weightedPrecision()) + ";" +
                                df.format(evaluation.weightedRecall()) + ";" +
                                df.format(evaluation.weightedFMeasure()) + "\n"
                          );*/
                  
                  rocFile.write(dataset.getName() + ";" + dataset.getPath() + ";" + ao + ";" + "N/D" + 
                                ";" + "N/D" + ";" + 
                                df.format(evaluation.weightedAreaUnderROC()) + ";" +
                                df.format(evaluation.weightedPrecision()) + ";" +
                                df.format(evaluation.weightedRecall()) + ";" +
                                df.format(evaluation.weightedFMeasure()) + ";" +
                                df.format(evaluation.weightedTruePositiveRate()) + ";" +
                                df.format(evaluation.weightedFalsePositiveRate()) + ";" +
                                (int)evaluation.numInstances() + ";" +
                                (int)evaluation.correct() + ";" +
                                df.format(evaluation.pctCorrect()) + ";" +
                                (int)evaluation.incorrect() + ";" +
                                df.format(evaluation.pctIncorrect()) + ";" +
                                (int)(evaluation.numTrueNegatives(1) + evaluation.numFalseNegatives(0)) + ";" +
                                (int)(evaluation.numFalsePositives(0) + evaluation.numTruePositives(1)) + ";" +
                                (int)evaluation.numTrueNegatives(1) + ";" +
                                (int)evaluation.numFalseNegatives(0) + ";" + (int)evaluation.numFalsePositives(0) + ";" +  
                                (int)evaluation.numTruePositives(1) + "\n"
                          );
                  /*
                  if(afc == AttributesForClustering.Roc)
                      rocFile.write(";" + df.format(evaluation.weightedAreaUnderROC()));
                  else if(afc == AttributesForClustering.Roc_And_F_Measure)
                      rocFile.write(";" + df.format(evaluation.weightedAreaUnderROC()) + ";" + df.format(evaluation.weightedFMeasure()));
                  else if(afc == AttributesForClustering.Roc_And_Precision)
                      rocFile.write(";" + df.format(evaluation.weightedAreaUnderROC()) + ";" + df.format(evaluation.weightedPrecision()));
                  else if(afc == AttributesForClustering.Roc_And_Recall)
                      rocFile.write(";" + df.format(evaluation.weightedAreaUnderROC()) + ";" + df.format(evaluation.weightedRecall()));
                  else if(afc == AttributesForClustering.Recall)
                      rocFile.write(";" + df.format(evaluation.weightedRecall()));
                  else if(afc == AttributesForClustering.Recall_And_F_Measure)
                      rocFile.write(";" + df.format(evaluation.weightedRecall()) + ";" + df.format(evaluation.weightedFMeasure()));
                  else if(afc == AttributesForClustering.Precision)
                      rocFile.write(";" + df.format(evaluation.weightedPrecision()));
                  else if(afc == AttributesForClustering.Precision_And_F_Measure)
                      rocFile.write(";" + df.format(evaluation.weightedPrecision()) + ";" + df.format(evaluation.weightedFMeasure()));
                  else if(afc == AttributesForClustering.Precision_And_Recall)
                      rocFile.write(";" + df.format(evaluation.weightedPrecision()) + ";" + df.format(evaluation.weightedRecall()));
                  else if(afc == AttributesForClustering.F_Measure)
                      rocFile.write(";" + df.format(evaluation.weightedFMeasure()));*/
                  
                  //writeFields(fields,dataset.getName(),instances, eval, search, ao, aeo, smo); // gravando os campos
                  
                  System.out.println("instância processada: nome: " + dataset.getName()); // Satin: Gerando log em tela de quanto já foi executado do programa!
            }else{
                  System.out.println("Not computed: Less than 5 instances....");
            }
            
        }catch(Exception e){
            System.out.println("Tratando exceção para o arquivo: " + dataset.getName());
        }
    }
    
    public void generateRocFileAndAttributeFile(File[] datasets, File rocFile, File attributeFile, AttributesForClustering afc) throws IOException{
        FileWriter fwRoc = new FileWriter(rocFile);
        FileWriter fwAttribute = new FileWriter(attributeFile);
        
        fwRoc.write("Dataset;Path;Algoritmo;AttributeEvaluator;SearchMethod;ROC;Precision;Recall;FMeasure;TP-Rate;FP-Rate;TotalInstancias;InstanciasCorretas;%Corret.;InstanciasIncorretas;%Incorret.;NrInstanciasBuggy;NrInstanciasClean;TP;FP;FN;TN\n");
        
        for(File file: datasets){
            
            if(this.algorithmOption == AlgorithmsOptions.DecisionTable){
                if(this.attributeEvaluatorOption == AttributeEvaluatorOptions.CFS){
                    if(this.searchMethodOption == SearchMethodOptions.BestFirst){
                        writeRoc(file, fwRoc, fwAttribute, this.classifierDecisionTable, this.evalCfs, this.searchBestFirst, "DecisionTable", "CFS", "BestFirst");
                    }else if(this.searchMethodOption == SearchMethodOptions.GeneticSearch){
                        writeRoc(file, fwRoc, fwAttribute, this.classifierDecisionTable, this.evalCfs, this.searchGeneticSearch, "DecisionTable", "CFS", "GeneticSearch");
                    }else if(this.searchMethodOption == SearchMethodOptions.GreedyStepwise){
                        writeRoc(file, fwRoc, fwAttribute, this.classifierDecisionTable, this.evalCfs, this.searchGreedyStepwise, "DecisionTable", "CFS", "GreedyStepwise");
                    }else{
                        System.out.println("Erro durante a geração do arquivo contendo o ROC e do arquivo contendo as features");
                        return;
                    }
                }else if(this.attributeEvaluatorOption == AttributeEvaluatorOptions.InfoGain){
                    if(this.searchMethodOption == SearchMethodOptions.Ranker){
                        writeRoc(file, fwRoc, fwAttribute, this.classifierDecisionTable, this.evalInfoGain, this.searchRanker, "DecisionTable", "InfoGain", "Ranker");
                    }else{
                        System.out.println("Erro durante a geração do arquivo contendo o ROC e do arquivo contendo as features");
                        return;
                    }
                }
            }
            
            if(this.algorithmOption == AlgorithmsOptions.J48){
                if(this.attributeEvaluatorOption == AttributeEvaluatorOptions.CFS){
                    if(this.searchMethodOption == SearchMethodOptions.BestFirst){
                        writeRoc(file, fwRoc, fwAttribute, this.classifierJ48, this.evalCfs, this.searchBestFirst, "J48", "CFS", "BestFirst");
                    }else if(this.searchMethodOption == SearchMethodOptions.GeneticSearch){
                        writeRoc(file, fwRoc, fwAttribute, this.classifierJ48, this.evalCfs, this.searchGeneticSearch, "J48", "CFS", "GeneticSearch");
                    }else if(this.searchMethodOption == SearchMethodOptions.GreedyStepwise){
                        writeRoc(file, fwRoc, fwAttribute, this.classifierJ48, this.evalCfs, this.searchGreedyStepwise, "J48", "CFS", "GreedyStepwise");
                    }else{
                        System.out.println("Erro durante a geração do arquivo contendo o ROC e do arquivo contendo as features");
                        return;
                    }
                }else if(this.attributeEvaluatorOption == AttributeEvaluatorOptions.InfoGain){
                    if(this.searchMethodOption == SearchMethodOptions.Ranker){
                        writeRoc(file, fwRoc, fwAttribute, this.classifierJ48, this.evalInfoGain, this.searchRanker, "J48", "InfoGain", "Ranker");
                    }else{
                        System.out.println("Erro durante a geração do arquivo contendo o ROC e do arquivo contendo as features");
                        return;
                    }
                }
            }
            
            if(this.algorithmOption == AlgorithmsOptions.NaiveBayes){
                if(this.attributeEvaluatorOption == AttributeEvaluatorOptions.CFS){
                    if(this.searchMethodOption == SearchMethodOptions.BestFirst){
                        writeRoc(file, fwRoc, fwAttribute, this.classifierNaiveBayes, this.evalCfs, this.searchBestFirst, "NaiveBayes", "CFS", "BestFirst");
                    }else if(this.searchMethodOption == SearchMethodOptions.GeneticSearch){
                        writeRoc(file, fwRoc, fwAttribute, this.classifierNaiveBayes, this.evalCfs, this.searchGeneticSearch, "NaiveBayes", "CFS", "GeneticSearch");
                    }else if(this.searchMethodOption == SearchMethodOptions.GreedyStepwise){
                        writeRoc(file, fwRoc, fwAttribute, this.classifierNaiveBayes, this.evalCfs, this.searchGreedyStepwise, "NaiveBayes", "CFS", "GreedyStepwise");
                    }else{
                        System.out.println("Erro durante a geração do arquivo contendo o ROC e do arquivo contendo as features");
                        return;
                    }
                }else if(this.attributeEvaluatorOption == AttributeEvaluatorOptions.InfoGain){
                    if(this.searchMethodOption == SearchMethodOptions.Ranker){
                        writeRoc(file, fwRoc, fwAttribute, this.classifierNaiveBayes, this.evalInfoGain, this.searchRanker, "NaiveBayes", "InfoGain", "Ranker");
                    }else{
                        System.out.println("Erro durante a geração do arquivo contendo o ROC e do arquivo contendo as features");
                        return;
                    }
                }
            }
            
            if(this.algorithmOption == AlgorithmsOptions.RandomForest){
                if(this.attributeEvaluatorOption == AttributeEvaluatorOptions.CFS){
                    if(this.searchMethodOption == SearchMethodOptions.BestFirst){
                        writeRoc(file, fwRoc, fwAttribute, this.classifierRandomForest, this.evalCfs, this.searchBestFirst, "RandomForest", "CFS", "BestFirst");
                    }else if(this.searchMethodOption == SearchMethodOptions.GeneticSearch){
                        writeRoc(file, fwRoc, fwAttribute, this.classifierRandomForest, this.evalCfs, this.searchGeneticSearch, "RandomForest", "CFS", "GeneticSearch");
                    }else if(this.searchMethodOption == SearchMethodOptions.GreedyStepwise){
                        writeRoc(file, fwRoc, fwAttribute, this.classifierRandomForest, this.evalCfs, this.searchGreedyStepwise, "RandomForest", "CFS", "GreedyStepwise");
                    }else{
                        System.out.println("Erro durante a geração do arquivo contendo o ROC e do arquivo contendo as features");
                        return;
                    }
                }else if(this.attributeEvaluatorOption == AttributeEvaluatorOptions.InfoGain){
                    if(this.searchMethodOption == SearchMethodOptions.Ranker){
                        writeRoc(file, fwRoc, fwAttribute, this.classifierRandomForest, this.evalInfoGain, this.searchRanker, "RandomForest", "InfoGain", "Ranker");
                    }else{
                        System.out.println("Erro durante a geração do arquivo contendo o ROC e do arquivo contendo as features");
                        return;
                    }
                }
            }
            
            if(this.algorithmOption == AlgorithmsOptions.SimpleLogistic){
                if(this.attributeEvaluatorOption == AttributeEvaluatorOptions.CFS){
                    if(this.searchMethodOption == SearchMethodOptions.BestFirst){
                        writeRoc(file, fwRoc, fwAttribute, this.classifierSimpleLogistic, this.evalCfs, this.searchBestFirst, "SimpleLogistic", "CFS", "BestFirst");
                    }else if(this.searchMethodOption == SearchMethodOptions.GeneticSearch){
                        writeRoc(file, fwRoc, fwAttribute, this.classifierSimpleLogistic, this.evalCfs, this.searchGeneticSearch, "SimpleLogistic", "CFS", "GeneticSearch");
                    }else if(this.searchMethodOption == SearchMethodOptions.GreedyStepwise){
                        writeRoc(file, fwRoc, fwAttribute, this.classifierSimpleLogistic, this.evalCfs, this.searchGreedyStepwise, "SimpleLogistic", "CFS", "GreedyStepwise");
                    }else{
                        System.out.println("Erro durante a geração do arquivo contendo o ROC e do arquivo contendo as features");
                        return;
                    }
                }else if(this.attributeEvaluatorOption == AttributeEvaluatorOptions.InfoGain){
                    if(this.searchMethodOption == SearchMethodOptions.Ranker){
                        writeRoc(file, fwRoc, fwAttribute, this.classifierSimpleLogistic, this.evalInfoGain, this.searchRanker, "SimpleLogistic", "InfoGain", "Ranker");
                    }else{
                        System.out.println("Erro durante a geração do arquivo contendo o ROC e do arquivo contendo as features");
                        return;
                    }
                }
            }
        }
        fwRoc.flush();
        fwAttribute.flush();
    }
    
    public void generateRocFile(File[] datasets, File rocFile, AttributesForClustering afc) throws IOException{
        FileWriter fwRoc = new FileWriter(rocFile);
        fwRoc.write("Dataset;Path;Algoritmo;AttributeEvaluator;SearchMethod;ROC;Precision;Recall;FMeasure;TP-Rate;FP-Rate;TotalInstancias;InstanciasCorretas;%Corret.;InstanciasIncorretas;%Incorret.;NrInstanciasBuggy;NrInstanciasClean;TP;FP;FN;TN\n");
        
        for(File file: datasets){
            
            if(this.algorithmOption == AlgorithmsOptions.DecisionTable){
                writeRoc(file, fwRoc, this.classifierDecisionTable, "DecisionTable");
            }
            
            else if(this.algorithmOption == AlgorithmsOptions.J48){
                writeRoc(file, fwRoc, this.classifierJ48, "J48");
            }
            
            else if(this.algorithmOption == AlgorithmsOptions.NaiveBayes){
                writeRoc(file, fwRoc, this.classifierNaiveBayes, "NaiveBayes");
            }
            
            else if(this.algorithmOption == AlgorithmsOptions.RandomForest){
                writeRoc(file, fwRoc, this.classifierRandomForest, "RandomForest");
            }
            
            else if(this.algorithmOption == AlgorithmsOptions.SimpleLogistic){
                writeRoc(file, fwRoc, this.classifierSimpleLogistic, "SimpleLogistic");
            }
            
            else {
                System.out.println("Erro durante a geração do arquivo contendo o ROC");
                return;
            }
        }
        fwRoc.flush();
    }
}

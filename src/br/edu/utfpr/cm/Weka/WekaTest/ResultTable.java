/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

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
import weka.attributeSelection.GainRatioAttributeEval;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.GeneticSearch;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.Ranker;
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
public class ResultTable {
    public AlgorithmsOptions option;
    
    public AttributeSelectedClassifier attsel = new AttributeSelectedClassifier();
    
    // Ricardo - alteração dos tipos dos algotimos de pesquisa
    //public CfsSubsetEval evalCfs = new CfsSubsetEval();
    //public ASEvaluation evalInfoGain = new GainRatioAttributeEval(); Ricardo: não é o GainRatio que devemos usar e sim o InfoGain
    //public ASEvaluation evalInfoGain = new InfoGainAttributeEval();

    public ASEvaluation evalCfs = new CfsSubsetEval();
    public ASEvaluation evalInfoGain = new InfoGainAttributeEval();    
    
    
            
    // Ricardo - Alteração da declaração dos algoritmos de pesquisa de atributos
    //public BestFirst searchBestFirst = new BestFirst();
    //public GreedyStepwise searchGreedyStepwise = new GreedyStepwise();
    //public GeneticSearch searchGeneticSearch = new GeneticSearch();
    public ASSearch searchBestFirst = new BestFirst();
    public ASSearch searchGreedyStepwise = new GreedyStepwise();
    public ASSearch searchGeneticSearch = new GeneticSearch();
    public ASSearch searchRanker = new Ranker();
    
    public NaiveBayes classifierNaiveBayes = new NaiveBayes();
    public RandomForest classifierRandomForest = new RandomForest();
    public SimpleLogistic classifierSimpleLogistic = new SimpleLogistic();
    public J48 classifierJ48 = new J48();
    public DecisionTable classifierDecisionTable = new DecisionTable();
    
    public Evaluation evaluation = null;
    
    public AttributeSelection2 featsel = null; // Satin: Nossa implementaçao da Classe!
    
    public StringBuffer forPredictionsPrinting = new StringBuffer();
    public weka.core.Range attsToOutput = null; 
    public Boolean outputDistribution = true;
    
    private int code = 1;
    
    public ResultTable(AlgorithmsOptions option){
        this.option = option;
    }

    public void writeFields(int code, FileWriter fields, String dataset, Instances instances, ASEvaluation eval, ASSearch search, String nome1, String nome2) throws IOException {
        try {
            featsel = null;                            
            featsel = new AttributeSelection2(); // Satin: Nossa implementaçao da Classe!            
            if (eval instanceof CfsSubsetEval) {
                featsel.setEvaluator(eval);
                featsel.setSearch(search);
                featsel.SelectAttributes(instances);
                fields.write(featsel.getAttributsCFS(code, dataset, nome1, nome2));
            } else if (eval instanceof InfoGainAttributeEval) {
                featsel.setEvaluator(eval);
                featsel.setSearch(search);
                featsel.SelectAttributes(instances);
                fields.write(featsel.getAttributsRanker(code, dataset, nome1, nome2));                
            } else {
                throw new Exception("Tem Aliem aqui.");
            }
        } catch (Exception e) {
            System.out.println("Erro na geração dos fields: Code: " + code + " dataset: " + dataset);
        }
    }
    
    public void runNaiveBayes(File dataset, FileWriter table, FileWriter fields) throws IOException{
        DecimalFormat df = new DecimalFormat("#.000");
            
        try{
            Instances instances = new Instances(new FileReader(dataset));
            instances.setClassIndex(instances.numAttributes() - 1);
     
            /*****     RODANDO NaiveBayes com CFS + BestFirst     ****/
            attsel.setClassifier(this.classifierNaiveBayes);

            attsel.setEvaluator(this.evalCfs);
            attsel.setSearch(this.searchBestFirst);
            evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
                       
            if(instances.numInstances() >= 5){
                table.write(code + ";" + dataset.getName() + ';' + "Naive/Bayes;" + 
                  "CFS/(1);Best/First/(1);" + evaluation.numInstances() + ';' + 
                  evaluation.correct() + ';' + df.format(evaluation.pctCorrect()) + ';' + 
                  evaluation.incorrect() + ';' + df.format(evaluation.pctIncorrect()) + ';' + 
                  df.format(evaluation.areaUnderROC(1)) + ';' + 
                  df.format(evaluation.areaUnderROC(0)) + ';' + evaluation.numTrueNegatives(1) + 
                  ';' + evaluation.numFalsePositives(0) + ';' + evaluation.numFalseNegatives(0) + 
                  ';' + evaluation.numTruePositives(1) + ";" + "S\n");

                  writeFields(code,fields,dataset.getName(),instances, this.evalCfs, this.searchBestFirst, "CFS", "BestFirst"); // gravando os campos
                  
                  System.out.println("instância processada:" + code + " nome: " + dataset.getName()); // Satin: Gerando log em tela de quanto já foi executado do programa!
                  code++;
            }else{
                  System.out.println("Not computed: Less than 5 instances....");
            }
                        
            /*****     RODANDO NaiveBayes com CFS + GreedyStepwise     ****/
                        
            attsel.setSearch(this.searchGreedyStepwise);

            evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
                        
            if(instances.numInstances() >= 5){
                table.write(code + ";" + dataset.getName() + ';' + "Naive/Bayes;" + 
                 "CFS/(1);Greedy/Stepwise(2);" + evaluation.numInstances() + ';' + 
                  evaluation.correct() + ';' + df.format(evaluation.pctCorrect()) + ';' + 
                  evaluation.incorrect() + ';' + df.format(evaluation.pctIncorrect()) + ';' + 
                  df.format(evaluation.areaUnderROC(1)) + ';' + 
                  df.format(evaluation.areaUnderROC(0)) + ';' + evaluation.numTrueNegatives(1) + 
                  ';' + evaluation.numFalsePositives(0) + ';' + evaluation.numFalseNegatives(0) + 
                  ';' + evaluation.numTruePositives(1) + ";" + "S\n");
                
                  writeFields(code,fields,dataset.getName(),instances, this.evalCfs, this.searchGreedyStepwise, "CFS","GreedyStepwise"); // gravando os campos
                  System.out.println("instância processada:" + code + " nome: " + dataset.getName());// Satin: Gerando log em tela de quanto já foi executado do programa!
                  code++;                  
            }else{
                  System.out.println("Not computed: Less than 5 instances....");
            }
                
            /*****     RODANDO NaiveBayes com CFS + GeneticSearch     ****/
                        
            attsel.setSearch(this.searchGeneticSearch);

            evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
                        
            if(instances.numInstances() >= 5){
                 table.write(code + ";" + dataset.getName() + ';' + "Naive/Bayes;" + 
                  "CFS/(1);Genetic/Search(3);" + evaluation.numInstances() + ';' + 
                  evaluation.correct() + ';' + df.format(evaluation.pctCorrect()) + ';' + 
                  evaluation.incorrect() + ';' + df.format(evaluation.pctIncorrect()) + ';' + 
                  df.format(evaluation.areaUnderROC(1)) + ';' + 
                  df.format(evaluation.areaUnderROC(0)) + ';' + evaluation.numTrueNegatives(1) + 
                  ';' + evaluation.numFalsePositives(0) + ';' + evaluation.numFalseNegatives(0) + 
                  ';' + evaluation.numTruePositives(1) + ";" + "S\n");                
                  writeFields(code,fields,dataset.getName(),instances, this.evalCfs, this.searchGeneticSearch, "CFS", "GeneticSearch"); // gravando os campos                 
                  System.out.println("instância processada:" + code + " nome: " + dataset.getName());// Satin: Gerando log em tela de quanto já foi executado do programa!
                  code++;                  
            }else{
                  System.out.println("Not computed: Less than 5 instances....");
            }
                
            /*****     RODANDO NaiveBayes com InfoGain + Ranker     ****/
                
            attsel.setEvaluator(this.evalInfoGain);
            attsel.setSearch(this.searchRanker);

            evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
                        
            if(instances.numInstances() >= 5){
                 table.write(code + ";" + dataset.getName() + ';' + "Naive/Bayes;" + 
                 "InfoGain/(2);Ranker/(4);" + evaluation.numInstances() + ';' + 
                 evaluation.correct() + ';' + df.format(evaluation.pctCorrect()) + ';' + 
                 evaluation.incorrect() + ';' + df.format(evaluation.pctIncorrect()) + ';' + 
                 df.format(evaluation.areaUnderROC(1)) + ';' + 
                 df.format(evaluation.areaUnderROC(0)) + ';' + evaluation.numTrueNegatives(1) + 
                 ';' + evaluation.numFalsePositives(0) + ';' + evaluation.numFalseNegatives(0) + 
                 ';' + evaluation.numTruePositives(1) + ";" + "S\n");
                 writeFields(code,fields,dataset.getName(),instances, this.evalInfoGain, this.searchRanker, "InfoGain", "Ranker"); // gravando os campos                                  
                 System.out.println("instância processada:" + code + " nome: " + dataset.getName());// Satin: Gerando log em tela de quanto já foi executado do programa!
                 code++;                  
            }else{
                 System.out.println("Not computed: Less than 5 instances....");
            }
        }catch(Exception e){
            System.out.println("Tratando exceção para o arquivo: ");
        }
    }
    
    public void runRandomForest(File dataset,FileWriter table, FileWriter fields){
        DecimalFormat df = new DecimalFormat("#.00");
            
        try{
            Instances instances = new Instances(new FileReader(dataset));
            instances.setClassIndex(instances.numAttributes() - 1);
     
            /*****     RODANDO RandomForest com CFS + BestFirst     ****/
            attsel.setClassifier(this.classifierRandomForest);

            attsel.setEvaluator(this.evalCfs);
            attsel.setSearch(this.searchBestFirst);
            evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
                       
            if(instances.numInstances() >= 5){
                table.write(code + ";" + dataset.getName() + ';' + "Random/Forest;" + 
                  "CFS/(1);Best/First/(1);" + evaluation.numInstances() + ';' + 
                  evaluation.correct() + ';' + df.format(evaluation.pctCorrect()) + ';' + 
                  evaluation.incorrect() + ';' + df.format(evaluation.pctIncorrect()) + ';' + 
                  df.format(evaluation.areaUnderROC(1)) + ';' + 
                  df.format(evaluation.areaUnderROC(0)) + ';' + evaluation.numTrueNegatives(1) + 
                  ';' + evaluation.numFalsePositives(0) + ';' + evaluation.numFalseNegatives(0) + 
                  ';' + evaluation.numTruePositives(1) + ";" + "S\n");
                  writeFields(code,fields,dataset.getName(),instances, this.evalCfs, this.searchBestFirst, "CFS", "BestFirst"); // gravando os campos                                                  
                  System.out.println("instância processada:" + code + " nome: " + dataset.getName());// Satin: Gerando log em tela de quanto já foi executado do programa!
                  code++;                  
            }else{
                  System.out.println("Not computed: Less than 5 instances....");
            }
                        
            /*****     RODANDO RandomForest com CFS + GreedyStepwise     ****/
                        
            attsel.setSearch(this.searchGreedyStepwise);

            evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
                        
            if(instances.numInstances() >= 5){
                table.write(code + ";" + dataset.getName() + ';' + "Random/Forest;" + 
                 "CFS/(1);Greedy/Stepwise(2);" + evaluation.numInstances() + ';' + 
                  evaluation.correct() + ';' + df.format(evaluation.pctCorrect()) + ';' + 
                  evaluation.incorrect() + ';' + df.format(evaluation.pctIncorrect()) + ';' + 
                  df.format(evaluation.areaUnderROC(1)) + ';' + 
                  df.format(evaluation.areaUnderROC(0)) + ';' + evaluation.numTrueNegatives(1) + 
                  ';' + evaluation.numFalsePositives(0) + ';' + evaluation.numFalseNegatives(0) + 
                  ';' + evaluation.numTruePositives(1) + ";" + "S\n");
                  writeFields(code,fields,dataset.getName(),instances, this.evalCfs, this.searchGreedyStepwise, "CFS", "GreedyStepwise"); // gravando os campos                                                                  
                  System.out.println("instância processada:" + code + " nome: " + dataset.getName());// Satin: Gerando log em tela de quanto já foi executado do programa!
                  code++;                  
            }else{
                  System.out.println("Not computed: Less than 5 instances....");
            }
                
            /*****     RODANDO RandomForest com CFS + GeneticSearch     ****/
                        
            attsel.setSearch(this.searchGeneticSearch);

            evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
                        
            if(instances.numInstances() >= 5){
                 table.write(code + ";" + dataset.getName() + ';' + "RandomForest;" + 
                  "CFS/(1);Genetic/Search(3);" + evaluation.numInstances() + ';' + 
                  evaluation.correct() + ';' + df.format(evaluation.pctCorrect()) + ';' + 
                  evaluation.incorrect() + ';' + df.format(evaluation.pctIncorrect()) + ';' + 
                  df.format(evaluation.areaUnderROC(1)) + ';' + 
                  df.format(evaluation.areaUnderROC(0)) + ';' + evaluation.numTrueNegatives(1) + 
                  ';' + evaluation.numFalsePositives(0) + ';' + evaluation.numFalseNegatives(0) + 
                  ';' + evaluation.numTruePositives(1) + ";" + "S\n");
                  writeFields(code,fields,dataset.getName(),instances, this.evalCfs, this.searchGeneticSearch, "CFS","GeneticSearch"); // gravando os campos                                                  
                  System.out.println("instância processada:" + code + " nome: " + dataset.getName());// Satin: Gerando log em tela de quanto já foi executado do programa!
                  code++;                  
            }else{
                  System.out.println("Not computed: Less than 5 instances....");
            }
                
            /*****     RODANDO RandomForest com InfoGain + Ranker     ****/
                
            attsel.setEvaluator(this.evalInfoGain);
            attsel.setSearch(this.searchRanker);

            evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
                        
            if(instances.numInstances() >= 5){
                 table.write(code + ";" + dataset.getName() + ';' + "Random/Forest;" + 
                 "InfoGain/(2);Ranker/(4);" + evaluation.numInstances() + ';' + 
                 evaluation.correct() + ';' + df.format(evaluation.pctCorrect()) + ';' + 
                 evaluation.incorrect() + ';' + df.format(evaluation.pctIncorrect()) + ';' + 
                 df.format(evaluation.areaUnderROC(1)) + ';' + 
                 df.format(evaluation.areaUnderROC(0)) + ';' + evaluation.numTrueNegatives(1) + 
                 ';' + evaluation.numFalsePositives(0) + ';' + evaluation.numFalseNegatives(0) + 
                 ';' + evaluation.numTruePositives(1) + ";" + "S\n");
                  writeFields(code,fields,dataset.getName(),instances, this.evalInfoGain, this.searchRanker, "InfoGain", "Ranker"); // gravando os campos                                                  
                  System.out.println("instância processada:" + code + " nome: " + dataset.getName());// Satin: Gerando log em tela de quanto já foi executado do programa!
                  code++;                  
            }else{
                 System.out.println("Not computed: Less than 5 instances....");
            }
        }catch(Exception e){
            System.out.println("Tratando exceção para o arquivo: ");
        }
    }
    
    public void runSimpleLogistic(File dataset, FileWriter table, FileWriter fields){
        DecimalFormat df = new DecimalFormat("#.00");
            
        try{
            Instances instances = new Instances(new FileReader(dataset));
            instances.setClassIndex(instances.numAttributes() - 1);
     
            /*****     RODANDO SimpleLogistic com CFS + BestFirst     ****/
            attsel.setClassifier(this.classifierSimpleLogistic);

            attsel.setEvaluator(this.evalCfs);
            attsel.setSearch(this.searchBestFirst);
            evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
                       
            if(instances.numInstances() >= 5){
                table.write(code + ";" + dataset.getName() + ';' + "SimpleLogistic;" + 
                  "CFS/(1);Best/First/(1);" + evaluation.numInstances() + ';' + 
                  evaluation.correct() + ';' + df.format(evaluation.pctCorrect()) + ';' + 
                  evaluation.incorrect() + ';' + df.format(evaluation.pctIncorrect()) + ';' + 
                  df.format(evaluation.areaUnderROC(1)) + ';' + 
                  df.format(evaluation.areaUnderROC(0)) + ';' + evaluation.numTrueNegatives(1) + 
                  ';' + evaluation.numFalsePositives(0) + ';' + evaluation.numFalseNegatives(0) + 
                  ';' + evaluation.numTruePositives(1) + ";" + "S\n");
                  writeFields(code,fields,dataset.getName(),instances, this.evalCfs, this.searchBestFirst, "CFS", "BestFirst"); // gravando os campos                                                  
                  System.out.println("instância processada:" + code + " nome: " + dataset.getName());// Satin: Gerando log em tela de quanto já foi executado do programa!
                  code++;                  
            }else{
                  System.out.println("Not computed: Less than 5 instances....");
            }
                        
            /*****     RODANDO SimpleLogistic com CFS + GreedyStepwise     ****/
                        
            attsel.setSearch(this.searchGreedyStepwise);

            evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
                        
            if(instances.numInstances() >= 5){
                table.write(code + ";" + dataset.getName() + ';' + "SimpleLogistic;" + 
                 "CFS/(1);Greedy/Stepwise(2);" + evaluation.numInstances() + ';' + 
                  evaluation.correct() + ';' + df.format(evaluation.pctCorrect()) + ';' + 
                  evaluation.incorrect() + ';' + df.format(evaluation.pctIncorrect()) + ';' + 
                  df.format(evaluation.areaUnderROC(1)) + ';' + 
                  df.format(evaluation.areaUnderROC(0)) + ';' + evaluation.numTrueNegatives(1) + 
                  ';' + evaluation.numFalsePositives(0) + ';' + evaluation.numFalseNegatives(0) + 
                  ';' + evaluation.numTruePositives(1) + ";" + "S\n");
                  writeFields(code,fields,dataset.getName(),instances, this.evalCfs, this.searchGreedyStepwise, "CFS", "GreedyStepwise"); // gravando os campos                                                                  
                  System.out.println("instância processada:" + code + " nome: " + dataset.getName());// Satin: Gerando log em tela de quanto já foi executado do programa!
                  code++;                  
            }else{
                  System.out.println("Not computed: Less than 5 instances....");
            }
                
            /*****     RODANDO SimpleLogistic com CFS + GeneticSearch     ****/
                        
            attsel.setSearch(this.searchGeneticSearch);

            evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
                        
            if(instances.numInstances() >= 5){
                 table.write(code + ";" + dataset.getName() + ';' + "SimpleLogistic;" + 
                  "CFS/(1);Genetic/Search(3);" + evaluation.numInstances() + ';' + 
                  evaluation.correct() + ';' + df.format(evaluation.pctCorrect()) + ';' + 
                  evaluation.incorrect() + ';' + df.format(evaluation.pctIncorrect()) + ';' + 
                  df.format(evaluation.areaUnderROC(1)) + ';' + 
                  df.format(evaluation.areaUnderROC(0)) + ';' + evaluation.numTrueNegatives(1) + 
                  ';' + evaluation.numFalsePositives(0) + ';' + evaluation.numFalseNegatives(0) + 
                  ';' + evaluation.numTruePositives(1) + ";" + "S\n");
                  writeFields(code,fields,dataset.getName(),instances, this.evalCfs, this.searchGeneticSearch, "CFS", "GeneticSearch"); // gravando os campos                                                                   
                  System.out.println("instância processada:" + code + " nome: " + dataset.getName());// Satin: Gerando log em tela de quanto já foi executado do programa!
                  code++;                  
            }else{
                  System.out.println("Not computed: Less than 5 instances....");
            }
                
            /*****     RODANDO SimpleLogistic com InfoGain + Ranker     ****/
                
            attsel.setEvaluator(this.evalInfoGain);
            attsel.setSearch(this.searchRanker);

            evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
                        
            if(instances.numInstances() >= 5){
                 table.write(code + ";" + dataset.getName() + ';' + "SimpleLogistic;" + 
                 "InfoGain/(2);Ranker/(4);" + evaluation.numInstances() + ';' + 
                 evaluation.correct() + ';' + df.format(evaluation.pctCorrect()) + ';' + 
                 evaluation.incorrect() + ';' + df.format(evaluation.pctIncorrect()) + ';' + 
                 df.format(evaluation.areaUnderROC(1)) + ';' + 
                 df.format(evaluation.areaUnderROC(0)) + ';' + evaluation.numTrueNegatives(1) + 
                 ';' + evaluation.numFalsePositives(0) + ';' + evaluation.numFalseNegatives(0) + 
                 ';' + evaluation.numTruePositives(1) + ";" + "S\n");
                  writeFields(code,fields,dataset.getName(),instances, this.evalInfoGain, this.searchRanker, "InfoGain", "Ranker"); // gravando os campos                                                                   
                  System.out.println("instância processada:" + code + " nome: " + dataset.getName());// Satin: Gerando log em tela de quanto já foi executado do programa!
                  code++;                  
            }else{
                 System.out.println("Not computed: Less than 5 instances....");
            }
        }catch(Exception e){
            System.out.println("Tratando exceção para o arquivo: ");
        }
    }
    
    public void runJ48(File dataset, FileWriter table, FileWriter fields){
        DecimalFormat df = new DecimalFormat("#.00");
            
        try{
            Instances instances = new Instances(new FileReader(dataset));
            instances.setClassIndex(instances.numAttributes() - 1);
     
            /*****     RODANDO J48 com CFS + BestFirst     ****/
            attsel.setClassifier(this.classifierJ48);

            attsel.setEvaluator(this.evalCfs);
            attsel.setSearch(this.searchBestFirst);
            evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
                       
            if(instances.numInstances() >= 5){
                table.write(code + ";" + dataset.getName() + ';' + "J48;" + 
                  "CFS/(1);Best/First/(1);" + evaluation.numInstances() + ';' + 
                  evaluation.correct() + ';' + df.format(evaluation.pctCorrect()) + ';' + 
                  evaluation.incorrect() + ';' + df.format(evaluation.pctIncorrect()) + ';' + 
                  df.format(evaluation.areaUnderROC(1)) + ';' + 
                  df.format(evaluation.areaUnderROC(0)) + ';' + evaluation.numTrueNegatives(1) + 
                  ';' + evaluation.numFalsePositives(0) + ';' + evaluation.numFalseNegatives(0) + 
                  ';' + evaluation.numTruePositives(1) + ";" + "S\n");
                  writeFields(code,fields,dataset.getName(),instances, this.evalCfs, this.searchBestFirst, "CFS", "BestFirst"); // gravando os campos                                                                  
                  System.out.println("instância processada:" + code + " nome: " + dataset.getName());// Satin: Gerando log em tela de quanto já foi executado do programa!
                  code++;                  
            }else{
                  System.out.println("Not computed: Less than 5 instances....");
            }
                        
            /*****     RODANDO J48 com CFS + GreedyStepwise     ****/
                        
            attsel.setSearch(this.searchGreedyStepwise);

            evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
                        
            if(instances.numInstances() >= 5){
                table.write(code + ";" + dataset.getName() + ';' + "J48;" + 
                 "CFS/(1);Greedy/Stepwise(2);" + evaluation.numInstances() + ';' + 
                  evaluation.correct() + ';' + df.format(evaluation.pctCorrect()) + ';' + 
                  evaluation.incorrect() + ';' + df.format(evaluation.pctIncorrect()) + ';' + 
                  df.format(evaluation.areaUnderROC(1)) + ';' + 
                  df.format(evaluation.areaUnderROC(0)) + ';' + evaluation.numTrueNegatives(1) + 
                  ';' + evaluation.numFalsePositives(0) + ';' + evaluation.numFalseNegatives(0) + 
                  ';' + evaluation.numTruePositives(1) + ";" + "S\n");
                  writeFields(code,fields,dataset.getName(),instances, this.evalCfs, this.searchGreedyStepwise, "CFS", "GreedyStepwise"); // gravando os campos                                                                  
                  System.out.println("instância processada:" + code + " nome: " + dataset.getName());// Satin: Gerando log em tela de quanto já foi executado do programa!
                  code++;                  
            }else{
                  System.out.println("Not computed: Less than 5 instances....");
            }
                
            /*****     RODANDO J48 com CFS + GeneticSearch     ****/
                        
            attsel.setSearch(this.searchGeneticSearch);

            evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
                        
            if(instances.numInstances() >= 5){
                 table.write(code + ";" + dataset.getName() + ';' + "J48;" + 
                  "CFS/(1);Genetic/Search(3);" + evaluation.numInstances() + ';' + 
                  evaluation.correct() + ';' + df.format(evaluation.pctCorrect()) + ';' + 
                  evaluation.incorrect() + ';' + df.format(evaluation.pctIncorrect()) + ';' + 
                  df.format(evaluation.areaUnderROC(1)) + ';' + 
                  df.format(evaluation.areaUnderROC(0)) + ';' + evaluation.numTrueNegatives(1) + 
                  ';' + evaluation.numFalsePositives(0) + ';' + evaluation.numFalseNegatives(0) + 
                  ';' + evaluation.numTruePositives(1) + ";" + "S\n");
                  writeFields(code,fields,dataset.getName(),instances, this.evalCfs, this.searchGeneticSearch, "CFS", "GeneticSearch"); // gravando os campos                                                                   
                  System.out.println("instância processada:" + code + " nome: " + dataset.getName());// Satin: Gerando log em tela de quanto já foi executado do programa!
                  code++;                  
            }else{
                  System.out.println("Not computed: Less than 5 instances....");
            }
                
            /*****     RODANDO J48 com InfoGain + Ranker     ****/
                
            attsel.setEvaluator(this.evalInfoGain);
            attsel.setSearch(this.searchRanker);

            evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
                        
            if(instances.numInstances() >= 5){
                 table.write(code + ";" + dataset.getName() + ';' + "J48;" + 
                 "InfoGain/(2);Ranker/(4);" + evaluation.numInstances() + ';' + 
                 evaluation.correct() + ';' + df.format(evaluation.pctCorrect()) + ';' + 
                 evaluation.incorrect() + ';' + df.format(evaluation.pctIncorrect()) + ';' + 
                 df.format(evaluation.areaUnderROC(1)) + ';' + 
                 df.format(evaluation.areaUnderROC(0)) + ';' + evaluation.numTrueNegatives(1) + 
                 ';' + evaluation.numFalsePositives(0) + ';' + evaluation.numFalseNegatives(0) + 
                 ';' + evaluation.numTruePositives(1) + ";" + "S\n");
                  writeFields(code,fields,dataset.getName(),instances, this.evalInfoGain, this.searchRanker, "InfoGain", "Ranker"); // gravando os campos                                                                   
                  System.out.println("instância processada:" + code + " nome: " + dataset.getName());// Satin: Gerando log em tela de quanto já foi executado do programa!
                  code++;                  
            }else{
                 System.out.println("Not computed: Less than 5 instances....");
            }
        }catch(Exception e){
            System.out.println("Tratando exceção para o arquivo: ");
        }
    }
    
    public void runDecisionTable(File dataset, FileWriter table, FileWriter fields){
        DecimalFormat df = new DecimalFormat("#.00");
            
        try{
            Instances instances = new Instances(new FileReader(dataset));
            instances.setClassIndex(instances.numAttributes() - 1);
     
            /*****     RODANDO DecisionTable com CFS + BestFirst     ****/
            attsel.setClassifier(this.classifierDecisionTable);

            attsel.setEvaluator(this.evalCfs);
            attsel.setSearch(this.searchBestFirst);
            evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
                       
            if(instances.numInstances() >= 5){
                table.write(code + ";" + dataset.getName() + ';' + "DecisionTable;" + 
                  "CFS/(1);Best/First/(1);" + evaluation.numInstances() + ';' + 
                  evaluation.correct() + ';' + df.format(evaluation.pctCorrect()) + ';' + 
                  evaluation.incorrect() + ';' + df.format(evaluation.pctIncorrect()) + ';' + 
                  df.format(evaluation.areaUnderROC(1)) + ';' + 
                  df.format(evaluation.areaUnderROC(0)) + ';' + evaluation.numTrueNegatives(1) + 
                  ';' + evaluation.numFalsePositives(0) + ';' + evaluation.numFalseNegatives(0) + 
                  ';' + evaluation.numTruePositives(1) + ";" + "S\n");
                  writeFields(code,fields,dataset.getName(),instances, this.evalCfs, this.searchBestFirst, "CFS", "BestFirst"); // gravando os campos                                                                  
                  System.out.println("instância processada:" + code + " nome: " + dataset.getName());// Satin: Gerando log em tela de quanto já foi executado do programa!
                  code++;                  
            }else{
                  System.out.println("Not computed: Less than 5 instances....");
            }
                        
            /*****     RODANDO DecisionTable com CFS + GreedyStepwise     ****/
                        
            attsel.setSearch(this.searchGreedyStepwise);

            evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
                        
            if(instances.numInstances() >= 5){
                table.write(code + ";" + dataset.getName() + ';' + "DecisionTable;" + 
                 "CFS/(1);Greedy/Stepwise(2);" + evaluation.numInstances() + ';' + 
                  evaluation.correct() + ';' + df.format(evaluation.pctCorrect()) + ';' + 
                  evaluation.incorrect() + ';' + df.format(evaluation.pctIncorrect()) + ';' + 
                  df.format(evaluation.areaUnderROC(1)) + ';' + 
                  df.format(evaluation.areaUnderROC(0)) + ';' + evaluation.numTrueNegatives(1) + 
                  ';' + evaluation.numFalsePositives(0) + ';' + evaluation.numFalseNegatives(0) + 
                  ';' + evaluation.numTruePositives(1) + ";" + "S\n");
                  writeFields(code,fields,dataset.getName(),instances, this.evalCfs, this.searchGreedyStepwise, "CFS","GreedyStepwise"); // gravando os campos                                                                  
                  System.out.println("instância processada:" + code + " nome: " + dataset.getName());// Satin: Gerando log em tela de quanto já foi executado do programa!
                  code++;                  
            }else{
                  System.out.println("Not computed: Less than 5 instances....");
            }
                
            /*****     RODANDO DecisionTable com CFS + GeneticSearch     ****/
                        
            attsel.setSearch(this.searchGeneticSearch);

            evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
                        
            if(instances.numInstances() >= 5){
                 table.write(code + ";" + dataset.getName() + ';' + "DecisionTable;" + 
                  "CFS/(1);Genetic/Search(3);" + evaluation.numInstances() + ';' + 
                  evaluation.correct() + ';' + df.format(evaluation.pctCorrect()) + ';' + 
                  evaluation.incorrect() + ';' + df.format(evaluation.pctIncorrect()) + ';' + 
                  df.format(evaluation.areaUnderROC(1)) + ';' + 
                  df.format(evaluation.areaUnderROC(0)) + ';' + evaluation.numTrueNegatives(1) + 
                  ';' + evaluation.numFalsePositives(0) + ';' + evaluation.numFalseNegatives(0) + 
                  ';' + evaluation.numTruePositives(1) + ";" + "S\n");
                  writeFields(code,fields,dataset.getName(),instances, this.evalCfs, this.searchGeneticSearch, "CFS", "GeneticSearch"); // gravando os campos                                                                   
                  System.out.println("instância processada:" + code + " nome: " + dataset.getName());// Satin: Gerando log em tela de quanto já foi executado do programa!
                  code++;                  
            }else{
                  System.out.println("Not computed: Less than 5 instances....");
            }
                
            /*****     RODANDO DecisionTable com InfoGain + Ranker     ****/
                
            attsel.setEvaluator(this.evalInfoGain);
            attsel.setSearch(this.searchRanker);

            evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);
                        
            if(instances.numInstances() >= 5){
                 table.write(code + ";" + dataset.getName() + ';' + "DecisionTable;" + 
                 "InfoGain/(2);Ranker/(4);" + evaluation.numInstances() + ';' + 
                 evaluation.correct() + ';' + df.format(evaluation.pctCorrect()) + ';' + 
                 evaluation.incorrect() + ';' + df.format(evaluation.pctIncorrect()) + ';' + 
                 df.format(evaluation.areaUnderROC(1)) + ';' + 
                 df.format(evaluation.areaUnderROC(0)) + ';' + evaluation.numTrueNegatives(1) + 
                 ';' + evaluation.numFalsePositives(0) + ';' + evaluation.numFalseNegatives(0) + 
                 ';' + evaluation.numTruePositives(1) + ";" + "S\n");
                  writeFields(code,fields,dataset.getName(),instances, this.evalInfoGain, this.searchRanker, "InfoGain", "Ranker"); // gravando os campos                                                                   
                  System.out.println("instância processada:" + code + " nome: " + dataset.getName());// Satin: Gerando log em tela de quanto já foi executado do programa!
                  code++;                  
            }else{
                 System.out.println("Not computed: Less than 5 instances....");
            }
        }catch(Exception e){
            System.out.println("Tratando exceção para o arquivo: ");
        }
    }
            
    public void createResultTable(File[] datasets, File output, File fields) throws IOException, Exception{
            FileWriter fw = new FileWriter(output);
            FileWriter fd = new FileWriter(fields);
            fw.write("Código;Dataset;Algoritmo;Attribute/Evaluator;Search/Method;Total/Instancias;Instâncias/Corretas;%/Corret.;Instâncias/Incorretas;%/Incorret.;ROC/Buggy;ROC/Clean;a;a;b;b;Atributos\n");
            
            for(File f: datasets){
                if(option == AlgorithmsOptions.NaiveBayes || option == AlgorithmsOptions.All)
                    runNaiveBayes(f,fw,fd);
                if(option == AlgorithmsOptions.RandomForest || option == AlgorithmsOptions.All)
                    runRandomForest(f,fw, fd);
                if(option == AlgorithmsOptions.SimpleLogistic || option == AlgorithmsOptions.All)
                    runSimpleLogistic(f,fw, fd);
                if(option == AlgorithmsOptions.J48 || option == AlgorithmsOptions.All)
                    runJ48(f,fw, fd);
                if(option == AlgorithmsOptions.DecisionTable || option == AlgorithmsOptions.All)
                    runDecisionTable(f,fw, fd);
            }
            
            fw.flush();
            fd.flush();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
        /** Diretório contendo os arquivos arff **/
        //Samuel File directory = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_teste/saida/arff3");
        //Ricardo
        File directory = new File("C:/ACER_D/ricardo/Mestrado/Mestrado/Arquivos/tmp");
        File[] datasets = directory.listFiles();
        
        /** Diretório onde a tabela será salva **/
        //Samuel File output = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_teste/saida/tabela.csv");
        //Ricardo 
        File output = new File("C:/ACER_D/ricardo/Mestrado/Mestrado/Arquivos/Analise/tabela.csv");
        File fields = new File("C:/ACER_D/ricardo/Mestrado/Mestrado/Arquivos/Analise/fields.csv");
        
        new ResultTable(AlgorithmsOptions.All).createResultTable(datasets,output,fields);
    }
}

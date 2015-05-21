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
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.GeneticSearch;
import weka.attributeSelection.GreedyStepwise;
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
public class ResultTable {
    public AlgorithmsOptions option;
    
    public AttributeSelectedClassifier attsel = new AttributeSelectedClassifier();
    
    public AttributeSelection2 featsel; //= new AttributeSelection2(); // Satin: Nossa implementaçao da Classe!
    
    private StringBuffer forPredictionsPrinting = new StringBuffer();
    private weka.core.Range attsToOutput = null; 
    private Boolean outputDistribution = true;
    
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
    
    private int code = 1;

    
    public ResultTable(AlgorithmsOptions option){
        this.option = option;
    }

    public void writeFields(int code, FileWriter fields, String dataset, Instances instances, ASEvaluation eval, ASSearch search, String classifierName, String evalName, String searchName) throws IOException {
        try {
            this.featsel = new AttributeSelection2();
            featsel.setEvaluator(eval);            
            featsel.setSearch(search);
            featsel.SelectAttributes(instances);           
            fields.write(featsel.getAttributsCFS(code, dataset, classifierName, evalName, searchName));
        } catch (Exception e) {            
            System.out.println("Erro na geração dos fields: Code: " + code + " dataset: " + dataset);
        }
    }
    
    public void run(File dataset, FileWriter table, FileWriter fields, Classifier classifier, ASEvaluation eval, ASSearch search, String ao, String aeo, String smo){
        DecimalFormat df = new DecimalFormat("#.000");
        String line = "";
        
        try{
            
            Instances instances = new Instances(new FileReader(dataset));
            instances.setClassIndex(instances.numAttributes() - 1);
            
            attsel.setClassifier(classifier);
            attsel.setEvaluator(eval);
            attsel.setSearch(search);
            
            Evaluation evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);

            if(instances.numInstances() >= 5){
                line += code + ";" + dataset.getName() + ';' + ao + ";" + aeo + ";" + smo + ";";
                
                line += evaluation.numInstances() + ";" + 
                  evaluation.correct() + ";" + df.format(evaluation.pctCorrect()) + ";" + 
                  evaluation.incorrect() + ";" + df.format(evaluation.pctIncorrect()) + ";" + 
                  (int)(evaluation.numTrueNegatives(1) + evaluation.numFalseNegatives(0)) + ";" +
                  (int)(evaluation.numFalsePositives(0) + evaluation.numTruePositives(1)) + ";" +
                  df.format(evaluation.truePositiveRate(0)) + ";" + 
                  df.format(evaluation.truePositiveRate(1)) + ";" + 
                  df.format(evaluation.weightedTruePositiveRate()) + ";" +
                  df.format(evaluation.falsePositiveRate(0)) + ";" +  
                  df.format(evaluation.falsePositiveRate(1)) + ";" + 
                  df.format(evaluation.weightedFalsePositiveRate()) + ";" +
                  df.format(evaluation.precision(0)) + ";" +  df.format(evaluation.precision(1)) + ";" + 
                  df.format(evaluation.weightedPrecision()) + ";" +
                  df.format(evaluation.recall(0)) + ";" +  df.format(evaluation.recall(1)) + ";" +  
                  df.format(evaluation.weightedRecall()) + ";" +
                  df.format(evaluation.fMeasure(0)) + ";" +  df.format(evaluation.fMeasure(1)) + ";" + 
                  df.format(evaluation.weightedFMeasure()) + ";" +
                  df.format(evaluation.areaUnderROC(0)) + ";" + df.format(evaluation.areaUnderROC(1)) + ";" +
                  df.format(evaluation.weightedAreaUnderROC()) + ";" +
                  evaluation.numTrueNegatives(1) + ";" +
                  evaluation.numFalseNegatives(0) + ';' + evaluation.numFalsePositives(0) + ";" +  
                  evaluation.numTruePositives(1) + ";" + "S\n";
                
                table.write(line);  

                writeFields(code,fields,dataset.getName(),instances, eval, search, ao, aeo, smo); // gravando os campos
                  
                  System.out.println("instância processada:" + code + " nome: " + dataset.getName()); // Satin: Gerando log em tela de quanto já foi executado do programa!
                  code++;
            }else{
                  System.out.println("Not computed: Less than 5 instances....");
            }
            
        }catch(Exception e){
            System.out.println("Tratando exceção para o arquivo: " + dataset.getName());
            return;
        }
    }
    
            
    public void createResultTable(File[] datasets, File output, File fields) throws IOException, Exception{
            FileWriter fw = new FileWriter(output);
            FileWriter fd = new FileWriter(fields);
            fw.write("Codigo;Dataset;Algoritmo;AttributeEvaluator;SearchMethod;TotalInstancias;InstanciasCorretas;%Corret.;InstanciasIncorretas;%Incorret.;NrInstanciasBuggy;NrInstanciasClean;TP-RateBuggy;TP-RateClean;TP-RateModelo;FP-RateBuggy;FP-RateClean;FP-RateModelo;PrecisionBuggy;PrecisionClean;PrecisionModelo;RecallBuggy;RecallClean;RecallModelo;F-MeasureBuggy;F-MeasureClean;F-MeasureModelo;ROCBuggy;ROCClean;ROCModelo;TP;FP;FN;TN;Atributos\n");
            fd.write("Codigo;Dataset;Algoritmo;AttributeEvaluator;SearchMethod;IndexField;Feature;Ranking\n");
            for(File f: datasets){
                if(option == AlgorithmsOptions.NaiveBayes || option == AlgorithmsOptions.All){
                    //runNaiveBayes(f,fw,fd);
                    //fw.write(code + ";" + f.getName() + ';' + "NaiveBayes;" + "CFS;BestFirst;");
                    run(f,fw,fd,this.classifierNaiveBayes,this.evalCfs,this.searchBestFirst, "NaiveBayes", "CFS", "BestFirst");
                    //fw.write(code + ";" + f.getName() + ';' + "NaiveBayes;" + "CFS;GreedyStepwise;");
                    run(f,fw,fd,this.classifierNaiveBayes,this.evalCfs,this.searchGreedyStepwise, "NaiveBayes", "CFS", "GreedyStepwise");
                    //fw.write(code + ";" + f.getName() + ';' + "NaiveBayes;" + "CFS;GeneticSearch;");
                    run(f,fw,fd,this.classifierNaiveBayes,this.evalCfs,this.searchGeneticSearch, "NaiveBayes", "CFS", "GeneticSearch");
                    //fw.write(code + ";" + f.getName() + ';' + "NaiveBayes;" + "InfoGain;Ranker;");
                    run(f,fw,fd,this.classifierNaiveBayes,this.evalInfoGain,this.searchRanker, "NaiveBayes", "InfoGain", "Ranker");
                }
                if(option == AlgorithmsOptions.RandomForest || option == AlgorithmsOptions.All){
                    //runRandomForest(f,fw);
                    //fw.write(code + ";" + f.getName() + ';' + "RandomForest;" + "CFS;BestFirst;");
                    run(f,fw,fd,this.classifierRandomForest,this.evalCfs,this.searchBestFirst, "RandomForest", "CFS", "BestFirst");
                    //fw.write(code + ";" + f.getName() + ';' + "RandomForest;" + "CFS;GreedyStepwise;");
                    run(f,fw,fd,this.classifierRandomForest,this.evalCfs,this.searchGreedyStepwise, "RandomForest", "CFS", "GreedyStepwise");
                    //fw.write(code + ";" + f.getName() + ';' + "RandomForest;" + "CFS;GeneticSearch;");
                    run(f,fw,fd,this.classifierRandomForest,this.evalCfs,this.searchGeneticSearch, "RandomForest", "CFS", "GeneticSearch");
                    //fw.write(code + ";" + f.getName() + ';' + "RandomForest;" + "InfoGain;Ranker;");
                    run(f,fw,fd,this.classifierRandomForest,this.evalInfoGain,this.searchRanker, "RandomForest", "InfoGain", "Ranker");
                }
                if(option == AlgorithmsOptions.SimpleLogistic || option == AlgorithmsOptions.All){
                    //runSimpleLogistic(f,fw);
                    //fw.write(code + ";" + f.getName() + ';' + "SimpleLogistic;" + "CFS;BestFirst;");
                    run(f,fw,fd,this.classifierSimpleLogistic,this.evalCfs,this.searchBestFirst, "SimpleLogistic", "CFS", "BestFirst");
                    //fw.write(code + ";" + f.getName() + ';' + "SimpleLogistic;" + "CFS;GreedyStepwise;");
                    run(f,fw,fd,this.classifierSimpleLogistic,this.evalCfs,this.searchGreedyStepwise, "SimpleLogistic", "CFS", "GreedyStepwise");
                    //fw.write(code + ";" + f.getName() + ';' + "SimpleLogistic;" + "CFS;GeneticSearch;");
                    run(f,fw,fd,this.classifierSimpleLogistic,this.evalCfs,this.searchGeneticSearch, "SimpleLogistic", "CFS", "GeneticSearch");
                    //fw.write(code + ";" + f.getName() + ';' + "SimpleLogistic;" + "InfoGain;Ranker;");
                    run(f,fw,fd,this.classifierSimpleLogistic,this.evalInfoGain,this.searchRanker, "SimpleLogistic", "InfoGain", "Ranker");
                }
                if(option == AlgorithmsOptions.J48 || option == AlgorithmsOptions.All){
                    //runJ48(f,fw);
                    //fw.write(code + ";" + f.getName() + ';' + "J48;" + "CFS;BestFirst;");
                    run(f,fw,fd,this.classifierJ48,this.evalCfs,this.searchBestFirst, "J48", "CFS", "BestFirst");
                    //fw.write(code + ";" + f.getName() + ';' + "J48;" + "CFS;GreedyStepwise;");
                    run(f,fw,fd,this.classifierJ48,this.evalCfs,this.searchGreedyStepwise, "J48", "CFS", "GreedyStepwise");
                    //fw.write(code + ";" + f.getName() + ';' + "J48;" + "CFS;GeneticSearch;");
                    run(f,fw,fd,this.classifierJ48,this.evalCfs,this.searchGeneticSearch, "J48", "CFS", "GeneticSearch");
                    //fw.write(code + ";" + f.getName() + ';' + "J48;" + "InfoGain;Ranker;");
                    run(f,fw,fd,this.classifierJ48,this.evalInfoGain,this.searchRanker, "J48", "InfoGain", "Ranker");
                }
                if(option == AlgorithmsOptions.DecisionTable || option == AlgorithmsOptions.All){
                    //runDecisionTable(f,fw);
                    //fw.write(code + ";" + f.getName() + ';' + "DecisionTable;" + "CFS;BestFirst;");
                    run(f,fw,fd,this.classifierDecisionTable,this.evalCfs,this.searchBestFirst, "DecisionTable", "CFS", "BestFirst");
                    //fw.write(code + ";" + f.getName() + ';' + "DecisionTable;" + "CFS;GreedyStepwise;");
                    run(f,fw,fd,this.classifierDecisionTable,this.evalCfs,this.searchGreedyStepwise, "DecisionTable", "CFS", "GreedyStepwise");
                    //fw.write(code + ";" + f.getName() + ';' + "DecisionTable;" + "CFS;GeneticSearch;");
                    run(f,fw,fd,this.classifierDecisionTable,this.evalCfs,this.searchGeneticSearch, "DecisionTable", "CFS", "GeneticSearch");
                    //fw.write(code + ";" + f.getName() + ';' + "DecisionTable;" + "InfoGain;Ranker;");
                    run(f,fw,fd,this.classifierDecisionTable,this.evalInfoGain,this.searchRanker, "DecisionTable", "InfoGain", "Ranker");
                }
            }
            
            fw.flush();
            fd.flush();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
        /** Diretório contendo os arquivos arff **/
        
        //Samuel 
        File directory = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_teste/saida/arffTeste");
        
        //Ricardo
        //File directory = new File("C:/ACER_D/ricardo/Mestrado/Mestrado/Arquivos/tmp");
//>>>>>>> 21d264528aaf50c731c6f38a2c60a6bbdbf6887a
        
        File[] datasets = directory.listFiles();
        
        /** Diretório onde a tabela será salva **/
        //Samuel 
        File output = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_teste/saida/tabela.csv");
        File fields = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_teste/saida/fields.csv");
        
        //Ricardo 
        //File output = new File("C:/ACER_D/ricardo/Mestrado/Mestrado/Arquivos/Analise/tabela.csv");
        //File fields = new File("C:/ACER_D/ricardo/Mestrado/Mestrado/Arquivos/Analise/fields.csv");
        
        new ResultTable(AlgorithmsOptions.RandomForest).createResultTable(datasets,output,fields);
    }
}

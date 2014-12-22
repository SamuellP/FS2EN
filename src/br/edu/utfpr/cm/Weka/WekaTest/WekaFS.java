/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GainRatioAttributeEval;
import weka.attributeSelection.GeneticSearch;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.Ranker;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.core.FastVector;
import weka.core.Range;
/**
 *
 * @author igor
 */
public class WekaFS {

    public static void main(String[] args) throws IOException, Exception {
        /*Folder containing the arff files*/
        //String folderName = "C:/ACER_D/ricardo/Mestrado/Mestrado/Teste de SW/Dissertação/weka/Arff/test/";
        //String folderName ="results/inputs_1/";
        //String folderName = "C:/Arff/";
        //String folderName = "/home/samuel/Documentos/BCC/Projeto/arquivos_reunião";
        
        //Samuel
        //String folderName = "/home/samuel/Documentos/BCC/Projeto/arquivos_teste/saida/arff2";
        
        //Ricardo
        String folderName = "C:/ACER_D/ricardo/Mestrado/Mestrado/Arquivos/tmp";
        
        File[] listOfFiles = WekaTest.getListOfFiles(folderName);
        for (File f : listOfFiles) {
            //int[] toExclude = new int[]{16, 15, 13, 1, 0};
            Instances instances = new Instances(new FileReader(f));

            //Using the configuration: ALL, PRONE_CLEAN
            //for (int i = 0; i < toExclude.length; i++) {
            //    instances.deleteAttributeAt(toExclude[i]);
            //}

            instances.setClassIndex(instances.numAttributes() - 1);
            //Change (uncomment) here to call other two methods
//            AttributeSelection attsel = runUsingGainRatioRank(instances);
//            AttributeSelection attsel = runUsingWraperJ48(instances);            
            //AttributeSelection attsel = runUsingCSFEval(instances);
            
            System.out.println("===========================================");
            System.out.print("0 - Selected attributes for ");
            System.out.println(f.getName());
            System.out.println("===========================================***");
            
            Evaluation attsel = runUsingAttributeSelectedClassifier(instances);                          
                      
            /*FastVector resposta;
            resposta = attsel.predictions();
            
            
            Object obj [] = resposta.toArray();
            for (int i=0; i<obj.length-1;i++)
                System.out.println("Caramba: "+ obj[i].toString());
  */          

            if(attsel == null)
                System.out.println(f.getName());
            else{
            
                if(instances.numInstances() >= 5){
                    //Original
    //                attsel.SelectAttributes(instances);
    //                System.out.println(attsel.toResultsString());
                    // Fim Original
                    System.out.println("<C>");                    
                    System.out.println("C: " + attsel.toSummaryString()); //=== Summary ===
                    System.out.println("</C>");
                    
                    System.out.println("<D>");                                                            
                    System.out.println("D: " + attsel.toClassDetailsString()); //=== Detailed Accuracy By Class === 
                    System.out.println("</D>");                                                            
                    
                    System.out.println("<E>");                                                                                
                    System.out.println("E: " + attsel.toMatrixString()); //=== Confusion Matrix === 
                    System.out.println("</E>");                                                                                                    
                }else{
                    System.out.println("Not computed: Less than 5 instances....");
                }
            }
        }
    }

    public static AttributeSelection runUsingCSFEval(Instances instances) throws FileNotFoundException, IOException, Exception {
        AttributeSelection attsel = new AttributeSelection();  // package weka.attributeSelection! 
        CfsSubsetEval eval = new CfsSubsetEval();
        BestFirst search = new BestFirst();
        //GreedyStepwise search = new GreedyStepwise();
        attsel.setEvaluator(eval);
        attsel.setSearch(search);
        return attsel;

    }

    public static AttributeSelection runUsingWraperJ48(Instances instances) throws FileNotFoundException, IOException {
        AttributeSelection attsel = new AttributeSelection(); 
        WrapperSubsetEval eval = new WrapperSubsetEval();
        //BestFirst search = new BestFirst();
        //GreedyStepwise search = new GreedyStepwise();
        GeneticSearch search = new GeneticSearch();
        eval.setClassifier(new J48());
        attsel.setEvaluator(eval);
        attsel.setSearch(search);
        return attsel;
    }

    public static AttributeSelection runUsingGainRatioRank(Instances instances) throws FileNotFoundException, IOException, Exception {
        AttributeSelection attsel = new AttributeSelection();  
        ASEvaluation eval = new GainRatioAttributeEval();
        ASSearch search = new Ranker();
        attsel.setEvaluator(eval);
        attsel.setSearch(search);
        return attsel;
    }

    public static Evaluation runUsingAttributeSelectedClassifier(Instances instances) throws FileNotFoundException, IOException, Exception {
        try{
            //Ricardo - criei os elementos básicos para rodarmos um Ranker
            GainRatioAttributeEval gr = new GainRatioAttributeEval();
            Ranker ra = new Ranker(); 
                                
            AttributeSelectedClassifier attsel = new AttributeSelectedClassifier();          

            CfsSubsetEval eval = new CfsSubsetEval();
            eval.setLocallyPredictive(true);
            eval.setMissingSeparate(false);

            BestFirst     search     = new BestFirst();
            search.setLookupCacheSize(1);
            search.setSearchTermination(5);

            J48 classifier = new J48();
            float f = 0.25f;

            classifier.setBinarySplits(false);
            classifier.setConfidenceFactor(f);
            classifier.setDebug(false);
            classifier.setMinNumObj(2); 
            classifier.setNumFolds(3);
            classifier.setReducedErrorPruning(false);
            classifier.setSubtreeRaising(true);
            classifier.setUnpruned(false);
            classifier.setUseLaplace(false);

            attsel.setClassifier(classifier);
            
            //Ricardo: Manual mesmo, aqui trocamos a seleção de atributos de um ranker ou cfs.
            //attsel.setEvaluator(eval);                       
            //attsel.setSearch(search);         
            attsel.setEvaluator(gr);                       
            attsel.setSearch(ra);         

            Evaluation evaluation=new Evaluation(instances);

            StringBuffer forPredictionsPrinting = new StringBuffer();
            weka.core.Range attsToOutput = null; 
            Boolean outputDistribution = true; 

            evaluation.crossValidateModel(attsel,instances,10,new Random(1),forPredictionsPrinting ,attsToOutput, outputDistribution);

            //Imprimindo as informações " Attribute Selection on all input data " igual no WEKA
            //Ricardo: Aqui estou usando nossa nova implementação do Attribute Selection
            AttributeSelection2 as = new AttributeSelection2();
            //Ricardo: teste com CFS
            as.setEvaluator(eval);            
            as.setSearch(search);
            //Ricardo: Teste com Ranker
            //as.setEvaluator(gr);
            //as.setSearch(ra);            
            as.SelectAttributes(instances);
            
            System.out.println(" <A> ");
            System.out.println("A: " + as.toResultsString());
            System.out.println(" </A> ");
            
            //ricardo
            System.out.println("NR. de atributos selecionados: " + as.numberAttributesSelected());

            //Ricardo: Chamo para mostrar os atributos, com o método que implementei, tanto para um quanto para outro
            System.out.println(" <B> ");
            System.out.println(as.getAttributsCFS(1,"arquivo", "evaluator", "search"));
            //System.out.println(as.getAttributsRanker(1,"arquivo", "evaluator", "search"));
            System.out.println(" </B> ");
            
            return evaluation;
        }catch(Exception e){
            e.printStackTrace();;
            System.out.println("Erro para o arquivo: ");
            return null;
        }
    }
}

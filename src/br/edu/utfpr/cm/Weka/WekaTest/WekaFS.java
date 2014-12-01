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
import java.util.Iterator;
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
        String folderName = "/home/samuel/Documentos/BCC/Projeto/arquivos_reunião";
        
        
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
            System.out.print("Selected attributes for ");
            System.out.println(f.getName());
            System.out.println("===========================================***");
            
            Evaluation attsel = runUsingAttributeSelectedClassifier(instances);                          
                      
/*            FastVector resposta;
            resposta = attsel.predictions();
            
            
            Object obj [] = resposta.toArray();
            for (int i=0; i<obj.length-1;i++)
                System.out.println("Caramba: "+ obj[i].toString());
  */          

            

            
            
            if(instances.numInstances() >= 5){
                //Original
//                attsel.SelectAttributes(instances);
//                System.out.println(attsel.toResultsString());
                // Fim Original
                
                System.out.println(attsel.toSummaryString()); //=== Summary ===
                System.out.println(attsel.toClassDetailsString()); //=== Detailed Accuracy By Class ===                
                System.out.println(attsel.toMatrixString()); //=== Confusion Matrix === 
                
                //System.out.println(attsel.toSummaryString(true));
                //System.out.println(attsel.toCumulativeMarginDistributionString());
                //FastVector fv = new FastVector();
                
                
                //System.out.println(
                
            }else{
                System.out.println("Not computed: Less than 5 instances....");
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
        
        //http://weka.wikispaces.com/Generating+classifier+evaluation+output+manually 
        //http://www.programcreek.com/java-api-examples/index.php?api=weka.classifiers.trees.J48
        
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
        attsel.setEvaluator(eval);                       
        attsel.setSearch(search);         
        //attsel.setOptions(options);
        
        Evaluation evaluation=new Evaluation(instances);
        
        //String[] options = new String[2];
        //options[0] = "-i";
        //options[1] = "C:/ACER_D/ricardo/promisse selecionados/XXX/Feng/aff4.arff";
        //System.out.println("NOSSA" + Evaluation.evaluateModel(new J48(), options));
        
        StringBuffer forPredictionsPrinting = new StringBuffer();
        weka.core.Range attsToOutput = null; 
        Boolean outputDistribution = true; 

        evaluation.crossValidateModel(attsel,instances,10,new Random(1),forPredictionsPrinting ,attsToOutput, outputDistribution);
        
        //System.out.println(forPredictionsPrinting);
        
        //System.out.println(evaluation.toSummaryString());
        //System.out.println(evaluation.toMatrixString("OIA"));
        //System.out.println(evaluation.toClassDetailsString("Teste"));
        //System.out.println(evaluation.toClassDetailsString());
            
        //return attsel;
     
     //Imprimindo as informações " Attribute Selection on all input data " igual no WEKA
        AttributeSelection as = new AttributeSelection();
        as.setEvaluator(eval);
        as.setSearch(search);
        as.SelectAttributes(instances);
        System.out.println(as.toResultsString());
     //
        
        return evaluation;
    }
}

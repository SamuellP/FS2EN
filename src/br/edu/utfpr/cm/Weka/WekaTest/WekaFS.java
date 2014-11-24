/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GainRatioAttributeEval;
import weka.attributeSelection.Ranker;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.trees.J48;
import weka.core.Instances;

/**
 *
 * @author igor
 */
public class WekaFS {

    public static void main(String[] args) throws IOException, Exception {
              
        /*Folder containing the arff files*/
        String folderName = "C:/ACER_D/ricardo/Mestrado/Mestrado/Teste de SW/Dissertação/weka/Arff/test/";
        //String folderName ="results/inputs_1/";

        File[] listOfFiles = WekaTest.getListOfFiles(folderName);
        for (File f : listOfFiles) {
            int[] toExclude = new int[]{16, 15, 13, 1, 0};
            Instances instances = new Instances(new FileReader(f));

            //Using the configuration: ALL, PRONE_CLEAN
            for (int i = 0; i < toExclude.length; i++) {
                instances.deleteAttributeAt(toExclude[i]);
            }

            instances.setClassIndex(instances.numAttributes() - 1);
            //Change (uncomment) here to call other two methods
//            AttributeSelection attsel = runUsingGainRatioRank(instances);
            AttributeSelection attsel = runUsingWraperJ48(instances);
//            AttributeSelection attsel = runUsingCSFEval(instances);
            System.out.println("===========================================");
            System.out.print("Selected attributes for ");
            System.out.println(f.getName());
            System.out.println("===========================================***");
            if(instances.numInstances() >= 5){
                attsel.SelectAttributes(instances);
                System.out.println(attsel.toResultsString());
            }else{
                System.out.println("Not computed: Less than 5 instances....");
            }
        }
    }

    public static AttributeSelection runUsingCSFEval(Instances instances) throws FileNotFoundException, IOException, Exception {
        AttributeSelection attsel = new AttributeSelection();  // package weka.attributeSelection! 
        CfsSubsetEval eval = new CfsSubsetEval();
        BestFirst search = new BestFirst();
        attsel.setEvaluator(eval);
        attsel.setSearch(search);
        return attsel;

    }

    public static AttributeSelection runUsingWraperJ48(Instances instances) throws FileNotFoundException, IOException {
        AttributeSelection attsel = new AttributeSelection();  
        WrapperSubsetEval eval = new WrapperSubsetEval();
        BestFirst search = new BestFirst();
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

    
}

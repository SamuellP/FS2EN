/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GeneticSearch;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.InfoGainAttributeEval;
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
public class CalculateAttributes {
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
    
    public ASSearch searchBestFirst = new BestFirst();
    public ASSearch searchGreedyStepwise = new GreedyStepwise();
    public ASSearch searchGeneticSearch = new GeneticSearch();
    public ASSearch searchRanker = new Ranker();
    
    public NaiveBayes classifierNaiveBayes = new NaiveBayes();
    public RandomForest classifierRandomForest = new RandomForest();
    public SimpleLogistic classifierSimpleLogistic = new SimpleLogistic();
    public J48 classifierJ48 = new J48();
    public DecisionTable classifierDecisionTable = new DecisionTable();
    
    public CalculateAttributes(AlgorithmsOptions ao, AttributeEvaluatorOptions aeo, SearchMethodOptions smo){
        this.algorithmOption = ao;
        this.attributeEvaluatorOption = aeo;
        this.searchMethodOption = smo;
    }
    /**
     * 
     * @param dataset
     * @return Retorna um vetor onde cada posição contém: 0 - ROC, 1 - PRECISION, 2 - RECALL, 3 - F-MEASURE
     * @throws IOException
     * @throws Exception 
     */
    public Double[] getAttributes(Instances instances) throws IOException, Exception{
        Double[] attributes = new Double[4];
        attributes[0] = Double.POSITIVE_INFINITY;
        attributes[1] = Double.POSITIVE_INFINITY;
        attributes[2] = Double.POSITIVE_INFINITY;
        attributes[3] = Double.POSITIVE_INFINITY;
        
        try{
            if(this.algorithmOption == AlgorithmsOptions.DecisionTable){
                    if(this.attributeEvaluatorOption == AttributeEvaluatorOptions.CFS){
                        if(this.searchMethodOption == SearchMethodOptions.BestFirst){
                            this.attsel.setClassifier(this.classifierDecisionTable);
                            this.attsel.setEvaluator(this.evalCfs);
                            this.attsel.setSearch(this.searchBestFirst);

                            Evaluation evaluation = new Evaluation(instances);
                            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);

                            attributes[0] = evaluation.weightedAreaUnderROC();
                            attributes[1] = evaluation.weightedPrecision();
                            attributes[2] = evaluation.weightedRecall();
                            attributes[3] = evaluation.weightedFMeasure();

                            return attributes;
                        }else if(this.searchMethodOption == SearchMethodOptions.GeneticSearch){
                            this.attsel.setClassifier(this.classifierDecisionTable);
                            this.attsel.setEvaluator(this.evalCfs);
                            this.attsel.setSearch(this.searchGeneticSearch);

                            Evaluation evaluation = new Evaluation(instances);
                            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);

                            attributes[0] = evaluation.weightedAreaUnderROC();
                            attributes[1] = evaluation.weightedPrecision();
                            attributes[2] = evaluation.weightedRecall();
                            attributes[3] = evaluation.weightedFMeasure();

                            return attributes;
                        }else if(this.searchMethodOption == SearchMethodOptions.GreedyStepwise){
                            this.attsel.setClassifier(this.classifierDecisionTable);
                            this.attsel.setEvaluator(this.evalCfs);
                            this.attsel.setSearch(this.searchGreedyStepwise);

                            Evaluation evaluation = new Evaluation(instances);
                            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);

                            attributes[0] = evaluation.weightedAreaUnderROC();
                            attributes[1] = evaluation.weightedPrecision();
                            attributes[2] = evaluation.weightedRecall();
                            attributes[3] = evaluation.weightedFMeasure();

                            return attributes;
                        }else{
                            System.out.println("Erro durante a geração do arquivo contendo o ROC e do arquivo contendo as features");
                            return attributes;
                        }
                    }else if(this.attributeEvaluatorOption == AttributeEvaluatorOptions.InfoGain){
                        if(this.searchMethodOption == SearchMethodOptions.Ranker){
                            this.attsel.setClassifier(this.classifierDecisionTable);
                            this.attsel.setEvaluator(this.evalInfoGain);
                            this.attsel.setSearch(this.searchRanker);

                            Evaluation evaluation = new Evaluation(instances);
                            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);

                            attributes[0] = evaluation.weightedAreaUnderROC();
                            attributes[1] = evaluation.weightedPrecision();
                            attributes[2] = evaluation.weightedRecall();
                            attributes[3] = evaluation.weightedFMeasure();

                            return attributes;
                        }else{
                            System.out.println("Erro durante a geração do arquivo contendo o ROC e do arquivo contendo as features");
                            return attributes;
                        }
                    }
                }

                if(this.algorithmOption == AlgorithmsOptions.J48){
                    if(this.attributeEvaluatorOption == AttributeEvaluatorOptions.CFS){
                        if(this.searchMethodOption == SearchMethodOptions.BestFirst){
                            this.attsel.setClassifier(this.classifierJ48);
                            this.attsel.setEvaluator(this.evalCfs);
                            this.attsel.setSearch(this.searchBestFirst);

                            Evaluation evaluation = new Evaluation(instances);
                            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);

                            attributes[0] = evaluation.weightedAreaUnderROC();
                            attributes[1] = evaluation.weightedPrecision();
                            attributes[2] = evaluation.weightedRecall();
                            attributes[3] = evaluation.weightedFMeasure();

                            return attributes;
                        }else if(this.searchMethodOption == SearchMethodOptions.GeneticSearch){
                            this.attsel.setClassifier(this.classifierJ48);
                            this.attsel.setEvaluator(this.evalCfs);
                            this.attsel.setSearch(this.searchGeneticSearch);

                            Evaluation evaluation = new Evaluation(instances);
                            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);

                            attributes[0] = evaluation.weightedAreaUnderROC();
                            attributes[1] = evaluation.weightedPrecision();
                            attributes[2] = evaluation.weightedRecall();
                            attributes[3] = evaluation.weightedFMeasure();

                            return attributes;
                        }else if(this.searchMethodOption == SearchMethodOptions.GreedyStepwise){
                            this.attsel.setClassifier(this.classifierJ48);
                            this.attsel.setEvaluator(this.evalCfs);
                            this.attsel.setSearch(this.searchGreedyStepwise);

                            Evaluation evaluation = new Evaluation(instances);
                            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);

                            attributes[0] = evaluation.weightedAreaUnderROC();
                            attributes[1] = evaluation.weightedPrecision();
                            attributes[2] = evaluation.weightedRecall();
                            attributes[3] = evaluation.weightedFMeasure();

                            return attributes;
                        }else{
                            System.out.println("Erro durante a geração do arquivo contendo o ROC e do arquivo contendo as features");
                            return attributes;
                        }
                    }else if(this.attributeEvaluatorOption == AttributeEvaluatorOptions.InfoGain){
                        if(this.searchMethodOption == SearchMethodOptions.Ranker){
                            this.attsel.setClassifier(this.classifierJ48);
                            this.attsel.setEvaluator(this.evalInfoGain);
                            this.attsel.setSearch(this.searchRanker);

                            Evaluation evaluation = new Evaluation(instances);
                            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);

                            attributes[0] = evaluation.weightedAreaUnderROC();
                            attributes[1] = evaluation.weightedPrecision();
                            attributes[2] = evaluation.weightedRecall();
                            attributes[3] = evaluation.weightedFMeasure();

                            return attributes;
                        }else{
                            System.out.println("Erro durante a geração do arquivo contendo o ROC e do arquivo contendo as features");
                            return attributes;
                        }
                    }
                }

                if(this.algorithmOption == AlgorithmsOptions.NaiveBayes){
                    if(this.attributeEvaluatorOption == AttributeEvaluatorOptions.CFS){
                        if(this.searchMethodOption == SearchMethodOptions.BestFirst){
                            this.attsel.setClassifier(this.classifierNaiveBayes);
                            this.attsel.setEvaluator(this.evalCfs);
                            this.attsel.setSearch(this.searchBestFirst);

                            Evaluation evaluation = new Evaluation(instances);
                            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);

                            attributes[0] = evaluation.weightedAreaUnderROC();
                            attributes[1] = evaluation.weightedPrecision();
                            attributes[2] = evaluation.weightedRecall();
                            attributes[3] = evaluation.weightedFMeasure();

                            return attributes;
                        }else if(this.searchMethodOption == SearchMethodOptions.GeneticSearch){
                            this.attsel.setClassifier(this.classifierNaiveBayes);
                            this.attsel.setEvaluator(this.evalCfs);
                            this.attsel.setSearch(this.searchGeneticSearch);

                            Evaluation evaluation = new Evaluation(instances);
                            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);

                            attributes[0] = evaluation.weightedAreaUnderROC();
                            attributes[1] = evaluation.weightedPrecision();
                            attributes[2] = evaluation.weightedRecall();
                            attributes[3] = evaluation.weightedFMeasure();

                            return attributes;
                        }else if(this.searchMethodOption == SearchMethodOptions.GreedyStepwise){
                            this.attsel.setClassifier(this.classifierNaiveBayes);
                            this.attsel.setEvaluator(this.evalCfs);
                            this.attsel.setSearch(this.searchGreedyStepwise);

                            Evaluation evaluation = new Evaluation(instances);
                            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);

                            attributes[0] = evaluation.weightedAreaUnderROC();
                            attributes[1] = evaluation.weightedPrecision();
                            attributes[2] = evaluation.weightedRecall();
                            attributes[3] = evaluation.weightedFMeasure();

                            return attributes;
                        }else{
                            System.out.println("Erro durante a geração do arquivo contendo o ROC e do arquivo contendo as features");
                            return attributes;
                        }
                    }else if(this.attributeEvaluatorOption == AttributeEvaluatorOptions.InfoGain){
                        if(this.searchMethodOption == SearchMethodOptions.Ranker){
                            this.attsel.setClassifier(this.classifierNaiveBayes);
                            this.attsel.setEvaluator(this.evalInfoGain);
                            this.attsel.setSearch(this.searchRanker);

                            Evaluation evaluation = new Evaluation(instances);
                            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);

                            attributes[0] = evaluation.weightedAreaUnderROC();
                            attributes[1] = evaluation.weightedPrecision();
                            attributes[2] = evaluation.weightedRecall();
                            attributes[3] = evaluation.weightedFMeasure();

                            return attributes;
                        }else{
                            System.out.println("Erro durante a geração do arquivo contendo o ROC e do arquivo contendo as features");
                            return attributes;
                        }
                    }
                }

                if(this.algorithmOption == AlgorithmsOptions.RandomForest){
                    if(this.attributeEvaluatorOption == AttributeEvaluatorOptions.CFS){
                        if(this.searchMethodOption == SearchMethodOptions.BestFirst){
                            this.attsel.setClassifier(this.classifierRandomForest);
                            this.attsel.setEvaluator(this.evalCfs);
                            this.attsel.setSearch(this.searchBestFirst);

                            Evaluation evaluation = new Evaluation(instances);
                            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);

                            attributes[0] = evaluation.weightedAreaUnderROC();
                            attributes[1] = evaluation.weightedPrecision();
                            attributes[2] = evaluation.weightedRecall();
                            attributes[3] = evaluation.weightedFMeasure();

                            return attributes;
                        }else if(this.searchMethodOption == SearchMethodOptions.GeneticSearch){
                            this.attsel.setClassifier(this.classifierRandomForest);
                            this.attsel.setEvaluator(this.evalCfs);
                            this.attsel.setSearch(this.searchGeneticSearch);

                            Evaluation evaluation = new Evaluation(instances);
                            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);

                            attributes[0] = evaluation.weightedAreaUnderROC();
                            attributes[1] = evaluation.weightedPrecision();
                            attributes[2] = evaluation.weightedRecall();
                            attributes[3] = evaluation.weightedFMeasure();

                            return attributes;
                        }else if(this.searchMethodOption == SearchMethodOptions.GreedyStepwise){
                            this.attsel.setClassifier(this.classifierRandomForest);
                            this.attsel.setEvaluator(this.evalCfs);
                            this.attsel.setSearch(this.searchGreedyStepwise);

                            Evaluation evaluation = new Evaluation(instances);
                            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);

                            attributes[0] = evaluation.weightedAreaUnderROC();
                            attributes[1] = evaluation.weightedPrecision();
                            attributes[2] = evaluation.weightedRecall();
                            attributes[3] = evaluation.weightedFMeasure();

                            return attributes;
                        }else{
                            System.out.println("Erro durante a geração do arquivo contendo o ROC e do arquivo contendo as features");
                            return attributes;
                        }
                    }else if(this.attributeEvaluatorOption == AttributeEvaluatorOptions.InfoGain){
                        if(this.searchMethodOption == SearchMethodOptions.Ranker){
                            this.attsel.setClassifier(this.classifierRandomForest);
                            this.attsel.setEvaluator(this.evalInfoGain);
                            this.attsel.setSearch(this.searchRanker);

                            Evaluation evaluation = new Evaluation(instances);
                            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);

                            attributes[0] = evaluation.weightedAreaUnderROC();
                            attributes[1] = evaluation.weightedPrecision();
                            attributes[2] = evaluation.weightedRecall();
                            attributes[3] = evaluation.weightedFMeasure();

                            return attributes;
                        }else{
                            System.out.println("Erro durante a geração do arquivo contendo o ROC e do arquivo contendo as features");
                            return attributes;
                        }
                    }
                }

                if(this.algorithmOption == AlgorithmsOptions.SimpleLogistic){
                    if(this.attributeEvaluatorOption == AttributeEvaluatorOptions.CFS){
                        if(this.searchMethodOption == SearchMethodOptions.BestFirst){
                            this.attsel.setClassifier(this.classifierSimpleLogistic);
                            this.attsel.setEvaluator(this.evalCfs);
                            this.attsel.setSearch(this.searchBestFirst);

                            Evaluation evaluation = new Evaluation(instances);
                            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);

                            attributes[0] = evaluation.weightedAreaUnderROC();
                            attributes[1] = evaluation.weightedPrecision();
                            attributes[2] = evaluation.weightedRecall();
                            attributes[3] = evaluation.weightedFMeasure();

                            return attributes;
                        }else if(this.searchMethodOption == SearchMethodOptions.GeneticSearch){
                            this.attsel.setClassifier(this.classifierSimpleLogistic);
                            this.attsel.setEvaluator(this.evalCfs);
                            this.attsel.setSearch(this.searchGeneticSearch);

                            Evaluation evaluation = new Evaluation(instances);
                            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);

                            attributes[0] = evaluation.weightedAreaUnderROC();
                            attributes[1] = evaluation.weightedPrecision();
                            attributes[2] = evaluation.weightedRecall();
                            attributes[3] = evaluation.weightedFMeasure();

                            return attributes;
                        }else if(this.searchMethodOption == SearchMethodOptions.GreedyStepwise){
                            this.attsel.setClassifier(this.classifierSimpleLogistic);
                            this.attsel.setEvaluator(this.evalCfs);
                            this.attsel.setSearch(this.searchGreedyStepwise);

                            Evaluation evaluation = new Evaluation(instances);
                            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);

                            attributes[0] = evaluation.weightedAreaUnderROC();
                            attributes[1] = evaluation.weightedPrecision();
                            attributes[2] = evaluation.weightedRecall();
                            attributes[3] = evaluation.weightedFMeasure();

                            return attributes;
                        }else{
                            System.out.println("Erro durante a geração do arquivo contendo o ROC e do arquivo contendo as features");
                            return attributes;
                        }
                    }else if(this.attributeEvaluatorOption == AttributeEvaluatorOptions.InfoGain){
                        if(this.searchMethodOption == SearchMethodOptions.Ranker){
                            this.attsel.setClassifier(this.classifierSimpleLogistic);
                            this.attsel.setEvaluator(this.evalInfoGain);
                            this.attsel.setSearch(this.searchRanker);

                            Evaluation evaluation = new Evaluation(instances);
                            evaluation.crossValidateModel(attsel,instances,10,new Random(1),this.forPredictionsPrinting ,this.attsToOutput, this.outputDistribution);

                            attributes[0] = evaluation.weightedAreaUnderROC();
                            attributes[1] = evaluation.weightedPrecision();
                            attributes[2] = evaluation.weightedRecall();
                            attributes[3] = evaluation.weightedFMeasure();

                            return attributes;
                        }else{
                            System.out.println("Erro durante a geração do arquivo contendo o ROC e do arquivo contendo as features");
                            return attributes;
                        }
                    }
                }
        }catch(Exception e){
            
        }
            return attributes;
    }
    
    public ArrayList<String> getSelectedFeatures(Instances instances){
        ArrayList<String> features = null;
        
        try {
            features = new ArrayList<String>();
            if (this.attributeEvaluatorOption == AttributeEvaluatorOptions.CFS) {
                if (this.searchMethodOption == SearchMethodOptions.BestFirst) {

                    this.featsel = new AttributeSelection2();
                    this.featsel.setEvaluator(this.evalCfs);
                    this.featsel.setSearch(this.searchBestFirst);
                    this.featsel.SelectAttributes(instances);

                    features = this.featsel.getAttributsCFS();

                } else if (this.searchMethodOption == SearchMethodOptions.GeneticSearch) {
                    this.featsel = new AttributeSelection2();
                    this.featsel.setEvaluator(this.evalCfs);
                    this.featsel.setSearch(this.searchGeneticSearch);
                    this.featsel.SelectAttributes(instances);

                    features = this.featsel.getAttributsCFS();
    
                } else if (this.searchMethodOption == SearchMethodOptions.GreedyStepwise) {
                    this.featsel = new AttributeSelection2();
                    this.featsel.setEvaluator(this.evalCfs);
                    this.featsel.setSearch(this.searchGreedyStepwise);
                    this.featsel.SelectAttributes(instances);

                    features = this.featsel.getAttributsCFS();

                } else {
                    System.out.println("Erro durante a seleção das features!");
                    return null;
                }
            } else if (this.attributeEvaluatorOption == AttributeEvaluatorOptions.InfoGain) {
                if (this.searchMethodOption == SearchMethodOptions.Ranker) {
                    this.featsel = new AttributeSelection2();
                    this.featsel.setEvaluator(this.evalInfoGain);
                    this.featsel.setSearch(this.searchRanker);
                    this.featsel.SelectAttributes(instances);

                    features = this.featsel.getAttributsCFS();

                } else {
                    System.out.println("Erro durante a seleção das features!");
                    return null;
                }
            }
        } catch (Exception e) {
        
        }
        
        return features;
    }  
}

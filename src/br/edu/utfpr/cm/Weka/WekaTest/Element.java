/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

import java.util.ArrayList;

/**
 *
 * @author samuel
 */
public class Element {
    private String path;
    public String dataset;
    public String algoritmo;
    public String attributeEvaluator;
    public String searchMethod;
    public double roc;
    public double precision;
    public double recall;
    public double fMeasure;
    public double distance; // Distance do arquivo para o cluster que ele pertence
    
    public ArrayList<String> features;
    public int amountFeatures;
    
    Element(String dataset,String algoritmo,String attributeEvaluator,String searchMethod,double roc, double precision, double recall, double fMeasure){
        this.dataset = dataset;
        this.algoritmo = algoritmo;
        this.attributeEvaluator = attributeEvaluator;
        this.searchMethod = searchMethod;
        this.roc = roc;
        this.precision = precision;
        this.recall = recall;
        this.fMeasure = fMeasure;
        this.features = new ArrayList<String>(); // Atributos selecionados na classificação
        this.amountFeatures = 0;
    }
    
    public String getDataset(){
        return this.dataset;
    }
    
    public String getAlgoritmo(){
        return this.algoritmo;
    }
    
    public String getAttributeEvaluator(){
        return this.attributeEvaluator;
    }
    
    public String getSearchMethod(){
        return this.searchMethod;
    }
    
    public double getRoc(){
        return this.roc;
    }
    
    public double getDistance(){
        return this.distance;
    }
    
    public void setDistance(double distance){
        this.distance = distance;
    }
    
    public void addFeature(String feature){
        this.features.add(feature);
        this.amountFeatures++;
    }
    
    public int getAmountFeatures(){
        return this.amountFeatures;
    }
    
    public ArrayList<String> getFeatures(){
        return this.features;
    }
    
    public void setPath(String path){
        this.path = path;
    }
    
    public String getPath(){
        return this.path;
    }
}

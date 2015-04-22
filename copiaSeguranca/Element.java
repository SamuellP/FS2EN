/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

/**
 *
 * @author samuel
 */
public class Element {
    public String dataset;
    public String algoritmo;
    public String attributeEvaluator;
    public String searchMethod;
    public double roc;
    public double distance; // Distance do arquivo para o cluster que ele pertence
    
    Element(String dataset,String algoritmo,String attributeEvaluator,String searchMethod,double roc){
        this.dataset = dataset;
        this.algoritmo = algoritmo;
        this.attributeEvaluator = attributeEvaluator;
        this.searchMethod = searchMethod;
        this.roc = roc;
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
}

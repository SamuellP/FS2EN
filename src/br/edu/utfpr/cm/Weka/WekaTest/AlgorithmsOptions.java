/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

/**
 *
 * @author samuel
 */
public enum AlgorithmsOptions {
    NaiveBayes(0), 
    RandomForest(1),
    SimpleLogistic(2),
    J48(3),
    DecisionTable(4),
    All(5);
    
    private int option;
    
    AlgorithmsOptions(int option){
        this.setOption(option);
    }
    
    public int getOption(){
        return this.option;
    }
    
    public void setOption(int option){
        this.option = option;
    }    
}

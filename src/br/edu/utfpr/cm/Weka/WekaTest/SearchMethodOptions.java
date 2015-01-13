/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

/**
 *
 * @author RicardoSatinVostro
 */
public enum SearchMethodOptions {
    BestFirst (1),
    GreedyStepwise(2), 
    GeneticSearch(3),
    Ranker(4),
    All(0);    
    
    private int option;
    
    SearchMethodOptions(int option){
        this.setOption(option);
    }
    
    public int getOption(){
        return this.option;
    }
    
    public void setOption(int option){
        this.option = option;
    }           
}

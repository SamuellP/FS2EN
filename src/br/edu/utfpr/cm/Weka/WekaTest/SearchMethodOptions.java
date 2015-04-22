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
    
    public String getOptionToString(){
        if(this.option == 1)
            return "BestFirst";
        else if(this.option == 2)
            return "GreedyStepwise";
        else if(this.option == 3)
            return "GeneticSearch";
        else if(this.option == 4)
            return "Ranker";
        return null;
    }
}

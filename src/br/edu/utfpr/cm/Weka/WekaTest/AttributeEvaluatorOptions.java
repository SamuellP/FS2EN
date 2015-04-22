/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

/**
 *
 * @author RicardoSatinVostro
 */
public enum AttributeEvaluatorOptions {
    CFS (1),
    InfoGain(2),
    All(0);
    
    
    private int option;
    
    AttributeEvaluatorOptions(int option){
        this.setOption(option);
    }
    
    public int getOption(){
        return this.option;
    }
    
    public void setOption(int option){
        this.option = option;
<<<<<<< HEAD
    }
    
    public String getOptionToString(){
        if(this.option == 1)
            return "CFS";
        else if(this.option == 2)
            return "InfoGain";
        return null;
    }
=======
    }        
>>>>>>> 553418117f68c76cbf42844e7d8f1546a8366c43
}

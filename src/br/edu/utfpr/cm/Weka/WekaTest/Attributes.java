/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

/**
 *
 * @author samuel
 */
public class Attributes {
    private String atributo = null;
    private boolean ehClasse;
    
    public void addAtribute(String atributo,boolean ehClasse){
        this.atributo = atributo;
        this.ehClasse = ehClasse;
    }
    
    public String getAtribute(){
        return this.atributo;
    }
    
    public boolean attributeIsClass(){
        if(this.ehClasse)
            return true;
        return false;
    }
}

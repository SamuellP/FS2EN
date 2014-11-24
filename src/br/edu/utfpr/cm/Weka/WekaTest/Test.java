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
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ArrayList<Attributes> list = new ArrayList<Attributes>();
        
        Attributes at = new Attributes();
        at.addAtribute("abc", true);
        
        list.add(at);
        
        at = null;
        
        at = new Attributes();
        at.addAtribute("abc", true);
        
        if(list.contains(at))
            System.out.println(list.get(0).getAtribute());
        
        at = null;
    }
}

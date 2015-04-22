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
public class t {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String a = "Teste";
        String b = "Teste";
        
        ArrayList<String> list = new ArrayList<String>();
        
        list.add(a);
        
        if(list.contains(b))
            System.out.println("Funcionou!!!");
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author samuel
 */
public class OperatorAttributeValues {
    private HashMap<String,ArrayList<String>> values = new HashMap<String,ArrayList<String>>();
    
    public void putValues(String key,ArrayList<String> values){
        if(this.values.containsKey(key) == false)
            this.values.put(key, new ArrayList<String>());
        
        for(int i=0;i<values.size();i++){
            if(this.values.get(key).contains(values.get(i)) == false)
                this.values.get(key).add(values.get(i));
        }
    }
    
    public String getValues(String key){
        String string = null;
        
        if(values.containsKey(key) == false)
            return null;
        
        ArrayList<String> list = this.values.get(key);
        
        for(int i=0;i<list.size();i++){
            if(i == 0)
                string = list.get(i);
            else
                string = string + "," + list.get(i);
        }
        return string;
    }
}
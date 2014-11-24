/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author samuel
 */
public class OperatorAttributes {
    String attribute;
    
    public OperatorAttributes(String attribute){
        this.attribute = attribute;
    }
    
    public String getAttributeType(){
        String string = "";
        int i=0;
        while(this.attribute.charAt(i) != '{'){
            string = string + attribute.charAt(i);
            i++;
        }
        return string;
    }
    
    public ArrayList<String> getAttributeValue(){
        ArrayList<String> list = new ArrayList<String>();
        String string = "";
        int beginIndex = this.attribute.indexOf("{");
        int endIndex = this.attribute.indexOf("}");
        
        for(int i = beginIndex+1;i <= endIndex;i++){
            if(this.attribute.charAt(i) == ',' || this.attribute.charAt(i) == '}'){
                list.add(string);
                string = "";
            }else{
                if(this.attribute.charAt(i) != ' ')
                    string = string + this.attribute.charAt(i);
            }
        }
        return list;
    }
    
    public void setAttribute(String attribute){
        this.attribute = attribute;
    }
    
    public static void main(String[] args) throws IOException, Exception{
        OperatorAttributes a = new OperatorAttributes("@asdf sassf {123,456,789}");
        ArrayList<String> list = a.getAttributeValue();
        
        System.out.println(a.getAttributeType() + "\n");
        System.out.println(list);
    }
}

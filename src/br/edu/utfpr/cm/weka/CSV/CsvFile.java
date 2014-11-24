/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.weka.CSV;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Samuel
 */
public class CsvFile {
    public static void splitCsvFile(File input, File output) throws FileNotFoundException, IOException{
        FileReader fr = new FileReader(input);
        BufferedReader br = new BufferedReader(fr);
        
        FileWriter fw = null;
        
        String parameters = br.readLine();
        parameters = parameters.replace(';', ',');
        String line = null;
        String project = null;
        
        while(br.ready()){
            line = br.readLine();
            line = line.replace(';', ',');
            
            if(project == null){
                project = getProject(line);
                fw = new FileWriter(output + "/" + project + ".csv");
                fw.write(parameters + "\n");
            }else{
                String s = getProject(line);
                if(s.equals(project) == false){
                    project = s;
                    fw.flush();
                    fw = new FileWriter(output + "/" + project + ".csv");
                    fw.write(parameters + "\n");
                }
            }
            
            fw.write(line + "\n");   
        }
        fw.flush();
    }
    
    public static String getProject(String s){
        int cont = 0;
        String project = "";
        
        for(int i=0;cont < 3;i++){
            if(s.charAt(i) == ','){
                cont++;
            }else{
                if(cont == 2){
                    project += s.charAt(i);
                }
            }
        }
        
        return project;
    }
}

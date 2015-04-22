/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.CSV;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

/**
 *
 * @author samuel
 */
public class Converter2 {
    public static void converterCsvToArff(File[] input, File output) throws IOException{
        CSVLoader csvLoader = null;
        ArffSaver arffSaver = null;
        Instances data = null;
        String project = null;
        File saida = null;
        
        for(int i=0;i<input.length;i++){
            project = input[i].getName();
            project = project.replaceAll("csv", "arff");
            
            //if(output.toString().contains("C:") || output.toString().contains("c:")){
            //    if(output.toString().charAt(output.toString().length() - 1) == '\\')
            //        saida = new File(output + project);
            //    else
            //        saida = new File(output + "\\\\" + project);
            //}else{
                if(output.toString().charAt(output.toString().length() - 1) == '/')
                    saida = new File(output + project);
                else
                    saida = new File(output + "/" + project);
            //}
            /** Carregando arquivo CSV **/
            csvLoader = new CSVLoader();
            csvLoader.setSource(input[i]);
            data = csvLoader.getDataSet();
            
            /** Apagando atributos "Project" e "File" **/
            data.deleteAttributeAt(0);
            data.deleteAttributeAt(0);
            
            /** Adicionando uma nova instância
                FastVector fv = new FastVector();
                fv.addElement("buggy");
                fv.addElement("clean");
                Attribute at = new Attribute("Buggy", fv);
                data.insertAttributeAt(at, 66);
            **/
            
            /** Convertendo o arquivo CSV carregado para ARFF **/
            arffSaver = new ArffSaver();
            arffSaver.setInstances(data);
            arffSaver.setFile(saida);
            //arffSaver.setDestination(output);
            arffSaver.writeBatch();
        }
    }
    
    public void setClasses(File[] input, File directoryOutput) throws FileNotFoundException, IOException{
        BufferedReader br;
        FileWriter fw;
        String line;
        
        for(File file: input){
            br = new BufferedReader(new FileReader(file));
            fw = new FileWriter(new File(directoryOutput.toString() + "/" + file.getName()));
            
            while(br.ready()){
                line = br.readLine();
                
                if(line.contains("@attribute") && line.contains("Buggy"))
                    fw.write("@attribute Buggy {buggy,clean}");
                else
                    fw.write(line);
                fw.write("\n");
            }
            fw.flush();
        }
    }
}

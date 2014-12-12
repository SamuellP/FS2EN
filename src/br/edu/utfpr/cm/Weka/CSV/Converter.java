/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.CSV;

import java.io.File;
import java.io.IOException;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

/**
 *
 * @author samuel
 */
public class Converter {
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
            
            /** Convertendo o arquivo CSV carregado para ARFF **/
            arffSaver = new ArffSaver();
            arffSaver.setInstances(data);
            arffSaver.setFile(saida);
            //arffSaver.setDestination(output);
            arffSaver.writeBatch();
        }
    }
}

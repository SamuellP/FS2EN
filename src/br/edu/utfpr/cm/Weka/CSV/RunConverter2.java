/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.CSV;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author samuel
 */
public class RunConverter2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        /** Diretório que contém os arquivos CSV a serem convertidos em ARFF **/
        File directory = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_teste/saida/csv2");
        File[] csvFiles = directory.listFiles();
        
        /** Destino dos arquivos ARFF que serão gerados **/
        //File destination = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_teste/saida/arff_semProject_semFile_ClasseAjustada");
        File destination = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_teste/saida/arff2");
        new Converter2().converterCsvToArff(csvFiles, destination);
        
        new Converter2().setClasses(new File("/home/samuel/Documentos/BCC/Projeto/arquivos_teste/saida/arff2").listFiles(), new File("/home/samuel/Documentos/BCC/Projeto/arquivos_teste/saida/ARFF/"));
    }
}

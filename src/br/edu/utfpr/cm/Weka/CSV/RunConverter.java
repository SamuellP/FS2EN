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
public class RunConverter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        /** Diretório que contém os arquivos CSV a serem convertidos em ARFF **/
        //File directory = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_teste/saida/csv2");
        File directory = new File("/home/samuel/Documentos/BCC/5° Semestre/Inteligência Artificial/atividade_comparacao_KNN_svm_arvoreDeDecisao/DecisionTree");
        File[] csvFiles = directory.listFiles();
        
        /** Destino dos arquivos ARFF que serão gerados **/
        //File destination = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_teste/saida/arff2");
        File destination = new File("/home/samuel/Documentos/BCC/5° Semestre/Inteligência Artificial/atividade_comparacao_KNN_svm_arvoreDeDecisao");
        new Converter().converterCsvToArff(csvFiles, destination);
    }
}

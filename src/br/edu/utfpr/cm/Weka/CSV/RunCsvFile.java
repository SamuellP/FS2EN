/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.CSV;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author samuel
 */
public class RunCsvFile {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        /** Arquivo CSV a ser dividido **/
        File input = new File("/home/samuel/Documentos/BCC/Projeto/arquivos/visaoRicardoTotal_Limpo_semNominal.csv");
    
        /** Diretório para salvar os arquivos CSV gerados **/
        File output = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_teste/saida/csv2");
        
        /** Executando método para quebrar um arquivo CSV em vários arquivos **/
        new CsvFile().splitCsvFile(input, output);
    }
}

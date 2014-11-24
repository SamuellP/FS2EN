/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

import br.edu.utfpr.cm.Weka.WekaTest.appendFile;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 *
 * @author Ricardo Satin
 */
public class WekaTestAppendFile {

    public static void main(String[] args) throws IOException, Exception{
    appendFile aF = new appendFile();
    
    FileFilter filter = new Filter().FilterFiles(".arff");
    
    File diretorio = new File("/home/samuel/Documentos/BCC/Projeto/arquivos");
    File[] entradas = diretorio.listFiles(filter);
    File saida = null;
    
    saida = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_teste/saida/novo.arff");
    
    aF.appendF(entradas,saida);
}
            
}
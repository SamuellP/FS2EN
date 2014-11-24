/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author samuel
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        /*appendFile aF = new appendFile();
        ChooseArff choose = new ChooseArff();
        File[] entradas = choose.chooseFiles();
        System.out.println("Tamanho: " + entradas.length + "\n");
        File saida = null;
    
        saida = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_teste/saida/novo.arff");
    
        if(entradas != null)
            aF.appendF(entradas,saida);
        else
            System.out.println("Nenhum arquivo foi especificado\n");*/
        
        new ScreenUnifyFiles().executar();
    }
}

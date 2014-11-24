/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

import java.io.IOException;

/**
 *
 * @author RicardoSatinVostro
 */
public class WekaJ48 {

    public static String caminhoDados;
    
        public static void main(String[] args) throws IOException, Exception {
        
        // Indica onde estão os dados (neste exemplo, eles estão no formato .data)
        caminhoDados = "C:/ACER_D/ricardo/Mestrado/Mestrado/Teste de SW/Dissertação/weka/Arff/test/weather.nominal.arff";
        classifyJ48 exJ48 = new classifyJ48(caminhoDados);
 
        exJ48.leDados();
        exJ48.imprimeDados();    
        exJ48.classifyJ48();
        exJ48.crossValidationJ48();
        }
    
}

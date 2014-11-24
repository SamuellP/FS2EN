/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

import java.io.File;

/**
 *
 * @author RicardoSatinVostro
 */
class WekaTest {

    static File[] getListOfFiles(String folderName) {
        File folder = new File(folderName);
        File [] files = folder.listFiles();
        return files;     
    }
    
}

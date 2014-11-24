/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author samuel
 */
public class Filter {
    public FileFilter FilterFiles(String extension){
        FileFilter filter = new FileFilter() {

            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".arff");
            }
        };
        
        return filter;
    }
}

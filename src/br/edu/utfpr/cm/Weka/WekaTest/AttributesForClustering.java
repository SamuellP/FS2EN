/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

/**
 *
 * @author samuel
 */
public enum AttributesForClustering {
    Roc (0),
    Precision (1),
    Recall (2),
    F_Measure (3),
    Roc_And_Precision (4),
    Roc_And_Recall (5),
    Roc_And_F_Measure (6),
    Precision_And_Recall (7),
    Precision_And_F_Measure (8),
    Recall_And_F_Measure (9),
    All(10);
    
    private int option;
    
    AttributesForClustering(int option){
        this.option = option;
    }
}

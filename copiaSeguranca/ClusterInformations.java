/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

import java.util.ArrayList;

/**
 *
 * @author samuel
 */
public final class ClusterInformations {
    private double cohesion;
    private double centroid;
    
    ClusterInformations(double cohesion, double centroid){
        setCohesion(cohesion);
        setCentroid(centroid);
    }
    
    public void setCohesion(double cohesion){
        this.cohesion = cohesion;
    }
    
    public void setCentroid(double centroid){
        this.centroid = centroid;
    }
    
    public double getCohesion(){
        return this.cohesion;
    }
    
    public double getCentroid(){
        return this.centroid;
    }
    
    public void recalculateCentroid(ArrayList<Element> list){
        double c = 0; // Vai armazenar o somatório de todas as distâncias do cluster
        
        for(Element e: list){
            c += e.distance;
        }
        
        setCentroid(c / list.size());
    }
}

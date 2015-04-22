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
public final class Cluster {
    private ArrayList<Element> vectors;
    private double cohesion;
    private double centroid;
    
    private double sumRoc; // Para não precisar percorrer todos os vetores sempre que o centroide for recalculado
    private int qtdVectors;
    
    Cluster(){
        this.vectors = new ArrayList<Element>();
        setCohesion(Double.POSITIVE_INFINITY);
        setCentroid(Double.POSITIVE_INFINITY);
        this.sumRoc = 0; // Armazena a soma dos valores ROC de cada vetor
        this.qtdVectors = 0;
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
    
    public int getQtdVectors(){
        return this.qtdVectors;
    }
    
    public ArrayList<Element> getVectors(){
        return this.vectors;
    }
    
    public void recalculateCentroid(){
        /*double c = 0; // Vai armazenar o somatório de todas as distâncias do cluster
        
        for(Element e: this.vectors){
            c += e.distance;
        }
        
        setCentroid(c / this.vectors.size());*/
        //this.sumRoc += this.vectors.get(this.vectors.size() - 1).getDistance();
        setCentroid(this.sumRoc / this.qtdVectors);
    }
    
    public void calculateCohesion(){
        double sum = 0;
        
        for(Element e: this.getVectors()){
            sum += e.getDistance() * e.getDistance();
        }
        
        this.setCohesion(sum);
    }
    
    public void recalculateDistanceVectors(){
        for(Element e: this.vectors){
            double distance = e.getRoc() - this.getCentroid();
            if(distance < 0)
                distance *= (-1);
            
            e.setDistance(distance);
        }
    }
    
    public void addVector(Element e){
        this.vectors.add(e);
        this.qtdVectors++;
        this.sumRoc += e.getRoc();
    }
}

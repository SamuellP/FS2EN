/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author samuel
 */
public class BSAS {
    /**
     * 
     * @param elements - Todos os vetores que vão ser classificados em clusters.
     * @param value - Uma distância de tolerância para a criação dos clusters.
     * @param maximumCluster - Quantidade maxima de clusters que pode ser criado.
     * @return - Retorna um Hash contendo os clusters que foram formados.
     */
    public HashMap<Integer,Cluster> runBSAS(ArrayList<Element> elements, double value, int maximumCluster){
        HashMap<Integer,Cluster> hm = new HashMap<Integer,Cluster>();
        ArrayList<Element> list;
        
        int qtdCluster = 1;
        int contList = 0;
        int key = 1;
        
        double distance;
        double maxRoc;
        
        if(elements.isEmpty()) return null;
        
     // Inserindo primeiro elemento no cluster   
        Element firstElement = this.getFirstVector(elements);
        firstElement.distance = 0; // A verificar
        hm.put(key, new Cluster());
        hm.get(key).addVector(firstElement);
        hm.get(key).setCentroid(firstElement.getRoc());
        
        key++;
        //contList++;
    //
        
        while(contList < elements.size()){
            Element aux = elements.get(contList);
            
            if(aux != firstElement){
                double roc = aux.getRoc();
            
                int k = searchMinCluster(hm, roc);// k é a chave do cluster de menor distância //
            
                list = hm.get(k).getVectors();// Obtendo cluster //
            
            /*maxRoc = getMaxRoc(list);// Obtendo o valor maximo de ROC dentre todos os elementos do cluster //
         
       // Calculando distancia euclidiana   
            distance = roc - maxRoc;
            if(distance < 0)
                distance *= (-1);*/
      //
                distance = getMinDistance(list,roc);

                if(distance > value && qtdCluster < maximumCluster){
                    aux.distance = 0; // A verificar
                    hm.put(key, new Cluster());
                    hm.get(key).addVector(aux);
                    hm.get(key).setCentroid(aux.getRoc());
                
                    key++;
                    qtdCluster++;
                }else{
                    hm.get(k).addVector(aux);
                
                    hm.get(k).recalculateCentroid();
                    hm.get(k).recalculateDistanceVectors();
                }
            }
            
            contList++;
        }
        
        return hm;
    }
    
    // Retorna a chave do cluster do qual o elemento mais se aproxima //
    public int searchMinCluster(HashMap<Integer,Cluster> hash, double roc){
        Set<Integer> listKey = hash.keySet();
        ArrayList<Element> listElement;
        int key = 0;
        double maxRoc;
        double minDistance = Float.POSITIVE_INFINITY;
        double distance;
        
        for(Integer k: listKey){
            listElement = hash.get(k).getVectors();
            //maxRoc = getMaxRoc(listElement);
            
            /*distance = roc - maxRoc;
            if(distance < 0)
                distance = distance * (-1);*/
            distance = getMinDistance(listElement,roc);
            
            if(distance < minDistance){
                key = k;
                minDistance = distance;
            }
        }
        
        return key;
    }
    
    // Retorna a menor distancia entre o arquivo que está sendo tratado e um cluster qualquer //
    public double getMinDistance(ArrayList<Element> list, double roc){
        double minDistance = Float.POSITIVE_INFINITY;
        double distance;
        
        for(Element e: list){
            distance = e.roc - roc;
            if(distance < 0)
                distance *= (-1);
            
            if(distance < minDistance)
                minDistance = distance;
        }
        
        return minDistance;
    }
    
    public double getMaxRoc(ArrayList<Element> list){
        Element aux;
        double maxRoc = Float.NEGATIVE_INFINITY;
        
        for(int i=0; i<list.size(); i++){
            aux = list.get(i);
            if(aux.getRoc() > maxRoc)
                maxRoc = aux.getRoc();
        }
        
        return maxRoc;
    }
    
    public Element getFirstVector(ArrayList<Element> vectors){
        double maxRoc = Double.NEGATIVE_INFINITY;
        Element element = null;
        
        for(Element e: vectors){
            if(e.getRoc() > maxRoc){
                maxRoc = e.getRoc();
                element = e;
            }
        }
        
        return element;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Attribute;
import weka.core.Instances;

/**
 *
 * @author samuel
 */
public class InformationGroups {
    public void generateInformationGroups(Clusteriza clusteriza, File output){
        FileWriter fwOutput;
        
        try{
            fwOutput = new FileWriter(output);
            
            this.generateGroupOne(fwOutput, clusteriza.getSubClusters());
            fwOutput.write("\n\n");
            this.generateGroupTwo(clusteriza.getSubClusters(), fwOutput, clusteriza.getAlgorithmsOptions(), clusteriza.getAttributeEvaluatorOptions(), clusteriza.getSearchMethodOptions());
            fwOutput.write("\n\n");
            this.generateGroupThreeFourAndFive(clusteriza.getSubClusters(), clusteriza.getAmountOfDatasetsClustered(), fwOutput, clusteriza.getAttributeEvaluatorOptions(), clusteriza.getSearchMethodOptions());
            fwOutput.write("\n\n");
            this.generateGroupSixSevenAndEight(clusteriza.getSubClusters(), clusteriza.getAmountOfDatasetsClustered(), fwOutput, AttributeEvaluatorOptions.InfoGain, SearchMethodOptions.Ranker);
            
            fwOutput.flush();
        }catch(Exception e){
            
        }
    }
    
    public void generateGroupOne(Writer output, Map<Double,Cluster> clusters){
        DecimalFormat df = new DecimalFormat("#.000");
        
        try{
            output.write("Grupo_de_informacao_1\n");
            output.write("Obs:_Contem_informacoes_do_Cluster_original\n\n");
            output.write("N_Cluster;Centroide;Dataset;Algoritmo;AttributeEvaluator;SearchMethod;ROC\n");
            
            for(Double key: clusters.keySet()){
                Cluster cluster = clusters.get(key);
                
                for(Element element: cluster.getVectors()){
                    output.write(key + ";" + df.format(cluster.getCentroid()) + ";" + 
                                 element.getDataset() + ";" + element.getAlgoritmo() + ";" + 
                                 element.getAttributeEvaluator() + ";" + element.getSearchMethod() + 
                                 ";" + df.format(element.getRoc()) + "\n");
                }
            }
        }catch(Exception e){
            
        }
    }
    
    public void generateGroupTwo(Map<Double,Cluster> clusters, Writer output, AlgorithmsOptions aoOption, AttributeEvaluatorOptions aeOption, SearchMethodOptions smOption){
        String[] datasetsOfCluster = null;
        Double[] roc = null;
        int i = 0;
        double centroide = 0;
        
        DecimalFormat df = new DecimalFormat("#.000");
        
        try{
            output.write("Grupo_de_informacao_2\n");
            output.write("Obs:_Resultados_executando_com_as_mesmas_entradas_iniciais_porem_considerando_apenas_as_features_selecionadas_na_classificacao_inicial\n\n");
            output.write("N_Cluster;Centroide;Dataset;ROC;Distancia_para_o_cluster_inicial\n");
            
            for(Double key: clusters.keySet()){
                Cluster cluster = clusters.get(key);
                
                datasetsOfCluster = new String[cluster.getQtdVectors()];
                roc = new Double[cluster.getQtdVectors()];
                
                ArrayList<String> features = cluster.getCommonFeatures();
                
                for(Element element: cluster.getVectors()){
                    Instances instances = new Instances(new BufferedReader(new FileReader(element.getPath())));
                    
                    System.out.println(key + " - " + features);
                    Enumeration attributes = instances.enumerateAttributes();
                    
                    while(attributes.hasMoreElements()){
                        Attribute attribute = (Attribute) attributes.nextElement();

                        if(attributes.hasMoreElements() && !features.contains(attribute.name())){
                            instances.deleteAttributeAt(instances.attribute(attribute.name()).index());
                            System.out.println(element.getDataset() + " Deletando o atributo: " + attribute.name());
                        }
                    }
                    
                    instances.setClassIndex(instances.numAttributes() - 1);
                    
                    Double[] results = new CalculateAttributes(aoOption, aeOption, smOption).getAttributes(instances);
                    
                    datasetsOfCluster[i] = element.getDataset();
                    centroide += results[0];
                    roc[i] = results[0];
                    i++;
                }
                
                centroide = centroide / cluster.getQtdVectors();
                
                for(int j = 0; j < roc.length; j++){
                    output.write(key + ";" + df.format(centroide) + ";" + datasetsOfCluster[j] + ";"
                                 + df.format(roc[j]) + ";" + df.format(centroide - cluster.getCentroid()) + "\n");
                }
                i = 0;
                centroide = 0;
            }
        }catch(Exception e){
            System.out.println("Error!");
        }
    }
    
    public void generateGroupThreeFourAndFive(Map<Double,Cluster> clusters, int amountDataset, Writer output, AttributeEvaluatorOptions aeOption, SearchMethodOptions smOption){
        Map<Double,Double> centroidRandomForestCluster;
        Map<Double,Double> centroidSimpleLogisticCluster;
        Map<Double,Double> centroidNaiveBayesCluster;
        ArrayList<String> selectedFeatures;
        // Estão interligados por posição
        String[] datasetsOfCluster = null;
        Double[] rocRandomForest = null;
        Double[] rocSimpleLogistic = null;
        Double[] rocNaiveBayes = null;
        //
        int i;
        double centroideRandomForest;
        double centroideSimpleLogistic;
        double centroideNaiveBayes;
        
        DecimalFormat df = new DecimalFormat("#.000");
        
        try{
            centroidNaiveBayesCluster = new HashMap<Double,Double>();
            centroidRandomForestCluster = new HashMap<Double, Double>();
            centroidSimpleLogisticCluster = new HashMap<Double, Double>();
            //selectedFeatures = new ArrayList<String>();
            i = 0;
            centroideNaiveBayes = 0;
            centroideRandomForest = 0;
            centroideSimpleLogistic = 0;
            
            rocRandomForest = new Double[amountDataset];
            rocNaiveBayes = new Double[amountDataset];
            rocSimpleLogistic = new Double[amountDataset];
            datasetsOfCluster = new String[amountDataset];
            
            for(Double key: clusters.keySet()){
                Cluster cluster = clusters.get(key);
                /*
                // Fazendo intersecção das features entre todos os elementos do cluster
                if(cluster.getQtdVectors() > 1){
                    Element e1 = cluster.getVectors().get(0);
                    for(String s: e1.getFeatures()){
                        boolean canKeepThisFeature = true;
                        for(int j = 1; j < cluster.getVectors().size(); j++){
                            Element e2 = cluster.getVectors().get(j);
                            if(!e2.getFeatures().contains(s)){
                                canKeepThisFeature = false;
                                break;
                            }
                        }
                        if(canKeepThisFeature)
                            selectedFeatures.add(s);
                        else
                            canKeepThisFeature = true;
                    }
                }
                //*/
                selectedFeatures = cluster.getCommonFeatures();
                
                for(Element element: cluster.getVectors()){
                    Instances instances = new Instances(new BufferedReader(new FileReader(element.getPath())));
                    instances.setClassIndex(instances.numAttributes() - 1);
                    
                    // Armazenando as features que foram selecionadas em cada classificação
                    /*ArrayList<String> sf1 = new CalculateAttributes(AlgorithmsOptions.RandomForest, AttributeEvaluatorOptions.CFS, SearchMethodOptions.BestFirst).getSelectedFeatures(instances);
                    ArrayList<String> sf2 = new CalculateAttributes(AlgorithmsOptions.NaiveBayes, AttributeEvaluatorOptions.CFS, SearchMethodOptions.GeneticSearch).getSelectedFeatures(instances);
                    ArrayList<String> sf3 = new CalculateAttributes(AlgorithmsOptions.SimpleLogistic, AttributeEvaluatorOptions.CFS, SearchMethodOptions.GreedyStepwise).getSelectedFeatures(instances);
                    
                    for(String s: sf1){
                        if(sf2.contains(s) && sf3.contains(s))
                            selectedFeatures.add(s);
                    }*/
                    //selectedFeatures.addAll(new CalculateAttributes(AlgorithmsOptions.RandomForest, AttributeEvaluatorOptions.CFS, SearchMethodOptions.BestFirst).getSelectedFeatures(instances));
                    
                    //selectedFeatures.addAll(new CalculateAttributes(AlgorithmsOptions.NaiveBayes, AttributeEvaluatorOptions.CFS, SearchMethodOptions.GeneticSearch).getSelectedFeatures(instances));
                    
                    //selectedFeatures.addAll(new CalculateAttributes(AlgorithmsOptions.SimpleLogistic, AttributeEvaluatorOptions.CFS, SearchMethodOptions.GreedyStepwise).getSelectedFeatures(instances));
                    System.out.println("\n\n" + element.getDataset() + ": " + selectedFeatures + "\n\n");
                    //
                    
                    // Deletando as features que não foram selecionadas
                    if(!selectedFeatures.isEmpty()){
                        Enumeration attributes = instances.enumerateAttributes();

                        while(attributes.hasMoreElements()){
                            Attribute attribute = (Attribute) attributes.nextElement();

                            if(!selectedFeatures.contains(attribute.name())){
                                instances.deleteAttributeAt(instances.attribute(attribute.name()).index());
                                System.out.println(element.getDataset() + ": Deletando o atributo: " + attribute.name());
                            }
                        }
                    }
                    //selectedFeatures.clear();
                    //sf1.clear();
                    //sf2.clear();
                    //sf3.clear();
                    //
                    
                    instances.setClassIndex(instances.numAttributes() - 1);
                    
                    // Rodando Random Forest
                    Double[] results = new CalculateAttributes(AlgorithmsOptions.RandomForest, aeOption, smOption).getAttributes(instances);
                    
                    datasetsOfCluster[i] = element.getDataset();
                    centroideRandomForest += results[0];
                    rocRandomForest[i] = results[0];
                    //
                    
                    // Rodando Naive Bayes
                    results = new CalculateAttributes(AlgorithmsOptions.NaiveBayes, aeOption, smOption).getAttributes(instances);

                    centroideNaiveBayes += results[0];
                    rocNaiveBayes[i] = results[0];
                    //
                    
                    // Rodando Simple Logistic
                    results = new CalculateAttributes(AlgorithmsOptions.SimpleLogistic, aeOption, smOption).getAttributes(instances);
                    
                    centroideSimpleLogistic += results[0];
                    rocSimpleLogistic[i] = results[0];
                    //
                    
                    i++;
                }
                //selectedFeatures.clear();
                
                centroideNaiveBayes = centroideNaiveBayes / cluster.getQtdVectors();
                centroideRandomForest = centroideRandomForest / cluster.getQtdVectors();
                centroideSimpleLogistic = centroideSimpleLogistic / cluster.getQtdVectors();
                
                // Guardando os resultados obtidos para cada cluster
                centroidNaiveBayesCluster.put(key, new Double(centroideNaiveBayes));
                centroidRandomForestCluster.put(key, new Double(centroideRandomForest));
                centroidSimpleLogisticCluster.put(key, new Double(centroideSimpleLogistic));
                //
                
                //i = 0;
                centroideNaiveBayes = 0;
                centroideRandomForest = 0;
                centroideSimpleLogistic = 0;
            }
            
            // Escrevendo os resultados
            i = 0;
            output.write("Grupo_de_informacao_3\n");
            output.write("Obs:_Resultados_obtidos_rodando_RandomForest\n\n");
            output.write("N_Cluster;Centroide;Dataset;ROC;Distancia_para_o_cluster_inicial\n");
            for(Double key: clusters.keySet()){
                for(int j = 0; j < clusters.get(key).getQtdVectors(); j++){
                    double centroid = centroidRandomForestCluster.get(key);
                    output.write(key + ";" + df.format(centroid) + ";" +
                                 datasetsOfCluster[i] + ";" + df.format(rocRandomForest[i]) + ";" +
                                 df.format(centroid - clusters.get(key).getCentroid())
                                 + "\n");
                    i++;
                }
            }
            
            i = 0;
            output.write("\n\nGrupo_de_informacao_4\n");
            output.write("Obs:_Resultados_obtidos_rodando_SimpleLogistic\n\n");
            output.write("N_Cluster;Centroide;Dataset;ROC;Distancia_para_o_cluster_inicial\n");
            for(Double key: clusters.keySet()){
                for(int j = 0; j < clusters.get(key).getQtdVectors(); j++){
                    double centroid = centroidSimpleLogisticCluster.get(key).doubleValue();
                    output.write(key + ";" + df.format(centroid) + ";" +
                                 datasetsOfCluster[i] + ";" + df.format(rocSimpleLogistic[i]) + ";" +
                                 df.format(centroid - clusters.get(key).getCentroid())
                                 + "\n");
                    i++;
                }
            }
            
            i = 0;
            output.write("\n\nGrupo_de_informacao_5\n");
            output.write("Obs:_Resultados_obtidos_rodando_NaiveBayes\n\n");
            output.write("N_Cluster;Centroide;Dataset;ROC;Distancia_para_o_cluster_inicial\n");
            for(Double key: clusters.keySet()){
                for(int j = 0; j < clusters.get(key).getQtdVectors(); j++){
                    double centroid = centroidNaiveBayesCluster.get(key).doubleValue();
                    output.write(key + ";" + df.format(centroid) + ";" +
                                 datasetsOfCluster[i] + ";" + df.format(rocNaiveBayes[i]) + ";" +
                                 df.format(centroid - clusters.get(key).getCentroid())
                                 + "\n");
                    i++;
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(InformationGroups.class.getName()).log(Level.SEVERE, null, ex);
        }catch (Exception ex) {
            Logger.getLogger(InformationGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void generateGroupSixSevenAndEight(Map<Double,Cluster> clusters, int amountDataset, Writer output, AttributeEvaluatorOptions aeOption, SearchMethodOptions smOption){
        Map<Double,Double> centroidRandomForestCluster;
        Map<Double,Double> centroidSimpleLogisticCluster;
        Map<Double,Double> centroidNaiveBayesCluster;
        ArrayList<String> selectedFeatures;
        // Estão interligados por posição
        String[] datasetsOfCluster = null;
        Double[] rocRandomForest = null;
        Double[] rocSimpleLogistic = null;
        Double[] rocNaiveBayes = null;
        //
        int i;
        double centroideRandomForest;
        double centroideSimpleLogistic;
        double centroideNaiveBayes;
        
        DecimalFormat df = new DecimalFormat("#.000");
        
        try{
            centroidNaiveBayesCluster = new HashMap<Double,Double>();
            centroidRandomForestCluster = new HashMap<Double, Double>();
            centroidSimpleLogisticCluster = new HashMap<Double, Double>();
            //selectedFeatures = new ArrayList<String>();
            i = 0;
            centroideNaiveBayes = 0;
            centroideRandomForest = 0;
            centroideSimpleLogistic = 0;
            
            rocRandomForest = new Double[amountDataset];
            rocNaiveBayes = new Double[amountDataset];
            rocSimpleLogistic = new Double[amountDataset];
            datasetsOfCluster = new String[amountDataset];
            
            for(Double key: clusters.keySet()){
                Cluster cluster = clusters.get(key);
                /*
                // Fazendo intersecção das features entre todos os elementos do cluster
                if(cluster.getQtdVectors() > 1){
                    Element e1 = cluster.getVectors().get(0);
                    
                    Instances insE1 = new Instances(new BufferedReader(new FileReader(e1.getPath())));
                    insE1.setClassIndex(insE1.numAttributes() - 1);
                    
                    ArrayList<String> featuresE1 = new CalculateAttributes(null, aeOption, smOption).getSelectedFeatures(insE1);
                    for(String s: featuresE1){
                        boolean canKeepThisFeature = true;
                        for(int j = 1; j < cluster.getVectors().size(); j++){
                            Element e2 = cluster.getVectors().get(j);
                            
                            Instances insE2 = new Instances(new BufferedReader(new FileReader(e2.getPath())));
                            insE2.setClassIndex(insE2.numAttributes() - 1);
                            
                            ArrayList<String> featuresE2 = new CalculateAttributes(null, aeOption, smOption).getSelectedFeatures(insE2);
                            if(!featuresE2.contains(s)){
                                canKeepThisFeature = false;
                                break;
                            }
                        }
                        if(canKeepThisFeature)
                            selectedFeatures.add(s);
                        else
                            canKeepThisFeature = true;
                    }
                }
                //*/
                selectedFeatures = cluster.getCommonFeatures();
                
                for(Element element: cluster.getVectors()){
                    Instances instances = new Instances(new BufferedReader(new FileReader(element.getPath())));
                    instances.setClassIndex(instances.numAttributes() - 1);
                    
                    // Armazenando as features que foram selecionadas em cada classificação
                    //selectedFeatures.addAll(new CalculateAttributes(null, AttributeEvaluatorOptions.InfoGain, SearchMethodOptions.Ranker).getSelectedFeatures(instances));
                    System.out.println(element.getDataset() + ": " + selectedFeatures + "\n\n");
                    //
                    
                    // Deletando as features que não foram selecionadas
                    if(!selectedFeatures.isEmpty()){
                        Enumeration attributes = instances.enumerateAttributes();

                        while(attributes.hasMoreElements()){
                            Attribute attribute = (Attribute) attributes.nextElement();

                            if(!selectedFeatures.contains(attribute.name())){
                                instances.deleteAttributeAt(instances.attribute(attribute.name()).index());
                                System.out.println(element.getDataset() + ": Deletando o atributo: " + attribute.name());
                            }
                        }
                    }
                    //selectedFeatures.clear();
                    //
                    
                    instances.setClassIndex(instances.numAttributes() - 1);
                    
                    // Rodando Random Forest
                    Double[] results = new CalculateAttributes(AlgorithmsOptions.RandomForest, aeOption, smOption).getAttributes(instances);
                    
                    datasetsOfCluster[i] = element.getDataset();
                    centroideRandomForest += results[0];
                    rocRandomForest[i] = results[0];
                    //
                    
                    // Rodando Naive Bayes
                    results = new CalculateAttributes(AlgorithmsOptions.NaiveBayes, aeOption, smOption).getAttributes(instances);

                    centroideNaiveBayes += results[0];
                    rocNaiveBayes[i] = results[0];
                    //
                    
                    // Rodando Simple Logistic
                    results = new CalculateAttributes(AlgorithmsOptions.SimpleLogistic, aeOption, smOption).getAttributes(instances);
                    
                    centroideSimpleLogistic += results[0];
                    rocSimpleLogistic[i] = results[0];
                    //
                    
                    i++;
                }
                //selectedFeatures.clear();
                
                centroideNaiveBayes = centroideNaiveBayes / cluster.getQtdVectors();
                centroideRandomForest = centroideRandomForest / cluster.getQtdVectors();
                centroideSimpleLogistic = centroideSimpleLogistic / cluster.getQtdVectors();
                
                // Guardando os resultados obtidos para cada cluster
                centroidNaiveBayesCluster.put(key, new Double(centroideNaiveBayes));
                centroidRandomForestCluster.put(key, new Double(centroideRandomForest));
                centroidSimpleLogisticCluster.put(key, new Double(centroideSimpleLogistic));
                //
                
                //i = 0;
                centroideNaiveBayes = 0;
                centroideRandomForest = 0;
                centroideSimpleLogistic = 0;
            }
            
            // Escrevendo os resultados
            i = 0;
            output.write("Grupo_de_informacao_6\n");
            output.write("Obs:_Resultados_obtidos_rodando_RandomForest\n\n");
            output.write("N_Cluster;Centroide;Dataset;ROC;Distancia_para_o_cluster_inicial\n");
            for(Double key: clusters.keySet()){
                for(int j = 0; j < clusters.get(key).getQtdVectors(); j++){
                    double centroid = centroidRandomForestCluster.get(key);
                    output.write(key + ";" + df.format(centroid) + ";" +
                                 datasetsOfCluster[i] + ";" + df.format(rocRandomForest[i]) + ";" +
                                 df.format(centroid - clusters.get(key).getCentroid())
                                 + "\n");
                    i++;
                }
            }
            
            i = 0;
            output.write("\n\nGrupo_de_informacao_7\n");
            output.write("Obs:_Resultados_obtidos_rodando_SimpleLogistic\n\n");
            output.write("N_Cluster;Centroide;Dataset;ROC;Distancia_para_o_cluster_inicial\n");
            for(Double key: clusters.keySet()){
                for(int j = 0; j < clusters.get(key).getQtdVectors(); j++){
                    double centroid = centroidSimpleLogisticCluster.get(key).doubleValue();
                    output.write(key + ";" + df.format(centroid) + ";" +
                                 datasetsOfCluster[i] + ";" + df.format(rocSimpleLogistic[i]) + ";" +
                                 df.format(centroid - clusters.get(key).getCentroid())
                                 + "\n");
                    i++;
                }
            }
            
            i = 0;
            output.write("\n\nGrupo_de_informacao_8\n");
            output.write("Obs:_Resultados_obtidos_rodando_NaiveBayes\n\n");
            output.write("N_Cluster;Centroide;Dataset;ROC;Distancia_para_o_cluster_inicial\n");
            for(Double key: clusters.keySet()){
                for(int j = 0; j < clusters.get(key).getQtdVectors(); j++){
                    double centroid = centroidNaiveBayesCluster.get(key).doubleValue();
                    output.write(key + ";" + df.format(centroid) + ";" +
                                 datasetsOfCluster[i] + ";" + df.format(rocNaiveBayes[i]) + ";" +
                                 df.format(centroid - clusters.get(key).getCentroid())
                                 + "\n");
                    i++;
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(InformationGroups.class.getName()).log(Level.SEVERE, null, ex);
        }catch (Exception ex) {
            Logger.getLogger(InformationGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

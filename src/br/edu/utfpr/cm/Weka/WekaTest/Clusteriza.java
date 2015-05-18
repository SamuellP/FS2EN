/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author samuel
 */
public final class Clusteriza {

    private ArrayList<Element> elements;
    private HashMap<Integer, Cluster> clusters;
    private HashMap<Double, Cluster> subClusters;
    public AlgorithmsOptions aOptions;
    public AttributeEvaluatorOptions aeOptions; // opções de avaliadores de atributo - para clusterizar
    public SearchMethodOptions smOptions;// opções de pesquisa de atributo - para clusterizar 
    File[] datasets; // Diretorio contendo os arff's para clasterização
    File file; // Arquivo contendo os ROC
    File features; // Arquivo contendo as features
    
    private AttributesForClustering attributesForClustering;

    Clusteriza(AlgorithmsOptions aoOptions, AttributeEvaluatorOptions aeOptions, SearchMethodOptions smOptions, File[] datasets, File file, File features, AttributesForClustering attributes) throws FileNotFoundException, IOException, Exception {
        this.elements = new ArrayList<Element>();
        this.clusters = null;
        this.aOptions = aoOptions;
        this.aeOptions = aeOptions;
        this.smOptions = smOptions;
        this.datasets = datasets;
        this.file = file;
        this.features = features;
        
        this.attributesForClustering = attributes;
        
        new FileGenerator(aoOptions, aeOptions, smOptions).generateRocFileAndAttributeFile(datasets, this.file, this.features, this.attributesForClustering);
        initialize(aoOptions, aeOptions, smOptions);
    }
    
    Clusteriza(AlgorithmsOptions aoOptions, File[] datasets, File file, AttributesForClustering attributes) throws FileNotFoundException, IOException, Exception {
        this.elements = new ArrayList<Element>();
        this.clusters = null;
        this.aOptions = aoOptions;
        this.aeOptions = null;
        this.smOptions = null;
        this.datasets = datasets;
        this.file = file;
        this.features = null;
        
        this.attributesForClustering = attributes;
        
        new FileGenerator(aoOptions, null, null).generateRocFile(datasets, this.file, this.attributesForClustering);
        initialize(aoOptions, null, null);
    }
    /*
    public void initializeElements(File[] datasets, String algoritmo, String attributeEvaluator, String searchMethod) throws IOException, Exception{
        Double[] attributes;
        double roc;
        double precision;
        double recall;
        double fMeasure;
        
        HashMap<String, ArrayList<String>> hash = loadFeatures(algoritmo, attributeEvaluator, searchMethod);
        
        for(File dataset: datasets){
            attributes = new CalculateAttributes(this.aOptions, this.aeOptions, this.smOptions).getAttributes(dataset);
            roc = attributes[0];
            precision = attributes[1];
            recall = attributes[2];
            fMeasure = attributes[3];
            
            Element e = new Element(dataset.getName(), algoritmo, attributeEvaluator, searchMethod, roc, precision, recall, fMeasure);
                //loadFeatures(e, dataset, algoritmo, attributeEvaluator, searchMethod);
                if (hash.containsKey(dataset.getName())) {// Verificando se o vetor em questão possui atributos
                    e.features = hash.get(dataset.getName());
                    e.amountFeatures = hash.get(dataset.getName()).size();
                    this.elements.add(e);
                }
        }
    }*/
    
    // Este método inicializa os elementos utilizando o arquivo com os ROC's //
    public void initializeElements(String aoOptions, String aeOptions, String smOptions) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.file));
        String line;
        String dataset;
        String path;
        String algoritmo;
        String attributeEvaluator;
        String searchMethod;
        double roc;
        double precision;
        double recall;
        double fMeasure;
        
        BufferedReader brFeatures = null;
        HashMap<String, ArrayList<String>> hash = null;
        if(this.features != null){
            brFeatures = new BufferedReader(new FileReader(this.features));
            hash = loadFeatures(aoOptions, aeOptions, smOptions);
        }

        while (br.ready()) {
            line = br.readLine();
            Scanner scanner = new Scanner(line);
            scanner.useDelimiter(";");

            dataset = scanner.next();
            path = scanner.next();
            algoritmo = scanner.next();
            attributeEvaluator = scanner.next();
            searchMethod = scanner.next();
            roc = Double.parseDouble(scanner.next().replaceAll(",", "."));
            precision = Double.parseDouble(scanner.next().replaceAll(",", "."));
            recall = Double.parseDouble(scanner.next().replaceAll(",", "."));
            fMeasure = Double.parseDouble(scanner.next().replaceAll(",", "."));
            
            if ((aoOptions == null || algoritmo.equals(aoOptions)) && (aeOptions == null || attributeEvaluator.equals(aeOptions)) && (smOptions == null || searchMethod.equals(smOptions))) {
                Element e = new Element(dataset, algoritmo, attributeEvaluator, searchMethod, roc, precision, recall, fMeasure);
                e.setPath(path);
                //loadFeatures(e, dataset, algoritmo, attributeEvaluator, searchMethod);
                if (hash != null && hash.containsKey(dataset)) {// Verificando se o vetor em questão possui atributos
                    e.features = hash.get(dataset);
                    e.amountFeatures = hash.get(dataset).size();
                    this.elements.add(e);
                }else{
                    e.features = null;
                    e.amountFeatures = 0;
                    this.elements.add(e);
                }
            }
        }
        if(hash != null) hash.clear();
    }

    public void loadFeatures2(Element element, String d, String a, String ae, String sm) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.features));
        String line = null;
        String dataset = null;
        String algoritmo = null;
        String attributeEvaluator = null;
        String searchMethod = null;
        String feature = null;

        Scanner scanner;

        while (br.ready() && (!d.equals(dataset) || !a.equals(algoritmo) || !ae.equals(attributeEvaluator) || !sm.equals(searchMethod))) {
            line = br.readLine();
            scanner = new Scanner(line);
            scanner.useDelimiter(";");
            dataset = scanner.next();
            algoritmo = scanner.next();
            attributeEvaluator = scanner.next();
            searchMethod = scanner.next();
            feature = scanner.next();
        }

        if (!br.ready()) {
            System.out.println("Problema com o arquivo de atributos!!! - " + d + " " + dataset);
            return;
        }

        while (br.ready() && d.equals(dataset) && a.equals(algoritmo) && ae.equals(attributeEvaluator) && sm.equals(searchMethod)) {
            element.addFeature(feature);

            line = br.readLine();
            scanner = new Scanner(line);
            scanner.useDelimiter(";");
            dataset = scanner.next();
            algoritmo = scanner.next();
            attributeEvaluator = scanner.next();
            searchMethod = scanner.next();
            feature = scanner.next();
        }

        if (d.equals(dataset) && a.equals(algoritmo) && ae.equals(attributeEvaluator) && sm.equals(searchMethod)) {
            element.addFeature(feature);
        }

    }

    public HashMap<String, ArrayList<String>> loadFeatures(String a, String ae, String sm) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.features));

        HashMap<String, ArrayList<String>> hash = new HashMap<String, ArrayList<String>>();

        Scanner scanner;

        String line = null;
        String dataset = null;
        String algoritmo = null;
        String attributeEvaluator = null;
        String searchMethod = null;
        String feature = null;

        while (br.ready()) {
            line = br.readLine();
            scanner = new Scanner(line);
            scanner.useDelimiter(";");
            dataset = scanner.next();
            algoritmo = scanner.next();
            attributeEvaluator = scanner.next();
            searchMethod = scanner.next();
            scanner.next(); // Gastando uma informação do arquivo que não é necessária
            feature = scanner.next();

            if (a.equals(algoritmo) && ae.equals(attributeEvaluator) && sm.equals(searchMethod)) {
                String key = dataset;
                if (hash.containsKey(key)) {
                    hash.get(key).add(feature);
                } else {
                    hash.put(key, new ArrayList<String>());
                    hash.get(key).add(feature);
                }
            }
        }

        return hash;
    }

    public void initialize(AlgorithmsOptions aoOptions, AttributeEvaluatorOptions aeOptions, SearchMethodOptions smOptions) throws FileNotFoundException, IOException, Exception {
        String ao = null;
        String ae = null;
        String sm = null;

        if (aoOptions == AlgorithmsOptions.DecisionTable) {
            ao = "DecisionTable";
        }
        if (aoOptions == AlgorithmsOptions.J48) {
            ao = "J48";
        }
        if (aoOptions == AlgorithmsOptions.NaiveBayes) {
            ao = "NaiveBayes";
        }
        if (aoOptions == AlgorithmsOptions.RandomForest) {
            ao = "RandomForest";
        }
        if (aoOptions == AlgorithmsOptions.SimpleLogistic) {
            ao = "SimpleLogistic";
        }

        if (aeOptions == AttributeEvaluatorOptions.CFS) {
            ae = "CFS";
        }
        if (aeOptions == AttributeEvaluatorOptions.InfoGain) {
            ae = "InfoGain";
        }

        if (smOptions == SearchMethodOptions.BestFirst) {
            sm = "BestFirst";
        }
        if (smOptions == SearchMethodOptions.GeneticSearch) {
            sm = "GeneticSearch";
        }
        if (smOptions == SearchMethodOptions.GreedyStepwise) {
            sm = "GreedyStepwise";
        }
        if (smOptions == SearchMethodOptions.Ranker) {
            sm = "Ranker";
        }

        //initializeElements(this.datasets, ao, ae, sm);
        initializeElements(ao, ae, sm);
    }

    public void calculateCohesion() {
        if (this.clusters == null) {
            System.out.println("Nenhum cluster foi formado!");
            return;
        }

        Set<Integer> keys = this.clusters.keySet();

        for (Integer k : keys) {
            this.clusters.get(k).calculateCohesion();
        }
    }

    public void clusterList() {
        if (this.clusters == null) {
            System.out.println("Nenhum cluster foi formado");
            return;
        }

        Set<Integer> listKey = this.clusters.keySet();

        for (Integer key : listKey) {
            System.out.println("      ** Cluster " + key + ": **\n");
            System.out.println("Centroide: " + this.clusters.get(key).getCentroid());
            ArrayList<Element> e = this.clusters.get(key).getVectors();

            for (int j = 0; j < e.size(); j++) {
                System.out.println("Dataset: " + e.get(j).getDataset() + "\n");
                System.out.println("Algoritmo: " + e.get(j).getAlgoritmo() + "\n");
                System.out.println("Attribute Evaluator: " + e.get(j).getAttributeEvaluator() + "\n");
                System.out.println("Search Method: " + e.get(j).getSearchMethod() + "\n");
                System.out.println("Roc: " + e.get(j).getRoc() + "\n");
                System.out.println("Distancia: " + e.get(j).distance + "\n\n\n");
            }
        }
    }

    public void writeResultsClusters(File output) throws IOException {
        if (this.clusters == null) {
            System.out.println("Nenhum cluster foi formado!");
            return;
        }

        DecimalFormat df = new DecimalFormat("#.000000");

        FileWriter fw = new FileWriter(output);

        fw.write("ClustersFormados:\n");
        fw.write("Cluster;Centroide;Coesao\n");

        for (Integer key : this.clusters.keySet()) {
            Cluster cluster = this.clusters.get(key);
            fw.write(key + ";" + df.format(cluster.getCentroid()) + ";" + df.format(cluster.getCohesion()) + "\n");
        }
        
        df = new DecimalFormat("#.000");

        fw.write("\n\n\n");
        fw.write("Cluster;Dataset;Algoritmo;AttributeEvaluator;SearchMethod;Roc\n");

        for (Integer key : this.clusters.keySet()) {
            Cluster cluster = this.clusters.get(key);
            for (Element e : cluster.getVectors()) {
                fw.write(key + ";" + e.getDataset() + ";" + e.getAlgoritmo() + ";"
                        + e.getAttributeEvaluator() + ";" + e.getSearchMethod() + ";" + df.format(e.getRoc()) + "\n");
            }
            fw.write("\n");
        }
        fw.flush();
    }
    
    public void writeResultsSubClusters(File output) throws IOException {
        if (this.subClusters == null) {
            System.out.println("Nenhum sub-cluster foi formado!");
            return;
        }

        DecimalFormat df = new DecimalFormat("#.000000");

        FileWriter fw = new FileWriter(output);

        fw.write("ClustersFormados:\n");
        fw.write("Cluster;Centroide;Coesao\n");

        for (Double key : this.subClusters.keySet()) {
            Cluster cluster = this.subClusters.get(key);
            fw.write(key + ";" + df.format(cluster.getCentroid()) + ";" + df.format(cluster.getCohesion()) + "\n");
        }
        
        df = new DecimalFormat("#.000");

        fw.write("\n\n\n");
        fw.write("Cluster;Dataset;Algoritmo;AttributeEvaluator;SearchMethod;Roc\n");

        for (Double key : this.subClusters.keySet()) {
            Cluster cluster = this.subClusters.get(key);
            for (Element e : cluster.getVectors()) {
                fw.write(key + ";" + e.getDataset() + ";" + e.getAlgoritmo() + ";"
                        + e.getAttributeEvaluator() + ";" + e.getSearchMethod() + ";" + df.format(e.getRoc()) + "\n");
            }
            fw.write("\n");
        }
        fw.flush();
    }

    public void compareFeatures(Integer min, Integer max, File output) throws IOException {
        Element firstElement = null;
        int maximumFeatures;
        boolean foundGrouping = false;

        FileWriter fw = new FileWriter(output);

        Set<Integer> keys = this.clusters.keySet();

        for (Integer key : keys) {
            Cluster cluster = this.clusters.get(key);

            maximumFeatures = Integer.MIN_VALUE;
            /** Procurando o vetor com mais features **/
            for (Element element : cluster.getVectors()) {
                if (element.getAmountFeatures() > maximumFeatures) {
                    maximumFeatures = element.getAmountFeatures();
                    firstElement = element;
                }
            }

            foundGrouping = false;
            int equalAmountFeatures = 0;
            for (int i = max; i >= min && foundGrouping == false; i--) {
                // Comparando o primeiro vetor com todo mundo    
                for (Element element : cluster.getVectors()) {
                    if (element != firstElement) {
                        for (String feature : element.getFeatures()) {
                            if (firstElement.getFeatures().contains(feature)) {
                                equalAmountFeatures++;
                            }
                        }

                        if (equalAmountFeatures >= i && foundGrouping == false) {
                            fw.write("Cluster" + key + "\n");
                            fw.write("Agrupamento/com/" + i + "/features/em/comum\n\n");
                            fw.write(firstElement.getDataset() + "/" + element.getDataset() + "\n");
                            foundGrouping = true;
                        } else if (equalAmountFeatures >= i && foundGrouping == true) {
                            fw.write(firstElement.getDataset() + "/" + element.getDataset() + "\n");
                        }
                        equalAmountFeatures = 0;
                    }
                }
                //
                // Comparando todo mundo com todo mundo
                for (int j = 0; j < cluster.getQtdVectors(); j++) {
                    Element e1 = cluster.getVectors().get(j);
                    if (e1 != firstElement) {
                        for (int k = j + 1; k < cluster.getQtdVectors(); k++) {
                            Element e2 = cluster.getVectors().get(k);
                            if (e2 != firstElement) {
                                for (String feature : e1.getFeatures()) {
                                    if (e2.getFeatures().contains(feature)) {
                                        equalAmountFeatures++;
                                    }
                                }

                                if (equalAmountFeatures >= i && foundGrouping == false) {
                                    fw.write("Cluster" + key + "\n");
                                    fw.write("Agrupamento/com/" + i + "/features/em/comum\n\n");
                                    fw.write(e1.getDataset() + "/" + e2.getDataset() + "\n");
                                    foundGrouping = true;
                                } else if (equalAmountFeatures >= i && foundGrouping == true) {
                                    fw.write(e1.getDataset() + "/" + e2.getDataset() + "\n");
                                }
                                equalAmountFeatures = 0;
                            }
                        }
                    }
                }
            }
            fw.write("\n");
        }

        fw.flush();
    }
    
    public boolean groupAlreadyFormed(ArrayList<String> list, String grouping){
        for(String s: list){
            if(s.equals(grouping) || s.contains(grouping))
                return true;
        }
        
        return false;
    }
    
    public HashMap<Double,Cluster> compareFeatures() {
        HashMap<Double,Cluster> subClusters = new HashMap<Double,Cluster>();
        HashMap<String,ArrayList<Element>> clustersFormed = new HashMap<String,ArrayList<Element>>();
        ArrayList<Element> clusteredElements = new ArrayList<Element>();
        Cluster cluster;
        Element e1, e2;
        String commonFeatures;
        Scanner read = new Scanner(System.in);
        Scanner scanner;
        Integer maximumFeatures = Integer.MIN_VALUE;
        Integer min, max, qtdFeatures;
        double a = 1.0, b = 0.1;
        
        try{
            System.out.println("Limite inferior: ");
            min = read.nextInt();
            System.out.println("Limite superior: ");
            max = read.nextInt();
            
            for(Integer key: this.clusters.keySet()){
                cluster = this.clusters.get(key);
                a += 1.0;
                b = 0.1;
                
                for(int i = max; i >= min; i--){
                    
                    for(int j = 0; j < cluster.getQtdVectors(); j++){
                        e1 = cluster.getVectors().get(j);
                        if(e1.getAmountFeatures() >= i && !clusteredElements.contains(e1)){
                            for(int k = j + 1; k < cluster.getQtdVectors(); k++){
                                e2 = cluster.getVectors().get(k);
                                if(e2.getAmountFeatures() >= i && !clusteredElements.contains(e2)){
                                    commonFeatures = "";
                                    qtdFeatures = 0;
                                    for(String s: e1.getFeatures()){
                                        if(qtdFeatures < i && e2.getFeatures().contains(s)){
                                            if(commonFeatures.equals("")) commonFeatures += s;
                                            else commonFeatures += "#" + s;
                                            qtdFeatures++;
                                        }
                                        if(qtdFeatures == i) break;
                                    }
                                    
                                    if(qtdFeatures >= i){
                                        if(!clusteredElements.contains(e1)) clusteredElements.add(e1);
                                        clusteredElements.add(e2);
                                        if(clustersFormed.containsKey(commonFeatures)){
                                            if(!clustersFormed.get(commonFeatures).contains(e1))
                                                clustersFormed.get(commonFeatures).add(e1);
                                            clustersFormed.get(commonFeatures).add(e2);
                                        }else{
                                            clustersFormed.put(commonFeatures, new ArrayList<Element>());
                                            clustersFormed.get(commonFeatures).add(e1);
                                            clustersFormed.get(commonFeatures).add(e2);
                                        }
                                    }
                                    commonFeatures = "";
                                    qtdFeatures = 0;
                                    
                                }
                            }
                            
                        }
                    }
                    
                    for(String features: clustersFormed.keySet()){
                        ArrayList<String> list = new ArrayList<String>();
                        scanner = new Scanner(features);
                        scanner.useDelimiter("#");
                        while(scanner.hasNext()){
                            list.add(scanner.next());
                        }
                        Cluster c = new Cluster();
                        c.setCommonFeatures(list);
                        for(Element e: clustersFormed.get(features)){
                            c.addVector(e);
                        }
                        System.out.println("\n***\n" + (key+b) + "\n" + list.size() + "\n" + c.getQtdVectors());
                        
                        c.recalculateCentroid();
                        c.recalculateDistanceVectors();
                        c.calculateCohesion();
                        
                        subClusters.put(key+b, c);
                        System.out.println(subClusters.get(key+b).getCommonFeatures());
                        
                        b += 0.1;
                    }
                    clustersFormed.clear();
                }
            }
            
        }catch(Exception e){
            System.out.println("Erro durante o refinamento das features!");
        }finally{
            return subClusters;
        }
    }
    
    public void compareFeatures(File resultCompareFeatures) throws IOException {
        int max, min, opCluster;
        Scanner read = new Scanner(System.in);

        System.out.println("Comparar Features\n\n");
        System.out.println("Chave do cluster: ");

        opCluster = read.nextInt();

        if (!this.clusters.containsKey(opCluster)) {
            System.out.println("O cluster que foi informado não existe!");
            return;
        }

        System.out.println("Limite inferior: ");
        min = read.nextInt();
        System.out.println("Limite superior: ");
        max = read.nextInt();
        
        ArrayList<String> groupsAlreadyFormed = new ArrayList<String>(); /* Armazena todos os grupos que foram formados, evitando que um mesmo grupo seja formado mais de uma vez */
        HashMap<String,ArrayList<String>> groups = new HashMap<String,ArrayList<String>>();
        Set<String> keys;
        //ArrayList<String> groupsAlreadyFormed2 = new ArrayList<String>(); /* Armazena todos os grupos com exatamente 2 vetores */
        Cluster cluster = this.clusters.get(opCluster);
        String features = "";
        Element firstElement = null;
        int maximumFeatures;
        boolean foundGrouping = false;
        boolean wroteHeader = false;
        
        FileWriter fwResultsCompareFeatures = new FileWriter(resultCompareFeatures);
        fwResultsCompareFeatures.write("Cluster" + opCluster + "\n\n");

        maximumFeatures = Integer.MIN_VALUE;
        /** Procurando o vetor com mais features **/
        for (Element element : cluster.getVectors()) {
            if (element.getAmountFeatures() > maximumFeatures) {
                maximumFeatures = element.getAmountFeatures();
                firstElement = element;
            }
        }

        wroteHeader = false;
        int equalAmountFeatures = 0;
        for (int i = max; i >= min; i--) {
            wroteHeader = false;
            // Comparando o primeiro vetor com todo mundo    
            for (Element element : cluster.getVectors()) {
                if (element != firstElement) {
                    for (String feature : element.getFeatures()) {
                        if (firstElement.getFeatures().contains(feature)) {
                            equalAmountFeatures++;
                            if(equalAmountFeatures <= i)
                                features += feature;
                        }
                    }
                    
                    if(equalAmountFeatures >= i){
                        if(groups.containsKey(features) && !groups.get(features).contains(element.getDataset())){
                            groups.get(features).add(element.getDataset());
                            foundGrouping = true;
                        }else if(!groups.containsKey(features)) {
                            groups.put(features, new ArrayList<String>());
                            groups.get(features).add(firstElement.getDataset());
                            groups.get(features).add(element.getDataset());
                            foundGrouping = true;
                        }
                    }
                    equalAmountFeatures = 0;
                    features = "";
                }
            }
            /*
            keys = groups.keySet();
            for(String key: keys){
                ArrayList<String> g = groups.get(key);
                String s = "";
                if(!foundGrouping){
                    System.out.println("\n\nGrupos formados com " + i + " features em comum\n\n");
                    foundGrouping = true;
                }
                for(int k = 0; k < g.size(); k++){
                    if(k == 0)
                        s = g.get(k);
                    else
                        s = s + " ; " + g.get(k);
                }
                if(!groupsAlreadyFormed.contains(s)){
                    System.out.println(s);
                    groupsAlreadyFormed.add(s);
                }
            }
            
            groups.clear();
            groups = new HashMap<String, ArrayList<String>>();*/
            
            //
            // Comparando todo mundo com todo mundo
            for (int j = 0; j < cluster.getQtdVectors(); j++) {
                Element e1 = cluster.getVectors().get(j);
                if (e1 != firstElement) {
                    for (int k = j + 1; k < cluster.getQtdVectors(); k++) {
                        Element e2 = cluster.getVectors().get(k);
                        if (e2 != firstElement) {
                            for (String feature : e1.getFeatures()) {
                                if (e2.getFeatures().contains(feature)) {
                                    equalAmountFeatures++;
                                    if(equalAmountFeatures <= i)
                                        features += feature;
                                }
                            }
                            
                            if(equalAmountFeatures >= i){
                                if(groups.containsKey(features) && !groups.get(features).contains(e2.getDataset())){
                                    groups.get(features).add(e2.getDataset());
                                    foundGrouping = true;
                                }else if(!groups.containsKey(features)) {
                                    groups.put(features, new ArrayList<String>());
                                    groups.get(features).add(e1.getDataset());
                                    groups.get(features).add(e2.getDataset());
                                    foundGrouping = true;
                                }
                            }
                            equalAmountFeatures = 0;
                            features = "";
                        }
                    }

                    /*keys = groups.keySet();
                    for(String key: keys){
                        ArrayList<String> g = groups.get(key);
                        String s = "";
                        if(!foundGrouping){
                            System.out.println("\n\nGrupos formados com " + i + " features em comum\n\n");
                            foundGrouping = true;
                        }
                        for(int k = 0; k < g.size(); k++){
                            if(k == 0)
                                s = g.get(k);
                            else
                                s = s + " ; " + g.get(k);
                        }
                        if(!groupsAlreadyFormed.contains(s)){
                            System.out.println(s);
                            groupsAlreadyFormed.add(s);
                        }
                    }
                    groups.clear();
                    groups = new HashMap<String,ArrayList<String>>();*/
                }
            }
            keys = groups.keySet();
            if(foundGrouping){
                    for(String key: keys){
                        ArrayList<String> g = groups.get(key);
                        String s = "";
                        if(!wroteHeader){
                            fwResultsCompareFeatures.write("Grupo_formado_com_" + i + "_features_em_comum\n\n");
                            System.out.println("\n\nGrupos formados com " + i + " features em comum\n\n");
                            wroteHeader = true;
                        }
                        for(int k = 0; k < g.size(); k++){
                            if(k == 0)
                                s = g.get(k);
                            else
                                s = s + ";" + g.get(k);
                        }
                        if(!groupsAlreadyFormed.contains(s)){
                            fwResultsCompareFeatures.write(s + "\n");
                            System.out.println("Agrupamento entre: " + s);
                            groupsAlreadyFormed.add(s);
                        }
                    }
                    foundGrouping = false;
                    groups.clear();
                    //groups = new HashMap<String,ArrayList<String>>();
            }else{
                fwResultsCompareFeatures.write("\nNao_foram_formados_grupos_com_" + i + "_fetures_em_comum!\n");
            }
            fwResultsCompareFeatures.flush();
        }
    }
    
    public Map<Integer,Cluster> getClusters(){
        return this.clusters;
    }
    
    public File[] getDatasets(){
        return this.datasets;
    }
    
    public AlgorithmsOptions getAlgorithmsOptions(){
        return this.aOptions;
    }
    
    public AttributeEvaluatorOptions getAttributeEvaluatorOptions(){
        return this.aeOptions;
    }
    
    public SearchMethodOptions getSearchMethodOptions(){
        return this.smOptions;
    }

    public HashMap<Double, Cluster> getSubClusters() {
        return subClusters;
    }

    public void setSubClusters(HashMap<Double, Cluster> subClusters) {
        this.subClusters = subClusters;
    }
    
    
    
    public int getAmountOfDatasetsClustered(){
        int counter = 0;
        
        for(Integer key: this.getClusters().keySet()){
            Cluster cluster = this.getClusters().get(key);
            counter += cluster.getQtdVectors();
        }
        return counter;
    }
            
    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        File[] datasets = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_teste/saida/arffTeste").listFiles();
        /** Arquivo contendo o valor de ROC AREA **/
        File file = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_reuniao_23.02.15/arquivos_roc.csv");
        /** Arquivo para armazenar os resultados da clusterização **/
        File results = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_reuniao_23.02.15/resultados_clusters.csv");
        /** Arquivo contendo os atributos selecionados **/
        File features = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_reuniao_23.02.15/atributos.csv");
        /** Arquivo para armazenar os resultados da comparação de features **/
        File resultCompareFeatures = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_reuniao_23.02.15/CompareFeatures.csv");
        /** Arquivo para armazenar os resultados das simulações com o cluster **/
        File simulationResult = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_reuniao_23.02.15/SimulationResult.csv");
        
        Scanner read = new Scanner(System.in);
        
        Clusteriza clusteriza = null;
        
        double toleranceValue = 0.034;
        int qtdMaxCluster = 10000000;
        
        System.out.println("Deseja aplicar CLASSIFICAÇÃO com FEATURE SELECTION? 0 - Não 1 - Sim");
        if(read.nextInt() == 1){ // Com feature selection
            clusteriza = new Clusteriza(AlgorithmsOptions.NaiveBayes, AttributeEvaluatorOptions.CFS, SearchMethodOptions.GeneticSearch, datasets, file, features, AttributesForClustering.Roc);
            //clusteriza = new Clusteriza(AlgorithmsOptions.RandomForest, datasets, file, AttributesForClustering.Roc);

            clusteriza.clusters = new BSAS().runBSAS(clusteriza.elements, toleranceValue, qtdMaxCluster);
            clusteriza.calculateCohesion();

            //clusteriza.clusterList();
            clusteriza.writeResultsClusters(results);

            // Os valores 1 e 5 é o intervalo com o qual o programa vai procurar features em comum //
            //clusteriza.compareFeatures(1, 50, resultCompareFeatures);
            System.out.println("Deseja aplicar o refinamento das features? 0 - Não 1 - Sim");
            if(read.nextInt() == 1){
                clusteriza.subClusters = clusteriza.compareFeatures();
                clusteriza.writeResultsSubClusters(resultCompareFeatures);
                //clusteriza.compareFeatures(resultCompareFeatures);
            }

            System.out.println("Deseja gerar simulação com os clusters? 0 - Não 1 - Sim");
            if(read.nextInt() == 1)
                new InformationGroups().generateInformationGroups(clusteriza, simulationResult);
        }else{ // Sem feature selection
            
            //clusteriza = new Clusteriza(AlgorithmsOptions.NaiveBayes, AttributeEvaluatorOptions.CFS, SearchMethodOptions.GeneticSearch, datasets, file, features, AttributesForClustering.Roc);
            clusteriza = new Clusteriza(AlgorithmsOptions.NaiveBayes, datasets, file, AttributesForClustering.Roc);

            clusteriza.clusters = new BSAS().runBSAS(clusteriza.elements, toleranceValue, qtdMaxCluster);
            clusteriza.calculateCohesion();

            //clusteriza.clusterList();
            clusteriza.writeResultsClusters(results);
            
        }
        
        System.out.println("Deseja aplicar cross-project prediction? 0 - Não 1 - Sim");
        if(read.nextInt() == 1){
            /** Diretório contendo todos os arquivos ARFF's (Pra esse caso, sem os atributos PROJECT e FILE e com o atributo classe igual para todos os arquivos **/
            File directory = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_teste/saida/ARFF");
            /** Diretório onde será armazenado os arquivos selecionados para teste **/
            File directoryTests = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_reuniao_23.02.15/tests");
            /** Diretório onde será armazenado os arquivos selecionados para treino **/
            File directoryTrains = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_reuniao_23.02.15/trains");
            /** Arquivo onde será armazenado o arquivo CSV com os resultados da execução **/
            File resultsCrossValidation = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_teste/saida/resultCrossValidation.csv");
            /** Arquivo que vai conter os arquivos selecionados como treino para cada cluster **/
            File relation = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_teste/saida/filesTrain.csv");
            new crossValidation().run(clusteriza.clusters, AlgorithmsOptions.NaiveBayes, AttributeEvaluatorOptions.CFS, SearchMethodOptions.GeneticSearch, directory, directoryTests, directoryTrains, resultsCrossValidation, relation);
        }
    }
}

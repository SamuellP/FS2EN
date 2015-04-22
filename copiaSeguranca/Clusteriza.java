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
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author samuel
 */
public final class Clusteriza {

    private ArrayList<Element> elements;
    private HashMap<Integer, Cluster> clusters;
    public AttributeEvaluatorOptions aeOptions; // opções de avaliadores de atributo - para clusterizar
    public SearchMethodOptions smOptions;// opções de pesquisa de atributo - para clusterizar 
    File file;
    File features;

    Clusteriza(AlgorithmsOptions aoOptions, AttributeEvaluatorOptions aeOptions, SearchMethodOptions smOptions, File file, File features) throws FileNotFoundException, IOException {
        this.elements = new ArrayList<Element>();
        this.clusters = null;
        this.aeOptions = aeOptions;
        this.smOptions = smOptions;
        this.file = file;
        this.features = features;

        initialize(aoOptions, aeOptions, smOptions);
    }

    public void initializeElements(String aoOptions, String aeOptions, String smOptions) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.file));
        String line;
        String dataset;
        String algoritmo;
        String attributeEvaluator;
        String searchMethod;
        double roc;

        BufferedReader brFeatures = new BufferedReader(new FileReader(this.features));

        HashMap<String, ArrayList<String>> hash = loadFeatures(aoOptions, aeOptions, smOptions);

        while (br.ready()) {
            line = br.readLine();
            Scanner scanner = new Scanner(line);
            scanner.useDelimiter(";");

            dataset = scanner.next();
            algoritmo = scanner.next();
            attributeEvaluator = scanner.next();
            searchMethod = scanner.next();
            roc = Double.parseDouble(scanner.next().replaceAll(",", "."));

            if ((aoOptions == null || algoritmo.equals(aoOptions)) && (aeOptions == null || attributeEvaluator.equals(aeOptions)) && (smOptions == null || searchMethod.equals(smOptions))) {
                Element e = new Element(dataset, algoritmo, attributeEvaluator, searchMethod, roc);
                //loadFeatures(e, dataset, algoritmo, attributeEvaluator, searchMethod);
                if (hash.containsKey(dataset)) {// Verificando se o vetor em questão possui atributos
                    e.features = hash.get(dataset);
                    e.amountFeatures = hash.get(dataset).size();
                    this.elements.add(e);
                }
            }
        }
        hash.clear();
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

    public void initialize(AlgorithmsOptions aoOptions, AttributeEvaluatorOptions aeOptions, SearchMethodOptions smOptions) throws FileNotFoundException, IOException {
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

    public void writeResults(File output) throws IOException {
        if (this.clusters == null) {
            System.out.println("Nenhum cluster foi formado!");
            return;
        }

        DecimalFormat df = new DecimalFormat("#.000000");

        FileWriter fw = new FileWriter(output);

        fw.write("ClustersFormados:\n");
        fw.write("Cluster;Centroide;Coesão\n");

        for (Integer key : this.clusters.keySet()) {
            Cluster cluster = this.clusters.get(key);
            fw.write(key + ";" + df.format(cluster.getCentroid()) + ";" + df.format(cluster.getCohesion()) + "\n");
        }

        fw.write("\n\n\n");
        fw.write("Cluster;Dataset;Algoritmo;AttributeEvaluator;SearchMethod;Roc\n");

        for (Integer key : this.clusters.keySet()) {
            Cluster cluster = this.clusters.get(key);
            for (Element e : cluster.getVectors()) {
                fw.write(key + ";" + e.getDataset() + ";" + e.getAlgoritmo() + ";"
                        + e.getAttributeEvaluator() + ";" + e.getSearchMethod() + ";" + e.getRoc() + "\n");
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
    
    public void compareFeatures() {
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
        String grouping = ""; /* Armazena grupos com 2 ou mais vetores */
        String features = "";
        Element firstElement = null;
        int maximumFeatures;
        boolean foundGrouping = false;
        boolean wroteHeader = false;

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
                        foundGrouping = true;
                        if(groups.containsKey(features) && !groups.get(features).contains(element.getDataset()))
                            groups.get(features).add(element.getDataset());
                        else if(!groups.containsKey(features)) {
                            groups.put(features, new ArrayList<String>());
                            groups.get(features).add(firstElement.getDataset());
                            groups.get(features).add(element.getDataset());
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
                                foundGrouping = true;
                                if(groups.containsKey(features) && !groups.get(features).contains(e2.getDataset()))
                                    groups.get(features).add(e2.getDataset());
                                else if(!groups.containsKey(features)) {
                                    groups.put(features, new ArrayList<String>());
                                    groups.get(features).add(e1.getDataset());
                                    groups.get(features).add(e2.getDataset());
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
                            System.out.println("\n\nGrupos formados com " + i + " features em comum\n\n");
                            wroteHeader = true;
                        }
                        for(int k = 0; k < g.size(); k++){
                            if(k == 0)
                                s = g.get(k);
                            else
                                s = s + " ; " + g.get(k);
                        }
                        if(!groupsAlreadyFormed.contains(s)){
                            System.out.println("Agrupamento entre: " + s);
                            groupsAlreadyFormed.add(s);
                        }
                    }
                    foundGrouping = false;
                    groups.clear();
                    //groups = new HashMap<String,ArrayList<String>>();
            }
        }
    }
            
    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        /** Arquivo contendo o valor de ROC AREA **/
        File file = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_reuniao_13.01.15/arquivos_roc.csv");
        /** Arquivo para armazenar os resultados da clusterização **/
        File results = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_reuniao_13.01.15/resultados_clusters.csv");
        /** Arquivo contendo os atributos selecionados **/
        File features = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_reuniao_13.01.15/atributos.csv");
        /** Arquivo para armazenar os resultados da comparação de features **/
        //File resultCompareFeatures = new File("/home/samuel/Documentos/BCC/Projeto/arquivos_reuniao_13.01.15/CompareFeatures.csv");

        Clusteriza clusteriza = new Clusteriza(AlgorithmsOptions.NaiveBayes, AttributeEvaluatorOptions.CFS, SearchMethodOptions.GeneticSearch, file, features);
        
        double toleranceValue = 0.034;
        int qtdMaxCluster = 10000000;

        clusteriza.clusters = new BSAS().runBSAS(clusteriza.elements, toleranceValue, qtdMaxCluster);
        clusteriza.calculateCohesion();

        //clusteriza.clusterList();
        clusteriza.writeResults(results);

        // Os valores 1 e 5 é o intervalo com o qual o programa vai procurar features em comum //
        //clusteriza.compareFeatures(1, 50, resultCompareFeatures);
        Scanner read = new Scanner(System.in);
        System.out.println("Deseja aplicar o refinamento das features? 0 - Não 1 - Sim");
        if(read.nextInt() == 1)
            clusteriza.compareFeatures();
        
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

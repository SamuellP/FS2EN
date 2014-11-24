/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

import java.util.Random;
import weka.core.Instances;
import weka.core.Instance;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.trees.J48;
import weka.classifiers.Evaluation;

/**
 *
 * @author RicardoSatinVostro
 */
public class classifyJ48 {

    public String caminhoDados;
     
    // As instancias do dado
    public Instances dados;
 
    public classifyJ48(String caminhoDados) {
        this.caminhoDados = caminhoDados;
    }
 
    public void leDados() throws Exception {
 
        DataSource fonte = new DataSource(caminhoDados);
        dados = fonte.getDataSet();
 
        // seta o atributo classe caso o formato de dados nao contenha essa informacao
        if (dados.classIndex() == -1)
            dados.setClassIndex(dados.numAttributes() - 1);
    }
 
    public void imprimeDados() {
 
        // Imprime cada instância (linha) dos dados
        System.out.println("Imprimindo todas as instâncias:" + "\n");
        
        for (int i = 0; i < dados.numInstances(); i++) {
            Instance atual = dados.instance(i);
            System.out.println((i + 1) + ": " + atual);
        }
        
        System.out.println("Fim da impressão das instâncias:" + "\n");
    }
 
    
    public void classifyJ48() throws Exception {
        
        // Cria uma nova instancia da arvore
        J48 tree = new J48();
        
        // Constrói classificador
        tree.buildClassifier(dados);
        System.out.println("Summary: " + "\n" + dados.toSummaryString());
 
        // Imprime a arvore
        System.out.println("Preparando para Imprimir Árvore");
        System.out.println(tree);
        
        // Avalia o resultado
        System.out.println("Mostrando dados da Classificação: \n");
        Evaluation avaliacao;
        avaliacao = new Evaluation(dados);
        avaliacao.evaluateModel(tree, dados);                
        System.out.println(avaliacao.toSummaryString("\nResults\n======\n", true));
        System.out.println(avaliacao.toClassDetailsString());
        System.out.println(avaliacao.toMatrixString());
        System.out.println("Instancias corretas: " + avaliacao.correct() + "\n");
        System.out.println("Instancias incorretas: " + avaliacao.incorrect() + "\n"); 
        
        System.out.println("FIM da classificação: \n");        
    }
    
    
    public void crossValidationJ48() throws Exception {
        // Cria uma nova instancia da arvore
        J48 tree = new J48();
        
        // Constrói classificador
        tree.buildClassifier(dados);
        System.out.println("Summary: " + "\n" + dados.toSummaryString());
 
        // Imprime a arvore
        System.out.println("Preparando para Imprimir Árvore");
        System.out.println(tree);
            
        // Avaliacao cruzada (cross-validation)
        System.out.println("Mostrando dados da Avaliacao Cruzada: \n");
        Evaluation avalCruzada;
        avalCruzada = new Evaluation(dados);
        avalCruzada.crossValidateModel(tree, dados, 10, new Random(1));
        System.out.println(avalCruzada.toSummaryString("\nResults\n======\n", true));
        System.out.println(avalCruzada.toClassDetailsString());
        System.out.println(avalCruzada.toMatrixString());
        System.out.println("Instancias corretas: " + avalCruzada.correct() + "\n");
        System.out.println("Instancias incorretas: " + avalCruzada.incorrect() + "\n");
          
        System.out.println("FIM: \n");
    }
    
}

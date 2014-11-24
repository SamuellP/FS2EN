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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Ricardo Satin 
 */
public class appendFile {
    public static void appendF(File[] entradas,File saida) throws IOException {
        /** HashMap contém como chave o campo @RELATION do arquivo e seus atributos como valores **/
        HashMap<String,ArrayList<String>> hash = new HashMap<String,ArrayList<String>>();
        ArrayList<String> novo = null; // Contem os atributos da intersecção de todos os arquivos
        ArrayList<String> list = null;// Contem os atributos de um determinado arquivo
        ArrayList<Integer> vetor = null;// Contem a posição dos valores referentes a seus atributos
        ArrayList<String> dados = null;// Contem os valores de determinada linha de determinado arquivo
        
        FileReader input = null;
        FileWriter output = null;
        BufferedReader br = null;
        String linha = null;
        String relacao = null;
        String atributo = null;
        
        OperatorAttributeValues valoresAtributosClasse = new OperatorAttributeValues();// Armazena em um HashMap os valores dos atributos classes
        
        try{
            output = new FileWriter(saida);
            output.write("@RELATION novo\n\n");
            novo = new ArrayList<String>();
            
            for(int i = 0;i<entradas.length;i++){
                input = new FileReader(entradas[i]);
                br = new BufferedReader(input);
                
                relacao = br.readLine();
                atributo = br.readLine();
                hash.put(relacao,new ArrayList<String>());
                
                while(!atributo.equals("@DATA") && !atributo.equals("@data")){
                    if(atributo.length() > 0 && atributo.charAt(0) == '@'){
                        if(atributo.contains("{") && atributo.contains("}")){// É um atributo de classe
                            OperatorAttributes operadorAtributos = new OperatorAttributes(atributo); // Possui metodos para diferenciar a descrição do atributo dos valores do atributo
                            String retorno = operadorAtributos.getAttributeType();
                            hash.get(relacao).add(retorno + "{}");
                            int indice = novo.indexOf(retorno + "{}");
                            if(indice == -1){ // Atributo até então não existia
                                novo.add(retorno + "{}");
                                valoresAtributosClasse.putValues(retorno + "{}", operadorAtributos.getAttributeValue());
                            }else{ //Atributo já foi detectado em outros arquivos
                                valoresAtributosClasse.putValues(retorno + "{}", operadorAtributos.getAttributeValue());
                            }
                        }
                        else{ // Atributo não é um atributo classe
                            hash.get(relacao).add(atributo);
                            if(novo.contains(atributo) == false){
                                novo.add(0,atributo);
                            }
                        }
                        /*hash.get(relacao).add(atributo);
                        if(!novo.contains(atributo)){
                            if(atributo.contains("{") && atributo.contains("}"))
                                novo.add(atributo);
                            else
                                novo.add(0,atributo);
                            //output.write(atributo + "\n");
                        }*/
                    }
                    atributo = br.readLine();
                }
                
            }
            /** Escrevendo todos os atributos no novo arquivo **/
            for(int i=0;i<novo.size();i++){
                if(novo.get(i).contains("{}")){
                    output.write(novo.get(i).substring(0, novo.get(i).length() - 2) + "{" + valoresAtributosClasse.getValues(novo.get(i)) + "}\n");
                }
                else{
                    output.write(novo.get(i) + "\n");
                }
            }
            output.write("\n@DATA\n");
            
            int contador = 0; // Contador para percorrer todos os atributos
            list = null; // Lista contendo os atributos do estado atual
            vetor = new ArrayList<Integer>(); // vetor que define o que deve ser escrito no novo arquivo gerado

            for(int i = 0;i<entradas.length;i++){
                input = new FileReader(entradas[i]);
                br = new BufferedReader(input);
                
                relacao = br.readLine();
                list = hash.get(relacao);
                
                contador = 0;
                /** gerando um vetor para ligar as posições de atributos de um arquivo com as novas posições que o mesmo vai ocupar no novo arquivo **/
                while(contador < novo.size()){
                    vetor.add(list.indexOf(novo.get(contador)));
                    contador++;
                }
                
                /** organizando o ponteiro para que os dados comecem a ser lidos **/
                do{
                    linha = br.readLine();
                }while(linha != null && !linha.equals("@DATA") && !linha.equals("@data"));

                Scanner scanner = null;
                int j = 0;
                
                dados = new ArrayList<String>();
                
                /** Escrevendo os resultados no arquivo final **/
                while(br.ready()){
                    linha = br.readLine();
                    scanner = new Scanner(linha);
                    scanner.useDelimiter(",");
                    
                    /** lendo os dados e armanenando em um ArrayList **/
                    while(scanner.hasNext()){
                        dados.add(scanner.next());
                    }
                    
                    /** Escrevendo os dados em ordem no novo arquivo **/
                    while(j < vetor.size()){
                        if(vetor.get(j).equals(-1) && j == 0)
                            output.write("?");
                        else if(vetor.get(j).equals(-1))
                            output.write(",?");
                        else if(j == 0)
                            output.write(dados.get(vetor.get(j)));
                        else
                            output.write("," + dados.get(vetor.get(j)));
                        j++;
                    }
                    dados.clear();// Limpando os dados para que novos sejam inseridos
                    output.write("\n");
                    j = 0;
                }
                
                vetor.clear();
            }
            
        }finally{
            if (input != null)  {  
                input.close();
                input = null;
            }  
            if (output != null)    {  
                output.flush();  
                output.close();
                output = null;
            }
            if(novo != null){
                novo.clear();
                novo = null;
            }
            if(list != null){
                list.clear();
                list = null;
            }
            if(hash != null){
                hash.clear();
                hash = null;
            }
            if(vetor != null){
                vetor.clear();
                vetor = null;
            }
        }
        
    }
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author RicardoSatinVostro
 */
public class Clusteriza01 {

    public String[][] tabela = null;
    public String[] arquivos = null;
    public int code = 0;
    public String[][] cluster;
    public String chave;
    public final int nrFiles = 1268;//3; // nr de linhas no máximo que teremos, de arquivos para tratar - é o tamanho do nosso dataset processado - arquivos.csv
    public final int nrLinhas = 23345;//4; //nr de linhas que teremos - no máximo - de atributos para tratarmos - atributos.csv 
    public final int nrFeatures = 73;//4; // nr máximo de atributos que iremos ter no nosso arquivo (tem 73 ao todo, mas já reduzimos).    

    public AttributeEvaluatorOptions aeOptions; // opções de avaliadores de atributo - para clusterizar
    public SearchMethodOptions smOptions;// opções de pesquisa de atributo - para clusterizar       
        
    public Clusteriza01(AttributeEvaluatorOptions option1, SearchMethodOptions option2) {
        //this.tabela = new String[3][4];
        this.tabela    = new String[nrFiles][nrFeatures];
        this.arquivos  = new String[nrFiles];
        this.cluster   = new String[nrLinhas][2];
        this.aeOptions = option1;
        this.smOptions = option2;
    }

    /**
     *
     * @author Ricardo Satin teste de inicialização
     */
    public void inicializa() {
        this.tabela[0][0] = "arq01";
        this.tabela[0][1] = "atrib1";
        this.tabela[0][2] = "atrib2";
        this.tabela[0][3] = "atrib3";

        this.tabela[1][0] = "arq02";
        this.tabela[1][1] = "atrib1";
        this.tabela[1][2] = "atrib3";
        this.tabela[1][3] = "atrib5";

        this.tabela[2][0] = "arq03";
        this.tabela[2][1] = "atrib1";
        this.tabela[2][2] = "atrib2";
        this.tabela[2][3] = "atrib6";
        

        this.arquivos[0] = "arq01";
        this.arquivos[1] = "arq02";
        this.arquivos[2] = "arq03";
    }
    
    
    
    public void inicializaArquivo(String arq) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(arq));
            String str;
            int contador = 0;
            while (in.ready()) {
                str = in.readLine();
                this.arquivos[contador]=str;
                contador++;
            }
            in.close();
        } catch (IOException e) {
        }
    }

    public void inicializaCampos(String arq) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(arq));
            String linha = "", arquivo = "", arquivoN = "", feature = "", strAtrib = "", strSearch = "", aux = "";
            int contadorArquivo = 0, contadorFeature = 1, posicaoPV = 0;
            while (in.ready()) {
                linha = in.readLine();
                posicaoPV = linha.indexOf(";");
                arquivo = linha.substring(0, posicaoPV); // arquivo separado
                aux = linha.substring(posicaoPV + 1, linha.length()); // pego o resto da linha
                posicaoPV = aux.indexOf(";");
                feature = aux.substring(0, posicaoPV); // feature separado
                aux = aux.substring(posicaoPV + 1, aux.length()); // pego o resto da linha
                posicaoPV = aux.indexOf(";");
                strAtrib = aux.substring(0, posicaoPV); // feature separado
                strSearch = aux.substring(posicaoPV + 1, aux.length()); // pego o resto da linha                

                if (((strAtrib.equals(this.aeOptions.name())) || (this.aeOptions == AttributeEvaluatorOptions.All)) && (((strSearch.equals(this.smOptions.name())) || (this.smOptions == SearchMethodOptions.All)))) {

                    if (arquivoN == "") {
                    }
                    if (arquivoN.equals(arquivo)) {
                        this.tabela[contadorArquivo][0] = arquivoN;
                        this.tabela[contadorArquivo][contadorFeature] = feature;
                        contadorFeature++;
                    } else {
                        arquivoN = arquivo;
                        contadorArquivo++;
                        contadorFeature = 1;
                        this.tabela[contadorArquivo][0] = arquivoN;
                        this.tabela[contadorArquivo][contadorFeature] = feature;
                        contadorFeature++;
                    }
                }

            }
            in.close();
        } catch (IOException e) {
            System.out.println("xxx");
        } catch (ArrayIndexOutOfBoundsException ee) {
            System.out.println(ee.getMessage());
        }
    }
    
    public void listarArquivos() {
        for (int i = 0; i < this.arquivos.length; i++) {
            System.out.println(this.arquivos[i]);
        }
    }

    public void listaTabela() {
        for (int i = 0; i < this.tabela.length; i++) {
            for (int j = 0; j < this.tabela[i].length; j++) {
                System.out.println(this.tabela[i][j]);
            }
        }
    }

    public String[] getAtributos(String tab) {
        String campos[] = new String[nrFeatures];
        for (int i = 0; i < this.tabela.length; i++) {
            if ((this.tabela[i][0] != null) && (this.tabela[i][0].equals(tab))) {
                for (int k = 1; k < this.tabela[i].length; k++) {
                    if (this.tabela[i][k] != null) {
                        campos[k - 1] = this.tabela[i][k];
                    } else {
                        break;
                    }
                }
            }
        }
        return campos;
    }

    public boolean achouNoPai(String filho, String[] pai) {
        boolean achou = false;

        for (int i = 0; i < pai.length; i++) {
            if ((pai[i] != null) && (pai[i].equals(filho))) {
                achou = true;
            }
        }

        return achou;
    }

    public int getAssinatura(String str) {
        int total = 0;
        for (int i = 0; i < str.length(); i++) {
            total = total + (int) str.charAt(i);
        }
        return total;
    }

    public boolean clusterExiste(String str) {
        boolean Achou = false;

        for (int i = 0; i < this.cluster.length; i++) {
            if ((this.cluster[i][0] != null) && (this.cluster[i][0].equals(str))) {
                Achou = true;
            }
        }
        return Achou;
    }

    /**
     * 
     * @param indice - é o número de elementos que queremos clusterizar - ele procura por arquivos com o mesmo número em comum,não necessariamente os mesmos!
     */
    public void agrupa(int indice) {
        String camposPai[] = new String[nrFeatures];
        String camposFilho[] = new String[nrFeatures];
        String concatenacao = "", clusterpossivel = "";
        int local = 0;

        for (int i = 0; i < this.arquivos.length; i++) {
            camposPai = getAtributos(arquivos[i]);
            concatenacao = arquivos[i];
            for (int k = 0; k < this.tabela.length; k++) {
                if ((this.tabela[k][0] == null) || (this.tabela[k][0].equals(this.arquivos[i]))) //são o mesmo, desconsiderar
                {
                    continue;
                } else {
                    camposFilho = getAtributos(this.tabela[k][0]);
                    concatenacao = concatenacao + "; " + this.tabela[k][0];
                    for (int w = 0; w < camposFilho.length; w++) {
                        if (this.achouNoPai(camposFilho[w], camposPai)) {
                            local++;
                        }
                    }
                }
                if (local >= indice) {
                    clusterpossivel = concatenacao;
                    local=0;
                } else {
                    concatenacao = arquivos[i];
                    local = 0;
                }
            } // é depois deste for que crio um cluster com o arquivo da lista de arquivos que foi processado.

            if (clusterpossivel.equals("")) {
                continue;
            } else {
                chave = String.valueOf(getAssinatura(clusterpossivel));
                if (clusterExiste(chave)) {
                    concatenacao = "";
                    clusterpossivel = "";
                    local = 0;
                } else {
                    this.cluster[this.code][0] = chave;
                    this.cluster[this.code][1] = clusterpossivel;
                    //System.out.println(chave + " - " + concatenacao + " - " + i);
                    this.code++;
                    concatenacao = "";
                    clusterpossivel = "";
                    local = 0;
                }
            }
        }
    }

    public void listaCluster(){
        for (int l = 0; l < this.cluster.length; l++) {
            if (this.cluster[l][0] != null)            
                System.out.println("Clustar formado entre: " + this.cluster[l][0] + " - " + this.cluster[l][1]);
        }
    }
    
    public static void main(String[] args) throws IOException, Exception {       
        // Vamos inicializar a memória conforme parametros que estamos passando - o que queremos clusterizar
        Clusteriza01 cl = new Clusteriza01(AttributeEvaluatorOptions.CFS ,SearchMethodOptions.GeneticSearch);
        
        String campos[] = new String[cl.nrFeatures];
        /*System.out.println("Inicializando teste");        
        cl.inicializa();
        cl.agrupa(2);
        cl.listaCluster();
        System.out.println("Finalizando teste");*/
        
        System.out.println("Inicializando memória");

        //cl.inicializaArquivo("C:/ACER_D/ricardo/Mestrado/Mestrado/Arquivos/Analise/23-12-14/arquivos.csv");
        cl.inicializaArquivo("/home/samuel/Documentos/Projeto/arquivos_reuniao_13.01.15/arquivos.csv");
        System.out.println("---Arquivos carregados");
        //cl.inicializaCampos ("C:/ACER_D/ricardo/Mestrado/Mestrado/Arquivos/Analise/23-12-14/atributosComAlgoritmo.csv");
        cl.inicializaCampos ("/home/samuel/Documentos/Projeto/arquivos_reuniao_13.01.15/atributosComAlgoritmo.csv");
        System.out.println("---Atributos carregados");
        System.out.println("Memória inicializada");
        
        
        System.out.println("Formando cluster");
        cl.agrupa(22);
        System.out.println("Cluster formado");
        System.out.println("Listando Cluster");
        cl.listaCluster();
        System.out.println("Processo finalizado!");
        
        
        
    }
}
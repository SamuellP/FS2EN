/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.Weka.WekaTest;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author samuel
 */
public class ScreenUnifyFiles {
    private JFrame janela;
    private JPanel painel;
    private JTextField caminhoArquivos;
    private JTextField caminhoDiretorio;
    
    public appendFile af = new appendFile();
    public File[] entradas = null;
    public File saida = null;
    
    private void preparaJanela(){
        this.janela = new JFrame("Unificar Arquivos");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private void matraJanela(){
        janela.pack();
        janela.setSize(800,400);
        janela.setVisible(true);
    }
    
    private void preparaPainel(){
        this.painel = new JPanel();
        janela.add(painel);
    }
    
    private void botaoEscolherArquivos(){
        JButton botao = new JButton("Choose Files");
        botao.addActionListener(new ActionListener() {
            File[] retorno = null;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                retorno = new ChooseArff().chooseFiles();
                if(retorno != null){
                    entradas = retorno;
                    caminhoArquivos.setText(entradas.toString());
                }
            }
        });
            
        painel.add(botao);
    }
    
    private void botaoEscolherDiretorio(){
        JButton botao = new JButton("Choose Directory");
        botao.addActionListener(new ActionListener() {
            File retorno = null;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                retorno = new File(new ChooseArff().chooseDirectory() + "/novo.arff");
                if(retorno != null){
                    saida = retorno;
                    caminhoDiretorio.setText(saida.toString());
                }
            }
        });
        
        painel.add(botao);
    }
    
    private void botaoExecutar(){
        JButton botao = new JButton("Executar");
        botao.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    af.appendF(entradas, saida);
                } catch (IOException ex) {
                    Logger.getLogger(ScreenUnifyFiles.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        painel.add(botao);
    }
    
    private void textoArquivos(){
        caminhoArquivos = new JTextField(20);
        caminhoArquivos.setEditable(false);
        
        painel.add(caminhoArquivos);
    }
    
    private void textoDiretorio(){
        caminhoDiretorio = new JTextField(20);
        caminhoDiretorio.setEditable(false);
        
        painel.add(caminhoDiretorio);
    }
    
    public void executar(){
        this.preparaJanela();
        this.preparaPainel();
        this.botaoEscolherArquivos();
        this.textoArquivos();
        this.botaoEscolherDiretorio();
        this.textoDiretorio();
        this.botaoExecutar();
        this.matraJanela();
    }
}

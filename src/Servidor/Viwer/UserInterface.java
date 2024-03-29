package Servidor.Viwer;

import Servidor.Model.Commands;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class UserInterface extends JFrame {
    private JTextArea txtAescreve = new JTextArea("Digite uma mensagem");
    private JTextArea txtAleitor = new JTextArea();
    private JList userslist = new JList();
    private PrintWriter escrever;
    private BufferedReader ler;
    private JScrollPane scrollTxtAleitor = new JScrollPane(txtAleitor);
    public UserInterface(){
        //INICIO DA INTERFACE VISUAL
        setTitle("Chat Secretaria Ambiental");
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        txtAescreve.setPreferredSize(new Dimension(500,40));
        txtAescreve.setBackground(Color.LIGHT_GRAY);
        txtAescreve.setToolTipText("Digite uma mensagem");
        add(txtAescreve, BorderLayout.SOUTH);

        add(scrollTxtAleitor, BorderLayout.CENTER);
        txtAleitor.setEditable(false);
        txtAleitor.setBackground(Color.white);
        userslist.setPreferredSize(new Dimension(100, 400));
        userslist.setBackground(new Color(204, 255, 153));
        add(new JScrollPane(userslist), BorderLayout.WEST);
        setSize(500,500);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        String[] usuarios = new String[]{""};
        gerarListaUsuarios(usuarios);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                escrever.println(Commands.userlist);
                    System.exit(0);
                }

        });
    }

    //gera lista de usuarios para aparecer na interface
    private void gerarListaUsuarios(String[] usuarios) {
        DefaultListModel mod = new DefaultListModel();
        userslist.setModel(mod);
        for (String usuario: usuarios){
            mod.addElement(usuario);
        }
    }

    private void escritorStart() {
        txtAescreve.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent me) {
                String txt = txtAescreve.getText().trim();
                if(txt.equals("Digite uma mensagem")){
                    txtAescreve.setText("");
                }
            }
        });
        txtAescreve.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_ENTER){

                    Object usuario = userslist.getSelectedValue();
                    if(usuario != null){
                        //envia a mensagem para tela (txtAleitor), tela usada para receber as mensagens
                        String msg=txtAescreve.getText().trim();
                        if(msg == null || msg.isEmpty()){
                            JOptionPane.showMessageDialog(UserInterface.this, "Digite uma mensagem");
                            return;
                        }
                        txtAleitor.append("Eu para "+ usuario + " :");
                        txtAleitor.append(txtAescreve.getText());
                        txtAleitor.append("\n");

                        escrever.println(Commands.msg + usuario);
                        escrever.println(txtAescreve.getText());

                        txtAescreve.setText("");
                        e.consume();

                    }else{
                        JOptionPane.showMessageDialog(UserInterface.this, "Selecione um usuario!");
                        return;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

    public void chatStart() {
        try{
            final Socket user = new Socket("localHost", 9999);
            escrever = new PrintWriter(user.getOutputStream(), true);
            ler = new BufferedReader(new InputStreamReader(user.getInputStream()));
        }catch (UnknownHostException e){
            System.out.println("Erro no endereço do host");
            e.printStackTrace();
        }catch (IOException ee){
            System.out.println("Falha na conecxao com o host");
            ee.printStackTrace();
        }

    }
    public static void main(String[] args) {
        UserInterface userchat = new UserInterface();
        userchat.chatStart();
        userchat.escritorStart();
        userchat.leitorstart();

    }

    public void atuazarUserList(){
        escrever.println(Commands.userlist);
    }

    public void leitorstart(){
                try {
                    while (true){
                        String mensagem = new String(ler.readLine().getBytes(), "UTF-8");

                        if(mensagem.equals(Commands.userlist)){
                            String[] usuarios = ler.readLine().split(",");
                            gerarListaUsuarios(usuarios);
                        }else if(mensagem.equals(Commands.username)){
                            String username = JOptionPane.showInputDialog("Qual seu nome de usuario?");
                            escrever.println(username);
                            this.setTitle("Chat Secretaria Ambiental - "+ username);

                        }else if(mensagem.equals(Commands.usuarionegado)){
                           JOptionPane.showMessageDialog (UserInterface.this, "O usuario é invalido");

                        }else if (mensagem.equals(Commands.usuarioaceito)){
                            atuazarUserList();
                        }else{
                            txtAleitor.append(mensagem);
                            txtAleitor.append("\n");
                            txtAleitor.setCaretPosition(txtAleitor.getDocument().getLength());
                        }
                    }

                }catch (IOException e){
                    System.out.println("Erro na leitura da mensagem");
                    e.printStackTrace();}
            }

    public DefaultListModel getUserslist() {
        return (DefaultListModel) userslist.getModel();
    }
}

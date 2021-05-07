package Servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class UserManager extends Thread{

    private Socket user;
    public String nomeUsuario;
    private static final Map<String, UserManager> users = new HashMap<String, UserManager>();
    private BufferedReader ler;
    private PrintWriter escrever;

    public UserManager(Socket user){
        this.user = user;
        start();
    }

    @Override
    public void run() {
        try {
            ler =  new BufferedReader(new InputStreamReader(user.getInputStream()));
            escrever = new PrintWriter(user.getOutputStream(), true);

            startLogin();
            String msg;

            while (true){
                msg = ler.readLine();
                if(msg.equals(Commands.quit)){
                    this.user.close();
                } else if (msg.startsWith(Commands.msg)){
                    String nomeDestinatario = msg.substring(Commands.msg.length());
                    System.out.println("Mensagem enviada para: " + nomeDestinatario);
                    UserManager destinatario = users.get(nomeDestinatario);
                    if(destinatario == null){
                        escrever.println(("Usuario não existe"));
                    }else{
                        destinatario.getEscritor().println(this.nomeUsuario + " disse: " + ler.readLine());
                    }
                }else if (msg.equals(Commands.userlist)){
                    atualizaUserList(this);
                    }else{
                    escrever.println(this.nomeUsuario + ", você disse: " + msg);
            }
        }
        }catch (IOException e){
            System.out.println("Falha na conexao");
            e.printStackTrace();
        }
    }

    private void startLogin() throws IOException {
        escrever.println(Commands.username);
        String msg = ler.readLine();
        this.nomeUsuario = msg.replace(",","");
        escrever.println(Commands.usuarioaceito);
        escrever.println("Seja bem vindo ao chat " + this.nomeUsuario);
        users.put(this.nomeUsuario, this);
        for(String user: users.keySet()){
            atualizaUserList(users.get(user));
        }
    }

    private void atualizaUserList(UserManager userManager) {
        StringBuffer str = new StringBuffer();
        for(String u: users.keySet()){
            str.append(u);
            str.append(",");
        }
        str.delete(str.length()-1, str.length());
        userManager.getEscritor().println(Commands.userlist);
        userManager.getEscritor().println(str.toString());
    }

    public PrintWriter getEscritor() {
        return escrever;
    }

//    public BufferedReader getLeitor() {
//        return leitor;
//    }

    public String getNomeCliente() {
        return nomeUsuario;
    }
}

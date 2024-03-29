package Servidor.Model;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
            //facilita na leitura de dados
            ler =  new BufferedReader(new InputStreamReader(user.getInputStream()));
            //para facilitar o envio de dados
            escrever = new PrintWriter(new OutputStreamWriter(user.getOutputStream(), StandardCharsets.UTF_8), true);

            startLogin();
            String msg;

            while (true){
                msg = ler.readLine();
                if (msg.startsWith(Commands.msg)){
                    String nomeDestinatario = msg.substring(Commands.msg.length());
                    System.out.println("Mensagem enviada para: " + nomeDestinatario);


                    if(nomeDestinatario.equals("todos")){
                        String msgtodos=ler.readLine();
                        for (String userDestinatario : users.keySet()) {
                            System.out.println(userDestinatario);
                            UserManager destinatariotodos = users.get(nomeDestinatario);
                            if (userDestinatario != this.nomeUsuario){
                                users.get(userDestinatario).getEscritor().println(this.nomeUsuario + " disse para todos: " + msgtodos);
                            }
                        }
                    }
                    else{
                        UserManager destinatario = users.get(nomeDestinatario);
                        if(destinatario == null){
                            escrever.println(("Usuario não existe"));
                        }else{
                            destinatario.getEscritor().println(this.nomeUsuario + " sussurrou para você: " + ler.readLine());
                        }
                    }

                }else if (msg.equals(Commands.userlist)){
                    atualizaUserList(this);
                    }else{
                    escrever.println(this.nomeUsuario + ", você disse: " + msg);
            }
        }
        }catch (IOException e){
            System.out.println("Usuario fechou conexao");
            users.remove(this.nomeUsuario);
            for (String user : users.keySet()) {
                atualizaUserList(users.get(user));
            }
            e.printStackTrace();
        }
    }
    //Faz o controle do nome de usuario, não permite nome null, vazio ou repetido
    private void startLogin() throws IOException {
        while (true) {
            escrever.println(Commands.username);
            this.nomeUsuario = ler.readLine().toLowerCase().replace(",", "");
            if(this.nomeUsuario.equals("null") || this.nomeUsuario.isEmpty() || this.nomeUsuario.equals("todos")){
                escrever.println((Commands.usuarionegado));
            }else if (users.containsKey(this.nomeUsuario)) {
                escrever.println(Commands.usuarionegado);
            }else {
                escrever.println(Commands.usuarioaceito);
                escrever.println("Seja bem vindo ao chat " + this.nomeUsuario);
                users.put(this.nomeUsuario, this);
                for (String user : users.keySet()) {
                    atualizaUserList(users.get(user));
                }
                break;
            }
        }
    }

    //atualizar a lista de usuarios
    private void atualizaUserList(UserManager userManager) {
        StringBuffer str = new StringBuffer();
        int qtdUsuarios=0;
        for(String u: users.keySet()){
            qtdUsuarios+=1;
            if(userManager.getNomeCliente().equals(u))
                continue;
            str.append(u);
            str.append(",");
        }
        if(qtdUsuarios>=3){
            str.append("todos");
            str.append(",");
        }
        if(str.length()>0)
            str.delete(str.length()-1, str.length());
        userManager.getEscritor().println(Commands.userlist);
        userManager.getEscritor().println(str.toString());
    }

    public PrintWriter getEscritor() {
        return escrever;
    }

    public String getNomeCliente() {
        return nomeUsuario;
    }
}

package Testes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class UserSocket {
    public static void main(String[] args) {
        try{
            final Socket user = new Socket("localHost", 9999);

            new Thread(){

                @Override
                public void run() {
                    try {
                        BufferedReader ler = new BufferedReader(new InputStreamReader(user.getInputStream()));
                        while (true){
                            String mensagem = ler.readLine();
                            System.out.println("Server: " + mensagem);
                        }

                    }catch (IOException e){
                        System.out.println("Erro na leitura da mensagem");
                        e.printStackTrace();}
                }
            }.start();

            PrintWriter escrever = new PrintWriter(user.getOutputStream(), true);
            BufferedReader lerTerminal = new BufferedReader(new InputStreamReader(System.in));
            while (true){
                String mensagemTerminal = lerTerminal.readLine();
                if(mensagemTerminal.equalsIgnoreCase("::quit")){
                    System.exit(0);
                }
                escrever.println(mensagemTerminal);
            }

        }catch (UnknownHostException e){
            System.out.println("Erro no endereço do host");
            e.printStackTrace();
        }catch (IOException ee){
            System.out.println("Falha na conecxao com o host");
            ee.printStackTrace();
        }

    }
}
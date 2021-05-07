package Testes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClienteSocket {
    public static void main(String[] args) {
        try{
            final Socket cliente = new Socket("localHost", 9999);

            new Thread(){

                @Override
                public void run() {
                    try {
                        BufferedReader leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                        while (true){
                            String mensagem = leitor.readLine();
                            System.out.println("O servidor disse: " + mensagem);
                        }

                    }catch (IOException e){
                        System.out.println("Falha na leitura da mensagem");
                        e.printStackTrace();}
                }
            }.start();

            PrintWriter escritor = new PrintWriter(cliente.getOutputStream(), true);
            BufferedReader leitorTerminal = new BufferedReader(new InputStreamReader(System.in));
            while (true){
                String mensagemTerminal = leitorTerminal.readLine();
                if(mensagemTerminal.equalsIgnoreCase("::quit")){
                    System.exit(0);
                }
                escritor.println(mensagemTerminal);
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

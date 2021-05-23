package Servidor.Controller;

import Servidor.Model.UserManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorSocket {
    public static void main(String[] args) {
        ServerSocket servidor = null;
        try {
            System.out.println("Starting Server...");
            //O socket precisa definir um número de porta para receber conexões de clientes
            // é importante ressaltar que devemos usar somente portas a partir de 1024,
            // pois as portas abaixo deste número são reservadas para uso do sistema
            servidor = new ServerSocket(9999);
            System.out.println("Server Started!");

            while (true){
                Socket user = servidor.accept();
                new UserManager(user);
            }

        } catch (IOException e) {
            try {
                if(servidor != null){
                servidor.close();
                }
            } catch (IOException ex) {

            }
            System.out.println("Erro ao se conectar com o servidor, porta ocupado ou servidor fechado");
            e.printStackTrace();
        }
    }
}

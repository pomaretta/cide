package com.github.pomaretta.cide.console;

import java.util.ArrayList;

import com.github.pomaretta.cide.service.CacheProtocol;
import com.github.pomaretta.termux.Console.DefaultConsole;
import com.github.pomaretta.termux.Menu.DefaultInteractiveMenu;
import com.github.pomaretta.termux.Menu.OptionMenu;
import com.github.pomaretta.termux.Menu.OptionsMenu;
import com.github.pomaretta.termux.Menu.SequentialMenu;

public class Console extends DefaultConsole {

    private String hostname;
    private int port;

    private CacheProtocol protocol;

    private void init() {

        String[] options = new String[]{
            "Conectarse a un servidor",
            "Salir"
        };

        final OptionsMenu menu = new OptionMenu(
            options,
            "",
            "Practica 2 - CIDE",
            "%s",
            1,
            true
        );

        Parser parser = new Parser() {
            @Override
            protected int callBack(String arg0) throws Exception {
                switch (Integer.parseInt(arg0)) {
                    case 1:
                        // Obtener un valor | Conectarse a un server
                        if (protocol == null) {
                            registerServer();
                            break;
                        }
                        getValueMenu();
                        break;
                    case 2:
                        // Crear un valor | Salir
                        if (protocol == null) return -1;
                        createValueMenu();
                        break;
                    case 3:
                        // Eliminar un valor
                        if (protocol == null) break;
                        deleteValueMenu();
                        break;
                    case 4:
                        // Modificar un valor
                        if (protocol == null) break;
                        replaceValueMenu();
                        break;
                    case 5:
                        // Desconectarse de un servidor
                        if (protocol == null) break;
                        protocol.close();
                        protocol = null;
                        break;
                    case 6:
                        // Salir
                        if (protocol == null) break;
                        protocol.close();
                        return -1;
                }
                
                // Wait for user input
                System.in.read();
                // Clear console
                // Make cls if windows else clear
                System.out.print("\033[H\033[2J");
                System.out.flush();

                return 0;
            }
        };

        DefaultInteractiveMenu interactiveMenu = new DefaultInteractiveMenu(
            this.errorLog,
            menu,
            parser,
            this.reader,
            "> "
        ) {
            protected void outsideLoop() {
            };
            protected void loopBlock() {

                if (protocol != null && protocol.isConnected()) {
                    this.optionMenu = new OptionMenu(
                        new String[]{
                            "Obtener un valor",
                            "Crear un valor",
                            "Eliminar un valor",
                            "Modificar un valor",
                            "Desconectarse del servidor",
                            "Salir"
                        },
                        "",
                        "Practica 2 - CIDE",
                        "%s",
                        1,
                        true
                    );

                    System.out.printf(
                        "Server: %s:%d",
                        hostname,
                        port
                    );

                } else {
                    this.optionMenu = menu;
                }          

                this.optionMenu.show();
            };
        };

        interactiveMenu.show();
    }
 
    // Configuration of Server
    private void registerServer() {

        String[] messages = {
            "Hostname",
            "Port"
        };

        String[] validation = {
            "^[a-zA-Z0-9]*$",
            "^[0-9]*$"
        };

        SequentialMenu menu = new SequentialMenu(messages, this.reader, "", this.errorLog, validation);
        menu.show();

        ArrayList<String> values = menu.getOutput();

        this.hostname = values.get(0);
        this.port = Integer.parseInt(values.get(1));

        try {
            System.out.print("\nTrying to connect");
            for (int i = 0; i < 3; i++) {
                System.out.print(".");
                Thread.sleep(1000);
            }
            this.protocol = new CacheProtocol(
                hostname,
                port
            );
            this.protocol.openSession();
            System.out.println("\nConnected!");
        } catch (Exception e) {
            this.protocol = null;
            System.out.println("\nError: " + e.getMessage());
        }

    }

    // Obtener un valor
    private void getValueMenu() throws Exception {

        String[] messages = {
            "Key",
        };

        String[] validation = {
            "^[a-zA-Z0-9]*$"
        };

        SequentialMenu menu = new SequentialMenu(messages, this.reader, "", this.errorLog, validation);
        menu.show();

        ArrayList<String> values = menu.getOutput();

        String key = values.get(0);

        String result = null;
        try {
            // Aqui obtengo el valor, solo hago eso, en ningun momento uso sockets ni me preocupo de ello.
            // De eso se encarga el protocolo.
            result = this.protocol.get(key);
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
            return;
        }

        System.out.printf(
            "\n\tKey: %s\n\tValue: %s\n",
            key,
            result
        );
    }

    // Crear un valor
    private void createValueMenu() throws Exception {

        String[] messages = {
            "Key",
            "Value"
        };

        String[] validation = {
            "^[a-zA-Z0-9]*$",
            "^[a-zA-Z0-9 ]*$"
        };

        SequentialMenu menu = new SequentialMenu(messages, this.reader, "", this.errorLog, validation);
        menu.show();

        ArrayList<String> values = menu.getOutput();

        String key = values.get(0);
        String value = values.get(1);

        try {
            this.protocol.put(key,value);
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
            return;
        }

        System.out.printf(
            "Successfully created key: %s\n",
            key
        );
    }

    // Eliminar un valor
    private void deleteValueMenu() {

        String[] messages = {
            "Key"
        };

        String[] validation = {
            "^[a-zA-Z0-9]*$"
        };

        SequentialMenu menu = new SequentialMenu(messages, this.reader, "", this.errorLog, validation);
        menu.show();

        ArrayList<String> values = menu.getOutput();

        String key = values.get(0);
        
        try {
            this.protocol.remove(key);
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
            return;
        }

        System.out.printf(
            "Successfully removed key: %s\n",
            key
        );
    }

    // Reemplazar un valor
    private void replaceValueMenu() {

        String[] messages = {
            "Key",
            "New value"
        };

        String[] validation = {
            "^[a-zA-Z0-9]*$",
            "^[a-zA-Z0-9 ]*$"
        };

        SequentialMenu menu = new SequentialMenu(messages, this.reader, "", this.errorLog, validation);
        menu.show();

        ArrayList<String> values = menu.getOutput();

        String key = values.get(0);
        String value = values.get(1);

        try {
            this.protocol.replace(key, value);
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
            return;
        }

        System.out.printf(
            "Successfully replace key %s with value %s\n",
            key,
            value
        );
    }

    @Override
    protected void main() {
        this.init();
    }

}
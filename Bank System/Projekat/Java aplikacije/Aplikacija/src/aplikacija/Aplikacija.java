/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aplikacija;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author novim
 */
public class Aplikacija {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        while (true) {
            try {
                ArrayList<String> list = new ArrayList<>();
                String action = "";
                String s = "http://localhost:8080/Server/api/";
                System.out.println("S cim zelite da radite:");
                System.out.println("1. Mestima");
                System.out.println("2. Filijalama");
                System.out.println("3. Komitentima");
                System.out.println("4. Racunima");
                System.out.println("5. Transakcijama");
                System.out.println("6. Rezervnim kopijama");
                Scanner inp = new Scanner(System.in);
                String opt = inp.nextLine();
                switch (opt) {
                    case "1":
                        s = s + "mesto/";
                        System.out.println("1. Kreiraj mesto");
                        System.out.println("2. Ispisi sva mesta");
                        opt = inp.nextLine();
                        switch (opt) {
                            case "1":
                                System.out.println("Unesi postanski broj");
                                list.add(inp.nextLine() + "/");
                                System.out.println("Unesi naziv");
                                list.add(inp.nextLine() + "/");
                                action = "put";
                                break;
                            case "2":
                                list.add("mesta/");
                                action = "get";
                                break;
                        }
                        break;
                    case "2":
                        s = s + "filijala/";
                        System.out.println("1. Kreiraj filijalu");
                        System.out.println("2. Ispisi sve filijale");
                        opt = inp.nextLine();
                        switch (opt) {
                            case "1":
                                action = "put";
                                System.out.println("Unesi naziv");
                                list.add(inp.nextLine() + "/");
                                System.out.println("Unesi adresu");
                                list.add(inp.nextLine() + "/");
                                System.out.println("Unesi postansi broj mesta");
                                list.add(inp.nextLine() + "/");
                                break;
                            case "2":
                                action = "get";
                                break;

                        }
                        break;
                    case "3":
                        s = s + "kom/";
                        System.out.println("1.Kreiraj komitenta");
                        System.out.println("2.Promeni mesto komitentu");
                        System.out.println("3.Dohvati sve komitente");
                        opt = inp.nextLine();
                        switch (opt) {
                            case "1":
                                action = "put";
                                System.out.println("Unesi naziv");
                                list.add(inp.nextLine() + "/");
                                System.out.println("Unesi adresu");
                                list.add(inp.nextLine() + "/");
                                System.out.println("Unesi postansi broj mesta");
                                list.add(inp.nextLine() + "/");
                                break;
                            case "2":
                                action = "post";
                                System.out.println("Unesi naziv");
                                list.add(inp.nextLine() + "/");
                                System.out.println("Unezi novi postanski broj mesta");
                                list.add(inp.nextLine() + "/");
                                break;
                            case "3":
                                action = "get";
                                break;
                        }
                        break;
                    case "4":
                        s = s + "racun/";
                        System.out.println("1.Kreiraj racun");
                        System.out.println("2.Ugasi racun");
                        System.out.println("3.Dohvati sve racune komitenta");
                        opt = inp.nextLine();
                        switch (opt) {
                            case "1":
                                action = "put";
                                System.out.println("Unesi broj racuna");
                                list.add(inp.nextLine() + "/");
                                System.out.println("Unesi mesto gde se kreira racun");
                                list.add(inp.nextLine() + "/");
                                System.out.println("Unesi komitent");
                                list.add(inp.nextLine() + "/");
                                break;
                            case "2":
                                action = "delete";
                                System.out.println("Unesi broj racuna");
                                list.add(inp.nextLine() + "/");
                                break;
                            case "3":
                                action = "get";
                                System.out.println("Unesi naziv komitenta");
                                list.add(inp.nextLine() + "/");
                                break;
                        }
                        break;
                    case "5":
                        s = s + "transakcija/";
                        System.out.println("1. Kreiraj prenos");
                        System.out.println("2. Kreiraj uplatu");
                        System.out.println("3. Kreiraj isplatu");
                        System.out.println("4. Dohvati sve transakcije nekog racuna");
                        opt = inp.nextLine();
                        switch (opt) {
                            case "1":
                                action = "put";
                                list.add("prenos/");
                                System.out.println("Unesi broj racuan koji placa");
                                list.add(inp.nextLine() + "/");
                                System.out.println("Unesi broj racuna koji prima");
                                list.add(inp.nextLine() + "/");
                                System.out.println("Unesi velicinu transakcije");
                                list.add(inp.nextLine() + "/");
                                System.out.println("Unesi svrhu");
                                list.add(inp.nextLine() + "/");
                                break;
                            case "2":
                                action = "put";
                                list.add("uplata/");
                                System.out.println("Unesi broj racuna");
                                list.add(inp.nextLine() + "/");
                                System.out.println("Unesi velicinu transakcije");
                                list.add(inp.nextLine() + "/");
                                System.out.println("Unesi svrhu");
                                list.add(inp.nextLine() + "/");
                                break;
                            case "3":
                                action = "put";
                                list.add("isplata/");
                                System.out.println("Unesi broj racuan");
                                list.add(inp.nextLine() + "/");
                                System.out.println("Unesi velicinu transakcije");
                                list.add(inp.nextLine() + "/");
                                System.out.println("Unesi svrhu");
                                list.add(inp.nextLine() + "/");
                                break;
                            case "4":
                                action = "get";
                                list.add("getAll/");
                                System.out.println("Unesi broj racuan");
                                list.add(inp.nextLine() + "/");
                                break;
                        }
                        break;
                    case "6":
                        s = s + "backup/";
                        System.out.println("1. Dohvati backup");
                        System.out.println("2. Pokazi razlike");
                        opt = inp.nextLine();
                        switch (opt) {
                            case "2":
                                action = "get";
                                list.add("diff/");
                        }
                        break;

                }
                for (String add : list) {
                    s = s + add;
                }
                URL url = new URL(s);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setUseCaches(false);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod(action.toUpperCase());
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String output;

                StringBuilder response = new StringBuilder();
                while ((output = in.readLine()) != null) {
                    response.append(output);
                }

                in.close();
                System.out.println("Response: " + response.toString());
            } catch (MalformedURLException ex) {
                Logger.getLogger(Aplikacija.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Aplikacija.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

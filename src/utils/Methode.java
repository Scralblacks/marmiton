package utils;

import java.sql.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Methode {

    public static String askString(String ask){
        try(Scanner scan = new Scanner(System.in)){
            System.out.println(ask);
            return scan.nextLine();
        }
    }

    public static int askInt(String ask){
        try(Scanner scan = new Scanner(System.in)){
            System.out.println(ask);
            return scan.nextInt();
        }
    }

    public static boolean askBoolean(String ask){
        try(Scanner scan = new Scanner(System.in)){
            System.out.println(ask);
            return scan.nextBoolean();
        }
    }

    public static LocalDate askDate(String ask){
        try(Scanner scan = new Scanner(System.in)){
            System.out.println(ask + "(format yyyy-mm-dd)");
            return LocalDate.parse(scan.nextLine(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        }
    }
}
package domain;

import dao.ConnectionManager;

public class Main {

    public static void main(String[] args) {
        ConnectionManager.getConnectionInstance();



        ConnectionManager.closeConnection();
    }

}

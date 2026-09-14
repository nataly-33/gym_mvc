import model.DatabaseConnection;
public class TestDB {
    public static void main(String[] args) {
        try {
            DatabaseConnection.getInstance();
            System.out.println("Base de datos inicializada correctamente.");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

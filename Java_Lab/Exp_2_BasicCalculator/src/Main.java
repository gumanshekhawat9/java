
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalculatorView view = new CalculatorView();
            CalculatorModel model = new CalculatorModel();
            new CalculatorController(view, model);
        });
    }
}
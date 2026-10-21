# MVC Calculator (Java Swing)

A GUI calculator built with Java Swing that follows the Model-View-Controller (MVC) architecture. It takes two numbers and performs addition, subtraction, multiplication and division.

## Aim
To design a GUI-based calculator using Java Swing that performs basic arithmetic operations using the MVC architecture.

## Architecture

| Part | Class | Responsibility |
|------|-------|----------------|
| Model | `CalculatorModel` | Arithmetic logic only, no GUI code |
| View | `CalculatorView` | Swing GUI only, never performs calculations |
| Controller | `CalculatorController` | Reads input from the View, calls the Model, shows the result back in the View |

```
View (Swing GUI)  -->  Controller (ActionListener)  -->  Model (Calculations)
```

## Features
- Add, subtract, multiply and divide two numbers
- Error dialog for empty or non-numeric input
- Error dialog for division by zero
- Read-only result field

## Concepts Used
- MVC design pattern and separation of concerns
- `JFrame`, `JLabel`, `JTextField`, `JButton`, `JOptionPane`
- Event handling with `ActionListener`
- Exception handling (`NumberFormatException`, `ArithmeticException`)
- `SwingUtilities.invokeLater` to start the GUI on the correct thread

## Project Structure
```
Exp_2_MVCCalculator/
├── src/
│   ├── CalculatorModel.java
│   ├── CalculatorView.java
│   ├── CalculatorController.java
│   └── Main.java
└── README.md
```

## How to Run
From the `Exp_2_MVCCalculator` folder:
```
javac -d out src/CalculatorModel.java src/CalculatorView.java src/CalculatorController.java src/Main.java
java -cp out Main
```

## Sample Output
| First Number | Second Number | Operation | Result |
|--------------|---------------|-----------|--------|
| 25 | 10 | + | 35.0 |
| 25 | 10 | - | 15.0 |
| 25 | 10 | * | 250.0 |
| 10 | 4 | / | 2.5 |
| 5 | 0 | / | Error: Cannot divide by zero |

## Requirements
- JDK 8 or higher

## Author
Your Name, B.Tech CSE, GIT Jaipur
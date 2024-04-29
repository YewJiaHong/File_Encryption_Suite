package com.example.realcalculator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Interface {
    private static final Logger logger = Logger.getLogger(Interface.class.getName());
    @FXML
    private TextField display;
    private String displayValue = "";
    private double result = 0;
    private boolean clearDisplay = false;

    @FXML
    protected void clearDisplay() {
        display.setText("");
        result = 0;
        displayValue = "";
    }

    @FXML
    protected void appendToDisplay(ActionEvent event){
        if (clearDisplay) {
            clearDisplay = false;
            clearDisplay();
        }

        Button button = (Button) event.getSource();
        String textAppend = button.getText();

        if (Objects.equals(textAppend, "C")) {
            clearDisplay();
            return;
        } else{
            displayValue += textAppend;
        }

        display.setText(displayValue);
    }

    @FXML
    protected void calculate(){
        if (Objects.equals(display.getText(), "")) return; //if display is empty don't do anything


        try{
            result = MathExpressionParser.evaluateExpression(display.getText().replace("x", "*"));
            display.setText(String.valueOf(result));
        } catch(Exception ex){
            logger.log(Level.WARNING, "The expression is invalid: " + ex.getMessage());
            display.setText("NA");
        } finally{
            clearDisplay = true;
        }
    }

    public boolean isDouble(String input){
        try{
            double result = Double.parseDouble(input);
            return true;
        } catch (Exception e){
            return false;
        }
    }

}

package com.debugerr;

import java.util.List;
import java.util.Map;
import java.util.Set;


//Entities

/*
Problem Statement

Design and implement a Coffee Vending Machine system that can serve different types of coffee, manage ingredient inventory, process payments, and handle user interactions such as selecting coffee and refilling ingredients.

Requirements

Multiple Coffee Types: The machine should support multiple coffee recipes (e.g., Espresso, Latte, Cappuccino).
Ingredient Management: The machine should track and manage ingredient levels, and prevent dispensing if ingredients are insufficient.
Payment Processing: The machine should process payments before dispensing coffee.
Refill Ingredients: The machine should allow refilling of ingredients.
Extensibility: Easy to add new coffee types or payment methods.


Entities
Interface : Recipe
Interface : Ingredient
Interface : Payments

VendingMachine

processPayment
dispenseCoffee


CoffeeVendingMachine : STATES : IDLE, SELECT, PAYMENT, DISPENSE_SELECTION

*/

enum DispenseState {SUCCESS, ERROR}

interface Recipe {
    void dispense();
    List<Ingredient> getIngredients();
}

class CoffeeRecipe implements Recipe {
    private List<Ingredient> ingredientsList;
    String recipeName;

    CoffeeRecipe(List<Ingredient> ingredientsList, String recipeName) {
        this.ingredientsList = ingredientsList;
        this.recipeName = recipeName;
    }

    public void dispense()
    {
        System.out.println("DISPENSING COFFEE : " +  recipeName);
    }

    public List<Ingredient> getIngredients() {
        return ingredientsList;
    }
}


interface Ingredient {
    Integer addQuantity(Integer qty);
    Integer getQuantity();
    Integer useQuantity(Integer qty);
    String getName();
}



class Milk implements Ingredient {

    Integer quantity;
    String name;

    Milk(String name) {
        this.name = name;
        this.quantity = 0;
    }
    Milk(String name, Integer qty) {
        this.name = name;
        this.quantity = qty;
    }

    public Integer addQuantity(Integer qty) {
        this.quantity += qty;
        return this.quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer useQuantity(Integer qty) {
        this.quantity -= qty;
        return this.quantity;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

interface PaymentStrategy {
    void processPayment();
    void revertPayment();
}

class UPIPaymentStrategy implements PaymentStrategy {
    public void processPayment() {
        System.out.println("UPI Payment Proccessed");
    }

    @Override
    public void revertPayment() {
        System.out.println("UPI Payment Reverted");

    }
}

class CoffeeDispenser {
    Map<String, Ingredient> ingredientsMap;


    boolean canBeDispensed(Recipe recipe) {
        for(var ingredient : recipe.getIngredients())
        {
            if(!ingredientsMap.containsKey(ingredient.getName())) return false;
            if(ingredient.getQuantity() < ingredientsMap.get(ingredient.getName()).getQuantity()) return false;
        }
        return true;
    }
    void addIngredient(Ingredient ingredient)
    {
        if(ingredientsMap.containsKey(ingredient.getName())) ingredientsMap.get(ingredient.getName()).addQuantity(ingredient.getQuantity());
        else ingredientsMap.put(ingredient.getName(), ingredient);
    }
    DispenseState dispense(Recipe recipe){
        if(!canBeDispensed(recipe)) return DispenseState.ERROR;
        for(var ingredient : recipe.getIngredients())
        {
            ingredientsMap.get(ingredient.getName()).useQuantity(ingredient.getQuantity());
        }
        return DispenseState.SUCCESS;
    }
}

class CoffeeVendingMachine {
    CoffeeDispenser coffeeDispenser;
    Recipe selectedRecipe;
    List<Recipe> recipes;

    void addIngredients(Ingredient ingredient)
    {
        coffeeDispenser.addIngredient(ingredient);
    }
    void addRecipe(Recipe recipe)
    {
        recipes.add(recipe);
    }
    boolean selectRecipe(Recipe recipe)
    {
        if(coffeeDispenser.canBeDispensed(recipe)) return false;
        this.selectedRecipe = recipe;
        return true;
    }
    List<Recipe> getAllRecipes()
    {
        return recipes;
    }
    void processPaymentAndDispense(PaymentStrategy paymentStrategy)
    {
        paymentStrategy.processPayment();
        DispenseState dispenseState = coffeeDispenser.dispense(selectedRecipe);
        if(dispenseState.equals(DispenseState.ERROR)) {
            paymentStrategy.revertPayment();
        }
        selectedRecipe = null;
    }
}

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");
    }
}
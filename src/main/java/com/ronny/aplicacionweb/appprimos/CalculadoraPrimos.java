package com.ronny.aplicacionweb.appprimos;

import java.util.ArrayList; // Importa ArrayList para crear listas dinámicas
import java.util.List;      // Importa interfaz List

//Clase que calcula números primos en un rango dado

public class CalculadoraPrimos {

     //Encuentra todos los números primos entre inicio y fin
    public List<Integer> calcularPrimos(int inicio, int fin) {
        List<Integer> primos = new ArrayList<>(); // Crea lista vacía para guardar primos

        // Recorre cada número del rango
        for (int numero = inicio; numero <= fin; numero++) {
            if (esPrimo(numero)) { // Si el número es primo
                primos.add(numero); // Agrega el primo a la lista
            }
        }

        return primos; // Devuelve lista de primos encontrados
    }

    //Verifica si un número es primo

    private boolean esPrimo(int numero) {
        // Los números <= 1 no son primos
        if (numero <= 1) {
            return false;
        }
        // El 2 es el único primo par
        if (numero == 2) {
            return true;
        }
        // Los pares mayores que 2 no son primos
        if (numero % 2 == 0) {
            return false;
        }
        // Calcula la raíz cuadrada como límite superior
        int limite = (int) Math.sqrt(numero);

        // Verifica divisibilidad solo con números impares hasta el límite
        for (int i = 3; i <= limite; i += 2) {
            if (numero % i == 0) { // Si es divisible, no es primo
                return false;
            }
        }
        return true; // Si pasó todas las pruebas, es primo
    }
}
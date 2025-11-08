package com.ronny.aplicacionweb.appprimos;
/*
* Author Ronny Bastidas
* Fecha: 7/11/2025
* Descripcion:  * CLASE PRINCIPAL: PrimosServlet
 *
 * Este Servlet es el controlador principal de la aplicación.
 * Sus responsabilidades son:
 * 1. Recibir el formulario con el rango de números (inicio y fin)
 * 2. Validar que los datos sean correctos
 * 3. Calcular los números primos en ese rango
 * 4. Guardar los resultados en la sesión del usuario
 * 5. Mostrar los resultados en una página HTML
 *
 * La anotación @WebServlet mapea esta clase a la URL "/PrimosServlet"
 */


// Importaciones necesarias para el funcionamiento del Servlet
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**

 */
@WebServlet("/PrimosServlet")
public class PrimosServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Indica que vamos a enviar HTML con codificación UTF-8
        // (UTF-8 permite usar tildes, eñes y caracteres especiales)
        response.setContentType("text/html;charset=UTF-8");

        // El try-with-resources asegura que se cierre automáticamente
        try (PrintWriter out = response.getWriter()) {

            // getParameter("nombre") obtiene el valor del campo con ese nombre
            String inicioStr = request.getParameter("inicio");
            String finStr = request.getParameter("fin");

            // Si alguno es null, significa que no se enviaron correctamente
            if (inicioStr == null || finStr == null) {
                mostrarError(out, "Error: Parámetros inválidos");
                return; // Termina la ejecución del método
            }

            try {
                // parseInt() puede lanzar NumberFormatException si no son números
                int inicio = Integer.parseInt(inicioStr);
                int fin = Integer.parseInt(finStr);

                // - inicio debe ser >= 1 (no hay primos negativos o en cero)
                // - fin debe ser mayor que inicio (rango válido)
                if (inicio < 1 || fin < inicio) {
                    mostrarError(out, "Error: El rango es inválido. El inicio debe ser >= 1 y el fin debe ser mayor que el inicio.");
                    return;
                }

                // Se crea una instancia de CalculadoraPrimos (otra clase)
                // que contiene la lógica para encontrar números primos
                CalculadoraPrimos calculadora = new CalculadoraPrimos();
                List<Integer> primos = calculadora.calcularPrimos(inicio, fin);

                // La sesión permite almacenar datos que persisten entre peticiones
                // Esto es necesario para que PDFServlet pueda acceder a estos datos
                HttpSession session = request.getSession();
                session.setAttribute("inicio", inicio);      // Guarda el número inicial
                session.setAttribute("fin", fin);            // Guarda el número final
                session.setAttribute("primos", primos);      // Guarda la lista de primos

                // Mostrar los resultados en formato HTML
                mostrarResultados(out, inicio, fin, primos);

            } catch (NumberFormatException e) {
                // Se captura si parseInt() falla (si el usuario ingresó texto)
                mostrarError(out, "Error: Los valores ingresados no son números válidos");
            }
        }
    }

    private void mostrarResultados(PrintWriter out, int inicio, int fin, List<Integer> primos) {
        // ESTRUCTURA HTML BÁSICA
        out.println("<!DOCTYPE html>");
        out.println("<html lang='es'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Resultados - Números Primos</title>");

        // ESTILOS CSS INLINE
        // Define cómo se verá la página (colores, tamaños, espaciados)
        out.println("<style>");

        // Estilo del body: fuente, ancho máximo, centrado, fondo gris claro
        out.println("body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; background-color: #f5f5f5; }");

        // Contenedor principal: fondo blanco, bordes redondeados, sombra
        out.println(".container { background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");

        // Título centrado y en color gris oscuro
        out.println("h1 { color: #333; text-align: center; }");

        // Caja de información con fondo azul claro
        out.println(".info { background-color: #e3f2fd; padding: 15px; border-radius: 5px; margin: 20px 0; }");

        // Contenedor de los números primos con scroll si hay muchos
        out.println(".primos { background-color: #f5f5f5; padding: 15px; border-radius: 5px; margin: 20px 0; max-height: 400px; overflow-y: auto; }");

        // Estilo de cada número primo: caja verde con texto blanco
        out.println(".numero-primo { display: inline-block; background-color: #4CAF50; color: white; padding: 8px 12px; margin: 5px; border-radius: 5px; }");

        // Contenedor de botones en fila con espacio entre ellos
        out.println(".botones { display: flex; gap: 10px; margin-top: 20px; }");

        // Estilo base para los botones
        out.println(".btn { flex: 1; padding: 12px; text-align: center; text-decoration: none; border-radius: 5px; font-weight: bold; cursor: pointer; border: none; font-size: 16px; }");

        // Botón PDF en color rojo
        out.println(".btn-pdf { background-color: #f44336; color: white; }");

        // Botón volver en color azul
        out.println(".btn-volver { background-color: #2196F3; color: white; }");

        // Efecto hover: botones se vuelven más transparentes al pasar el mouse
        out.println(".btn:hover { opacity: 0.8; }");

        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='container'>");
        out.println("<h1>Resultados de Números Primos</h1>");

        // SECCIÓN DE INFORMACIÓN
        // Muestra el rango analizado y cuántos primos se encontraron
        out.println("<div class='info'>");
        out.println("<p><strong>Rango analizado:</strong> " + inicio + " - " + fin + "</p>");
        out.println("<p><strong>Cantidad de números primos encontrados:</strong> " + primos.size() + "</p>");
        out.println("</div>");

        // SECCIÓN DE NÚMEROS PRIMOS
        out.println("<h2>Números Primos Encontrados:</h2>");
        out.println("<div class='primos'>");

        // Si no hay primos, mostrar mensaje
        if (primos.isEmpty()) {
            out.println("<p>No se encontraron números primos en este rango.</p>");
        } else {
            // Mostrar cada número primo en una caja verde
            // El bucle recorre toda la lista de primos
            for (Integer primo : primos) {
                out.println("<span class='numero-primo'>" + primo + "</span>");
            }
        }
        out.println("</div>");

        // SECCIÓN DE BOTONES
        // Dos botones: uno para descargar PDF y otro para volver al inicio
        out.println("<div class='botones'>");
        out.println("<a href='PDFServlet' class='btn btn-pdf'>Descargar PDF</a>");
        out.println("<a href='index.html' class='btn btn-volver'>Nuevo Cálculo</a>");
        out.println("</div>");

        // Cerrar todas las etiquetas HTML
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }

    private void mostrarError(PrintWriter out, String mensaje) {
        // ESTRUCTURA HTML DE ERROR
        out.println("<!DOCTYPE html>");
        out.println("<html lang='es'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Error</title>");

        // ESTILOS PARA LA PÁGINA DE ERROR
        out.println("<style>");

        // Estilo del body similar al de resultados
        out.println("body { font-family: Arial, sans-serif; max-width: 600px; margin: 50px auto; padding: 20px; }");

        // Contenedor de error: fondo rojo claro con borde rojo a la izquierda
        out.println(".error-container { background-color: #ffebee; padding: 30px; border-radius: 10px; border-left: 5px solid #f44336; }");

        // Título en color rojo oscuro
        out.println("h1 { color: #c62828; }");

        // Botón para volver al formulario en color azul
        out.println("a { display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #2196F3; color: white; text-decoration: none; border-radius: 5px; }");

        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='error-container'>");
        out.println("<h1>Error</h1>");

        // Mostrar el mensaje de error recibido como parámetro
        out.println("<p>" + mensaje + "</p>");

        // Enlace para volver al formulario inicial
        out.println("<a href='index.html'>Volver al formulario</a>");

        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
}
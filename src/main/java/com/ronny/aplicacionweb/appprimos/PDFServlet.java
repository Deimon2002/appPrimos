package com.ronny.aplicacionweb.appprimos;
/*
 * Author Ronny Bastidas
 * Fecha: 7/11/2025
 * Descripcion: * CLASE PRINCIPAL: PDFServlet
 *
 * Este Servlet es responsable de generar un archivo PDF con los resultados
 * de los números primos que fueron calculados previamente por PrimosServlet.
 *
 * Funcionamiento:
 * 1. Recupera los datos de la sesión (que fueron guardados por PrimosServlet)
 * 2. Valida que los datos existan
 * 3. Crea un documento PDF profesional usando iText
 * 4. Formatea y organiza la información en el PDF
 * 5. Envía el PDF al navegador para descarga
 *
 * La anotación @WebServlet mapea esta clase a la URL "/PDFServlet"
 * Cuando el usuario hace clic en "Descargar PDF", se ejecuta este Servlet
 */

// IMPORTACIONES DE ITEXT (Librería para generar PDFs)
import com.itextpdf.text.pdf.draw.LineSeparator; // Para crear líneas separadoras en el PDF
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.itextpdf.text.*;  // Clases base para crear documentos PDF
import com.itextpdf.text.pdf.*; // Clases específicas para trabajar con PDFs
import java.io.IOException;
import java.util.List;


@WebServlet("/PDFServlet")
public class PDFServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // getSession() obtiene la sesión actual donde PrimosServlet guardó los datos
        HttpSession session = request.getSession();

        // Extraer los datos almacenados en la sesión
        // getAttribute() recupera el valor asociado a cada clave
        // El cast (Integer) convierte el Object devuelto al tipo correcto
        Integer inicio = (Integer) session.getAttribute("inicio");
        Integer fin = (Integer) session.getAttribute("fin");

        // @SuppressWarnings("unchecked") evita warnings del compilador
        // porque estamos haciendo un cast de Object a List<Integer>
        @SuppressWarnings("unchecked")
        List<Integer> primos = (List<Integer>) session.getAttribute("primos");

        // Si alguno es null, significa que el usuario accedió directamente
        // sin haber calculado primos primero
        if (inicio == null || fin == null || primos == null) {
            // Redirigir al formulario principal si no hay datos
            response.sendRedirect("index.html");
            return; // Terminar la ejecución
        }

        // setContentType indica que el contenido es un archivo PDF
        response.setContentType("application/pdf");

        // setHeader configura el nombre del archivo y fuerza la descarga
        // "attachment" hace que el navegador descargue en lugar de mostrar
        // "filename=" define el nombre del archivo descargado
        response.setHeader("Content-Disposition", "attachment; filename=numeros_primos.pdf");

        try {
            // GENERACIÓN DEL DOCUMENTO PDF
            // Document es la clase principal que representa el PDF
            Document documento = new Document(PageSize.A4);

            // PdfWriter conecta el documento con response.getOutputStream()
            // para que el PDF se envíe directamente al navegador
            PdfWriter.getInstance(documento, response.getOutputStream());

            // Sin esto, no se puede escribir nada en el PDF
            documento.open();


            // Crear fuente para el título: Helvetica, tamaño 18, negrita, gris oscuro
            Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);

            // Crear párrafo con el título
            Paragraph titulo = new Paragraph("Reporte de Números Primos", fuenteTitulo);

            // Centrar el título
            titulo.setAlignment(Element.ALIGN_CENTER);

            // Agregar espacio de 20 puntos después del título
            titulo.setSpacingAfter(20);

            // Agregar el título al documento
            documento.add(titulo);

            // SECCIÓN 2: INFORMACIÓN DEL RANGO


            // Crear fuente normal para información: Helvetica, tamaño 12
            Font fuenteInfo = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

            // Agregar párrafo con el rango analizado
            documento.add(new Paragraph("Rango analizado: " + inicio + " - " + fin, fuenteInfo));

            // Agregar párrafo con la cantidad de primos encontrados
            // primos.size() devuelve el número de elementos en la lista
            documento.add(new Paragraph("Cantidad de números primos: " + primos.size(), fuenteInfo));

            // Agregar una línea en blanco
            documento.add(Chunk.NEWLINE);

            // SECCIÓN 3: LÍNEA SEPARADORA

            // Crear y agregar una línea horizontal para separar secciones
            LineSeparator linea = new LineSeparator();
            documento.add(linea);

            // Agregar otra línea en blanco después de la línea separadora
            documento.add(Chunk.NEWLINE);

            // SECCIÓN 4: SUBTÍTULO

            // Crear fuente para subtítulo: Helvetica, tamaño 14, negrita
            Font fuenteSubtitulo = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);

            // Crear párrafo con el subtítulo
            Paragraph subtitulo = new Paragraph("Números Primos Encontrados:", fuenteSubtitulo);

            // Agregar espacio de 10 puntos después del subtítulo
            subtitulo.setSpacingAfter(10);

            // Agregar el subtítulo al documento
            documento.add(subtitulo);

            // SECCIÓN 5: TABLA DE NÚMEROS PRIMOS

            // Verificar si hay números primos para mostrar
            if (primos.isEmpty()) {
                // Si no hay primos, mostrar mensaje informativo
                documento.add(new Paragraph("No se encontraron números primos en este rango.", fuenteInfo));
            } else {
                // Si hay primos, crear una tabla para organizarlos

                // Crear tabla con 10 columnas
                // Esto significa que habrá 10 números por fila
                PdfPTable tabla = new PdfPTable(10);

                // setWidthPercentage(100) hace que la tabla ocupe todo el ancho
                tabla.setWidthPercentage(100);

                // Agregar espacio de 10 puntos antes de la tabla
                tabla.setSpacingBefore(10);

                // Configurar fuente para las celdas: Helvetica, tamaño 10
                Font fuenteCelda = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

                // Recorrer todos los números primos y agregarlos a la tabla
                for (Integer primo : primos) {
                    // Crear una celda con el número primo
                    // toString() convierte el Integer a String
                    // Phrase es un contenedor de texto con formato
                    PdfPCell celda = new PdfPCell(new Phrase(primo.toString(), fuenteCelda));

                    // Centrar el texto horizontalmente dentro de la celda
                    celda.setHorizontalAlignment(Element.ALIGN_CENTER);

                    // setPadding(5) agrega espacio interno de 5 puntos en todos los lados
                    celda.setPadding(5);

                    // Establecer color de fondo gris claro (RGB: 240, 240, 240)
                    celda.setBackgroundColor(new BaseColor(240, 240, 240));

                    // Agregar la celda a la tabla
                    // La tabla automáticamente organiza las celdas en filas de 10
                    tabla.addCell(celda);
                }

                // Agregar la tabla completa al documento
                documento.add(tabla);
            }

            // SECCIÓN 6: PIE DE PÁGINA CON FECHA

            // Agregar dos líneas en blanco para separar del contenido
            documento.add(Chunk.NEWLINE);
            documento.add(Chunk.NEWLINE);

            // Crear fuente para el pie: Helvetica, tamaño 9, cursiva, gris
            Font fuentePie = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY);

            // Crear párrafo con la fecha y hora de generación
            // new java.util.Date() obtiene la fecha/hora actual del sistema
            Paragraph pie = new Paragraph("Generado el: " + new java.util.Date().toString(), fuentePie);

            // Alinear el pie a la derecha
            pie.setAlignment(Element.ALIGN_RIGHT);

            // Agregar el pie al documento
            documento.add(pie);

            // PASO 8: Cerrar el documento
            // Esto finaliza el PDF y lo envía al navegador
            // Es MUY IMPORTANTE cerrar el documento
            documento.close();

        } catch (DocumentException e) {
            // DocumentException se lanza si hay problemas al crear el PDF
            // Por ejemplo: errores de formato, problemas con iText, etc.

            // Convertir la excepción de iText a IOException
            // y propagar el error con un mensaje descriptivo
            throw new IOException("Error al generar el PDF: " + e.getMessage());
        }
    }
}
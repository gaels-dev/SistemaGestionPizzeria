package mx.uv.sistemagestionpizzeria.util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import mx.uv.sistemagestionpizzeria.dto.DetalleRegistroInventarioDTO;
import mx.uv.sistemagestionpizzeria.dto.RegistroInventarioDTO;

public class GeneradorPDF {

    private static final Font FONT_TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
    private static final Font FONT_SUBTITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.DARK_GRAY);
    private static final Font FONT_TEXTO_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
    private static final Font FONT_TEXTO = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
    private static final Font FONT_TABLA_HDR = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);

    public static void generarReporteInventario(RegistroInventarioDTO registro, File destino) throws DocumentException, IOException {
        Document documento = new Document();
        PdfWriter.getInstance(documento, new FileOutputStream(destino));
        documento.open();

        // Encabezado
        Paragraph titulo = new Paragraph("ITALIA PIZZA - REPORTE DE INVENTARIO", FONT_TITULO);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        documento.add(titulo);

        // Información del Registro
        documento.add(new Paragraph("Información General", FONT_SUBTITULO));
        documento.add(new Paragraph("ID Registro: " + registro.getIdRegistro(), FONT_TEXTO));
        documento.add(new Paragraph("Fecha de Validación: " + registro.getFecha(), FONT_TEXTO));
        documento.add(new Paragraph("Empleado Responsable: " + registro.getNombreEmpleado(), FONT_TEXTO));
        documento.add(new Paragraph("Notas: " + (registro.getNotas() != null ? registro.getNotas() : "N/A"), FONT_TEXTO));
        documento.add(new Paragraph(" ", FONT_TEXTO)); // Espacio

        // Tabla de Detalles
        PdfPTable tabla = new PdfPTable(6); // 6 columnas
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(10f);
        tabla.setWidths(new float[]{15f, 35f, 10f, 13f, 13f, 14f});

        // Cabeceras de la tabla
        agregarCeldaCabecera(tabla, "Código");
        agregarCeldaCabecera(tabla, "Insumo");
        agregarCeldaCabecera(tabla, "Unidad");
        agregarCeldaCabecera(tabla, "Sistema");
        agregarCeldaCabecera(tabla, "Real");
        agregarCeldaCabecera(tabla, "Dif.");

        // Filas de la tabla
        for (DetalleRegistroInventarioDTO detalle : registro.getDetalles()) {
            tabla.addCell(new Phrase(detalle.getCodigoProducto(), FONT_TEXTO));
            tabla.addCell(new Phrase(detalle.getNombreProducto(), FONT_TEXTO));
            tabla.addCell(new Phrase(detalle.getUnidad(), FONT_TEXTO));
            tabla.addCell(new Phrase(String.format("%.2f", detalle.getCantidadSistema()), FONT_TEXTO));
            tabla.addCell(new Phrase(String.format("%.2f", detalle.getCantidadReal()), FONT_TEXTO));
            
            PdfPCell celdaDif = new PdfPCell(new Phrase(String.format("%.2f", detalle.getDiferencia()), FONT_TEXTO));
            if (detalle.getDiferencia() < 0) {
                celdaDif.setBackgroundColor(new BaseColor(255, 230, 230)); // Fondo rojizo suave
            } else if (detalle.getDiferencia() > 0) {
                celdaDif.setBackgroundColor(new BaseColor(230, 255, 230)); // Fondo verdoso suave
            }
            tabla.addCell(celdaDif);
        }

        documento.add(tabla);

        // Pie de página
        Paragraph pie = new Paragraph("\nReporte generado automáticamente por Sistema de Gestión Italia Pizza.", FONT_TEXTO);
        pie.setAlignment(Element.ALIGN_RIGHT);
        documento.add(pie);

        documento.close();
    }

    private static void agregarCeldaCabecera(PdfPTable tabla, String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, FONT_TABLA_HDR));
        celda.setBackgroundColor(new BaseColor(34, 34, 34));
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setPadding(5);
        tabla.addCell(celda);
    }
}

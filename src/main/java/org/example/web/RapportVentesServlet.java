package org.example.web;

import org.example.service.RapportVentesService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

@WebServlet("/rapport-ventes")
public class RapportVentesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest requete, HttpServletResponse reponse) throws IOException {
        YearMonth mois = YearMonth.now().minusMonths(1);
        Map<String, BigDecimal> totaux = new RapportVentesService().rapportMensuel(mois);
        reponse.setContentType("text/html;charset=UTF-8");
        PrintWriter sortie = reponse.getWriter();
        sortie.println("<!DOCTYPE html><html lang=\"fr\"><head><meta charset=\"UTF-8\"><title>Rapport de ventes</title></head><body>");
        sortie.println("<h1>Ventes du mois " + mois + "</h1><table><thead><tr><th>Catégorie</th><th>Chiffre d'affaires</th></tr></thead><tbody>");
        for (Map.Entry<String, BigDecimal> resultat : totaux.entrySet()) {
            sortie.println("<tr><td>" + echapperHtml(resultat.getKey()) + "</td><td>" + resultat.getValue().toPlainString() + "</td></tr>");
        }
        sortie.println("</tbody></table></body></html>");
    }

    private String echapperHtml(String texte) {
        return texte.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}

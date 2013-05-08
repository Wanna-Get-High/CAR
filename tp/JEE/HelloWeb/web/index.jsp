<%-- 
    Document   : index
    Created on : Apr 29, 2013, 3:49:52 PM
    Author     : WannaGetHigh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    String titre = request.getParameter("titre");
    String auteur = request.getParameter("auteur");
    String annee = request.getParameter("annee");
    titre = titre==null?"":titre;
    auteur = auteur==null?"":auteur;
    annee = annee==null?"":annee;
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="refresh" content="3;
              url=http://localhost:8080/HelloWeb/form.jsp?titre=<%out.print(titre);%>&auteur=<%out.print(auteur);%>&annee=<%out.print(annee);%>"> 
        <title>JSP Page</title>
    </head>
    <body>
        


        <h1>Vous avez entré: </h1>
        <b>Titre: </b>
            <% out.println(titre); %> <br>
        <b>Auteur: </b>
            <% out.println(auteur); %><br>
        <b>Année de parution:</b>
            <% out.println(annee); %>

        <br>
        <h3>Vous allez être redirigé dans 3 secondes </h3>
    </body>
</html>
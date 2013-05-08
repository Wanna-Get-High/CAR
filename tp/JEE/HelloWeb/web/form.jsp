<%-- 
    Document   : form
    Created on : Apr 29, 2013, 5:56:37 PM
    Author     : WannaGetHigh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <div>Form</div>
        
        <FORM NAME="form" method="post" action="http://localhost:8080/HelloWeb/index.jsp">
            Titre: <INPUT TYPE="text" NAME="titre" VALUE="<%out.print(request.getParameter("titre"));%>"> <BR> 
            Nom d'auteur: <INPUT TYPE="text" NAME="auteur" VALUE="<%out.print(request.getParameter("auteur"));%>"> <BR> 
            Année de parution: <INPUT TYPE="text" NAME="annee" VALUE="<%out.print(request.getParameter("annee"));%>"> <BR> 
            <input type="submit" value= "valider">
            <input type="reset" value="annuler" />
        </FORM>
    </body>
</html>

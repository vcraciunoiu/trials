<html>

<head>
<title>BAAAAAAAAAAH</title>
</head>

<body>
<h1>Product details page</h1>



    <%
       java.util.Enumeration e = System.getProperties().propertyNames();
       while( e.hasMoreElements() ){
       	String prop = (String)e.nextElement();
       	out.print(prop);
       	out.print(" = ");
       	out.print( System.getProperty(prop) );
       	out.print("<br>");
       }
    %>





<h2>AC1/PROD links</h2>
</body>

</html>
1. The method "Main.processProperty" doesn't make much sense now; 
   I created it in order to have the possibility for future extensions,
   as the property could be used for various purposes.
   
2. For the files in classpath, I understood that the prefix "classpath:resources/"
   is a custom one and the file "jdbc.properties" will be in the root of the JAR,
   as it is packaged by Maven.
   
3. I assumed based on the examples in the assignment description,
   that the JSON properties files will only be delivered by the
   web service and that the files from classpath and file-system
   will be in the "key=value" format.
   

MQTTDemoServer
you will need Java 17 and javafx-17.0.2 or later to run

to compile:  mvn -T24 package
to run: java --module-path $PATH_TO_FX --add-modules=javafx.controls,javafx.fxml -jar target/MQTTDemoServer-1.0.jar
echo $PATH_TO_FX
/data/java/javafx-sdk-17.0.2/lib

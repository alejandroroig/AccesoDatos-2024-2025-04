package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import modelos.Equipo;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoUtils {

    private static String URI;

    // MongoClient es el cliente que necesitamos para conectarnos al servidor de MongoDB
    private static final MongoClient mongoClient;
    // MongoDatabase representa la base de datos de MongoDB
    private static final MongoDatabase database;

    // Iniciar la conexión MongoDB con propiedades leídas desde el archivo mongo.properties
    static {
        cargarConfiguracion();

        // MongoDB usa codecs para convertir entre los objetos Java y los documentos BSON almacenados en la BBDD
        // sin necesidad de convertir manualmente objetos Java (POJOs) a objetos Document de MongoDB
        // CodecProvider se configura para habilitar la conversión automática de POJOs a BSON y viceversa
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        // CodecRegistry es un contenedor de codecs que se utilizan (el predeterminado y el de los POJOs)
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));

        mongoClient = MongoClients.create(URI);
        database = mongoClient.getDatabase(Objects.requireNonNull(new ConnectionString(URI).getDatabase()))
                .withCodecRegistry(pojoCodecRegistry);
    }

    // Obtener la colección 'equipos'
    private static MongoCollection<Equipo> getEquipoCollection() {
        return database.getCollection("equipos", Equipo.class);
    }

    // Insertar un nuevo equipo
    public static void insertarEquipo(Equipo equipo) {
        MongoCollection<Equipo> collection = getEquipoCollection();
        collection.insertOne(equipo);
    }

    // Buscar equipo por nombre
    public static Equipo obtenerEquipo(String nombre) {
        MongoCollection<Equipo> collection = getEquipoCollection();
        return collection.find(eq("nombre", nombre)).first();
    }

    public static List<Equipo> obtenerTodosLosEquipos() {
        MongoCollection<Equipo> collection = getEquipoCollection();
        return collection.find().into(new ArrayList<>());
    }

    // Actualizar equipo
    public static void actualizarEquipo(String nombre, Equipo equipoNuevo) {
        MongoCollection<Equipo> collection = getEquipoCollection();
        collection.replaceOne(eq("nombre", nombre), equipoNuevo);
    }

    // Eliminar equipo
    public static void eliminarEquipo(String nombre) {
        MongoCollection<Equipo> collection = getEquipoCollection();
        collection.deleteOne(eq("nombre", nombre));
    }

    private static void cargarConfiguracion() {
        Properties properties = new Properties();
        try (InputStream input = MongoUtils.class.getClassLoader().getResourceAsStream("mongo.properties")) {
            if (input == null) {
                System.err.println("No se encuentra el archivo de propiedades");
                return;
            }
            properties.load(input);
            URI = properties.getProperty("mongo.uri");
        } catch (IOException ex) {
            System.err.println("Error al cargar el archivo de propiedades: " + ex.getMessage());
        }
    }

    public static void poblarEquiposDesdeJson(String rutaArchivo) {
        MongoCollection<Equipo> collection = getEquipoCollection();
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        try (Reader reader = Files.newBufferedReader(Path.of(rutaArchivo))) {
            List<Equipo> equipos = objectMapper.readValue(reader, new TypeReference<>() {});
            collection.insertMany(equipos);
            System.out.println("Insertados equipos desde JSON");
        } catch (IOException e) {
            System.err.println("Error al leer el archivo JSON: " + e.getMessage());
        }
    }

    public static void vaciarColeccion() {
        MongoCollection<Equipo> collection = getEquipoCollection();
        collection.drop();
        System.out.println("Colección vaciada");
    }
}

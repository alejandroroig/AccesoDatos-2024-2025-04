package ejercicio.utils;

import ejemplo.modelos.Equipo;
import ejercicio.modelos.CasaHogwarts;
import ejercicio.modelos.Estudiante;
import ejercicio.modelos.Profesor;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class Operaciones {

    public Operaciones() {
        RdsManager rdsManager = new RdsManager();
        DynamoDbManager dynamoDbManager = new DynamoDbManager();
    }

    public void migrarCasasHogwarts() {
        try (Connection conn = RdsManager.getConnection()) {
            List<CasaHogwarts> casas = obtenerCasasHogwarts(conn);
            crearTabla();
            casas.forEach(Operaciones::insertarCasa);
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    }

    private static List<CasaHogwarts> obtenerCasasHogwarts(Connection conexion) throws SQLException {
        String consulta = "SELECT " +
                "casa.nombre_casa AS casa_nombre, " +
                "casa.fundador AS fundador, " +
                "casa.fantasma AS fantasma, " +
                "prof.nombre || ' ' || prof.apellido AS jefe_nombre, " +
                "asig.nombre_asignatura AS jefe_asignatura, " +
                "estu.nombre || ' ' || estu.apellido AS estudiante_nombre, " +
                "estu.anyo_curso AS curso, " +
                "estu.fecha_nacimiento AS fecha_nacimiento, " +
                "masc.nombre_mascota AS mascota " +
                "FROM Casa casa " +
                "JOIN Profesor prof ON casa.id_jefe = prof.id_profesor " +
                "LEFT JOIN Asignatura asig ON prof.id_asignatura = asig.id_asignatura " +
                "LEFT JOIN Estudiante estu ON estu.id_casa = casa.id_casa " +
                "LEFT JOIN Mascota masc ON masc.id_estudiante = estu.id_estudiante " +
                "ORDER BY casa.nombre_casa, estu.nombre, estu.apellido;";

        Map<String, CasaHogwarts> casasMap = new HashMap<>();

        try (PreparedStatement stmt = conexion.prepareStatement(consulta);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String nombreCasa = rs.getString("casa_nombre");

                CasaHogwarts casa = casasMap.get(nombreCasa);
                if (casa == null) {
                    // Crear el jefe de casa
                    String jefeNombre = rs.getString("jefe_nombre");
                    String jefeAsignatura = rs.getString("jefe_asignatura");
                    Profesor jefe = new Profesor(jefeNombre, jefeAsignatura);

                    // Crear la nueva casa y agregar al mapa
                    casa = new CasaHogwarts(
                            nombreCasa,
                            rs.getString("fundador"),
                            rs.getString("fantasma"),
                            jefe,
                            new ArrayList<>()
                    );
                    casasMap.put(nombreCasa, casa);
                }
                // Crear el estudiante si tiene datos en esa fila
                String estudianteNombre = rs.getString("estudiante_nombre");
                if (estudianteNombre != null) {
                    int curso = rs.getInt("curso");
                    LocalDate fechaNacimiento = rs.getDate("fecha_nacimiento").toLocalDate();
                    String mascota = rs.getString("mascota");

                    Estudiante estudiante = new Estudiante(
                            estudianteNombre, curso, fechaNacimiento, mascota
                    );
                    casa.getEstudiantes().add(estudiante);
                }
            }
        }
        // Devolver como lista
        return new ArrayList<>(casasMap.values());
    }

    public static void insertarCasa(CasaHogwarts casa) {
        // Obtener la tabla para realizar operaciones
        DynamoDbTable<CasaHogwarts> tabla = DynamoDbManager.getEnhancedClient()
                .table("CasasHogwarts", TableSchema.fromBean(CasaHogwarts.class));
        // Insertar la casa en la tabla
        tabla.putItem(casa);
        System.out.println("Casa insertada correctamente: " + casa.getNombre());
    }

    private static void crearTabla() {
        if (tablaExiste()) {
            System.out.println("La tabla CasasHogwarts ya existe");
            return;
        }
        // Crear la tabla usando la clase de esquema de la entidad Equipo
        DynamoDbManager.getEnhancedClient().table("CasasHogwarts", TableSchema.fromBean(CasaHogwarts.class)).createTable();
        System.out.println("Creando tabla CasasHogwarts");

        // Esperar hasta que la tabla esté activa
        try (DynamoDbWaiter waiter = DynamoDbManager.getClient().waiter()) {
            waiter.waitUntilTableExists(DescribeTableRequest.builder().tableName("CasasHogwarts").build());
            System.out.println("Tabla CasasHogwarts creada y activa.");
        } catch (Exception e) {
            System.err.println("Error al esperar a que la tabla esté activa: " + e.getMessage());
        }
    }

    private static boolean tablaExiste () {
        try {
            // Intenta describir la tabla. Si no existe, se lanzará una excepción
            DynamoDbManager.getClient().describeTable(r -> r.tableName("CasasHogwarts"));
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    public List<CasaHogwarts> obtenerTodasCasas() {
        DynamoDbTable<CasaHogwarts> tabla = DynamoDbManager.getEnhancedClient()
                .table("CasasHogwarts", TableSchema.fromBean(CasaHogwarts.class));
        // Crear una solicitud de escaneo para obtener todos los elementos
        ScanEnhancedRequest scanRequest = ScanEnhancedRequest.builder().build();

        // Escanear la tabla y convertir los resultados a una lista
        List<CasaHogwarts> listaCasas = new ArrayList<>();
        // Escanear la tabla y obtener un iterable de páginas de resultados
        tabla.scan(scanRequest).forEach(page -> listaCasas.addAll(page.items()));
        return listaCasas;
    }

    public CasaHogwarts obtenerCasa(String nombreCasa) {
        DynamoDbTable<CasaHogwarts> tabla = DynamoDbManager.getEnhancedClient()
                .table("CasasHogwarts", TableSchema.fromBean(CasaHogwarts.class));
        Key key = Key.builder().partitionValue(nombreCasa).build();
        // Obtener la casa usando la clave
        CasaHogwarts casa = tabla.getItem(r -> r.key(key));
        if (casa != null) {
            System.out.println("Casa encontrado: " + casa.getNombre());
        } else {
            System.out.println("Casa no encontrada");
        }
        return casa;
    }

    public void actualizarCasa(CasaHogwarts casa) {
        // Primero, verificar si la casa existe antes de actualizar
        if (obtenerCasa(casa.getNombre()) != null) {
            DynamoDbTable<CasaHogwarts> tabla = DynamoDbManager.getEnhancedClient()
                    .table("CasasHogwarts", TableSchema.fromBean(CasaHogwarts.class));
            // Actualizar la casa en la tabla
            tabla.updateItem(casa);
            System.out.println("Casa actualizada: " + casa.getNombre());
        } else {
            System.out.println("La casa no existe y no se puede actualizar: " + casa.getNombre());
        }
    }

    public void borrarCasa(String nombre) {
        DynamoDbTable<CasaHogwarts> tabla = DynamoDbManager.getEnhancedClient()
                .table("CasasHogwarts", TableSchema.fromBean(CasaHogwarts.class));
        // Crear la clave de la casa a borrar
        Key key = Key.builder().partitionValue(nombre).build();
        // Borrar la casa de la tabla
        tabla.deleteItem(r -> r.key(key));
        System.out.println("Casa borrada: " + nombre);
    }

    public void borrarTabla() {
        // Verificar si la tabla existe antes de intentar borrarla
        if (!tablaExiste()) {
            System.out.println("La tabla CasasHogwarts no existe");
            return;
        }

        // Solicitud para borrar la tabla
        DynamoDbManager.getClient().deleteTable(DeleteTableRequest.builder().tableName("CasasHogwarts").build());
        System.out.println("Eliminando la tabla CasasHogwarts");

        // Esperar hasta que la tabla ya no exista
        try (DynamoDbWaiter waiter = DynamoDbManager.getClient().waiter()) {
            waiter.waitUntilTableNotExists(DescribeTableRequest.builder().tableName("CasasHogwarts").build());
        }
        System.out.println("Tabla CasasHogwarts eliminada");
    }
}

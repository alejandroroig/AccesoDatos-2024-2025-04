import modelos.Equipo;
import modelos.Piloto;
import utils.MongoUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class App {
    public static void main(String[] args) {

        // Poblamos la base de datos con datos reales y mostramos los equipos insertados
        MongoUtils.poblarEquiposDesdeJson("src/main/resources/equipos.json");
        List<Equipo> equipos = MongoUtils.obtenerTodosLosEquipos();
        equipos.forEach(System.out::println);

        // Creamos pilotos y equipo de prueba
        Piloto piloto1 = new Piloto("Isack Hadjar",
                98,
                LocalDate.of(2004, 9, 28),
                "France");

        Piloto piloto2 = new Piloto("Pepe Martí",
                99,
                LocalDate.of(2005, 6, 13),
                "Spain");

        Equipo equipo = new Equipo("Campos Racing",
                "Spain",
                Arrays.asList(piloto1, piloto2));

        // Insertamos el equipo
        MongoUtils.insertarEquipo(equipo);
        System.out.println("Equipo insertado");

        // Obtenemos el equipo
        Equipo equipoObtenido = MongoUtils.obtenerEquipo("Campos Racing");
        if (equipoObtenido == null) {
            System.out.println("Equipo no encontrado");
        }
        else {
            System.out.println("Equipo obtenido: " + equipoObtenido);
            // Actualizamos el equipo
            equipoObtenido.setNacionalidad("Andorra");
            MongoUtils.actualizarEquipo("Campos Racing", equipoObtenido);
            // Obtenemos el equipo actualizado
            equipoObtenido = MongoUtils.obtenerEquipo("Campos Racing");
            System.out.println("Equipo actualizado: " + equipoObtenido);
        }

        // Borramos el equipo
        MongoUtils.eliminarEquipo("Campos Racing");
        System.out.println("Equipo borrado");

        // Vaciamos la colección equipos
        MongoUtils.vaciarColeccion();
    }
}

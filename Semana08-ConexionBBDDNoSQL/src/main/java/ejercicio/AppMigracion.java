package ejercicio;

import ejercicio.modelos.CasaHogwarts;
import ejercicio.modelos.Estudiante;
import ejercicio.modelos.Profesor;
import ejercicio.utils.Operaciones;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class AppMigracion {
    public static void main(String[] args) {
        Operaciones operaciones = new Operaciones();
        operaciones.migrarCasasHogwarts();

        // Crear estudiantes, jefe y casa
        Estudiante estudiante1 = new Estudiante();
        estudiante1.setNombre("Selena Shade");
        estudiante1.setCurso(4);
        estudiante1.setFechaNacimiento(LocalDate.of(2007, 5, 23));
        estudiante1.setMascota("Umbra");

        Estudiante estudiante2 = new Estudiante();
        estudiante2.setNombre("Theo Blackthorn");
        estudiante2.setCurso(3);
        estudiante2.setFechaNacimiento(LocalDate.of(2008, 10, 11));

        Estudiante estudiante3 = new Estudiante();
        estudiante3.setNombre("Luna Ashwood");
        estudiante3.setCurso(5);
        estudiante3.setFechaNacimiento(LocalDate.of(2006, 1, 17));
        estudiante3.setMascota("Mistral");

        Profesor jefe = new Profesor();
        jefe.setNombre("Aldous Nightshade");
        jefe.setAsignatura("MAgia Arcana");

        CasaHogwarts casa = new CasaHogwarts();
        casa.setNombre("Dracorfan");
        casa.setFundador("Leonis Dracorfan");
        casa.setFantasma("La Sombra de Ébano");
        casa.setJefe(jefe);
        casa.setEstudiantes(new ArrayList<>());
        casa.setEstudiantes(Arrays.asList(estudiante1, estudiante2, estudiante3));

        // Insertar casa
        Operaciones.insertarCasa(casa);
        System.out.println("Número de casas " + operaciones.obtenerTodasCasas().size());

        // Obtener la casa
        CasaHogwarts casaObtenida = operaciones.obtenerCasa("Dracorfan");
        System.out.println("Casa obtenida: " + casaObtenida);

        // Actualizar casa con nuevo estudiante
        Estudiante nuevoEstudiante = new Estudiante();
        nuevoEstudiante.setNombre("Cyrus Stormrider");
        nuevoEstudiante.setCurso(1);
        nuevoEstudiante.setFechaNacimiento(LocalDate.of(2011, 12, 20));
        nuevoEstudiante.setMascota("Tempus");

        casaObtenida.getEstudiantes().add(nuevoEstudiante);

        // Actualizar casa
        operaciones.actualizarCasa(casaObtenida);

        // Obtener la casa
        casaObtenida = operaciones.obtenerCasa("Dracorfan");
        System.out.println("Casa obtenida: " + casaObtenida);

        // Borrar casa
        operaciones.borrarCasa("Dracorfan");
        System.out.println("Número de casas " + operaciones.obtenerTodasCasas().size());

        // Borrar la tabla al finalizar
        operaciones.borrarTabla();
    }
}

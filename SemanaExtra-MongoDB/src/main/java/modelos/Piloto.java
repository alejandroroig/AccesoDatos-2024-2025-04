package modelos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Piloto {
    private String nombre;
    private int numero;
    private LocalDate fechaNacimiento;
    private String nacionalidad;
}

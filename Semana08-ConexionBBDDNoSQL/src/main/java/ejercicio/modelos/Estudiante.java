package ejercicio.modelos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class Estudiante {
    private String nombre;
    private int curso;
    private LocalDate fechaNacimiento;
    private String mascota;
}

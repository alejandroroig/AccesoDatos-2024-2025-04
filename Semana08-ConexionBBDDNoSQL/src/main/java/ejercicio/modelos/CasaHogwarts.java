package ejercicio.modelos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class CasaHogwarts {
    private String nombre;
    private String fundador;
    private String fantasma;
    private Profesor jefe;
    private List<Estudiante> estudiantes;

    @DynamoDbPartitionKey
    public String getNombre() {
        return nombre;
    }
}

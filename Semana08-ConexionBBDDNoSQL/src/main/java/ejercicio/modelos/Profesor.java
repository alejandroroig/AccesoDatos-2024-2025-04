package ejercicio.modelos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class Profesor {
    private String nombre;
    private String asignatura;
}
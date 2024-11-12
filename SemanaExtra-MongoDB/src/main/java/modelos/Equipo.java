package modelos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.codecs.pojo.annotations.BsonRepresentation;
import org.bson.types.ObjectId;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipo {
    @BsonId // Indicamos que este atributo es el identificador (_id) del documento
    private ObjectId id;
    private String nombre;
    private String nacionalidad;
    private List<Piloto> pilotos;

    // @BsonCreator define un constructor específico que MongoDB debe utilizar para deserializar
    // los documentos BSON en instancias de Equipo.
    @BsonCreator
    public Equipo(
            // @BsonProperty mapea el nombre de la clase Java con el nombre de la propiedad en MongoDB.
            // Realmente, no sería necesario, ya que los nombres coinciden, pero para que lo tengas en cuenta
            @BsonProperty("nombre") String nombre,
            @BsonProperty("nacionalidad") String nacionalidad,
            @BsonProperty("pilotos") List<Piloto> pilotos) {
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
        this.pilotos = pilotos;
    }
}

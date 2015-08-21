package springstuff.postgres;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="tabela1pgres")
public class ClasaUnuPgres {

	@Id
	@GeneratedValue
	Long id;
	
	String nume;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNume() {
		return nume;
	}

	public void setNume(String nume) {
		this.nume = nume;
	}

}

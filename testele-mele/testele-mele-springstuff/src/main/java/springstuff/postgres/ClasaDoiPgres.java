package springstuff.postgres;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="tabela2pgres")
public class ClasaDoiPgres {

	@Id
	Long id;
	String comentariu;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getComentariu() {
		return comentariu;
	}

	public void setComentariu(String comentariu) {
		this.comentariu = comentariu;
	}

}
